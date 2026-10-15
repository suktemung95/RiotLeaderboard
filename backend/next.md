# current
- clean up unnecessary functions in PlayerController + Service
- divide refresh function into multiple helpers

# next
- caching players
- make sure all data calls are retrieved from database first, then API
  - on this note, make sure the database or even cache returns, then somehow make an API refresh
- if a call is taken from database, do an async refresh from the API directly afterward
- figure out a way to fix N + 1 in getPlayerMatches and getPlayerMatchesDetails
- possibly remove (or create new + smaller) DTO for Participants that doesn't include user information (unnecessary)

# Done
- refreshPlayer + Matches
- find other information to add to PlayerMatchDataResponse DTO (teammates, gameDuration, queueId (or name), role(s), items, gold (differential))
- add "teamPosition" to participants DTO to replace role
- standardize the return from riotApi calls
- reformat API calls to riot into wrapper for simplicity
- finish matches by making DTO and taking in all raw data and more
- get match(es history)
- get matches by id
- change PlayerMatch to hold stats that can be used for when a player is searched. KDA, win / lose, champion played, etc... for player searching