# MapsAroundYou

Offline smart rental search scaffold for the MapsAroundYou team project.

## Demo Video

Watch the MapsAroundYou demo video on YouTube:

- [MapsAroundYou Demo Video](https://youtu.be/_u43spy3ggE)

## Quick Start

Prerequisite: Java 21 or newer (x86_64/AMD64) on `PATH` or in `JAVA_HOME`.

Run the JavaFX GUI entrypoint:

```powershell
.\gradlew runGui
```

On Windows ARM64, `\.\gradlew runGui` auto-switches to an installed x64 Java runtime when available.

Run the default Gradle entrypoint (GUI):

```powershell
.\gradlew run
```

This launches the packaged JavaFX app through the same launcher used by the runnable JAR.

Build the runnable fat JAR:

```powershell
.\gradlew shadowJar
```

Run the CLI entrypoint from the built fat JAR:

```powershell
java -cp .\build\libs\MapsAroundYou-0.5.1-all.jar mapsaroundyou.cli.MapsAroundYouApp search --destination D01 --max-rent 2200 --max-commute 45 --max-transfers 1 --max-walk 10 --result-limit 5 --sort balanced --require-aircon --exclude-walk-dominant
```

Run the local quality gate:

```powershell
.\gradlew clean check
```

## Documentation

- **User Guide (end users)**: [`docs/UserGuide.md`](docs/UserGuide.md)
- **Developer Guide (contributors)**: [`docs/developer-guide.md`](docs/developer-guide.md)

## Current Scope

- The project currently supports both JavaFX GUI and CLI entrypoints.
- GUI startup is available through the `runGui` and default `run` Gradle tasks.
- The codebase is organized into `gui`, `cli`, `app`, `logic`, `service`, `storage`, and `model` layers, with `ApplicationFactory` as the sole composition root.
- Runtime data stays local under `src/main/resources/commute_data/`.
- Last-used search preferences are stored locally under the current user's home directory.
- Persona preset and dark-mode settings are persisted for returning users.

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

The offline commute matrix generator uses a teammate-local OneMap token via `ONEMAP_TOKEN`. See [docs/ops/build-and-run.md](docs/ops/build-and-run.md) for token setup and regeneration steps.
