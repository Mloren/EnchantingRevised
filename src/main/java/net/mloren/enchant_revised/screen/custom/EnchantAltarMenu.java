package net.mloren.enchant_revised.screen.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.screen.ModMenuTypes;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class EnchantAltarMenu extends AbstractContainerMenu
{
    static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    public final EnchantAltarBlockEntity blockEntity;
    private final Level level;
    public final ItemStackHandler itemStackHandler;

    private static final int FIRST_SLOT_ID = 36; //To skip over 36 player inventory slots
    private static final int LAPIS_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    public EnchantAltarMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnchantAltarMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity)
    {
        super(ModMenuTypes.ENCHANT_ALTAR_MENU.get(), containerId);
        this.blockEntity = ((EnchantAltarBlockEntity) blockEntity);
        this.level = playerInventory.player.level();
        this.itemStackHandler = this.blockEntity.itemStackHandler;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, LAPIS_SLOT, 27, 53) {
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(Items.LAPIS_LAZULI);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_LAPIS_LAZULI);
            }
        });
        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, OUTPUT_SLOT, 130, 35));

        //addDataSlots(data);
    }
    
//    @Override
//    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player)
//    {
//        if(slotId < FIRST_SLOT_ID || level.isClientSide())
//        {
//            super.clicked(slotId, dragType, clickTypeIn, player);
//            return;
//        }
//
//        ItemStack lapisSlot = itemStackHandler.getStackInSlot(LAPIS_SLOT);
//        ItemStack outputSlot = itemStackHandler.getStackInSlot(OUTPUT_SLOT);
//        if(slotId == FIRST_SLOT_ID + LAPIS_SLOT)
//        {
//
//            if(!lapisSlot.isEmpty() && lapisSlot.is(Items.LAPIS_LAZULI))
//            {
//                if(outputSlot.isEmpty())
//                {
//                    itemStackHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(Items.LAPIS_BLOCK, 1));
//                }
//            }
//            else
//            {
//                if(!outputSlot.isEmpty())
//                {
//                    itemStackHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
//                }
//            }
//        }
//        else if(slotId == FIRST_SLOT_ID + OUTPUT_SLOT)
//        {
//            if(outputSlot.isEmpty())
//            {
//                if (!lapisSlot.isEmpty()) {
//                    itemStackHandler.setStackInSlot(LAPIS_SLOT, ItemStack.EMPTY);
//                }
//            }
//        }
//
//        super.clicked(slotId, dragType, clickTypeIn, player);
//    }

//    public boolean isCrafting()
//    {
//        return data.get(0) > 0;
//    }

//    public int getScaledArrowProgress()
//    {
//        int progress = this.data.get(0);
//        int maxProgress = this.data.get(1);
//        int arrowPixelSize = 24;
//
//        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
//    }

//    @Override
//    public void slotsChanged(Container inventory)
//    {
//        if (inventory == this.enchantSlots) {
//            ItemStack itemstack = inventory.getItem(0);
//            ItemStack itemstack2 = inventory.getItem(1);
//            if (!itemstack.isEmpty() && itemstack.is(Items.LAPIS_LAZULI) && itemstack2.isEmpty()) {
//                inventory.setItem(1, new ItemStack(Items.LAPIS_BLOCK, itemstack.getCount()));
//            }
//        }
//
//        super.slotsChanged(inventory);
//    }

//    @Override
//    public void removed(Player player) {
//        super.removed(player);
//        ItemStack itemstack2 = this.enchantSlots.getItem(1);
//        if(!itemstack2.isEmpty())
//            this.enchantSlots.removeItemNoUpdate(1);
//        this.clearContainer(player, this.enchantSlots);
//    }


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

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex)
    {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;  //EMPTY_ITEM

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
        {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false))
            {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        }
        else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT)
        {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0)
        {
            sourceSlot.set(ItemStack.EMPTY);
        }
        else
        {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.ENCHANT_ALTAR.get());
    }

    private void addPlayerInventory(Inventory playerInventory)
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory)
    {
        for(int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
