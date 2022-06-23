package spoilagesystem.rpkit;

import com.rpkit.core.service.Services;
import com.rpkit.food.bukkit.expiry.RPKExpiryService;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;
import java.time.OffsetDateTime;

public class FoodSpoilageRpkitExpiryService implements RPKExpiryService {

    private final FoodSpoilage plugin;
    private final LocalTimeStampService timeStampService;

    public FoodSpoilageRpkitExpiryService(FoodSpoilage plugin, LocalTimeStampService timeStampService) {
        this.plugin = plugin;
        this.timeStampService = timeStampService;
        Services.INSTANCE.set(RPKExpiryService.class, this);
    }

    @Override
    public FoodSpoilage getPlugin() {
        return plugin;
    }

    @Override
    public void setExpiry(ItemStack item, Duration duration) {
        timeStampService.assignTimeStamp(item, duration);
    }

    @Override
    public void setExpiry(ItemStack item, OffsetDateTime dateTime) {
        timeStampService.assignTimeStamp(item, Duration.between(OffsetDateTime.now(), dateTime));
    }

    @Override
    public void setExpiry(ItemStack itemStack) {
        timeStampService.assignTimeStamp(itemStack);
    }

    @Override
    public OffsetDateTime getExpiry(ItemStack item) {
        return timeStampService.getTimeStamp(item);
    }

    @Override
    public boolean isExpired(ItemStack item) {
        return timeStampService.timeReached(item);
    }
}
