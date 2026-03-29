# Data Generation Guide

This guide explains how to add new rental listings to MapsAroundYou using the data generation workflow.

## Workflow Overview

The application maintains a two-layer CSV data model:

1. **`Rental_List.csv`** - Origin identity and commute reference
   - Source file that defines covered rental origin locations
   - Used as reference data for listings and transit matrix alignment
   - Treat each row as one covered origin node, not one app listing row
   - Columns: `Flat_ID`, `Postal_Code`, `Region`, `Area_Name`

2. **`listings.csv`** - App-facing listing data
   - Auto-generated for each origin by the unified script
   - Multiple listing rows may share the same `originNodeId`
   - Columns: `listingId`, `title`, `monthlyRent`, `hasAircon`, `originNodeId`, `address`, `roomType`, `sourcePlatform`, `notes`

3. **`transit_matrix.csv`** - Commute lookup matrix
   - Auto-generated offline from postal-code distance heuristics (no API)
   - Aligned with `Rental_List.csv` via `flat_id` foreign key
   - Columns: `flat_id`, `destination_id`, `pt_total`, `pt_walk`, `pt_bus`, `pt_rail`, `pt_transit`, `pt_fare`, `drive_total`, `cycle_total`, `walk_total`

The generator appends rows to all three files in one run.

## Adding New Listings

### Prerequisites

- Python 3 on `PATH`
- No API token is required for transit generation
- See [Build and Run Guide](../ops/build-and-run.md) for the broader local setup

### Step 1: Choose Random or Manual Origins

Run from repository root:

```bash
python scripts/generate_merged_listings.py --location-mode random --new-origin-count 5 --listings-per-origin 3
```

or choose manual input mode:

```bash
python scripts/generate_merged_listings.py --location-mode manual --listings-per-origin 3
```

In manual mode, the script prompts for region, area, street name, postal code, and housing hint (HDB/Condo).

### Step 2: Generate App-Facing Listings and Transit Rows

One unified run will:
- Append new origins to `Rental_List.csv`
- Append new listing rows to `listings.csv`
- Append new commute rows to `transit_matrix.csv` for each destination in `Dst_List.csv`

### Step 3: Verify Flat ID Alignment

After generation, verify the new `Flat_ID` values appear consistently in all three files.

**Important:** `Flat_ID` in `Rental_List.csv`, `originNodeId` in `listings.csv`, and `flat_id` in `transit_matrix.csv` must stay aligned.

## Data Format Details

### CSV Field Mappings

| Rental_List | → | listings.csv |
|---|---|---|
| `Flat_ID` | → | `originNodeId` |
| (generated) | → | `listingId` (L001, L002, ...) |
| (generated) | → | `title` (descriptive, auto-generated) |
| (generated) | → | `monthlyRent` (random, profile-based) |
| (generated) | → | `hasAircon` (`true` or `false`) |
| (generated) | → | `roomType` (for example, "Condo room", "HDB room") |
| (generated) | → | `sourcePlatform` (for example, "PropertyGuru", "99.co") |
| (generated) | → | `notes` (generated listing note) |
| (generated from street + unit) | → | `address` |

### hasAircon Field

- **Type:** Boolean CSV value (`true` / `false`)
- **NOT a nested JSON field**
- **Direct column in `listings.csv`** for filtering

### One Origin, Multiple Units

- `Rental_List.csv` tracks commute origins, not one row per final listing unit
- The generator can emit multiple listings for each `originNodeId`
- Transit rows are generated per origin-destination pair, not per listing row

## Transit Data Logic (No API)

- Distance is computed from postal-code-derived coordinates using the Haversine formula
- Route distance is estimated from straight-line distance with a multiplier
- Walk/cycle/drive/PT durations are generated from speed heuristics and overheads
- Very short distances are constrained to avoid unrealistic long transit legs
- PT fare is estimated from distance brackets

## Optional Arguments

```bash
# Use random Singapore-like locations
python scripts/generate_merged_listings.py --location-mode random --new-origin-count 8 --listings-per-origin 4 --seed 2103

# Prompt for specific user-provided locations
python scripts/generate_merged_listings.py --location-mode manual --listings-per-origin 3 --seed 2103
```

## Backward Compatibility

`scripts/generate_travel_data.py` remains available as a wrapper and forwards to `scripts/generate_merged_listings.py`.

## Troubleshooting

### ID Issues
If IDs look wrong:
- Ensure existing values in CSVs follow `R###` and `L###` style formats
- The script increments from the highest existing `Flat_ID` and `listingId`

### Manual Input Validation
If manual mode rejects a postal code:
- Ensure `Postal_Code` is exactly 6 digits

### Transit Plausibility
If commute times appear suspicious:
- Re-run with a fixed `--seed` for reproducible comparison
- Check that postal codes are realistic for the intended region

### CSV Encoding Issues
The script reads UTF-8 BOM safely (`utf-8-sig`) and writes UTF-8.

## Key Design Principles

- **Unified append flow** - One command updates all 3 runtime CSVs
- **No transit API dependency** - Commute generation is offline
- **Repo-portable paths** - Uses repository-relative file locations
- **Flat_ID alignment** - IDs stay consistent across rental, listings, and transit files

## See Also

- [API Schema Documentation](../api/mock-api.md) - Full schema details
- [Build and Run Guide](../ops/build-and-run.md) - How to run the CLI application
