# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- The plugin now reports usage events — `startup` on enable, `command` on each use of `/foodspoilage` — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml`. The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is never rewritten: the plugin reads the bundled defaults for any key the file lacks. These three keys are read once at startup rather than on `/fs reload`.
- The plugin says on the console at every start whether usage reporting is on and how to turn it off. A server-wide switch, `plugins/trace/config.yml`, is created on first start and honoured by every plugin that reports to trace; the environment variables `TRACE_USAGE_REPORTING=off` and `DO_NOT_TRACK=1` turn it off for the whole process. On a server whose `config.yml` predates the `usage-reporting` block, the block is now written with the bundled values so the opt-out is visible in the file.
- `spoiled-food-material` config option setting which item food turns into once it spoils, defaulting to `ROTTEN_FLESH` so that existing servers are unaffected. A name that matches no material, or that names a block which cannot be held as an item, falls back to the default and logs a warning. Whichever material is configured is itself excluded from spoilage and from waxing, in place of the previously hard-coded rotten flesh exclusion.
- Waxing feature: food combined with a wax material (`HONEYCOMB` by default) in a crafting grid becomes non-perishable but inedible, controlled by the `enable-waxing`, `wax-material` and `text.waxed-food-lore` config keys.
- `timestamp-furnace-output` config option to re-enable stamping of furnace output items on Minecraft versions where it does not stall the furnace.
- `USER_GUIDE.md` covering prerequisites, first steps, common scenarios and permissions.
- `COMMANDS.md`, `CONFIG.md` and `CONTRIBUTING.md` documentation, plus a restructured `README.md`.
- `Build` and `Release` GitHub Actions workflows, and `.github/copilot-instructions.md`.
- A JUnit 5 and Mockito test source set, covering how `CraftItemListener` delivers craft results, what `WaxingCraftListener` produces while the waxing feature is on and off, and how `LocalTimeStampService` picks up a changed `expiry-date-format`.

### Fixed

- `/fs reload` reported success without applying `debug`, `expiry-date-format` or `wax-material`, all of which were read once while the plugin was starting up. All three are now applied by the command.
- Setting `enable-waxing` to `false` and reloading left the waxing recipe registered, so its placeholder result — an item named "Waxed Food (varies)" carrying no waxed marker — became craftable. The recipe is now unregistered and registered again on reload, which also makes switching the feature back on take effect without a restart, and a disabled feature clears the crafting result rather than leaving the placeholder in place.
- A craft that was only partially spoiled and taken with an ordinary click handed the player just one of the two resulting stacks, silently destroying the other. The unspoiled remainder is now left in the result slot and the spoiled portion is added to the player's inventory, or dropped at their feet when there is no room.
- `DateTimeParseException` crash when a `spoil-time` value was `0` or otherwise not a valid ISO-8601 duration; such values now fall back to no spoilage.
- `/fs timeleft` incorrectly reporting that an item will never spoil when `text.expiry-date-lore` was configured as empty.
- JUnit 4 and Hamcrest classes, carried in from the `ponder` fat jar, were being shipped unrelocated inside the plugin jar and placed on the shared server classpath; they are now excluded along with `ponder`'s own bundled test class, which also reduces the jar from roughly 581 KB to 155 KB.
- `CONFIG.md` described `spoil-chance` as the probability that an item spoils when its timer expires. It is in fact rolled once per unit at craft time, replacing the affected units with `Spoiled Food`; the section has been rewritten to match.
- `CONFIG.md` presented all 43 default `spoil-time` entries as active. Eleven of them name materials that Bukkit does not report as edible and are therefore never acted on; they are now marked as such.
- `.github/copilot-instructions.md` listed the source layout without the `src/test/java` source set or the `trace/` package, so it read as though the project had no automated tests. Both are now listed, and the coding conventions state that behaviour changes are expected to come with JUnit coverage; the README's build section likewise notes that the build runs the tests.
