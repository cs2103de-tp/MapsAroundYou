# API Specification

**Smart Rental Search Algorithm — Logic Layer**

This document specifies the operations exposed by the Logic layer for the UI. All operations are synchronous and operate on local data.

---

## Logic Operations

### Destination & Preferences

| Operation | Parameters | Description |
|-----------|------------|-------------|
| `setDestination(stationId)` | `stationId: String` | Sets the primary destination MRT station in `UserPreferences` |
| `setPreferences(maxRent, maxCommuteMinutes, requireAircon, transportMode)` | `maxRent: int`, `maxCommuteMinutes: int`, `requireAircon: boolean`, `transportMode: TransportMode` | Updates search constraints; transport mode defaults to MRT for MVP |

### Search

| Operation | Returns | Description |
|-----------|---------|-------------|
| `generateShortlist()` | `List<SearchResult>` | Executes full pipeline: load listings → filter by rent/aircon → estimate commute → filter by max commute → rank and sort. Returns ranked results. |

### Details

| Operation | Parameters | Returns | Description |
|-----------|------------|---------|-------------|
| `getListingDetails(listingId)` | `listingId: String` | `ListingDetails` | Returns full listing info for a given listing |
| `getCommuteDetails(listingId)` | `listingId: String` | `CommuteEstimate` | Returns commute estimate for the listing (see RouteAnalyzer for summaries) |

---

## Service Layer (Internal)

These are used by Logic; not directly called by UI.

### ListingFilter

| Operation | Parameters | Returns | Description |
|-----------|------------|---------|-------------|
| `filterByRent(listings, maxRent)` | `listings: List<RentalListing>`, `maxRent: int` | `List<RentalListing>` | Filters listings by monthly rent ≤ maxRent |
| `filterByAircon(listings, requireAircon)` | `listings: List<RentalListing>`, `requireAircon: boolean` | `List<RentalListing>` | Filters by aircon requirement |

### CommuteEstimator

| Operation | Parameters | Returns | Description |
|-----------|------------|---------|-------------|
| `estimate(fromStationId, toStationId, mode)` | `fromStationId: String`, `toStationId: String`, `mode: TransportMode` | `CommuteEstimate` | Computes shortest path (Dijkstra) on local transit graph |

### ListingRanker

- Deterministic sorting: lowest commute, then lowest rent, then `listingId` tie-breaker
- Optional score: `w1 * normalizedCommute + w2 * normalizedRent`

### RouteAnalyzer (V1.4)

| Operation | Parameters | Returns | Description |
|-----------|------------|---------|-------------|
| `isWalkDominant(commuteEstimate)` | `commuteEstimate: CommuteEstimate` | `boolean` | True if walking dominates (per config threshold) |
| `summarize(commuteEstimate)` | `commuteEstimate: CommuteEstimate` | `CommuteSummary` | Returns transit vs walking breakdown, transfers, total time |

---

## Data Types

| Type | Key Fields |
|------|------------|
| `SearchResult` | `listing`, `commute`, `score` |
| `ListingDetails` | Full `RentalListing` + optional commute breakdown |
| `CommuteEstimate` | `totalMinutes`, `transitMinutes`, `walkMinutes`, `transfers`, `routeStations` |
| `CommuteSummary` | Human-readable breakdown for UI |

---

## Error Handling

- Logic centralizes error handling with user-friendly messages
- Invalid inputs (e.g., unknown stationId) should return clear feedback
- Load errors (missing/invalid data files) should be surfaced to UI

---

## Related Documents

- [Software Design Document](../design/sdd.md)
- [Architecture Overview](../design/architecture.md)
- [Mock API / Data Schemas](./mock-api.md)
