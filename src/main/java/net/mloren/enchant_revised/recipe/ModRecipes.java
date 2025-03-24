package net.mloren.enchant_revised.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.mloren.enchant_revised.MainMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MainMod.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MainMod.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnchantAltarRecipe>> ENCHANT_ALTAR_SERIALIZER =
            SERIALIZERS.register("enchant_altar", EnchantAltarRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EnchantAltarRecipe>> ENCHANT_ALTAR_TYPE =
            TYPES.register("enchant_altar", () -> new RecipeType<EnchantAltarRecipe>()
            {
                @Override
                public String toString()
                {
                    return "enchant_altar";
                }
            });

    public static void register(IEventBus eventBus)
    {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
