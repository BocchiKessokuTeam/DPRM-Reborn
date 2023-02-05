package org.bocchikessokuteam.dprmreborn.hander;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.bocchikessokuteam.dprmreborn.screen.*;
import org.bocchikessokuteam.dprmreborn.network.Networking;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHander {
    @SubscribeEvent
    public static void onClineSetupEvent(FMLClientSetupEvent event) {
        MenuScreens.register(Registry.craftingContainer.get(), CraftingScreen::new);
        MenuScreens.register(Registry.furnaceContainer.get(), FurnaceScreen::new);
        MenuScreens.register(Registry.stonecuttingContainer.get(), StonecuttingScreen::new);
        MenuScreens.register(Registry.smithingContainer.get(),SmithingScreen::new);
        MenuScreens.register(Registry.recipeListContainer.get(), RecipeListScreen::new);
    }
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        Networking.registerMessage();
    }

}
