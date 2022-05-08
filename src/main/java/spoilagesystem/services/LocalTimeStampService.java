package spoilagesystem.services;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Calendar.HOUR_OF_DAY;

/**
 * @author Daniel McCoy Stephenson
 */
public class LocalTimeStampService {
    private static LocalTimeStampService instance;

    private LocalTimeStampService() {

    }

    public static LocalTimeStampService getInstance() {
        if (instance == null) {
            instance = new LocalTimeStampService();
        }
        return instance;
    }

    String pattern = "MM/dd/yyyy";

    public ItemStack assignTimeStamp(ItemStack item, int hoursUntilSpoilage) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(asList(
                    "",
                    ChatColor.WHITE + LocalConfigService.getInstance().expiryDateText,
                    ChatColor.WHITE + getDateStringPlusTime(hoursUntilSpoilage)
            ));

            item.setItemMeta(meta);
        }

        return item;
    }

    private Date getDate() {
        return Calendar.getInstance().getTime();
    }

    private String getDateStringPlusTime(int hours) {
        return new SimpleDateFormat(pattern).format(getDatePlusTime(hours));
    }

    private Date getDatePlusTime(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public boolean timeStampAssigned(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();

                if (lore != null) {
                    // System.out.println("Debug] Time stamp is already assigned to this item!");
                    return lore.toString().contains(LocalConfigService.getInstance().expiryDateText);
                }
            }
        }

        // System.out.println("[Debug] Time stamp is not yet applied to this item!");
        return false;
    }

    public boolean timeReached(ItemStack item) {
        String timestamp = getTimeStamp(item);

        if (timestamp != null) {

            DateFormat df = new SimpleDateFormat(pattern + "HH:mm:ss");

            timestamp = timestamp + "01:01:01";
            timestamp = timestamp.substring(2);

            Date date = null;
            try {
                date = df.parse(timestamp);
            } catch (Exception e) {
                System.out.println("Something went wrong parsing timestamp " + timestamp + " with pattern " + pattern + "HH:mm:ss");
            }

            if (date != null) {
                return getDate().after(date);
            }
        }
        return false;
    }

    private String getTimeStamp(ItemStack item) {
        if (timeStampAssigned(item)) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && lore.size() > 2) {
                    return lore.get(2);
                }
            }
        }
        return null;
    }

    public String getTimeLeft(ItemStack item) {
        if (!timeStampAssigned(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        List<String> lore = meta.getLore();

        if (lore == null || lore.size() < 3) {
            return null;
        }

        String timestamp = getTimeStamp(item);

        if (timestamp == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(pattern + "HH:mm:ss");

        timestamp = timestamp + "01:01:01";
        timestamp = timestamp.substring(2);

        Date date = null;
        try {
            date = df.parse(timestamp);
        } catch (Exception e) {
            System.out.println("Something went wrong parsing timestamp " + timestamp + " with pattern " + pattern + "HH:mm:ss");
        }

        ZonedDateTime zDate = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(zDate, now);
        double totalSeconds = duration.getSeconds();
        int minutes = (int) totalSeconds/60;
        int hours = minutes / 60;
        int days = hours / 24;

        days = days * -1;

        if (days == 0) {
            return LocalConfigService.getInstance().lessThanADay;
        }
        else {
            return String.format(LocalConfigService.getInstance().timeLeftText, days + " days");
        }
    }
}