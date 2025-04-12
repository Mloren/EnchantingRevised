package net.mloren.enchant_revised.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

public class EnchantAltarRecipeCategory implements IRecipeCategory<EnchantAltarRecipe>
{
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "enchant_altar");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID,
            "textures/gui/enchant_altar/enchant_altar_gui.png");

    public static final RecipeType<EnchantAltarRecipe> ENCHANT_ALTAR_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, EnchantAltarRecipe.class);

    private static final int BACKGROUND_X = 26;
    private static final int BACKGROUND_Y = 16;
    private static final int BACKGROUND_WIDTH = 125;
    private static final int BACKGROUND_HEIGHT = 54;

    public final IDrawable background;
    public final IDrawable icon;

    public EnchantAltarRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(TEXTURE, BACKGROUND_X, BACKGROUND_Y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
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
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(new ItemStack(Items.LAPIS_LAZULI, recipe.lapisCost()));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addItemStack(itemStackFromIngredient(recipe.primaryIngredient()));

        if(recipe.secondaryIngredient().isPresent())
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 37).addItemStack(itemStackFromIngredient(recipe.secondaryIngredient().get()));

        builder.addSlot(RecipeIngredientRole.INPUT, 46, 19).addItemStack(new ItemStack(Items.BOOK, 1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 19).addItemStack(recipe.getExampleResult());
    }

    private ItemStack itemStackFromIngredient(SizedIngredient ingredient)
    {
        if(ingredient.getItems().length == 0)
            return ItemStack.EMPTY;

        ItemStack itemStack = ingredient.getItems()[0];
        if(itemStack == null || itemStack.isEmpty())
            return ItemStack.EMPTY;

        return new ItemStack(itemStack.getItem(), ingredient.count());
    }

    @Override
    public int getWidth()
    {
        return this.background.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.background.getHeight();
    }

    @Override
    public void draw(EnchantAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
    }
}
