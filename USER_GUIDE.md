# User Guide

## Prerequisites

- A Spigot or Paper Minecraft server (API version 1.13 or later).
- The FoodSpoilage plugin jar placed in the `plugins/` folder and the server restarted.
- *(Optional)* rpk-food-lib-bukkit – provides extended food definitions via RPKit integration.

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

### Preserving a food item so it never spoils

1. Confirm that `enable-waxing` is set to `true` in the configuration (it is by default).
2. Place the food item and a honeycomb (or whichever material `wax-material` is set to) together in any crafting grid.
3. Take the result. The waxed item will never spoil, but it can no longer be eaten.

See [CONFIG.md](CONFIG.md#waxing) for the full description of this feature.

### Reloading the configuration

After editing `plugins/FoodSpoilage/config.yml`:

```
/fs reload
```

Every configuration key is applied by this command; no setting requires the server to be restarted. See [Applying Changes](CONFIG.md#applying-changes) for the one caveat, which concerns the waxing recipe and players who are already online.

## Permissions

| Permission    | Description                              | Default  |
|---------------|------------------------------------------|----------|
| `fs.default`  | Allows use of the base `/fs` command     | Everyone |
| `fs.help`     | Allows use of `/fs help`                 | Everyone |
| `fs.timeleft` | Allows use of `/fs timeleft`             | Everyone |
| `fs.reload`   | Allows use of `/fs reload`               | OP only  |
