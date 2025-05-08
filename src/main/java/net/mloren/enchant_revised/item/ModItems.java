package net.mloren.enchant_revised.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.item.custom.EmptyEnchantedBook;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MainMod.MOD_ID);

    public static final DeferredItem<Item> EMPTY_ENCHANTED_BOOK = ITEMS.register("empty_enchanted_book",
            () -> new EmptyEnchantedBook(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
