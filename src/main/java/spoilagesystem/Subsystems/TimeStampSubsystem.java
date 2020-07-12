package spoilagesystem.Subsystems;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spoilagesystem.Main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeStampSubsystem {

    Main main = null;

    public TimeStampSubsystem(Main plugin) {
        main = plugin;
    }

    public void assignTimeStamp(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        lore.add("Expiry Date:");
        lore.add(getDateString());

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private String getDateString() {
        String pattern = "MM/dd/yyyy HH:mm:ss";

        DateFormat df = new SimpleDateFormat(pattern);

        Date now = getDate();

        return df.format(now);
    }

    private Date getDate() {
        return Calendar.getInstance().getTime();
    }

    public boolean timeStampAssigned(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        for (String string : lore) {
            if (string.equalsIgnoreCase("Expiry Date:")) {
                return true;
            }
        }
        return false;
    }

    public boolean timeReached(ItemStack item) {
        String timestamp = getTimeStamp(item);

        if (timestamp != null) {

            String pattern = "MM/dd/yyyy HH:mm:ss";

            DateFormat df = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = df.parse(timestamp);
            } catch (Exception e) {
                System.out.println("Something went wrong parsing a date.");
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
            return lore.get(1);
        }
        return null;
    }

}
