package net.mloren.enchant_revised.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EnchantAltarRecipe(Ingredient primaryIngredient, ItemStack output) implements Recipe<EnchantAltarRecipeInput>
{
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(primaryIngredient);
        return list;
    }

    @Override
    public boolean matches(@NotNull EnchantAltarRecipeInput input, Level level)
    {
        if(level.isClientSide())
            return false;

        return primaryIngredient.test(input.getItem(Constants.PRIMARY_INGREDIENT_SLOT));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull EnchantAltarRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@Nullable Provider registries)
    {
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer()
    {
        return ModRecipes.ENCHANT_ALTAR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return ModRecipes.ENCHANT_ALTAR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<EnchantAltarRecipe>
    {
        //format of the JSON file
        //"ingredient" and "result" are the fields in the JSON file
        public static final MapCodec<EnchantAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(EnchantAltarRecipe::primaryIngredient),
                ItemStack.CODEC.fieldOf("result").forGetter(EnchantAltarRecipe::output)
        ).apply(inst, EnchantAltarRecipe::new));

        //synchronized over the network
        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, EnchantAltarRecipe::primaryIngredient,
                        ItemStack.STREAM_CODEC, EnchantAltarRecipe::output,
                        EnchantAltarRecipe::new);

        @Override
        public MapCodec<EnchantAltarRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
