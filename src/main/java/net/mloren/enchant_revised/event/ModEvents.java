package net.mloren.enchant_revised.event;

import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.villager.VillagerTrades;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents
{
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event)
    {
        VillagerTrades.addCustomTrades(event);
    }
}
