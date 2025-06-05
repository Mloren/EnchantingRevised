package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class CommonConfig
{
    public final ModConfigSpec.BooleanValue disableXP;
    public final ModConfigSpec.BooleanValue enableBottleOfEnchant;

    public final ModConfigSpec.BooleanValue enableEnchantAltar;
    public final ModConfigSpec.BooleanValue enableEnchantTable;
    public final ModConfigSpec.BooleanValue enableEnchantTableXPCosts;

    public final ModConfigSpec.BooleanValue enableAnvilXPCosts;
    public final ModConfigSpec.BooleanValue enableBookUpgrading;

    public final ModConfigSpec.BooleanValue replaceMending;
    public final ModConfigSpec.IntValue repairDelayTicks;
    public final ModConfigSpec.IntValue repairDelayTicksPerLevel;

    public final ModConfigSpec.BooleanValue enableEchoingBookTrade;
    public final ModConfigSpec.BooleanValue enableLapisTrade;
    public final ModConfigSpec.BooleanValue enableEnchantedBookTrade;

    public final ModConfigSpec.BooleanValue repairNetheriteWithDiamonds;
    public final ModConfigSpec.BooleanValue repairNetheriteWithScrap;

    public final ModConfigSpec.BooleanValue provideStartingLoot;

    public CommonConfig(ModConfigSpec.@NotNull Builder builder)
    {
        builder.push("experience");
        disableXP = builder.define("disableXP", true);
        enableBottleOfEnchant = builder.worldRestart().define("enableBottleOfEnchant", false);
        builder.pop();

        builder.push("enchanting");
        enableEnchantAltar = builder.worldRestart().define("enableEnchantAltar", true);
        enableEnchantTable = builder.worldRestart().define("enableEnchantTable", false);
        enableEnchantTableXPCosts = builder.define("enableEnchantTableXPCosts", false);
        builder.pop();

        builder.push("anvil");
        enableAnvilXPCosts = builder.define("enableAnvilXPCosts", false);
        enableBookUpgrading = builder.define("enableBookUpgrading", false);
        builder.pop();

        //Repair 1 durability every 10 seconds. Reduced by 2 seconds for levels above 1.
        builder.push("mending");
        replaceMending = builder.worldRestart().define("replaceMending", true);
        repairDelayTicks = builder.defineInRange("repairDelayTicks", 200, 1, 9999999);
        repairDelayTicksPerLevel = builder.defineInRange("repairDelayTicksPerLevel", 40, 1, 9999999);
        builder.pop();

        builder.push("villager");
        enableLapisTrade = builder.worldRestart().define("enableLapisTrade", true);
        enableEchoingBookTrade = builder.worldRestart().define("enableEchoingBookTrade", true);
        enableEnchantedBookTrade = builder.worldRestart().define("enableEnchantedBookTrade", false);
        builder.pop();

        builder.push("netherite_tools");
        repairNetheriteWithDiamonds = builder.define("repairNetheriteWithDiamonds", true);
        repairNetheriteWithScrap = builder.define("repairNetheriteWithScrap", false);
        builder.pop();

        provideStartingLoot = builder.worldRestart().define("provideStartingLoot", true);
    }
}
