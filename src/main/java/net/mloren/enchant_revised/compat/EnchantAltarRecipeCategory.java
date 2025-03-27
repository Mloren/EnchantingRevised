package net.mloren.enchant_revised.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.util.Constants;
import org.jetbrains.annotations.Nullable;

public class EnchantAltarRecipeCategory implements IRecipeCategory<EnchantAltarRecipe>
{
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "enchant_altar");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID,
            "textures/gui/enchant_altar/enchant_altar_gui.png");

    public static final RecipeType<EnchantAltarRecipe> ENCHANT_ALTAR_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, EnchantAltarRecipe.class);

    public final IDrawable background;
    public final IDrawable icon;

    public EnchantAltarRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ENCHANT_ALTAR));
    }

    @Override
    public RecipeType<EnchantAltarRecipe> getRecipeType()
    {
        return ENCHANT_ALTAR_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle()
    {
        return Component.translatable("block.enchant_revised.enchant_altar");
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantAltarRecipe recipe, IFocusGroup focuses)
    {
        //builder.addSlot(RecipeIngredientRole.INPUT, 27, 53).addIngredients(recipe.getIngredients().get(Constants.LAPIS_SLOT));
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 35).addIngredients(recipe.getIngredients().get(Constants.PRIMARY_INGREDIENT_SLOT));
        //builder.addSlot(RecipeIngredientRole.INPUT, 27, 17).addIngredients(recipe.getIngredients().get(Constants.SECONDARY_INGREDIENT_SLOT));
        //builder.addSlot(RecipeIngredientRole.INPUT, 72, 35).addIngredients(recipe.getIngredients().get(Constants.TARGET_ITEM_SLOT));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 35).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public @Nullable IDrawable getBackground()
    {
        return background;
    }

    // crashes
//    @Override
//    public void draw(GrowthChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
//    {
//        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
//        background.draw(guiGraphics);
//    }
}
