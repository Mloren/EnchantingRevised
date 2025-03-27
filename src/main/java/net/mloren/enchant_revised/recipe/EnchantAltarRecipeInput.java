package net.mloren.enchant_revised.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.mloren.enchant_revised.util.Constants;
import org.jetbrains.annotations.NotNull;

public record EnchantAltarRecipeInput(ItemStack primaryIngredient) implements RecipeInput
{
    public EnchantAltarRecipeInput(ItemStack primaryIngredient)
    {
        this.primaryIngredient = primaryIngredient;
    }

    @Override
    public @NotNull ItemStack getItem(int index)
    {
        if(index == Constants.PRIMARY_INGREDIENT_SLOT)
            return this.primaryIngredient;
        else
            throw new IllegalArgumentException("Recipe does not contain slot " + index);
    }

    @Override
    public int size()
    {
        return Constants.INPUT_SLOT_COUNT;
    }

    public boolean isEmpty()
    {
        return this.primaryIngredient.isEmpty();
    }
}
