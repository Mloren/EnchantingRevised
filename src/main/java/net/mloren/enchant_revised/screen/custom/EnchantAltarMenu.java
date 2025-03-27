package net.mloren.enchant_revised.screen.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.screen.ModMenuTypes;
import net.mloren.enchant_revised.util.Constants;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class EnchantAltarMenu extends ModdedContainerMenu
{
    static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    public final EnchantAltarBlockEntity blockEntity;
    public final ItemStackHandler inputStackHandler;
    public final ItemStackHandler resultStackHandler;

    public EnchantAltarMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnchantAltarMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity)
    {
        super(ModMenuTypes.ENCHANT_ALTAR_MENU.get(), containerId, playerInventory, blockEntity);
        this.blockEntity = ((EnchantAltarBlockEntity) blockEntity);
        this.inputStackHandler = this.blockEntity.inputStackHandler;
        this.resultStackHandler = this.blockEntity.resultStackHandler;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addSlots();
    }

    private void addSlots()
    {
        this.addSlot(new SlotItemHandler(this.inputStackHandler, Constants.LAPIS_SLOT, 27, 53)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack)
            {
                return itemStack.is(Items.LAPIS_LAZULI);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
            {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_LAPIS_LAZULI);
            }
        });

        this.addSlot(new SlotItemHandler(this.inputStackHandler, Constants.PRIMARY_INGREDIENT_SLOT, 27, 35));
        this.addSlot(new SlotItemHandler(this.inputStackHandler, Constants.SECONDARY_INGREDIENT_SLOT, 27, 17));
        this.addSlot(new SlotItemHandler(this.inputStackHandler, Constants.TARGET_ITEM_SLOT, 72, 35));

        this.addSlot(new SlotItemHandler(this.resultStackHandler, Constants.OUTPUT_SLOT, 130, 35)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack)
            {
                return false;
            }

            @Override
            public boolean isFake()
            {
                return true;
            }
        });
    }

    @Override
    protected Block getBlock()
    {
        return ModBlocks.ENCHANT_ALTAR.get();
    }

    @Override
    protected int getInputSlotCount()
    {
        return Constants.INPUT_SLOT_COUNT;
    }

    @Override
    protected int getOutputSlotCount()
    {
        return Constants.OUTPUT_SLOT_COUNT;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        this.clearItemStackHandler(player, this.inputStackHandler);
    }
}
