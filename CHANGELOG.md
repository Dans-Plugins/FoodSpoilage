# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- Waxing feature: food combined with a wax material (`HONEYCOMB` by default) in a crafting grid becomes non-perishable but inedible, controlled by the `enable-waxing`, `wax-material` and `text.waxed-food-lore` config keys.
- `timestamp-furnace-output` config option to re-enable stamping of furnace output items on Minecraft versions where it does not stall the furnace.
- `USER_GUIDE.md` covering prerequisites, first steps, common scenarios and permissions.
- `COMMANDS.md`, `CONFIG.md` and `CONTRIBUTING.md` documentation, plus a restructured `README.md`.
- `Build` and `Release` GitHub Actions workflows, and `.github/copilot-instructions.md`.

### Fixed

- `DateTimeParseException` crash when a `spoil-time` value was `0` or otherwise not a valid ISO-8601 duration; such values now fall back to no spoilage.
- `/fs timeleft` incorrectly reporting that an item will never spoil when `text.expiry-date-lore` was configured as empty.
- JUnit 4 and Hamcrest classes, carried in from the `ponder` fat jar, were being shipped unrelocated inside the plugin jar and placed on the shared server classpath; they are now excluded along with `ponder`'s own bundled test class, which also reduces the jar from roughly 581 KB to 155 KB.
