package net.mloren.enchant_revised.config;

import net.mloren.enchant_revised.MainMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigClient
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_RECIPE_BOOK = BUILDER
            .comment("Should the Enchanting Altar recipe book button be shown?")
            .define("enableRecipeBook", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableRecipeBook;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableRecipeBook = ENABLE_RECIPE_BOOK.get();
    }
}
