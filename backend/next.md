# current

# next
- caching players
- make sure all data calls are retrieved from database first, then API
- figure out a way to fix N + 1 in getPlayerMatches and getPlayerMatchesDetails
- possibly remove (or create new + smaller) DTO for Participants that doesn't include user information (unnecessary)
- find other information to add to PlayerMatchDataResponse DTO (teammates, gameDuration, queueId (or name), role(s), items, gold (differential)) 

# Done
- standardize the return from riotApi calls
- reformat API calls to riot into wrapper for simplicity
- finish matches by making DTO and taking in all raw data and more
- get match(es history)
- get matches by id
- change PlayerMatch to hold stats that can be used for when a player is searched. KDA, win / lose, champion played, etc... for player searching