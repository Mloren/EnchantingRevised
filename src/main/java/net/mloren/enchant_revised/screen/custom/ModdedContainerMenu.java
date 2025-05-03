package net.mloren.enchant_revised.screen.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModdedContainerMenu extends AbstractContainerMenu
{
    protected final Level level;
    protected final BlockEntity blockEntity;

    protected ModdedContainerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, BlockEntity blockEntity)
    {
        super(menuType, containerId);
        this.level = playerInventory.player.level();
        this.blockEntity = blockEntity;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex)
    {
        int inputSlotCount = getInputSlotCount();
        int outputSlotCount = getOutputSlotCount();

        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;  //EMPTY_ITEM

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
        {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + inputSlotCount, false))
                return ItemStack.EMPTY;  // EMPTY_ITEM
        }
        else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + inputSlotCount + outputSlotCount)
        {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
                return ItemStack.EMPTY;
        }
        else
        {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, getBlock());
    }

    protected void addPlayerInventory(Inventory playerInventory)
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory)
    {
        for(int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return !slot.isFake();
    }

    protected abstract Block getBlock();
    protected abstract int getInputSlotCount();
    protected abstract int getOutputSlotCount();

    protected void clearItemStackHandler(Player player, ItemStackHandler itemStackHandler)
    {
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected())
        {
            for (int j = 0; j < itemStackHandler.getSlots(); j++)
            {
                ItemStack itemStack = itemStackHandler.getStackInSlot(j);
                if(!itemStack.isEmpty())
                {
                    player.drop(itemStack, false);
                    itemStackHandler.setStackInSlot(j, ItemStack.EMPTY);
                }
            }
        }
        else
        {
            for (int i = 0; i < itemStackHandler.getSlots(); i++)
            {
                ItemStack itemStack = itemStackHandler.getStackInSlot(i);
                if(!itemStack.isEmpty())
                {
                    Inventory inventory = player.getInventory();
                    if (inventory.player instanceof ServerPlayer)
                    {
                        inventory.placeItemBackInInventory(itemStack);
                        itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
