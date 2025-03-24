package net.mloren.enchant_revised.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.screen.custom.EnchantAltarMenu;
import net.mloren.enchant_revised.screen.custom.PedestalMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MainMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PedestalMenu>> PEDESTAL_MENU =
            registerMenuType("pedestal_menu", PedestalMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EnchantAltarMenu>> ENCHANT_ALTAR_MENU =
            registerMenuType("enchant_altar_menu", EnchantAltarMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory)
    {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus)
    {
        MENUS.register(eventBus);
    }
}
