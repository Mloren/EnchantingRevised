package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ClientConfig
{
    public final ModConfigSpec.BooleanValue enableRecipeBook;

    public ClientConfig(ModConfigSpec.@NotNull Builder builder)
    {
        enableRecipeBook = builder.define("enableRecipeBook", true);
    }
}
