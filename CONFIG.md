# Configuration Guide

The configuration file for Food Spoilage is located at `plugins/FoodSpoilage/config.yml`. Changes to the configuration file can be applied in-game using `/fs reload`.

## Applying Changes

Every key listed on this page takes effect on `/fs reload`; no key requires the server to be restarted.

`enable-waxing` and `wax-material` are applied by unregistering the waxing recipe and registering it again from the new values. What a crafting grid produces changes immediately, but Minecraft sends the recipe list to a client when it connects, so a player who is already online may keep seeing a stale entry in their recipe book until they reconnect. Should a server implementation refuse to unregister the recipe, a warning naming these two keys is written to the console and a restart is needed for them; nothing else about the reload is affected.

Configuration file migrations, driven by the `version` key, are the one exception: they run only while the plugin is starting up.

## General Options

| Key | Description | Default |
|-----|-------------|---------|
| `version` | The version of the configuration file | `3.0.0` |
| `debug` | Enable debug logging | `false` |
| `expiry-date-format` | The date format used for expiry dates displayed in item lore | `MM/dd/yyyy` |
| `enable-waxing` | Enable the waxing feature, allowing players to craft food with a wax material to make it non-perishable but inedible | `true` |
| `wax-material` | The [Bukkit Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) name used as the waxing ingredient | `HONEYCOMB` |
| `timestamp-furnace-output` | Stamp items in the furnace output slot with an expiry date immediately when cooked. This works correctly on older versions of Minecraft, but on 1.20.5+ it causes furnaces to stall after cooking one item, since Minecraft will not continue cooking while custom data is present on the output slot. When left at the default, items are instead stamped lazily the next time they reach a player (inventory close/open, pickup, item spawn, or player join). | `false` |

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
| `text.waxed-food-lore` | The lore lines added to waxed food items. | `['', '&fWaxed', '&7This item will not spoil but cannot be eaten.']` |

## Spoil Times

Spoil times are defined under the `spoil-time` key using [ISO 8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations) format (e.g., `PT24H` for 24 hours, `PT48H` for 48 hours). A value of `0` means the item never spoils. Any value that is missing or cannot be parsed as an ISO-8601 duration (and is not `0`) also falls back to no spoilage rather than causing an error.

A `default` value is used for any food item not explicitly listed.

| Key | Description | Default |
|-----|-------------|---------|
| `spoil-time.default` | Default spoil time for unlisted food items | `PT24H` (24 hours) |

### Only edible materials can spoil

A `spoil-time` entry takes effect only for materials that Bukkit reports as edible — that is, materials a player can actually eat. Rotten flesh is excluded as well, and waxed items are skipped. An entry for any other material is accepted by the configuration parser but never acted on, because every code path that stamps an item with an expiry date checks edibility first.

Eleven of the entries shipped in the default configuration name materials that are **not** edible, and therefore have no effect: `WHEAT`, `HAY_BLOCK`, `MELON`, `PUMPKIN`, `BROWN_MUSHROOM`, `RED_MUSHROOM`, `NETHER_WART`, `CAKE`, `SUGAR`, `EGG` and `SUGAR_CANE`. They are listed in the table below for completeness, and are marked accordingly. Whether they should be removed from the default configuration or the edibility restriction relaxed is tracked in [#257](https://github.com/Dans-Plugins/FoodSpoilage/issues/257).

The one exception is the optional RPKit integration: when `rpk-food-lib-bukkit` is installed, expiry dates requested through RPKit are applied without the edibility check.

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
| `WHEAT` | `PT48H` | 48 hours (no effect — not edible) |
| `HAY_BLOCK` | `PT48H` | 48 hours (no effect — not edible) |
| `MELON` | `PT48H` | 48 hours (no effect — not edible) |
| `PUMPKIN` | `PT48H` | 48 hours (no effect — not edible) |
| `BROWN_MUSHROOM` | `PT48H` | 48 hours (no effect — not edible) |
| `RED_MUSHROOM` | `PT48H` | 48 hours (no effect — not edible) |
| `NETHER_WART` | `PT168H` | 168 hours (7 days) (no effect — not edible) |
| `MELON_SLICE` | `PT24H` | 24 hours |
| `CAKE` | `PT24H` | 24 hours (no effect — not edible) |
| `PUMPKIN_PIE` | `PT24H` | 24 hours |
| `SUGAR` | `PT72H` | 72 hours (no effect — not edible) |
| `EGG` | `PT72H` | 72 hours (no effect — not edible) |
| `SUGAR_CANE` | `PT48H` | 48 hours (no effect — not edible) |
| `APPLE` | `PT48H` | 48 hours |
| `COOKIE` | `PT94H` | 94 hours |
| `POISONOUS_POTATO` | `PT24H` | 24 hours |
| `CHORUS_FRUIT` | `PT94H` | 94 hours |
| `DRIED_KELP` | `PT72H` | 72 hours |
| `BAKED_POTATO` | `PT94H` | 94 hours |
| `SWEET_BERRIES` | `PT48H` | 48 hours |

### Adding Custom Food Items
You can add spoil times for any edible Minecraft material by adding entries under `spoil-time`. Use the [Bukkit Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) name as the key. Entries for materials that are not edible are accepted but have no effect — see [Only edible materials can spoil](#only-edible-materials-can-spoil).

Example:
```yaml
spoil-time:
  GOLDEN_APPLE: PT168H
```

## Spoil Chance

The `spoil-chance` section defines the probability (0.0 to 1.0) that each unit of a food item is **already spoiled at the moment it is crafted**. It is not a second chance applied when an item's expiry timer runs out — an item whose timer expires always spoils.

When a player crafts a food item that has a non-zero spoil time, the roll is made once per unit produced. Units that fail the roll are replaced with `Spoiled Food` (named and described by `text.spoiled-food-name` and `text.spoiled-food-lore`); the remainder are stamped with an expiry date as usual. Crafting is the only path that consults `spoil-chance`; food obtained by cooking, fishing, mob drops, or picking items up is never randomly spoiled.

If a material has no `spoil-chance` entry, its chance is 0 and none of its crafted output is spoiled. There is no `spoil-chance.default` key.

Because the roll happens on the crafting path, it is subject to the same edibility restriction described above.

| Key | Description | Default |
|-----|-------------|---------|
| `spoil-chance.WHEAT` | Chance that each crafted wheat is spoiled. `WHEAT` is not edible, so this shipped entry currently has no effect (see [#257](https://github.com/Dans-Plugins/FoodSpoilage/issues/257)). | `0.3` (30%) |

## Waxing

The waxing feature allows players to preserve food items by combining them with a wax material (default: honeycomb) in a crafting grid. Waxed food will never spoil but cannot be eaten, making it ideal for preserving sentimental "lore items".

To wax a food item, place it alongside a honeycomb (or the configured `wax-material`) in any crafting grid. Rotten flesh cannot be waxed, and neither can an item that has already been waxed. The result will be a waxed version of the food item that:
- Will never receive an expiry timestamp
- Cannot be consumed (eating is prevented)
- Displays the configured `text.waxed-food-lore` on the item

If the food item has not yet been stamped with an expiry date, any existing custom lore (e.g., from lore items) is preserved alongside the waxed lore.

The feature can be disabled by setting `enable-waxing: false` in the config. This setting and `wax-material` are both applied by `/fs reload`, which unregisters the waxing recipe and registers it again from the current configuration; see [Applying Changes](#applying-changes) for the one caveat.
