package spoilagesystem.timestamp;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE;
import static java.util.logging.Level.SEVERE;
import static org.bukkit.persistence.PersistentDataType.STRING;

/**
 * @author Daniel McCoy Stephenson
 */
public final class LocalTimeStampService {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;

    private final DateTimeFormatter dateFormatter;
    private final NamespacedKey expiryKey;

    public LocalTimeStampService(FoodSpoilage plugin, LocalConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;

        dateFormatter = DateTimeFormatter.ofPattern(plugin.getConfig().getString("expiry-date-format", "MM/dd/yyyy"));
        expiryKey = new NamespacedKey(plugin, "expiry");
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

            meta.getPersistentDataContainer().set(
                    expiryKey,
                    STRING,
                    ISO_OFFSET_DATE.format(OffsetDateTime.now().plus(timeUntilSpoilage))
            );
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack assignTimeStamp(ItemStack item) {
        Duration time = configService.getTime(item.getType());
        if (!time.equals(Duration.ZERO)) {
            return assignTimeStamp(item, time);
        }
        return item;
    }

    private String getDateStringPlusTime(Duration time) {
        return dateFormatter.format(OffsetDateTime.now().plus(time));
    }

    public boolean timeStampAssigned(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (parseExpiryFromPersistentData(meta) != null) return true;
                if (meta.hasLore()) {
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
        }

        plugin.getLogger().fine("Time stamp is not yet applied to this item!");
        return false;
    }

    public boolean timeReached(ItemStack item) {
        OffsetDateTime timestamp = getTimeStamp(item);
        if (timestamp != null) {
            return OffsetDateTime.now().isAfter(timestamp);
        }
        return false;
    }

    private OffsetDateTime getTimeStamp(ItemStack item) {
        if (timeStampAssigned(item)) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                OffsetDateTime expiryFromPersistentData = parseExpiryFromPersistentData(meta);
                if (expiryFromPersistentData != null) return expiryFromPersistentData;
                return parseExpiryFromLore(meta);
            }
        }
        return null;
    }

    @Nullable
    private OffsetDateTime parseExpiryFromPersistentData(ItemMeta meta) {
        String expiryString = meta.getPersistentDataContainer().get(expiryKey, STRING);
        if (expiryString != null) {
            try {
                return OffsetDateTime.parse(expiryString, ISO_OFFSET_DATE);
            } catch (DateTimeParseException exception) {
                plugin.getLogger().log(SEVERE, "Failed to parse expiry from persistent data container", exception);
            }
        }
        return null;
    }

    @Nullable
    private OffsetDateTime parseExpiryFromLore(ItemMeta meta) {
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
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime();
        }).orElse(null);
    }

    public String getTimeLeft(ItemStack item) {
        if (!timeStampAssigned(item)) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        List<String> lore = meta.getLore();
        if (lore == null || lore.size() < 3) return null;

        OffsetDateTime timestamp = getTimeStamp(item);
        if (timestamp == null) return null;

        OffsetDateTime now = OffsetDateTime.now();
        Duration duration = Duration.between(now, timestamp);
        double totalSeconds = duration.getSeconds();
        int minutes = (int) totalSeconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        if (days == 0) {
            if (hours == 0) {
                return configService.getLessThanAnHour();
            } else {
                return configService.getLessThanADay();
            }
        } else if (days > 0) {
            return configService.getTimeLeftText().replace("${time}", days + " days");
        } else {
            return configService.getNoTimeLeftText();
        }
    }
}