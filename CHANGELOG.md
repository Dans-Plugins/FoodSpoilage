# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `master` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get foodspoilage --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

## [4.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- FoodSpoilage is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `4.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.
