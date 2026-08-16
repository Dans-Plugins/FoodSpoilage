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
- A JUnit 5 and Mockito test source set, covering how `CraftItemListener` delivers craft results.

### Fixed

- A craft that was only partially spoiled and taken with an ordinary click handed the player just one of the two resulting stacks, silently destroying the other. The unspoiled remainder is now left in the result slot and the spoiled portion is added to the player's inventory, or dropped at their feet when there is no room.
- `DateTimeParseException` crash when a `spoil-time` value was `0` or otherwise not a valid ISO-8601 duration; such values now fall back to no spoilage.
- `/fs timeleft` incorrectly reporting that an item will never spoil when `text.expiry-date-lore` was configured as empty.
- JUnit 4 and Hamcrest classes, carried in from the `ponder` fat jar, were being shipped unrelocated inside the plugin jar and placed on the shared server classpath; they are now excluded along with `ponder`'s own bundled test class, which also reduces the jar from roughly 581 KB to 155 KB.
- `CONFIG.md` described `spoil-chance` as the probability that an item spoils when its timer expires. It is in fact rolled once per unit at craft time, replacing the affected units with `Spoiled Food`; the section has been rewritten to match.
- `CONFIG.md` claimed that all configuration changes can be applied with `/fs reload`. `debug`, `expiry-date-format` and `wax-material` are read only during startup, and the restriction is now documented.
- `CONFIG.md` presented all 43 default `spoil-time` entries as active. Eleven of them name materials that Bukkit does not report as edible and are therefore never acted on; they are now marked as such.
