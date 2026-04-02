# Product Requirements Document (PRD)

**Product:** MapsAroundYou — Smart Rental Search
**Version:** 1.0
**Last Updated:** March 6, 2026
**Status:** Active

---

## 1. Overview

MapsAroundYou is a desktop GUI application that helps newcomers to Singapore — primarily international students and new working professionals — find rental listings that fit their budget and commute constraints. Users specify a primary destination (e.g., a campus, office, hospital, or other landmark such as NUS, NTU, SMU, NUH, or Orchard), set filters such as maximum rent and maximum commute time, and receive a ranked shortlist of rental listings that meet their criteria.

The application runs entirely offline using local datasets for supported destinations, curated rental listings, and travel-time records.

---

## 2. Problem Statement

International students and new working professionals arriving in Singapore face difficulty identifying rental options that are both affordable and close enough to their primary daily destination. Generic property portals surface listings by location or price but do not account for commute time via public transport — the primary mode of travel for this demographic. This forces users to manually cross-reference listings against map apps, which is slow and error-prone.

---

## 3. Goals

- Help users filter rental listings by commute time from a chosen supported destination.
- Surface only listings within the user's rent budget, commute cap, and walking cap.
- Let users choose shortlist size and sort mode while preserving deterministic ordering.
- Provide a breakdown of transit vs. walking time for each shortlisted listing (V1.4).
- Deliver the product as a runnable offline JAR with a GUI.

### Non-Goals

- Live routing APIs (e.g., Google Maps, OneMap).
- Real-time rental scraping or live listing updates.
- Address geocoding or map rendering.
- User accounts, authentication, or cloud sync.
- Multi-modal routing beyond MRT + walking (V1 scope).

---

## 4. Target Users

### Primary Persona

**International student or newcomer working professional in Singapore.**

- Commutes mainly between home and a fixed primary destination (university campus, office, hospital, or common place).
- Goal is to minimize daily travel time, not to secure a specific postal code.
- Budget-conscious; typically looking for HDB rooms or small condo units.
- Unfamiliar with Singapore's neighbourhoods and transit network.

---

## 5. Product Roadmap

| Release | Milestone | Target |
|---------|-----------|--------|
| **V1.2** | First Feature Increment — destination + rent filter | Week 9 |
| **V1.3** | MVP — commute time cap + shortlist output | Week 10 |
| **V1.4** | Alpha — anti-walk filter + commute breakdown | Week 11 |

---

## 6. Features and User Stories

### V1.2 — First Feature Increment

#### US1 — Set Primary Destination
As a renter, I want to set a primary destination (e.g., NUS, NTU, SMU, NUH, Orchard) so that listings can be evaluated by commute distance.

**Acceptance Criteria:**
- User can enter a destination via a dropdown or text field in the left input panel.
- System validates the input against a predefined static destination list and corresponding travel-time data.

#### US2 — Filter by Monthly Rent
As a renter, I want to set a maximum rent limit so that I only see affordable options.

**Acceptance Criteria:**
- System parses a bundled local static dataset of curated housing listings (`listings.json` or `.csv`).
- Listings with a rent value higher than the user's limit are not processed.

---

### V1.3 — MVP

#### US3 — Set Commute Time Cap
As a renter, I want to filter listings by a maximum travel time limit so that I can manage my daily travel.

**Acceptance Criteria:**
- User can input a maximum travel time in minutes via the GUI.
- System looks up basic commute time using the bundled local travel-time matrix and excludes listings exceeding the cap.
- User can also input a maximum walking time in minutes.

#### US4 — Require Air-Conditioning
As a renter, I want to require air-conditioning so that unsuitable listings are removed.

**Acceptance Criteria:**
- User can toggle an "Air-Con Required" checkbox.
- When enabled, listings without the air-con attribute are excluded from the final list.

#### US5 — Generate GUI Output
As a renter, I want to see a clean output of the best matching listings in a display panel so that I can review my options.

**Acceptance Criteria:**
- System outputs the top N listings (default N=10) that pass all filters to the right display panel.
- Each listing card displays the rent, address, and commute summary.
- User can choose whether results are sorted by commute, rent, or a balanced score.

---

### V1.4 — Alpha

#### US6 — Anti-Walk-Dominant Route Filter
As a renter, I want the system to reject routes that are primarily walking so that I receive realistic public transport suggestions.

**Acceptance Criteria:**
- System applies a configured walk-dominant threshold (default 0.6 of total commute time).
- Routes where the walking ratio (`walkMinutes / totalMinutes`) is greater than or equal to the configured threshold are rejected.

#### US7 — Commute Summary Breakdown
As a renter, I want to see the commute details split by transit and walking so that I understand the journey better.

**Acceptance Criteria:**
- The output for each shortlisted item specifies "Transit Time" and "Walking Time".

#### US8 — Set Persona Preset *(Stretch)*
As a renter, I want to select a preset (Student vs. Worker) so that default time caps and budgets are automatically applied.

**Acceptance Criteria:**
- Selecting 'Student' sets max rent and default commute caps automatically based on typical student budgets.
- User can manually override these default values in the left input panel.

---

## 7. Functional Requirements

### 7.1 Destination and Preference Input

| ID | Requirement |
|----|-------------|
| FR-01 | User can select a supported destination from a predefined list via a dropdown or text field. |
| FR-02 | User can set a maximum monthly rent (SGD integer). |
| FR-03 | User can set a maximum commute time in minutes. |
| FR-04 | User can set a maximum walking time in minutes. |
| FR-05 | User can toggle an "Air-Con Required" filter. |
| FR-06 | User can choose a result limit and sort mode for the shortlist. |
| FR-07 | System validates all inputs before executing a search. Invalid destination IDs return a user-friendly error. |

### 7.2 Search and Filtering

| ID | Requirement |
|----|-------------|
| FR-08 | System loads curated housing listings from a local static dataset on search. |
| FR-09 | System excludes listings with `monthlyRent > maxRent`. |
| FR-10 | System excludes listings without air-con when the aircon filter is enabled. |
| FR-11 | System computes commute time from each listing's `originNodeId` to the selected destination using the local travel-time dataset. |
| FR-12 | System excludes listings where computed `totalMinutes > maxCommuteMinutes`. |
| FR-13 | System excludes listings where computed `walkMinutes > maxWalkMinutes`. |

### 7.3 Ranking and Results Display

| ID | Requirement |
|----|-------------|
| FR-14 | Shortlisted listings are ranked deterministically according to the selected sort mode, with stable tie-breakers. |
| FR-15 | Results panel displays the top N listings (default N=10) with rent, address, and commute summary per card. |
| FR-16 | User can click a listing to view full details including commute breakdown (V1.4). |

### 7.4 Commute Breakdown (V1.4)

| ID | Requirement |
|----|-------------|
| FR-17 | System provides transit time, walking time, number of transfers, and route stations for each listing. |
| FR-18 | System can reject listings where the walking ratio (`walkMinutes / totalMinutes`) is greater than or equal to the configured walk-dominant threshold when the user enables the filter. |

### 7.5 Data Freshness Notice

| ID | Requirement |
|----|-------------|
| FR-19 | The UI displays a notice such as "Data accurate as of \<last-updated date\>" based on dataset metadata. |
| FR-20 | The application persists the last successful search preferences locally and restores them on startup when possible. |

---

## 8. Non-Functional Requirements

| ID | Requirement |
|----|-------------|
| NFR-01 | Application runs fully offline; no network calls are made at runtime. |
| NFR-02 | Application is delivered as a runnable JAR. |
| NFR-03 | GUI is required for all core user flows; no CLI-only mode. |
| NFR-04 | Search results must be returned in a time acceptable for interactive use (target: < 2 seconds for typical dataset sizes). |
| NFR-05 | Data files (JSON/CSV) are schema-validated on load; load errors are surfaced to the user with a clear message. |
| NFR-06 | Domain logic must reside in the Logic/Services layers; no business logic in the UI layer. |
| NFR-07 | Ranking must be deterministic — identical inputs always produce the same result ordering. |

---

## 9. Data Model Summary

| Entity | Key Fields |
|--------|------------|
| **Destination** | `destinationId`, `name`, `category`, `area` |
| **TravelTimeRecord** | `originNodeId`, `destinationId`, `totalMinutes`, `transitMinutes`, `walkMinutes`, `transfers`, `source` |
| **TravelTimeMatrix** | keyed lookup: `Map<originNodeId, Map<destinationId, TravelTimeRecord>>` |
| **RentalListing** | `listingId`, `title`, `monthlyRent`, `hasAircon`, `originNodeId`, `address`, `roomType`, `sourcePlatform`, `destinationTags`, `notes` |
| **UserPreferences** | `destinationId`, `maxRent`, `maxCommuteMinutes`, `maxWalkMinutes`, `requireAircon`, `transportMode`, `resultLimit`, `sortMode`, `excludeWalkDominantRoutes` |
| **CommuteEstimate** | `totalMinutes`, `transitMinutes`, `walkMinutes`, `transfers`, `routeStations` |
| **SearchResult** | `listing`, `commute`, `score` |

All data is loaded from local files. The MVP transport mode defaults to public transport; walking is modelled minimally (V1.4).

---

## 10. Constraints and Assumptions

- Destination must come from the finite supported destination list.
- Each listing provides an `originNodeId` for commute lookup; no geocoding is performed.
- Commute times are approximations derived from precomputed local travel-time records, preferably sourced from LTA or equivalent public data.
- The application must be runnable as a JAR (Java desktop GUI).
- Data files must be bundled with the application or placed in a known local path.

---

## 11. Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Local dataset incomplete or inconsistent | Empty or incorrect results | Schema validation on load; curated demo dataset; clear load error messages |
| Travel-time lookup errors or stale records | Wrong commute times shown | Unit tests with deterministic fixtures; source attribution; dataset freshness metadata |
| GUI scope creep | Delivery delays | Minimal screen set: Search + Results + Details dialog |
| UI–Logic coupling | Integration pain | Strict interfaces + view models; no domain logic in UI |
| Performance with larger listing datasets | Slow search | Keep curated demo dataset small; index by `originNodeId` and destination |
| Ambiguous walk-dominant threshold | Feature disagreement | Define threshold (e.g., `walkMinutes / totalMinutes >= T`) in config; document in SDD |

---

## 12. Related Documents

- [User Stories](./user-stories.md)
- [Architecture Overview](../design/architecture.md)
- [Software Design Document](../design/sdd.md)
- [API Specification](../api/api-spec.md)
- [Mock API / Data Schemas](../api/mock-api.md)
