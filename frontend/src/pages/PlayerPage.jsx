import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react'
import "./PlayerPage.css";

const URL_BASE = "http://localhost:8080/"

const DDRAGON_VERSION = "16.20.1";

export default function PlayerPage() {

  const [matches, setMatches] = useState([]);
  const [player, setPlayer] = useState(null);

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
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

    const days = Math.floor(diffHours / 24);
    const hours = diffHours % 24;

    if (days > 0) {
      return `${days}d ${hours}h ago`;
    }

    if (hours > 0) {
      return `${hours}h ago`;
    }

    return "Less than 1h ago";
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
  
  useEffect(() => {
    async function fetchData() {
      const params = new URLSearchParams({
        region,
        gameName,
        tagLine
      });

      const overviewUrl = URL_BASE + `players/overview?${params}`;
      const matchesUrl = URL_BASE + `players/matches?${params}`;

      const [overviewResponse, matchesResponse] = await Promise.all([
        fetch(overviewUrl),
        fetch(matchesUrl)
      ]);

      const overviewResult = await overviewResponse.json();
      const matchesResult = await matchesResponse.json();

      console.log(overviewResult);
      console.log(matchesResult);

      setPlayer(overviewResult.data)
      setMatches(matchesResult.data);
    }

    fetchData();
  }, [region, gameName, tagLine]);

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

          <button className="refresh-button">
            Last refresh: {formatTimeAgo(matches[0].recordedAt)}
            <br/>
            ↻ Refresh Now?
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

              <button className="small-button">View all</button>
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

              <div className="chart-placeholder">
                <span>Rank history graph</span>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}