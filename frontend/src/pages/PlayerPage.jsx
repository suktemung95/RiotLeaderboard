import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react'
import "./PlayerPage.css";

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer
} from "recharts";

const URL_BASE = "http://localhost:8080/"

const DDRAGON_VERSION = "16.20.1";

export default function PlayerPage() {

  const [matches, setMatches] = useState([]);
  const [player, setPlayer] = useState(null);
  const [rankHistory, setRankHistory] = useState([])
  const [refreshing, setRefreshing] = useState(false);
  const [oldestMatchCursor, setOldestMatchCursor] = useState(null);
  const [hasMore, setHasMore] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);

  const { region, gameName, tagLine } = useParams();

  function getChampionIcon(championName) {
    return `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/img/champion/${championName}.png`;
  }

  function getItemIcon(itemId) {
    if (!itemId || itemId === 0) {
      return null;
    }

    return `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/img/item/${itemId}.png`;
  }

  function getGameMode(queueId) {
    switch (queueId) {
      case 420:
        return "Ranked Solo";
      case 440:
        return "Ranked Flex";
      case 450:
        return "ARAM";
      case 1750:
        return "Arena";
      default:
        return `Queue ${queueId}`;
    }
  }

  function getRankEmblem(tier) {
    if (!tier) return null;

    return `https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/ranked-emblem/emblem-${tier.toLowerCase()}.png`;
  }

  function formatGameDate(timestamp) {
    const date = new Date(timestamp);

    return date.toLocaleString("en-US", {
      month: "numeric",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit"
    });
  }

  function formatTimeAgo(timestamp) {
    const now = new Date();
    const recorded = new Date(timestamp);

    const diffMs = now - recorded;
    const diffMinutes = Math.floor(diffMs / (1000 * 60));

    const days = Math.floor(diffMinutes / (60 * 24));
    const hours = Math.floor((diffMinutes % (60 * 24)) / 60);
    const minutes = diffMinutes % 60

    if (days > 0) {
      return `${days}d ${hours}h ago`;
    }

    if (hours > 0) {
      return `${hours}h ago`;
    }

    if (minutes > 5) {
      return "Less than 1h ago";
    }
    
    return "Just now"
  }

  function formatRole(teamPosition) {
    switch (teamPosition) {
      case "TOP":
        return "TOP";
      case "JUNGLE":
        return "JUNGLE";
      case "MIDDLE":
        return "MID";
      case "BOTTOM":
        return "ADC";
      case "UTILITY":
        return "SUPPORT";
      default:
        return teamPosition;
    }
  }

  function rankToValue(tier, rank, lp) {
    const tierValues = {
      IRON: 0,
      BRONZE: 400,
      SILVER: 800,
      GOLD: 1200,
      PLATINUM: 1600,
      EMERALD: 2000,
      DIAMOND: 2400,
      MASTER: 2800,
      GRANDMASTER: 3200,
      CHALLENGER: 3600
    };

    const rankValues = {
      IV: 0,
      III: 100,
      II: 200,
      I: 300
    };

    return (
      (tierValues[tier] ?? 0) +
      (rankValues[rank] ?? 0) +
      lp
    );
  }

  function valueToRankLabel(value) {
    const tiers = [
      "Iron",
      "Bronze",
      "Silver",
      "Gold",
      "Platinum",
      "Emerald",
      "Diamond"
    ];

    const tierIndex = Math.floor(value / 400);

    if (tierIndex >= tiers.length) {
      if (value >= 3600) return "Challenger";
      if (value >= 3200) return "Grandmaster";
      return "Master";
    }

    const withinTier = value % 400;
    const divisionIndex = Math.floor(withinTier / 100);

    const divisions = ["IV", "III", "II", "I"];

    return `${tiers[tierIndex]} ${divisions[divisionIndex]}`;
  }

  async function refreshPlayer() {

    try {
      setRefreshing(true)
      const params = new URLSearchParams({
        region,
        gameName,
        tagLine
      });

      const response = await fetch(
        URL_BASE + `players/refresh?${params}`,
        {
          method: "POST"
        }
      );

      if (!response.ok) {
        throw new Error("Failed to refresh player");
      }

      const result = await response.json();

      setPlayer(result.data.playerOverview);
      setMatches(result.data.matchesData.items);
      setOldestMatchCursor(result.data.matchesData.nextCursor);
      setHasMore(result.data.matchesData.hasMore);
    } finally {
      setRefreshing(false)
    }
  }

  async function loadMoreMatches() {
    if (oldestMatchCursor == null) return;

    try {
      setLoadingMore(true);

      const params = new URLSearchParams({
        region,
        gameName,
        tagLine,
        before: oldestMatchCursor
      });

      const response = await fetch(
        URL_BASE + `players/matches?${params}`
      );

      if (!response.ok) {
        throw new Error("Failed to load more matches");
      }

      const result = await response.json();

      setMatches(previousMatches => [
        ...previousMatches,
        ...result.data.items
      ]);

      setOldestMatchCursor(result.data.nextCursor)
      setHasMore(result.data.hasMore)
      } finally {
        setLoadingMore(false);
      }
  }
  
  useEffect(() => {
    async function fetchData() {
      const params = new URLSearchParams({
        region,
        gameName,
        tagLine
      });

      const overviewUrl = URL_BASE + `players/overview?${params}`;
      const matchesUrl = URL_BASE + `players/matches?${params}`;
      const rankHistoryUrl = URL_BASE + `players/history?${params}`;

      const [overviewResponse, matchesResponse, rankHistoryResponse] = await Promise.all([
        fetch(overviewUrl),
        fetch(matchesUrl),
        fetch(rankHistoryUrl)
      ]);

      const overviewResult = await overviewResponse.json();
      const matchesResult = await matchesResponse.json();
      const rankHistoryResult = await rankHistoryResponse.json();

      console.log("Overview: ", overviewResult);
      console.log("Matches: ", matchesResult);
      console.log("Rank history:", rankHistoryResult);

      setPlayer(overviewResult.data);
      setMatches(matchesResult.data);
      setMatches(matchesResult.data.items);
      setOldestMatchCursor(matchesResult.data.nextCursor);
      setHasMore(matchesResult.data.hasMore);
      setRankHistory(rankHistoryResult.data);
    }

    fetchData();
  }, [region, gameName, tagLine]);

  const graphData = [...rankHistory]
    .sort(
      (a, b) =>
        new Date(a.recordedAt) - new Date(b.recordedAt)
    )
    .map((snapshot, index) => ({
      ...snapshot,

      game: index + 1,

      rankValue: rankToValue(
        snapshot.tier,
        snapshot.rank,
        snapshot.leaguePoints
      ),

      dateLabel: new Date(snapshot.recordedAt).toLocaleString("en-US", {
        month: "short",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit"
      })
    }));

  return (
    <div className="player-page">
      <header className="player-navbar">
        <div className="player-navbar-content">
          <a href="/" className="brand-link">
            <div className="logo">⌁</div>
            <span>RIFTLINE</span>
          </a>

          <nav>
            <a href="/">Players</a>
            <a href="/">Champions</a>
            <a href="/">Leaderboards</a>
          </nav>

          <div className="status">
            <span className="live-dot"></span>
            LIVE
          </div>
        </div>
      </header>

      <main className="player-main">
        <section className="player-header">
          <div className="player-avatar">
            <span>?</span>
          </div>

          <div className="player-identity">
            <span className="region-badge">{region}</span>

            <h1>
              {gameName}
              <span>#{tagLine}</span>
            </h1>

            <p>Ranked Solo / Duo</p>
          </div>

          <button 
            className="refresh-button"
            onClick={refreshPlayer}
            disabled={refreshing}
          >
            Last refresh:  {
              player?.rankSnapshotResponse?.recordedAt
              ? formatTimeAgo(player.rankSnapshotResponse.recordedAt)
              : "..."
            }
            <br />
            {refreshing ? "Refreshing..." : "↻ Refresh Now?"}
          </button>
        </section>

        <section className="rank-grid">
          <div className="card rank-card">
            <div className="card-label">CURRENT RANK</div>

            <div className="rank-content">
              <div className="rank-emblem">
                {player && (
                  <img
                    src={getRankEmblem(player.rankSnapshotResponse.tier)}
                    alt={`${player.rankSnapshotResponse.tier} rank`}
                  />
                )}
              </div>

              <div>
                <h2>{player ? player.rankSnapshotResponse.tier : '...'} {player && player.rankSnapshotResponse.rank}</h2>
                <p>{player ? player.rankSnapshotResponse.leaguePoints : "..."} LP</p>
              </div>
            </div>
          </div>

          <div className="card stat-card">
            <div className="card-label">WIN RATE</div>
            <strong>{
              player ?
                (100 * player.rankSnapshotResponse.wins / (player.rankSnapshotResponse.wins + player.rankSnapshotResponse.losses)).toFixed(2)
                : "..."}%</strong>
            <span>{player && player.rankSnapshotResponse.wins}W · {player && player.rankSnapshotResponse.losses}L</span>
          </div>

          <div className="card stat-card">
            <div className="card-label">TOTAL GAMES</div>
            <strong>{player ? player.rankSnapshotResponse.wins + player.rankSnapshotResponse.losses : "..."}</strong>
            <span>---</span>
          </div>

          <div className="card stat-card">
            <div className="card-label">LEAGUE POINTS</div>
            <strong>{player ? player.rankSnapshotResponse.leaguePoints : "..."}</strong>
            <span>Current LP</span>
          </div>
        </section>

        <section className="content-grid">
          <div className="card match-history-card">
            <div className="section-header">
              <div>
                <span className="card-label">MATCH HISTORY</span>
                <h3>Recent matches</h3>
              </div>
            </div>

            <div className="match-list">
              {matches.map((match) => {
                const participant = match.participant;

                const items = [
                  participant.item0,
                  participant.item1,
                  participant.item2,
                  participant.item3,
                  participant.item4,
                  participant.item5,
                ];

                const trinketItem = participant.item6;
                const roleItem = null; // this is temporary

                const cs =
                  participant.totalMinionsKilled +
                  participant.neutralMinionsKilled;

                const kda =
                  participant.deaths === 0
                    ? "Perfect"
                    : (
                        (participant.kills + participant.assists) /
                        participant.deaths
                      ).toFixed(2);

                return (
                  <div
                    key={match.matchId}
                    className={`match-row ${participant.win ? "win" : "loss"}`}
                  >
                    <div className="match-result">
                      <strong>{participant.win ? "WIN" : "LOSS"}</strong>
                      <span>{formatGameDate(match.info.gameStartTimestamp)}</span>
                    </div>

                    <div className="champion-placeholder">
                      <img
                        src={getChampionIcon(participant.championName)}
                        alt={participant.championName}
                      />
                    </div>

                    <div className="match-info">
                      <strong>{participant.championName}</strong>
                      <span>
                        {getGameMode(match.info.queueId)}
                        {match.info.queueId !== 1750 && ` · ${formatRole(participant.teamPosition)}`}
                      </span>
                    </div>

                    <div className="match-items-grid">

                      {/* Trinket */}
                      <div className="item-slot special-slot">
                        {trinketItem !== 0 && (
                          <img
                            src={getItemIcon(trinketItem)}
                            alt={`Trinket ${trinketItem}`}
                          />
                        )}
                      </div>

                      {/* Top 3 items */}
                      {items.slice(0, 3).map((itemId, index) => (
                        <div key={index} className="item-slot">
                          {itemId !== 0 && (
                            <img
                              src={getItemIcon(itemId)}
                              alt={`Item ${itemId}`}
                            />
                          )}
                        </div>
                      ))}

                      {/* Role — icon later */}
                      <div className="item-slot special-slot">
                        {roleItem}
                      </div>

                      {/* Bottom 3 items */}
                      {items.slice(3, 6).map((itemId, index) => (
                        <div key={index + 3} className="item-slot">
                          {itemId !== 0 && (
                            <img
                              src={getItemIcon(itemId)}
                              alt={`Item ${itemId}`}
                            />
                          )}
                        </div>
                      ))}

                    </div>

                    <div className="kda">
                      <strong>
                        {participant.kills} / {participant.deaths} /{" "}
                        {participant.assists}
                      </strong>
                      <span>{kda} KDA</span>
                    </div>

                    <div className="match-stats">
                      <span>{cs} CS</span>
                    </div>
                  </div>
                );
              })}
            </div>

            {hasMore ? 
              <button
                className="small-button"
                onClick={loadMoreMatches}
                disabled={loadingMore}
              >
                {loadingMore ? "Loading..." : "Load more"}
              </button> : null
            }
          </div>

          <div className="side-column">
            <div className="card summary-card">
              <div className="section-header">
                <div>
                  <span className="card-label">PLAYER SUMMARY</span>
                  <h3>Current season</h3>
                </div>
              </div>

              <div className="summary-row">
                <span>Tier</span>
                <strong>
                  {player ? player.rankSnapshotResponse.tier : "..."}
                </strong>
              </div>

              <div className="summary-row">
                <span>Division</span>
                <strong>
                  {player ? player.rankSnapshotResponse.rank : "..."}
                </strong>
              </div>

              <div className="summary-row">
                <span>League Points</span>
                <strong>
                  {player ? `${player.rankSnapshotResponse.leaguePoints} LP` : "..."}
                </strong>
              </div>

              <div className="summary-row">
                <span>Wins</span>
                <strong>
                  {player ? player.rankSnapshotResponse.wins : "..."}
                </strong>
              </div>

              <div className="summary-row">
                <span>Losses</span>
                <strong>
                  {player ? player.rankSnapshotResponse.losses : "..."}
                </strong>
              </div>
            </div>

            <div className="card history-card">
              <div className="section-header">
                <div>
                  <span className="card-label">RANK HISTORY</span>
                  <h3>Recent progression</h3>
                </div>

                <button className="small-button">View all</button>
              </div>

              <div className="rank-history-chart">
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={graphData}>

                    <XAxis
                      dataKey="game"
                    />

                    <YAxis
                      domain={[
                        dataMin => Math.floor((dataMin - 100) / 100) * 100,
                        dataMax => Math.ceil((dataMax + 100) / 100) * 100
                      ]}
                      tickFormatter={valueToRankLabel}
                    />

                    <Tooltip
                      labelFormatter={(game, payload) => {
                        if (!payload?.length) return `Game ${game}`;

                        return `Game ${game} · ${payload[0].payload.dateLabel}`;
                      }}

                      formatter={(value, name, props) => {
                        const snapshot = props.payload;

                        return [
                          `${snapshot.tier} ${snapshot.rank} - ${snapshot.leaguePoints} LP`,
                          "Rank"
                        ];
                      }}
                    />

                    <Line
                      type="monotone"
                      dataKey="rankValue"
                      strokeWidth={2}
                    />

                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}