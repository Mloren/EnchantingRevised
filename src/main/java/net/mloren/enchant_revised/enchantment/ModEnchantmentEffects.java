package net.mloren.enchant_revised.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.enchantment.custom.RepairOverTimeEnchantmentEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEnchantmentEffects
{
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, MainMod.MOD_ID);

    public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> REPAIR_OVER_TIME =
            ENTITY_ENCHANTMENT_EFFECTS.register("repair_over_time", () -> RepairOverTimeEnchantmentEffect.CODEC);

    public static void register(IEventBus eventBus)
    {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
