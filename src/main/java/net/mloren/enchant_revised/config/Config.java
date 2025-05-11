package net.mloren.enchant_revised.config;

import net.mloren.enchant_revised.config.types.ClientConfig;
import net.mloren.enchant_revised.config.types.CommonConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    public static final CommonConfig COMMON;
    public static final ClientConfig CLIENT;

    private static final ModConfigSpec commonSpec;
    private static final ModConfigSpec clientSpec;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = specPair.getLeft();
        commonSpec = specPair.getRight();
    }

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = specPair.getLeft();
        clientSpec = specPair.getRight();
    }

    public static void register(ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, commonSpec);
        modContainer.registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }
}
