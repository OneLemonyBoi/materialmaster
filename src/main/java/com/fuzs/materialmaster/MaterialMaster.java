package com.fuzs.materialmaster;

import com.fuzs.materialmaster.client.handler.ItemTooltipHandler;
import com.fuzs.materialmaster.common.handler.DigSpeedHandler;
import com.fuzs.materialmaster.common.handler.RegisterAttributeHandler;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import com.fuzs.materialmaster.core.PropertySyncManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(MaterialMaster.MODID)
public class MaterialMaster {

    public static final String MODID = "materialmaster";
    public static final String NAME = "Material Master";
    public static final Logger LOGGER = LogManager.getLogger(MaterialMaster.NAME);

    public MaterialMaster() {

        // general setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC, MODID + ".toml");
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(PropertySyncManager.getInstance()::onModConfig);
        MinecraftForge.EVENT_BUS.register(new RegisterAttributeHandler());
        MinecraftForge.EVENT_BUS.register(new DigSpeedHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new ItemTooltipHandler());
    }

}
