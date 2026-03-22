# User Guide

## Prerequisites

- A Spigot or Paper Minecraft server (API version 1.13 or later).
- The FoodSpoilage plugin jar placed in the `plugins/` folder and the server restarted.
- *(Optional)* [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) – enables additional item-lore features.
- *(Optional)* rpk-food-lib-bukkit – provides extended food definitions.

## First Steps

1. Start your server with the plugin installed. FoodSpoilage will generate a default `plugins/FoodSpoilage/config.yml`.
2. Review the configuration file and adjust spoil times and other settings to suit your server. See [CONFIG.md](CONFIG.md) for a full reference.
3. Reload the configuration without restarting: `/fs reload`
4. Give the food item you want to test to yourself and check its remaining time with `/fs timeleft`.

## Common Scenarios

### Checking when a food item will expire

1. Hold a food item in your main hand.
2. Run `/fs timeleft`.
3. The plugin will display the remaining time before the item spoils.

### Reloading the configuration

After editing `plugins/FoodSpoilage/config.yml`:

```
/fs reload
```

### Preventing a player from being affected by spoilage

Grant the `fs.bypass.spoilage` permission to the player. This prevents spoiled food from being converted to rotten flesh for that player.

### Preventing a player's items from receiving timestamps

Grant the `fs.bypass.timestamp` permission to the player. Items crafted or obtained by that player will not receive a spoilage timestamp.

### Using the waxing feature

If `enable-waxing: true` is set in the configuration, players can wax food items by combining them with the configured wax material in a crafting table. Waxed food will not spoil but also cannot be eaten.

### Using the salting feature

If `enable-salting: true` is set in the configuration, players can salt food items by combining them with the configured salt material in a crafting table. Salted food has its spoil timer extended.

## Permissions

| Permission           | Description                                              | Default |
|----------------------|----------------------------------------------------------|---------|
| `fs.default`         | Allows use of the base `/fs` command                     | Everyone |
| `fs.help`            | Allows use of `/fs help`                                 | Everyone |
| `fs.timeleft`        | Allows use of `/fs timeleft`                             | Everyone |
| `fs.reload`          | Allows use of `/fs reload`                               | OP only  |
| `fs.bypass`          | Parent permission granting both bypass permissions below | OP only  |
| `fs.bypass.spoilage` | Prevents spoiled food from being converted for this player | OP only |
| `fs.bypass.timestamp`| Prevents timestamp assignment on items for this player  | OP only  |
