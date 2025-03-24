package net.mloren.enchant_revised.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record EnchantAltarRecipe(Ingredient inputItem, ItemStack output) implements Recipe<EnchantAltarRecipeInput>
{
    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(EnchantAltarRecipeInput input, Level level)
    {
        if(level.isClientSide())
            return false;

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(EnchantAltarRecipeInput input, HolderLookup.Provider registries)
    {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipes.ENCHANT_ALTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return ModRecipes.ENCHANT_ALTAR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<EnchantAltarRecipe>
    {
        //format of the JSON file
        //"ingredient" and "result" are the fields in the JSON file
        public static final MapCodec<EnchantAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(EnchantAltarRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(EnchantAltarRecipe::output)
        ).apply(inst, EnchantAltarRecipe::new));

        //synchronized over the network
        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> STEAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, EnchantAltarRecipe::inputItem,
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
            return STEAM_CODEC;
        }
    }
}
