import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react'
import "./PlayerPage.css";

const URL_BASE = "http://localhost:8080/"

export default function PlayerPage() {

  const [matches, setMatches] = useState([]);

  const { region, gameName, tagLine } = useParams();

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

  useEffect(() => {
    async function fetchData() {
      const params = new URLSearchParams({
        region,
        gameName,
        tagLine
      });

      const overviewUrl = URL_BASE + `players/overview?${params}`;
      const matchesUrl = URL_BASE + `players/matches/data?${params}`;

      const [overviewResponse, matchesResponse] = await Promise.all([
        fetch(overviewUrl),
        fetch(matchesUrl)
      ]);

      const overviewResult = await overviewResponse.json();
      const matchesResult = await matchesResponse.json();

      console.log(overviewResult);
      console.log(matchesResult);

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
            ↻ Refresh
          </button>
        </section>

        <section className="rank-grid">
          <div className="card rank-card">
            <div className="card-label">CURRENT RANK</div>

            <div className="rank-content">
              <div className="rank-emblem">◆</div>

              <div>
                <h2>Emerald II</h2>
                <p>54 LP</p>
              </div>
            </div>
          </div>

          <div className="card stat-card">
            <div className="card-label">WIN RATE</div>
            <strong>54.2%</strong>
            <span>84W · 71L</span>
          </div>

          <div className="card stat-card">
            <div className="card-label">TOTAL GAMES</div>
            <strong>155</strong>
            <span>Ranked Solo</span>
          </div>

          <div className="card stat-card">
            <div className="card-label">LEAGUE POINTS</div>
            <strong>54</strong>
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
                const roleItem = participant.role;

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
                      <span>{match.info.gameStartTimestamp}</span>
                    </div>

                    <div className="champion-placeholder">
                      {participant.championName[0]}
                    </div>

                    <div className="match-info">
                      <strong>{participant.championName}</strong>
                      <span>
                        {getGameMode(match.info.queueId)} · {participant.role}
                      </span>
                    </div>

                    <div className="match-items-grid">
                      <div className="item-slot special-slot">
                        {trinketItem}
                      </div>

                      <div className="item-slot">
                        {items[0] !== 0 ? items[0] : "-"}
                      </div>

                      <div className="item-slot">
                        {items[1] !== 0 ? items[1] : "-"}
                      </div>

                      <div className="item-slot">
                        {items[2] !== 0 ? items[2] : "-"}
                      </div>

                      <div className="item-slot special-slot">
                        {roleItem}
                      </div>

                      <div className="item-slot">
                        {items[3] !== 0 ? items[3] : "-"}
                      </div>

                      <div className="item-slot">
                        {items[4] !== 0 ? items[4] : "-"}
                      </div>

                      <div className="item-slot">
                        {items[5] !== 0 ? items[5] : "-"}
                      </div>
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
                <strong>Emerald</strong>
              </div>

              <div className="summary-row">
                <span>Division</span>
                <strong>II</strong>
              </div>

              <div className="summary-row">
                <span>League Points</span>
                <strong>54 LP</strong>
              </div>

              <div className="summary-row">
                <span>Wins</span>
                <strong>84</strong>
              </div>

              <div className="summary-row">
                <span>Losses</span>
                <strong>71</strong>
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