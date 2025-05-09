package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ServerConfig
{
    public final ModConfigSpec.BooleanValue enableAnvilNoXPCosts;
    public final ModConfigSpec.BooleanValue enableNoBookCombining;

    public final ModConfigSpec.BooleanValue repairNetheriteWithDiamonds;
    public final ModConfigSpec.BooleanValue repairNetheriteWithScrap;

    public final ModConfigSpec.IntValue repairDelayTicks;
    public final ModConfigSpec.IntValue repairDelayTicksPerLevel;

    public ServerConfig(ModConfigSpec.@NotNull Builder builder)
    {
        builder.push("anvil");
        enableAnvilNoXPCosts = builder.define("enableAnvilNoXPCosts", true);
        enableNoBookCombining = builder.define("enableNoBookCombining", true);
        builder.pop();

        builder.push("netherite_tools");
        repairNetheriteWithDiamonds = builder.define("repairNetheriteWithDiamonds", true);
        repairNetheriteWithScrap = builder.define("repairNetheriteWithScrap", false);
        builder.pop();

        //Repair 1 durability every 10 seconds. Reduced by 2 seconds for levels above 1.
        builder.push("mending");
        repairDelayTicks = builder.defineInRange("repairDelayTicks", 200, 1, 9999999);
        repairDelayTicksPerLevel = builder.defineInRange("repairDelayTicksPerLevel", 40, 1, 9999999);
        builder.pop();
    }
}
