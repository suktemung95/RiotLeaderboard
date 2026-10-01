import { useParams } from "react-router-dom";
import { useEffect } from 'react'
import "./PlayerPage.css";

const URL_BASE = "http://localhost:8080/"

export default function PlayerPage() {
  const { region, gameName, tagLine } = useParams();

  useEffect(() => {
    async function fetchData() {
        const params = new URLSearchParams({ region, gameName, tagLine})
        const url = URL_BASE + `players/overview?${params}` 

        const response = await fetch(url)
        const result = await response.json()
        console.log(result)
    }

    fetchData()
  }, [region, gameName, tagLine])

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
              <div className="match-row win">
                <div className="match-result">
                  <strong>WIN</strong>
                  <span>28m ago</span>
                </div>

                <div className="champion-placeholder">A</div>

                <div className="match-info">
                  <strong>Ahri</strong>
                  <span>Ranked Solo</span>
                </div>

                <div className="kda">
                  <strong>8 / 2 / 11</strong>
                  <span>9.50 KDA</span>
                </div>

                <div className="match-stats">
                  <span>214 CS</span>
                  <span>+18 LP</span>
                </div>
              </div>

              <div className="match-row loss">
                <div className="match-result">
                  <strong>LOSS</strong>
                  <span>1h ago</span>
                </div>

                <div className="champion-placeholder">V</div>

                <div className="match-info">
                  <strong>Viktor</strong>
                  <span>Ranked Solo</span>
                </div>

                <div className="kda">
                  <strong>4 / 7 / 6</strong>
                  <span>1.43 KDA</span>
                </div>

                <div className="match-stats">
                  <span>198 CS</span>
                  <span>-22 LP</span>
                </div>
              </div>

              <div className="match-row win">
                <div className="match-result">
                  <strong>WIN</strong>
                  <span>2h ago</span>
                </div>

                <div className="champion-placeholder">S</div>

                <div className="match-info">
                  <strong>Syndra</strong>
                  <span>Ranked Solo</span>
                </div>

                <div className="kda">
                  <strong>11 / 4 / 8</strong>
                  <span>4.75 KDA</span>
                </div>

                <div className="match-stats">
                  <span>236 CS</span>
                  <span>+20 LP</span>
                </div>
              </div>
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