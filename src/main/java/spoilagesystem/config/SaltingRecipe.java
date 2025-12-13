package spoilagesystem.config;

import org.bukkit.Material;

import java.time.Duration;

/**
 * Represents a salting recipe configuration
 */
public final class SaltingRecipe {
    private final Material foodType;
    private final Material saltMaterial;
    private final int saltAmount;
    private final SaltingMode mode;
    private final Duration timeModifier;

    public SaltingRecipe(Material foodType, Material saltMaterial, int saltAmount, SaltingMode mode, Duration timeModifier) {
        this.foodType = foodType;
        this.saltMaterial = saltMaterial;
        this.saltAmount = saltAmount;
        this.mode = mode;
        this.timeModifier = timeModifier;
    }

    public Material getFoodType() {
        return foodType;
    }

    public Material getSaltMaterial() {
        return saltMaterial;
    }

    public int getSaltAmount() {
        return saltAmount;
    }

    public SaltingMode getMode() {
        return mode;
    }

    public Duration getTimeModifier() {
        return timeModifier;
    }

    public enum SaltingMode {
        RESET,    // Reset the spoilage timer to original duration
        EXTEND    // Extend the current timer by the specified duration
    }
}
