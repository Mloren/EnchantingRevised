package net.mloren.enchant_revised.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.config.Config;
import net.mloren.enchant_revised.config.types.ClientConfig;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.screen.RecipeBook.RecipeBookScreen;
import net.mloren.enchant_revised.util.EnchantAltar;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class EnchantAltarScreen extends AbstractContainerScreen<EnchantAltarMenu>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "textures/gui/enchant_altar/enchant_altar_gui.png");
    private static final ResourceLocation ERROR_ICON = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID,"enchant_altar/error");
    private static final ItemStack BOOKSHELF = new ItemStack(Items.BOOKSHELF, 1);

    private static final Component LAPIS_SLOT_TOOLTIP = Component.translatable("misc.enchant_revised.lapis_tooltip");
    private static final Component PRIMARY_SLOT_TOOLTIP = Component.translatable("misc.enchant_revised.primary_tooltip");
    private static final Component SECONDARY_SLOT_TOOLTIP = Component.translatable("misc.enchant_revised.secondary_tooltip");
    private static final Component TARGET_SLOT_TOOLTIP = Component.translatable("misc.enchant_revised.target_tooltip");

    private final EnchantAltarMenu menu;
    private final Level level;

    private final RecipeBookScreen recipeBook = new RecipeBookScreen();
    private boolean widthTooNarrow;

    public EnchantAltarScreen(EnchantAltarMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        this.menu = menu;
        this.level = menu.level;
    }

    @Override
    protected void init()
    {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBook.init(this.width, this.height, this.minecraft, this.widthTooNarrow, level);
        this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);

        if (Config.CLIENT.enableRecipeBook.get())
        {
            this.addRenderableWidget(new ImageButton(leftPos + 5, height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button ->
            {
                this.recipeBook.toggleVisibility();
                this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
                button.setPosition(leftPos + 5, height / 2 - 49);
            }));
        }
        else if(recipeBook.isVisible())
        {
            recipeBook.setVisible(false);
            this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int y = (height - imageHeight) / 2;

        boolean bookshelvesValid = menu.getBookshelvesValid();
        int bookshelvesRequired = menu.getBookshelvesRequired();

        // Background
        guiGraphics.blit(GUI_TEXTURE, leftPos, y, 0, 0, imageWidth, imageHeight);

        // Render number of bookshelves required
        if (!bookshelvesValid && bookshelvesRequired > 0)
        {
            guiGraphics.blitSprite(ERROR_ICON, leftPos + 93, y + 33, 28, 21);

            guiGraphics.renderItem(BOOKSHELF, leftPos + 98, y + 55);
            if(bookshelvesRequired > 0)
                guiGraphics.drawString(minecraft.font, String.valueOf(bookshelvesRequired), leftPos + 114, y + 59, 0xFF3F3F3F, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        Optional<Component> optional = Optional.empty();
        if (this.hoveredSlot != null)
        {
            ItemStack itemstack = this.hoveredSlot.getItem();
            if (itemstack.isEmpty())
            {
                if (this.hoveredSlot.index == EnchantAltar.INVENTORY_SLOT_COUNT + EnchantAltar.LAPIS_SLOT)
                    optional = Optional.of(LAPIS_SLOT_TOOLTIP);
                else if (this.hoveredSlot.index == EnchantAltar.INVENTORY_SLOT_COUNT + EnchantAltar.PRIMARY_INGREDIENT_SLOT)
                    optional = Optional.of(PRIMARY_SLOT_TOOLTIP);
                else if (this.hoveredSlot.index == EnchantAltar.INVENTORY_SLOT_COUNT + EnchantAltar.SECONDARY_INGREDIENT_SLOT)
                    optional = Optional.of(SECONDARY_SLOT_TOOLTIP);
                else if (this.hoveredSlot.index == EnchantAltar.INVENTORY_SLOT_COUNT + EnchantAltar.TARGET_ITEM_SLOT)
                    optional = Optional.of(TARGET_SLOT_TOOLTIP);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if(recipeBook.isVisible())
            recipeBook.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if(optional.isPresent())
            guiGraphics.renderTooltip(this.font, this.font.split(optional.get(), 115), mouseX, mouseY);

        if(recipeBook.isVisible())
            recipeBook.renderTooltips(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button))
        {
            this.setFocused(this.recipeBook);
            return true;
        }
        else
        {
            return this.widthTooNarrow && this.recipeBook.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        return this.recipeBook.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        return (!this.widthTooNarrow || !this.recipeBook.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton)
    {
        boolean flag = mouseX < (double)guiLeft
                || mouseY < (double)guiTop
                || mouseX >= (double)(guiLeft + this.imageWidth)
                || mouseY >= (double)(guiTop + this.imageHeight);
        return this.recipeBook.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseButton) && flag;
    }
}
