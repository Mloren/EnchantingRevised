package net.mloren.enchant_revised.screen.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipeInput;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.screen.ModMenuTypes;
import net.mloren.enchant_revised.util.Constants;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantAltarMenu extends ItemCombinerMenu
{
    static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    public final EnchantAltarBlockEntity blockEntity;
    private final Level level;
    //public final ItemStackHandler itemStackHandler;

    private RecipeHolder<EnchantAltarRecipe> selectedRecipe;

    public EnchantAltarMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnchantAltarMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, BlockEntity blockEntity)
    {
        super(ModMenuTypes.ENCHANT_ALTAR_MENU.get(), containerId, playerInventory, access);
        this.blockEntity = ((EnchantAltarBlockEntity) blockEntity);
        this.level = playerInventory.player.level();
        //this.itemStackHandler = this.blockEntity.itemStackHandler;

        //addPlayerInventory(playerInventory);
        //addPlayerHotbar(playerInventory);
        //addSlots();
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions()
    {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 8, 48, itemStack -> { return true; })
                .withSlot(1, 26, 48, itemStack -> { return true; })
                .withSlot(2, 44, 48, itemStack -> { return true; })
                .withResultSlot(3, 98, 48)
                .build();
    }

    @Override
    protected boolean isValidBlock(BlockState state)
    {
        return state.is(ModBlocks.ENCHANT_ALTAR.get());
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack)
    {
        return this.selectedRecipe != null && this.selectedRecipe.value().matches(this.createRecipeInput(), this.level);
    }

    @Override
    protected void onTake(Player player, ItemStack stack)
    {
        stack.onCraftedBy(player.level(), player, stack.getCount());
        this.resultSlots.awardUsedRecipes(player, this.getRelevantItems());
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.shrinkStackInSlot(2);
        this.access.execute((p_40263_, p_40264_) -> p_40263_.levelEvent(1044, p_40264_, 0));
    }

    private List<ItemStack> getRelevantItems()
    {
        return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
    }

    private EnchantAltarRecipeInput createRecipeInput()
    {
        return new EnchantAltarRecipeInput(this.inputSlots.getItem(Constants.PRIMARY_INGREDIENT_SLOT));
    }

    private void shrinkStackInSlot(int index)
    {
        ItemStack itemstack = this.inputSlots.getItem(index);
        if (!itemstack.isEmpty())
        {
            itemstack.shrink(1);
            this.inputSlots.setItem(index, itemstack);
        }
    }

    @Override
    public void createResult()
    {
        EnchantAltarRecipeInput enchantAltarRecipeInput = this.createRecipeInput();
        List<RecipeHolder<EnchantAltarRecipe>> list = this.level.getRecipeManager().getRecipesFor(ModRecipes.ENCHANT_ALTAR_TYPE.get(), enchantAltarRecipeInput, this.level);
        if (list.isEmpty())
        {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else
        {
            RecipeHolder<EnchantAltarRecipe> recipeholder = list.get(0);
            ItemStack itemstack = recipeholder.value().assemble(enchantAltarRecipeInput, this.level.registryAccess());
            if (itemstack.isItemEnabled(this.level.enabledFeatures()))
            {
                this.selectedRecipe = recipeholder;
                this.resultSlots.setRecipeUsed(recipeholder);
                this.resultSlots.setItem(0, itemstack);
                this.resultSlots.setChanged();
            }
        }
    }

//    private void addSlots()
//    {
//        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.LAPIS_SLOT, 27, 53)
//        {
//            public boolean mayPlace(@NotNull ItemStack itemStack)
//            {
//                return itemStack.is(Items.LAPIS_LAZULI);
//            }
//
//            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
//            {
//                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_LAPIS_LAZULI);
//            }
//        });
//
//        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.PRIMARY_INGREDIENT_SLOT, 27, 35));
//        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.SECONDARY_INGREDIENT_SLOT, 27, 17));
//        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.TARGET_ITEM_SLOT, 72, 35));
//
//        this.addSlot(new SlotItemHandler(this.blockEntity.itemStackHandler, Constants.OUTPUT_SLOT, 130, 35));
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
