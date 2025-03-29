package net.mloren.enchant_revised.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.mloren.enchant_revised.util.Constants;
import org.jetbrains.annotations.NotNull;

public record EnchantAltarRecipeInput(ItemStack primaryIngredient, ItemStack secondaryIngredient, ItemStack fuel, ItemStack targetItem) implements RecipeInput
{
    @Override
    public @NotNull ItemStack getItem(int index)
    {
        if(index == Constants.PRIMARY_INGREDIENT_SLOT)
            return this.primaryIngredient;
        else if(index == Constants.SECONDARY_INGREDIENT_SLOT)
            return this.secondaryIngredient;
        else if(index == Constants.LAPIS_SLOT)
            return this.fuel;
        else if(index == Constants.TARGET_ITEM_SLOT)
            return this.targetItem;
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
        return this.primaryIngredient.isEmpty() &&
                this.secondaryIngredient.isEmpty() &&
                this.fuel.isEmpty() &&
                this.targetItem.isEmpty();
    }
}
