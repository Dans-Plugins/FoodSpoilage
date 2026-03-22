# Food Spoilage

## Description
A minecraft plugin to make food go bad.

## Installation
1) You can download the plugin from [this page](https://www.spigotmc.org/resources/food-spoilage.81507/).
2) Once downloaded, place the jar in the plugins folder of your server files.
3) Restart your server.

## Usage

### Documentation
- [Commands Reference](COMMANDS.md) - Complete list of all commands
- [Configuration Guide](CONFIG.md) - Detailed config options

### Wiki & Additional Resources
- [User Guide](https://github.com/Dans-Plugins/FoodSpoilage/wiki/Guide)
- [FAQ](https://github.com/Dans-Plugins/FoodSpoilage/wiki/FAQ)

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/Dans-Plugins/FoodSpoilage/issues/new/choose).

- [Known Bugs](https://github.com/Dans-Plugins/FoodSpoilage/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Contributing
- [Contributing.md](CONTRIBUTING.md)
- [Notes for Developers](https://github.com/Dans-Plugins/FoodSpoilage/wiki/Developer-Notes)

## Testing
To build the plugin, you can use the following command:

Linux:
```bash
./gradlew clean build
```
Windows:
```cmd
.\gradlew.bat clean build
```

If you see BUILD SUCCESSFUL, then the build has passed.

## Development
### Test Server with Plugin Hot-Reloading
For development purposes, a Docker-based test server is available with integrated plugin hot-reloading capabilities using ServerUtils (a modern Plugman alternative).

#### Setup
1. Copy `sample.env` to `.env` and configure as needed
2. Build the plugin: `./gradlew build`
3. Start the test server: `./up.sh`

#### Plugin Hot-Reloading
After making changes to the plugin code, you can quickly reload it without restarting the server:

**Option 1: Using the reload script (recommended)**
```bash
./reload-plugin.sh
```

**Option 2: Manual reload**
1. Build the plugin: `./gradlew build`
2. Copy the new jar to the running container
3. Use ServerUtils commands in-game or console:
   - `/serverutils reload FoodSpoilage` - Reload the plugin
   - `/serverutils unload FoodSpoilage` - Unload the plugin
   - `/serverutils load FoodSpoilage` - Load the plugin
   - `/serverutils list` - List all plugins

This significantly speeds up the development cycle by eliminating the need for full server restarts during testing.

#### Stopping the Test Server
```bash
./down.sh
```

## Authors and acknowledgement
| Name              | Main Contributions                                                       |
|-------------------|--------------------------------------------------------------------------|
| Daniel Stephenson | Creator                                                                  |
| Undead_Zeratul    | Overhauled many parts of the plugin                                      |
| Caibinus          | Fixed some bugs                                                          |
| Callum            | Fixed some bugs and implemented a caching system for food spoilage times |
| alyphen           | Migrated the project to gradle, refactored services                      |

## License

This project is licensed under the [Apache License 2.0](LICENSE).

You are free to use, modify, and distribute this software, provided that:
- You include a copy of the license and any notices in your distribution.
- You state any changes made to the original code.
- You include an explicit grant of patent rights from contributors.

See the [LICENSE](LICENSE) file for the full text of the Apache License 2.0.

## Project Status
This project is in active development.

### bStats
You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/Food%20Spoilage/8992).
