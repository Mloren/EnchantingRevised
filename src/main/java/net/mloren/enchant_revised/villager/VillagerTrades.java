package net.mloren.enchant_revised.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.mloren.enchant_revised.config.Config;
import net.mloren.enchant_revised.item.ModItems;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;

public class VillagerTrades
{
    public static void addCustomTrades(VillagerTradesEvent event)
    {
        if(event.getType() == VillagerProfession.LIBRARIAN)
        {
            Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> villagerTrades = event.getTrades();

            //Removes all enchanted book trades from the librarian
            if(Config.COMMON.disableEnchantedBookTrade.get())
            {
                for (int i = 0; i < villagerTrades.size(); ++i)
                {
                    List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing> tradesByLevel = villagerTrades.get(i);
                    if (tradesByLevel != null)
                    {
                        for (int j = tradesByLevel.size() - 1; j >= 0; --j)
                        {
                            net.minecraft.world.entity.npc.VillagerTrades.ItemListing trade = tradesByLevel.get(j);
                            if (trade.getClass().getName().equals(net.minecraft.world.entity.npc.VillagerTrades.class.getName() + "$EnchantBookForEmeralds"))
                                tradesByLevel.remove(j);
                        }
                    }
                }
            }

            //Add lapis trade to librarian
            if(Config.COMMON.enableLapisTrade.get())
            {
                villagerTrades.get(2).add((trader, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 1),
                        new ItemStack(Items.LAPIS_LAZULI, 1), 12, 5, 0.05f));
            }

            //Add empty enchanted book trade to the librarian
            if(Config.COMMON.enableEmptyBookTrade.get())
            {
                villagerTrades.get(4).add((trader, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, random.nextIntBetweenInclusive(20, 64)),
                        new ItemStack(ModItems.EMPTY_ENCHANTED_BOOK.get(), 1), 3, 15, 0.2f));
            }
        }
        else if(event.getType() == VillagerProfession.CLERIC)
        {
            Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> villagerTrades = event.getTrades();

            //Removes bottle o' enchanting trades from the cleric
            if(Config.COMMON.disableBottleOfEnchant.get())
            {
                if(villagerTrades.containsKey(5))
                {
                    List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing> tradesByLevel = villagerTrades.get(5);
                    if(tradesByLevel.size() >= 2)
                        tradesByLevel.remove(1);
                }
            }
        }
    }
}
