package net.mloren.enchant_revised.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.item.ModItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class OnAnvilUpdate
{
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event)
    {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.is(Items.ENCHANTED_BOOK) && right.is(ModItems.EMPTY_ENCHANTED_BOOK))
        {
            event.setOutput(left.copy());
            event.setMaterialCost(1);
            event.setCost(0);
        }
    }
}
