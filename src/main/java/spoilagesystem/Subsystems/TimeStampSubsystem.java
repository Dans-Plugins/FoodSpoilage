package spoilagesystem.Subsystems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spoilagesystem.Main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeStampSubsystem {

    Main main = null;

    String pattern = "MM/dd/yyyy HH";

    public TimeStampSubsystem(Main plugin) {
        main = plugin;
    }

    public ItemStack assignTimeStamp(ItemStack item, int hoursUntilSpoilage) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.WHITE + "" + main.storage.createdText);
        lore.add(ChatColor.WHITE + "" + getDateString());
        lore.add("");
        lore.add(ChatColor.WHITE + "" + main.storage.expiryDateText);
        lore.add(ChatColor.WHITE + "" + getDateStringPlusTime(hoursUntilSpoilage));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String getDateString() {
        DateFormat df = new SimpleDateFormat(pattern);

        Date now = getDate();

        return df.format(now);
    }

    private Date getDate() {
        return Calendar.getInstance().getTime();
    }

    private String getDateStringPlusTime(int hours) {
        DateFormat df = new SimpleDateFormat(pattern);

        Date now = getDatePlusTime(hours);

        return df.format(now);
    }

    private Date getDatePlusTime(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public boolean timeStampAssigned(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            List<String> lore = meta.getLore();

            if (lore.toString().contains(main.storage.expiryDateText)) {
                System.out.println("Debug] Time stamp is already assigned to this item!");
                return true;
            }
        }
        System.out.println("[Debug] Time stamp is not yet applied to this item!");
        return false;
    }

    public boolean timeReached(ItemStack item) {
        String timestamp = getTimeStamp(item);

        if (timestamp != null) {

            DateFormat df = new SimpleDateFormat(pattern + ":mm:ss");

            Date date = null;
            try {
                timestamp = timestamp + ":01:01";
                timestamp = timestamp.substring(2);
                date = df.parse(timestamp);
            } catch (Exception e) {
                System.out.println("Something went wrong parsing timestamp " + timestamp + " with pattern " + pattern + ":mm:ss");
            }

            if (date != null) {

                Date now = getDate();

                return now.after(date);

            }

        }
        return false;
    }

    private String getTimeStamp(ItemStack item) {
        if (timeStampAssigned(item)) {
            ItemMeta meta = item.getItemMeta();

            List<String> lore = meta.getLore();
            return lore.get(5);
        }
        return null;
    }

}
