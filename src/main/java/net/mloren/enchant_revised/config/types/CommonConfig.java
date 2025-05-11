package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class CommonConfig
{
    public final ModConfigSpec.BooleanValue enableEnchantAltar;
    public final ModConfigSpec.BooleanValue enableEnchantTable;
    public final ModConfigSpec.BooleanValue replaceMending;

    public final ModConfigSpec.BooleanValue enableEchoingBookTrade;
    public final ModConfigSpec.BooleanValue enableLapisTrade;
    public final ModConfigSpec.BooleanValue disableEnchantedBookTrade;

    public final ModConfigSpec.BooleanValue disableBottleOfEnchant;

    public CommonConfig(ModConfigSpec.@NotNull Builder builder)
    {
        builder.push("enchanting");
        enableEnchantAltar = builder.worldRestart().define("enableEnchantAltar", true);
        enableEnchantTable = builder.worldRestart().define("enableEnchantTable", false);
        replaceMending = builder.worldRestart().define("replaceMending", true);
        builder.pop();

        builder.push("villager");
        enableEchoingBookTrade = builder.worldRestart().define("enableEchoingBookTrade", true);
        enableLapisTrade = builder.worldRestart().define("enableLapisTrade", true);
        disableEnchantedBookTrade = builder.worldRestart().define("disableEnchantedBookTrade", true);
        builder.pop();

        disableBottleOfEnchant = builder.worldRestart().define("disableBottleOfEnchant", true);
    }
}
