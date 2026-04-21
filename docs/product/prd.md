# Product Requirements Document (PRD)

**Product:** MapsAroundYou — Smart Rental Search
**Version:** 1.0
**Last Updated:** April 21, 2026
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
- Surface only listings within the user's rent budget, commute cap, transfer cap, and walking cap.
- Let users choose shortlist size and sort mode while preserving deterministic ordering.
- Provide a breakdown of transit time, walking time, and transfers for each shortlisted listing (V1.4).
- Restore last-used search preferences and settings for returning users.
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
| **v0.1** | Project setup, CI baseline, and initial runnable artifacts | Week 8 |
| **v0.2** | First increment — destination + rent filter with GUI baseline | Week 9 |
| **v0.3** | Earlier release — richer filters, route breakdown, settings, and persistence | Week 10 |
| **v0.4** | Previous release — professor-feedback hardening, GUI application boundary cleanup, and clearer results UX | Week 11 |
| **v0.5** | Previous release — cross-platform run flows, stronger onboarding docs, architecture diagrams, and targeted GUI polish | Week 12 |
| **v0.5.1** | Current release — GUI-guide screenshot refresh plus test-coverage hardening for service and storage layers | Week 12 follow-up |

---

## 6. Features and User Stories

### Implemented Before v0.3 (v0.1-v0.2)

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

### Included In v0.3

#### US3 — Set Commute Time Cap
As a renter, I want to filter listings by a maximum travel time limit so that I can manage my daily travel.

**Acceptance Criteria:**
- User can input a maximum travel time in minutes via the GUI.
- System looks up commute time using the bundled local travel-time matrix and excludes listings exceeding the cap.
- User can also input maximum walking time and maximum transfers.

#### US4 — Require Air-Conditioning
As a renter, I want to require air-conditioning so that unsuitable listings are removed.

**Acceptance Criteria:**
- User can toggle an "Air-Con Required" checkbox.
- When enabled, listings without the air-con attribute are excluded from the final list.

#### US5 — Generate GUI Output
As a renter, I want to see a clean output of the best matching listings in a display panel so that I can review my options.

**Acceptance Criteria:**
- System outputs the top N listings (default N=10) that pass all filters to the right display panel.
- Each listing row displays rent, aircon status, and commute summary, including total commute, walking time, and transfers.
- User can choose whether results are sorted by commute, rent, or a balanced score.

#### US6 — Anti-Walk-Dominant Route Filter
As a renter, I want the system to reject routes that are primarily walking so that I receive realistic public transport suggestions.

**Acceptance Criteria:**
- System applies a configured walk-dominant threshold (default 0.6 of total commute time).
- Routes where the walking ratio (`walkMinutes / totalMinutes`) is greater than or equal to the configured threshold are rejected.

#### US7 — Commute Summary Breakdown
As a renter, I want to see the commute details split by transit and walking so that I understand the journey better.

**Acceptance Criteria:**
- The output for each shortlisted item specifies "Transit Time", "Walking Time", and "Transfers".

#### US8 — Set Persona Preset
As a renter, I want to select a preset (Student vs. Worker) so that default time caps and budgets are automatically applied.

**Acceptance Criteria:**
- Selecting `Student` sets `Max rent (SGD)` to `1400`, `Max commute (minutes)` to `50`, and turns `Require aircon` off.
- Selecting `Worker` sets `Max rent (SGD)` to `2000`, `Max commute (minutes)` to `65`, and turns `Require aircon` off.
- User can manually override these default values in the left input panel before searching.
- On first app startup, the persona preset starts as `New User`, and the app prompts the user to choose between `Student` and `Worker`.
- After that, users can change the persona preset via the `Settings` window, and the updated defaults remain editable.
- The `Settings` window also includes a `Dark mode` toggle.

### Cross-Platform Onboarding And Design Docs In v0.5

- Added and documented cross-platform `runCli` entrypoints so GUI and CLI startup flows are clearer for Windows, macOS, and Linux users.
- Expanded the User Guide with first-time task flows, troubleshooting, glossary coverage, and clearer explanations of no-limit transfer behavior.
- Added a full Developer Guide plus refreshed UML diagrams so contributors can navigate the shipped architecture more confidently.
- Applied targeted GUI polish such as better dark-theme prompt contrast, centered wide-window layout behavior, and clearer "No limit" wording for max transfers.

---

## 7. Functional Requirements

### 7.1 Destination and Preference Input

| ID | Requirement |
|----|-------------|
| FR-01 | User can select a supported destination from a predefined list via a dropdown or text field. |
| FR-02 | User can set a maximum monthly rent (SGD integer). |
| FR-03 | User can set a maximum commute time in minutes. |
| FR-04 | User can set a maximum transfer count and maximum walking time in minutes. |
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
| FR-13 | System excludes listings where computed `transfers > maxTransfers` or `walkMinutes > maxWalkMinutes`. |
| FR-14 | System can reject listings where the walking ratio (`walkMinutes / totalMinutes`) is greater than or equal to the configured walk-dominant threshold when the user enables the filter. |

### 7.3 Ranking and Results Display

| ID | Requirement |
|----|-------------|
| FR-15 | Shortlisted listings are ranked deterministically according to the selected sort mode, with stable tie-breakers. |
| FR-16 | Results panel displays the top N listings (default N=10) with rent, aircon status, total commute, walking time, and transfers per row. |
| FR-17 | User can click a listing to view full details including a split commute breakdown for total time, transit, walk, transfers, and fare (V1.4). |

### 7.4 Persistence and Settings

| ID | Requirement |
|----|-------------|
| FR-18 | System provides transit time, walking time, number of transfers, and fare for each listing. |
| FR-19 | The application persists the last successful search preferences locally and restores them on startup when possible. |
| FR-20 | The application stores and restores the persona preset and dark-mode choice for returning users. |

### 7.5 Data Freshness Notice

| ID | Requirement |
|----|-------------|
| FR-21 | The UI displays a notice such as "Data accurate as of \<last-updated date\>" based on dataset metadata. |

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
| **UserPreferences** | `destinationId`, `maxRent`, `maxCommuteMinutes`, `maxTransfers`, `maxWalkMinutes`, `requireAircon`, `transportMode`, `resultLimit`, `sortMode`, `excludeWalkDominantRoutes` |
| **CommuteEstimate** | `totalMinutes`, `transitMinutes`, `walkMinutes`, `transfers`, `routeStations` |
| **SearchResult** | `listing`, `commute`, `score` |

All data is loaded from local files. The current release defaults to public transport and includes walking/transfers in the shipped commute output.

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
