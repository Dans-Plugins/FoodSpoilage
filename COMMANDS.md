# Commands Reference

## Base Command
The base command for Food Spoilage is `/foodspoilage` (alias: `/fs`).

Running `/foodspoilage` without any arguments will display plugin information, including the version and a link to the wiki. A subcommand that is not listed below is answered with `text.command-not-found`.

## Command List

| Command | Description | Permission |
|---------|-------------|------------|
| `/fs` | Display plugin information | `fs.default` |
| `/fs help` | View a list of available commands | `fs.help` |
| `/fs timeleft` | View how much time is left before the item in your hand expires | `fs.timeleft` |
| `/fs reload` | Reload the plugin's configuration files | `fs.reload` |

## `/fs timeleft` Replies

The command reports on the item in the player's main hand. It can only be used by a player; from the console it prints the command's usage line.

| Default reply | When it is sent | Config key |
|-------|-----------------|------------|
| This item will expire in *N* days. | At least a day is left, counted in whole days | `text.time-left` |
| This item will expire in less than a day. | Between one hour and one day is left | `text.less-than-a-day` |
| This item will expire in less than an hour. | Less than an hour is left | `text.less-than-an-hour` |
| This item has expired. | The expiry has passed | `text.no-time-left` |
| This item will never spoil. | The item has no expiry date | `text.never-spoil` |

The last reply is also what food gets before it has been stamped, as with an item received through `/give`. See [When food gets an expiry date](CONFIG.md#when-food-gets-an-expiry-date).

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `fs.default` | Allows use of the base `/fs` command | Everyone |
| `fs.help` | Allows use of `/fs help` | Everyone |
| `fs.timeleft` | Allows use of `/fs timeleft` | Everyone |
| `fs.reload` | Allows use of `/fs reload` | OP only |
