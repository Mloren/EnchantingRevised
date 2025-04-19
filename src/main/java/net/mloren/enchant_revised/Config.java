package net.mloren.enchant_revised;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_ALTAR = BUILDER
            .comment("Should the Enchanting Altar be enabled? (Requires reload)")
            .define("enableEnchantAltar", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_TABLE = BUILDER
            .comment("Should the vanilla Enchanting Table be enabled? (Requires reload)")
            .define("enableEnchantTable", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableEnchantAltar;
    public static boolean enableEnchantTable;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableEnchantAltar = ENABLE_ENCHANT_ALTAR.get();
        enableEnchantTable = ENABLE_ENCHANT_TABLE.get();
    }
}
