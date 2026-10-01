import { useState } from "react";
import { useNavigate } from "react-router-dom";

const URL_BASE = "http://localhost:8080/"

export default function HomePage() {
    const [region, setRegion] = useState("NA");
    const [player, setPlayer] = useState("");

    const navigate = useNavigate()

    async function handleSubmit(event) {
        event.preventDefault();

        console.log({
        region,
        player,
        });

        const [gameName, tagLine] = player.split("#")

        if (!gameName || !tagLine) {
        // display an error
        return
        }

        const params = new URLSearchParams({
        region,
        gameName,
        tagLine
        })

        const url = URL_BASE + `players/search?${params}` 
        const response = await fetch(url)
        const result = await response.json()
        console.log(result)

        if (result.success) {
            navigate(`/players/${region}/${gameName}/${tagLine}`)
        }
    }

    return (
        <div className="app">
        <header className="navbar">
            <div className="navbar-content">
            <div className="brand">
                <div className="logo">⌁</div>
                <span>RIFTLINE</span>
            </div>

            <nav>
                <a href="#">Players</a>
                <a href="#">Champions</a>
                <a href="#">Leaderboards</a>
            </nav>

            <div className="status">
                <span className="live-dot"></span>
                LIVE
                <span className="divider">•</span>
                PATCH 26.19
            </div>
            </div>
        </header>

        <main className="hero">
            <div className="hero-content">
            <div className="eyebrow">
                <span></span>
                LIVE RANKED INTELLIGENCE
            </div>

            <h1>
                Know the lobby before the game
                <br />
                starts.
            </h1>

            <p className="subtitle">
                Search any League player for live rank, champion form, matchup
                history, and performance trends across every major region.
            </p>

            <form className="search-box" onSubmit={handleSubmit}>
                <select
                value={region}
                onChange={(event) => setRegion(event.target.value)}
                className="region-select"
                >
                <option value="NA">NA</option>
                <option value="EUW">EUW</option>
                <option value="EUNE">EUNE</option>
                <option value="KR">KR</option>
                <option value="JP">JP</option>
                </select>

                <div className="search-input-wrapper">
                <span className="search-icon">⌕</span>

                <div className="input-content">
                    <label>PLAYER NAME + TAG</label>

                    <input
                    value={player}
                    onChange={(event) => setPlayer(event.target.value)}
                    placeholder="SummonerName #NA1"
                    />
                </div>
                </div>

                <button type="submit" className="search-button">
                <span>→</span>
                Search player
                </button>
            </form>

            <div className="examples">
                <span>Try:</span>
                <button>Doublelift #NA1</button>
                <button>Faker #KR1</button>
                <button>Caps #EUW</button>
            </div>

            <div className="stats">
                <div>
                <strong>42M+</strong>
                <span>profiles indexed</span>
                </div>

                <div>
                <strong>15</strong>
                <span>ranked regions</span>
                </div>

                <div>
                <strong>90s</strong>
                <span>data refresh</span>
                </div>
            </div>
            </div>
        </main>

        <footer className="bottom-strip"></footer>
        </div>
    );
}