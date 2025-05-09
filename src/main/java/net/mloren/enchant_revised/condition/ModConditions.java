package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
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

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MainMod.MOD_ID);

    public static final Supplier<MapCodec<EnchantAltarEnabledCondition>> ENCHANT_ALTAR_ENABLED =
            CONDITION_CODECS.register("enchant_altar_enabled", () -> EnchantAltarEnabledCondition.CODEC);

    public static final Supplier<MapCodec<EnchantTableEnabledCondition>> ENCHANT_TABLE_ENABLED =
            CONDITION_CODECS.register("enchant_table_enabled", () -> EnchantTableEnabledCondition.CODEC);

    public static final Supplier<LootItemConditionType> MENDING_REPLACED =
            LOOT_CONDITION_TYPES.register("mending_replaced", () -> new LootItemConditionType(MendingReplacedCondition.CODEC));

    public static void register(IEventBus eventBus)
    {
        CONDITION_CODECS.register(eventBus);
    }
}
