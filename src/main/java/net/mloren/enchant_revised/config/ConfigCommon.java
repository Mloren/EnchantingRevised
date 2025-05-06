//package net.mloren.enchant_revised;
//
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.event.config.ModConfigEvent;
//import net.neoforged.neoforge.common.ModConfigSpec;
//
//@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
//public class ConfigServer
//{
//    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
//
//    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_ALTAR = BUILDER
//            .comment("Should the Enchanting Altar be enabled? (Requires reload)")
//            .define("enableEnchantAltar", true);
//
//    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_TABLE = BUILDER
//            .comment("Should the vanilla Enchanting Table be enabled? (Requires reload)")
//            .define("enableEnchantTable", false);
//
//    public static boolean enableEnchantAltar;
//    public static boolean enableEnchantTable;
//
//    static final ModConfigSpec SPEC = BUILDER.build();
//
//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event)
//    {
//        enableEnchantAltar = ENABLE_ENCHANT_ALTAR.get();
//        enableEnchantTable = ENABLE_ENCHANT_TABLE.get();
//    }
//}

package net.mloren.enchant_revised.config;

import net.neoforged.neoforge.common.ModConfigSpec;

//@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigCommon
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_ALTAR = BUILDER
            .comment("Should the Enchanting Altar be enabled? (Server only. Requires reload)")
            .define("enableEnchantAltar", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ENCHANT_TABLE = BUILDER
            .comment("Should the vanilla Enchanting Table be enabled? (Server only. Requires reload)")
            .define("enableEnchantTable", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event)
//    {
//    }

    public static boolean getEnchantAltarEnabled()
    {
        return ENABLE_ENCHANT_ALTAR.get();
    }

    public static boolean getEnchantTableEnabled()
    {
        return ENABLE_ENCHANT_TABLE.get();
    }
}
