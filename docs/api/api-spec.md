# API Specification

**Smart Rental Search Algorithm — Logic Layer**

This document specifies the operations exposed by the Logic layer for the UI. All operations are synchronous and operate on local data.

---

## Logic Operations

### Destination & Preferences

| Operation | Parameters | Description |
|-----------|------------|-------------|
| `updatePreferences(preferences)` | `preferences: UserPreferences` | Replaces the active search preferences, including destination, filters, result limit, sort mode, and walk-dominant toggle |

### Search

| Operation | Returns | Description |
|-----------|---------|-------------|
| `generateShortlist()` | `List<SearchResult>` | Executes full pipeline: load listings → filter by rent/aircon → estimate commute → reject over-max-commute, over-max-walk, and optionally walk-dominant routes → rank, sort, and truncate. Returns ranked results. |

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
| `estimate(originNodeId, destinationId, mode)` | `originNodeId: String`, `destinationId: String`, `mode: TransportMode` | `CommuteEstimate` | Looks up or derives commute time from the local travel-time matrix |

### ListingRanker

- `COMMUTE`: lowest commute, then lowest rent, then `listingId`
- `RENT`: lowest rent, then lowest commute, then `listingId`
- `BALANCED`: highest score, then lowest commute, then lowest rent, then `listingId`
- Score remains available for display: `1 - (0.6 * normalizedCommute + 0.4 * normalizedRent)`

### RouteAnalyzer (V1.4)

| Operation | Parameters | Returns | Description |
|-----------|------------|---------|-------------|
| `isWalkDominant(commuteEstimate)` | `commuteEstimate: CommuteEstimate` | `boolean` | True if `walkMinutes / totalMinutes` is greater than or equal to the configured walk-dominant threshold |
| `summarize(commuteEstimate)` | `commuteEstimate: CommuteEstimate` | `CommuteSummary` | Returns transit vs walking breakdown, transfers, total time |

---

## Data Types

| Type | Key Fields |
|------|------------|
| `SearchResult` | `listing`, `commute`, `score` |
| `ListingDetails` | Full `RentalListing` + optional commute breakdown |
| `CommuteEstimate` | `totalMinutes`, `transitMinutes`, `walkMinutes`, `transfers`, `routeStations` |
| `CommuteSummary` | Human-readable breakdown for UI |
| `UserPreferences` | `destinationId`, `maxRent`, `maxCommuteMinutes`, `maxWalkMinutes`, `requireAircon`, `transportMode`, `resultLimit`, `sortMode`, `excludeWalkDominantRoutes` |

---

## Error Handling

Logic centralizes all error handling. All exceptions are caught and converted to user-friendly messages before reaching the UI. The UI never receives raw exceptions.

### Exception Types

| Exception | Thrown By | Condition |
|-----------|-----------|----------|
| `InvalidInputException` | Logic | Input fails validation (null, out-of-range, or empty string) |
| `DestinationNotFoundException` | Logic, CommuteEstimator | `destinationId` not found in loaded destination dataset |
| `ListingNotFoundException` | Logic | `listingId` not found in listings dataset |
| `DataLoadException` | Storage | Data file missing, unreadable, or fails schema validation |
| `NoResultsException` | Logic | All listings filtered out; shortlist is empty |

### Invalid Input Rules

| Operation | Validation Rule |
|-----------|----------------|
| `updatePreferences(preferences)` | `destinationId` must be blank or present in the destination dataset; `maxRent` ≥ 0; `maxCommuteMinutes` ≥ 1; `maxWalkMinutes` ≥ 0; `transportMode` non-null; `resultLimit` ≥ 1; `sortMode` non-null |
| `generateShortlist()` | Destination must be set; preferences must pass all rules above |
| `getListingDetails(listingId)` | `listingId` must be non-null and present in the listings dataset |
| `getCommuteDetails(listingId)` | `listingId` must be non-null; destination must be set |
| `estimate(originNodeId, destinationId, mode)` | `originNodeId` must exist in the travel-time dataset; `destinationId` must exist in the destination dataset; `mode` non-null |

### UI Feedback Convention

| Exception | User-facing Message |
|-----------|-------------------|
| `InvalidInputException` | Inline validation message next to the offending input field |
| `DestinationNotFoundException` | "Unknown destination. Please select a supported place from the list." |
| `ListingNotFoundException` | "Listing not found. It may have been removed from the dataset." |
| `DataLoadException` | "Failed to load data. Please check the application files and restart." |
| `NoResultsException` | "No listings match your filters. Try relaxing your rent, commute, or walking limits." |

---

## Related Documents

- [Software Design Document](../design/sdd.md)
- [Architecture Overview](../design/architecture.md)
- [Mock API / Data Schemas](./mock-api.md)
