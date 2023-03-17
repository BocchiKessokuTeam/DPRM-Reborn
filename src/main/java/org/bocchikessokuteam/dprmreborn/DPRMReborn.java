package org.bocchikessokuteam.dprmreborn;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bocchikessokuteam.dprmreborn.hander.Config;
import org.bocchikessokuteam.dprmreborn.hander.Registry;

@Mod(DPRMReborn.MODID)
public class DPRMReborn {
    public static final String MODID = "dprmreborn";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public DPRMReborn(){
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::setup);
        Registry.CONTAINERS_TYPE.register(MOD_BUS);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }
    public void setup(final FMLCommonSetupEvent event){

    }

}
