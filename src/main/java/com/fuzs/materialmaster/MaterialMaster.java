package com.fuzs.materialmaster;

import com.fuzs.materialmaster.api.MaterialMasterReference;
import com.fuzs.materialmaster.api.PropertyProviderUtils;
import com.fuzs.materialmaster.api.core.ModSyncManager;
import com.fuzs.materialmaster.api.core.PropertySyncManager;
import com.fuzs.materialmaster.client.handler.AttackIndicatorHandler;
import com.fuzs.materialmaster.client.handler.ItemTooltipHandler;
import com.fuzs.materialmaster.common.handler.DigSpeedHandler;
import com.fuzs.materialmaster.common.handler.RegisterAttributeHandler;
import com.fuzs.materialmaster.config.ConfigBuildHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(MaterialMasterReference.MODID)
public class MaterialMaster {

    public static final String MODID = MaterialMasterReference.MODID;
    public static final String NAME = MaterialMasterReference.NAME;
    public static final Logger LOGGER = MaterialMasterReference.LOGGER;

    private static final EnumMap<ModConfig.Type, ModConfig> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public MaterialMaster() {

        // general setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModProcess);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);

        // config setup
        registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC);
        CONFIGS.values().forEach(ModLoadingContext.get().getActiveContainer()::addConfig);
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new RegisterAttributeHandler());
        MinecraftForge.EVENT_BUS.register(new MaterialMasterReference());
        MinecraftForge.EVENT_BUS.register(new DigSpeedHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new ItemTooltipHandler());
        MinecraftForge.EVENT_BUS.register(new AttackIndicatorHandler());
    }

    private void onInterModEnqueue(final InterModEnqueueEvent evt) {

        // register mod to be searched for sync providers
        InterModComms.sendTo(MaterialMaster.MODID, MaterialMasterReference.MODID, MaterialMasterReference.REGISTER_SYNC_PROVIDER,
                () -> null);
    }

    private void onInterModProcess(final InterModProcessEvent evt) {

        // store stream as set as it'll be consumed otherwise
        Set<InterModComms.IMCMessage> messages = InterModComms.getMessages(MaterialMasterReference.MODID).collect(Collectors.toSet());

        // check for registered sync providers types
        messages.stream()
                .filter(message -> message.getMethod().equals(MaterialMasterReference.REGISTER_SYNC_PROVIDER))
                .forEach(message -> {

                    PropertyProviderUtils.registerSyncProviderMod(message.getSenderModId(), null);
                    InterModComms.sendTo(MaterialMaster.MODID, message.getSenderModId(), MaterialMasterReference.RETURN_CONFIG_EVENT,
                            () -> (Consumer<ModConfig.ConfigReloading>) PropertySyncManager.getInstance()::onModConfig);
                });

        // check for registered config provider fields
        messages.stream()
                .filter(message -> message.getMethod().equals(MaterialMasterReference.REGISTER_CONFIG_PROVIDER))
                .forEach(message -> {

                    PropertyProviderUtils.registerSyncProviderMod(message.getSenderModId(), (ModConfig) message.getMessageSupplier().get());
                    InterModComms.sendTo(MaterialMaster.MODID, message.getSenderModId(), MaterialMasterReference.RETURN_CONFIG_EVENT,
                            () -> (Consumer<ModConfig.ConfigReloading>) ModSyncManager.getInstance()::onModConfig);
                });
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        // add listener to config events
        InterModComms.getMessages(MaterialMaster.MODID)
                .filter(message -> message.getMethod().equals(MaterialMasterReference.RETURN_CONFIG_EVENT))
                .map(message -> (Consumer<? extends Event>) message.getMessageSupplier().get())
                .forEach(FMLJavaModLoadingContext.get().getModEventBus()::addListener);

        ModSyncManager.getInstance().processSyncProviders();
    }

    private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {

        CONFIGS.put(type, new ModConfig(type, spec, ModLoadingContext.get().getActiveContainer()));
    }

}
