package spoilagesystem;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Calendar.HOUR_OF_DAY;

public class TimeStamper {

    private static TimeStamper instance;

    private TimeStamper() {

    }

    public static TimeStamper getInstance() {
        if (instance == null) {
            instance = new TimeStamper();
        }
        return instance;
    }

    String pattern = "MM/dd/yyyy HH";

    public ItemStack assignTimeStamp(ItemStack item, int hoursUntilSpoilage) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(asList(
                    "",
                    ChatColor.WHITE + FoodSpoilage.getInstance().storage.createdText,
                    ChatColor.WHITE + getDateString(),
                    "",
                    ChatColor.WHITE + FoodSpoilage.getInstance().storage.expiryDateText,
                    ChatColor.WHITE + getDateStringPlusTime(hoursUntilSpoilage)
            ));

            item.setItemMeta(meta);
        }

        return item;
    }

    private String getDateString() {
        return new SimpleDateFormat(pattern).format(getDate());
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
                    return lore.toString().contains(FoodSpoilage.getInstance().storage.expiryDateText);
                }
            }
        }

        // System.out.println("[Debug] Time stamp is not yet applied to this item!");
        return false;
    }

    public boolean timeReached(ItemStack item) {
        String timestamp = getTimeStamp(item);

        if (timestamp != null) {

            DateFormat df = new SimpleDateFormat(pattern + ":mm:ss");

            timestamp = timestamp + ":01:01";
            timestamp = timestamp.substring(2);

            Date date = null;
            try {
                date = df.parse(timestamp);
            } catch (Exception e) {
                System.out.println("Something went wrong parsing timestamp " + timestamp + " with pattern " + pattern + ":mm:ss");
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

                if (lore != null && lore.size() > 5) {
                    return lore.get(5);
                }
            }
        }
        return null;
    }

}
