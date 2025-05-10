package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ClientConfig
{
    public final ModConfigSpec.BooleanValue enableRecipeBook;
    public final ModConfigSpec.BooleanValue hideXPBar;
    public final ModConfigSpec.IntValue guiHeight;

    public ClientConfig(ModConfigSpec.@NotNull Builder builder)
    {
        enableRecipeBook = builder.define("enableRecipeBook", true);
        hideXPBar = builder.define("hideXPBar", true);

        //Height of the health bar on the UI, 39 is vanilla
        guiHeight = builder.defineInRange("guiHeight", 33, 0, 10000);
    }
}
