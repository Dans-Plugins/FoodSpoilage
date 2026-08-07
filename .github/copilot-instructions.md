# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

## Technology Stack

- Language: Java
- Build tool: Gradle (Groovy DSL)
- Target platform: Spigot / Paper

## Project Structure

- `src/main/java/spoilagesystem/` – Plugin source code
  - `commands/` – Command executors (e.g. `HelpCommand`, `ReloadCommand`, `TimeleftCommand`)
  - `config/` – Configuration service (`LocalConfigService`)
  - `listeners/` – Bukkit event listeners (crafting, inventory, player interactions)
  - `timestamp/` – Timestamp assignment and lookup service
  - `FoodSpoilage.java` – Main plugin class, registers commands and listeners
- `src/main/resources/` – `plugin.yml` and `config.yml`

## Coding Conventions

- Prefer sourcing new or updated user-facing strings from `config.yml` via `LocalConfigService`; existing hard-coded messages may be refactored over time.
- Spoilage and timestamp logic is gated on `Material#isEdible()` — non-edible materials are ignored.
- Spoil durations are stored as ISO-8601 `java.time.Duration` strings (e.g. `PT24H`) in `config.yml`.
- Follow the existing package structure when adding new classes.

## Contribution Workflow

- Branch from `develop` for all changes.
- Open a pull request against `develop`, not `main`.
- Reference the related GitHub issue in every pull request description.
