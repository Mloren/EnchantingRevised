package net.mloren.enchant_revised.config.types;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class ServerConfig
{
    public final ModConfigSpec.BooleanValue enableAnvilNoXPCosts;
    public final ModConfigSpec.BooleanValue enableNoBookCombining;

    public ServerConfig(ModConfigSpec.@NotNull Builder builder)
    {
        builder.push("anvil");
        enableAnvilNoXPCosts = builder.define("enableAnvilNoXPCosts", true);
        enableNoBookCombining = builder.define("enableNoBookCombining", true);
        builder.pop();
    }
}
