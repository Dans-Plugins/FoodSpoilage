# Configuration Guide

The configuration file for Food Spoilage is located at `plugins/FoodSpoilage/config.yml`. Changes to the configuration file can be applied in-game using `/fs reload`.

## General Options

| Key | Description | Default |
|-----|-------------|---------|
| `version` | The version of the configuration file | `3.0.0` |
| `debug` | Enable debug logging | `false` |
| `expiry-date-format` | The date format used for expiry dates displayed in item lore | `MM/dd/yyyy` |

## Text Customization

All text values support Minecraft color codes using the `&` prefix (e.g., `&f` for white).

| Key | Description | Default |
|-----|-------------|---------|
| `text.expiry-date-lore` | The lore lines added to items showing the expiry date. Use `${expiry_date}` as a placeholder. | `['', '&fExpiry Date:', '&f${expiry_date}']` |
| `text.values-loaded` | Message shown when the config is reloaded | `Values loaded!` |
| `text.no-permission-reload` | Message shown when a player lacks permission to reload | `Sorry! In order to use this command, you need the following permission: 'fs.reload'` |
| `text.spoiled-food-name` | Display name for spoiled food items | `Spoiled Food` |
| `text.spoiled-food-lore` | Lore text for spoiled food items | `This food has gone bad.` |
| `text.never-spoil` | Message shown when an item will never spoil | `This item will never spoil.` |
| `text.time-left` | Message template for time remaining. Use `${time}` as a placeholder. | `This item will expire in ${time}.` |
| `text.less-than-an-hour` | Message shown when an item expires in less than an hour | `This item will expire in less than an hour.` |
| `text.less-than-a-day` | Message shown when an item expires in less than a day | `This item will expire in less than a day.` |
| `text.no-time-left` | Message shown when an item has already expired | `This item has expired.` |

## Spoil Times

Spoil times are defined under the `spoil-time` key using [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations) format (e.g., `PT24H` for 24 hours, `PT48H` for 48 hours).

A `default` value is used for any food item not explicitly listed.

| Key | Description | Default |
|-----|-------------|---------|
| `spoil-time.default` | Default spoil time for unlisted food items | `PT24H` (24 hours) |

### Configured Food Items

| Item | Spoil Time | Duration |
|------|-----------|----------|
| `BREAD` | `PT24H` | 24 hours |
| `POTATO` | `PT48H` | 48 hours |
| `CARROT` | `PT48H` | 48 hours |
| `BEETROOT` | `PT48H` | 48 hours |
| `BEEF` | `PT24H` | 24 hours |
| `PORKCHOP` | `PT24H` | 24 hours |
| `CHICKEN` | `PT24H` | 24 hours |
| `COD` | `PT24H` | 24 hours |
| `SALMON` | `PT24H` | 24 hours |
| `MUTTON` | `PT24H` | 24 hours |
| `RABBIT` | `PT24H` | 24 hours |
| `TROPICAL_FISH` | `PT24H` | 24 hours |
| `PUFFERFISH` | `PT24H` | 24 hours |
| `MUSHROOM_STEW` | `PT72H` | 72 hours |
| `RABBIT_STEW` | `PT96H` | 96 hours |
| `BEETROOT_SOUP` | `PT72H` | 72 hours |
| `COOKED_BEEF` | `PT72H` | 72 hours |
| `COOKED_PORKCHOP` | `PT72H` | 72 hours |
| `COOKED_CHICKEN` | `PT72H` | 72 hours |
| `COOKED_SALMON` | `PT72H` | 72 hours |
| `COOKED_MUTTON` | `PT72H` | 72 hours |
| `COOKED_RABBIT` | `PT72H` | 72 hours |
| `COOKED_COD` | `PT72H` | 72 hours |
| `WHEAT` | `PT48H` | 48 hours |
| `HAY_BLOCK` | `PT48H` | 48 hours |
| `MELON` | `PT48H` | 48 hours |
| `PUMPKIN` | `PT48H` | 48 hours |
| `BROWN_MUSHROOM` | `PT48H` | 48 hours |
| `RED_MUSHROOM` | `PT48H` | 48 hours |
| `NETHER_WART` | `PT168H` | 168 hours (7 days) |
| `MELON_SLICE` | `PT24H` | 24 hours |
| `CAKE` | `PT24H` | 24 hours |
| `PUMPKIN_PIE` | `PT24H` | 24 hours |
| `SUGAR` | `PT72H` | 72 hours |
| `EGG` | `PT72H` | 72 hours |
| `SUGAR_CANE` | `PT48H` | 48 hours |
| `APPLE` | `PT48H` | 48 hours |
| `COOKIE` | `PT94H` | 94 hours |
| `POISONOUS_POTATO` | `PT24H` | 24 hours |
| `CHORUS_FRUIT` | `PT94H` | 94 hours |
| `DRIED_KELP` | `PT72H` | 72 hours |
| `BAKED_POTATO` | `PT94H` | 94 hours |
| `SWEET_BERRIES` | `PT48H` | 48 hours |

### Adding Custom Food Items
You can add spoil times for any Minecraft material by adding entries under `spoil-time`. Use the [Bukkit Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) name as the key.

Example:
```yaml
spoil-time:
  GOLDEN_APPLE: PT168H
```

## Spoil Chance

The `spoil-chance` section allows you to define a probability (0.0 to 1.0) that individual items of a given type will spoil when their timer expires. If not specified for a material, the spoil chance is 0 (no random spoilage).

| Key | Description | Default |
|-----|-------------|---------|
| `spoil-chance.WHEAT` | Chance that wheat will spoil | `0.3` (30%) |
