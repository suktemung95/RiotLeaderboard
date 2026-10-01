import { useParams } from "react-router-dom";

export default function PlayerPage() {

    const {region, gameName, tagLine} = useParams()
    console.log("Params: ", region, gameName, tagLine)
}