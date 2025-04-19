package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.MainMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModConditions
{
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MainMod.MOD_ID);

    public static final Supplier<MapCodec<EnchantAltarEnabledCondition>> ENCHANT_ALTAR_ENABLED =
            CONDITION_CODECS.register("enchant_altar_enabled", () -> EnchantAltarEnabledCondition.CODEC);

    public static final Supplier<MapCodec<EnchantTableEnabledCondition>> ENCHANT_TABLE_ENABLED =
            CONDITION_CODECS.register("enchant_table_enabled", () -> EnchantTableEnabledCondition.CODEC);

    public static void register(IEventBus eventBus)
    {
        CONDITION_CODECS.register(eventBus);
    }
}
