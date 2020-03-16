package com.fuzs.materialmaster.common;

import com.fuzs.materialmaster.config.ConfigBuildHandler;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterAttributeHandler {

    public static final IAttribute ATTACK_REACH = new RangedAttribute(null, "generic.attackReach", 5.0, 0.0, 1024.0).setShouldWatch(true);

    @SuppressWarnings("unused")
    // never cancel this or NullPointerExceptions will occur
    @SubscribeEvent(receiveCanceled = true)
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntity();
            player.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(ConfigBuildHandler.DEFAULT_KNOCKBACK_RESISTANCE.get());
            player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_DAMAGE.get());
            player.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_KNOCKBACK.get());
            player.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_SPEED.get());
            player.getAttribute(PlayerEntity.REACH_DISTANCE).setBaseValue(ConfigBuildHandler.DEFAULT_REACH_DISTANCE.get());
            player.getAttributes().registerAttribute(ATTACK_REACH).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_REACH.get());
            player.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ConfigBuildHandler.DEFAULT_ARMOR.get());
            player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(ConfigBuildHandler.DEFAULT_ARMOR_TOUGHNESS.get());
        }
    }

}
