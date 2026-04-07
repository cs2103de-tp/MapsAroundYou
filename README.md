# MapsAroundYou

Offline smart rental search scaffold for the MapsAroundYou team project.

## Quick Start

Prerequisite: Java 21 or newer (x86_64/AMD64) on `PATH` or in `JAVA_HOME`.

Run the JavaFX GUI entrypoint:

```powershell
.\gradlew runGui
```

On Windows ARM64, `\.\gradlew runGui` auto-switches to an installed x64 Java runtime when available.

Run the CLI entrypoint:

```powershell
.\gradlew run
```

Interactive mode now stays open for repeated searches until the user types `exit`.

Run the flag-driven search mode:

```powershell
.\gradlew run --args="search --destination D01 --max-rent 2200 --max-commute 45 --require-aircon"
```

Run the local quality gate:

```powershell
.\gradlew clean check
```

## Current Scope

- The project currently supports both JavaFX GUI and CLI entrypoints.
- GUI startup is available through the `runGui` Gradle task.
- The codebase is organized into `storage`, `service`, `logic`, and `cli` layers so GUI work can plug in later.
- Runtime data stays local under `src/main/resources/commute_data/`.

## Contributing

All changes to `main` must go through a pull request. The required
`PR Quality Gate` check must pass before merge. That gate now aggregates
`PR Quality Check` on Ubuntu and the cross-OS `PR Build Gate (Linux)`,
`PR Build Gate (macOS)`, and `PR Build Gate (Windows)` runnable-JAR
builds on Temurin Java 21 `x64`. We recommend asking for at least
**1 approving review** before merging. While this is not enforced, it is
still best practice to request review.

### Workflow

1. **Fork** the repository to your GitHub account
2. **Clone** your fork locally
3. **Create** a feature branch for your work
4. **Push** your changes to your fork
5. **Create a PR** from your fork to `cs2103de-tp/MapsAroundYou`

See [docs/development/fork-workflow.md](docs/development/fork-workflow.md) for detailed setup instructions and [docs/development/git-commit-conventions.md](docs/development/git-commit-conventions.md) for commit guidelines.

## Offline Data Generation

Use the unified offline generator at `scripts/generate_merged_listings.py` to append data to `origin_nodes.csv`, `listings.csv`, and `transit_matrix.csv` in one run (no API token required, uses distance-based estimation instead of live routing APIs so commute times are approximate).

See [docs/development/DATA_GENERATION_GUIDE.md](docs/development/DATA_GENERATION_GUIDE.md) for random/manual location modes and usage examples.
