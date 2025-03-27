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
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.screen.ModMenuTypes;
import net.mloren.enchant_revised.util.Constants;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class EnchantAltarMenu extends AbstractContainerMenu
{
    static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    public final EnchantAltarBlockEntity blockEntity;
    private final Level level;
    public final ItemStackHandler itemStackHandler;

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
        addSlots();
    }

    private void addSlots()
    {
        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.LAPIS_SLOT, 27, 53)
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

        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.PRIMARY_INGREDIENT_SLOT, 27, 35));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.SECONDARY_INGREDIENT_SLOT, 27, 17));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.TARGET_ITEM_SLOT, 72, 35));

        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.OUTPUT_SLOT, 130, 35)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack)
            {
                return false;
            }

            @Override
            public ItemStack safeTake(int count, int decrement, Player player)
            {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack safeInsert(ItemStack stack, int increment)
            {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean isFake()
            {
                return true;
            }
        });
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

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = Constants.TOTAL_SLOT_COUNT;  // must be the number of slots you have!

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex)
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
                    + (TE_INVENTORY_SLOT_COUNT - 1), false))
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
    public boolean stillValid(@NotNull Player player)
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
