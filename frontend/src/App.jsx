import "./App.css";
import HomePage from "./pages/HomePage";
import PlayerPage from "./pages/PlayerPage";

import { Routes, Route } from "react-router-dom";

export default function App() {

  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/players/:region/:gameName/:tagLine" element={<PlayerPage />} />
    </Routes>
  )
  
}