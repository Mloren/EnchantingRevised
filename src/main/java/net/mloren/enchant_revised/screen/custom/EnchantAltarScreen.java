package net.mloren.enchant_revised.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.util.EnchantAltar;

import java.awt.*;
import java.util.Optional;

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

    public EnchantAltarScreen(EnchantAltarMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        this.menu = menu;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        boolean bookshelvesValid = menu.getBookshelvesValid();
        int bookshelvesRequired = menu.getBookshelvesRequired();

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (!bookshelvesValid && bookshelvesRequired > 0)
        {
            guiGraphics.blitSprite(ERROR_ICON, x + 93, y + 33, 28, 21);

            guiGraphics.renderItem(BOOKSHELF, x + 98, y + 55);
            if(bookshelvesRequired > 0)
                guiGraphics.drawString(minecraft.font, String.valueOf(bookshelvesRequired), x + 114, y + 59, 0xFF3F3F3F, false);
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
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if(optional.isPresent())
            guiGraphics.renderTooltip(this.font, this.font.split(optional.get(), 115), mouseX, mouseY);
    }
}
