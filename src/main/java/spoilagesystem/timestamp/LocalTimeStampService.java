package spoilagesystem.timestamp;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Daniel McCoy Stephenson
 */
public final class LocalTimeStampService {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;

    private final DateTimeFormatter dateFormatter;

    public LocalTimeStampService(FoodSpoilage plugin, LocalConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;

        dateFormatter = DateTimeFormatter.ofPattern(plugin.getConfig().getString("expiry-date-format", "MM/DD/yyyy"));
    }

    public ItemStack assignTimeStamp(ItemStack item, Duration timeUntilSpoilage) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(
                    configService.getExpiryDateText().stream()
                            .map(line -> line.replace(
                                    "${expiry_date}",
                                    getDateStringPlusTime(timeUntilSpoilage)
                            )).toList()
            );
            item.setItemMeta(meta);
        }

        return item;
    }

    private String getDateStringPlusTime(Duration time) {
        return dateFormatter.format(ZonedDateTime.now().plus(time));
    }

    public boolean timeStampAssigned(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();

                if (lore != null) {
                    plugin.getLogger().fine("Timestamp is already assigned to this item!");
                    List<String> expiryDateText = configService.getExpiryDateText();
                    if (lore.size() != expiryDateText.size()) return false;
                    for (int i = 0; i < expiryDateText.size(); i++) {
                        String expectedLoreLine = expiryDateText.get(i);
                        if (expectedLoreLine.contains("${expiry_date}")) {
                            int startIndex = expectedLoreLine.indexOf("${expiry_date}");
                            int endIndex = startIndex + "${expiry_date}".length();
                            String dateText = lore.get(i)
                                    .replace(expectedLoreLine.substring(0, startIndex), "")
                                    .replace(expectedLoreLine.substring(endIndex), "");
                            try {
                                LocalDate.parse(dateText, dateFormatter);
                            } catch (DateTimeParseException exception) {
                                return false;
                            }
                        } else {
                            if (!expectedLoreLine.equals(lore.get(i))) return false;
                        }
                    }
                    return true;
                }
            }
        }

        plugin.getLogger().fine("Time stamp is not yet applied to this item!");
        return false;
    }

    public boolean timeReached(ItemStack item) {
        ZonedDateTime timestamp = getTimeStamp(item);
        if (timestamp != null) {
            return ZonedDateTime.now().isAfter(timestamp);
        }
        return false;
    }

    private ZonedDateTime getTimeStamp(ItemStack item) {
        if (timeStampAssigned(item)) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) return null;
                Optional<String> expiryLine = configService.getExpiryDateText().stream().filter(line -> line.contains("${expiry_date}")).findFirst();
                return expiryLine.map(line -> {
                    int lineIndex = configService.getExpiryDateText().indexOf(line);
                    int startIndex = line.indexOf("${expiry_date}");
                    int endIndex = startIndex + "${expiry_date}".length();
                    return LocalDate.parse(
                            lore.get(lineIndex)
                                    .replace(line.substring(0, startIndex), "")
                                    .replace(line.substring(endIndex), ""),
                            dateFormatter
                    ).atTime(LocalTime.of(1, 1, 1))
                            .atZone(ZoneId.systemDefault());
                }).orElse(null);
            }
        }
        return null;
    }

    public String getTimeLeft(ItemStack item) {
        if (!timeStampAssigned(item)) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        List<String> lore = meta.getLore();
        if (lore == null || lore.size() < 3) return null;

        ZonedDateTime timestamp = getTimeStamp(item);
        if (timestamp == null) return null;

        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(now, timestamp);
        double totalSeconds = duration.getSeconds();
        int minutes = (int) totalSeconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        if (days == 0) {
            return configService.getLessThanADay();
        } else {
            return configService.getTimeLeftText().replace("${time}", days + " days");
        }
    }
}