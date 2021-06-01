package onelemonyboi.materialmaster.common.handler;

import onelemonyboi.materialmaster.api.MaterialMasterReference;
import onelemonyboi.materialmaster.config.ConfigBuildHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterAttributeHandler {

    public static final double REACH_DISTANCE_CREATIVE_BOOST = 0.5;
    public static final double ATTACK_REACH_CREATIVE_BOOST = 2.0;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityConstructing(final EntityEvent.EntityConstructing evt) {

        if (evt.getEntity() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntity();
            player.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntity();
            player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ConfigBuildHandler.DEFAULT_MAX_HEALTH.get());
            player.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(ConfigBuildHandler.DEFAULT_KNOCKBACK_RESISTANCE.get());
            player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ConfigBuildHandler.DEFAULT_MOVEMENT_SPEED.get());
            player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_DAMAGE.get());
            player.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_KNOCKBACK.get());
            player.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_SPEED.get());
            player.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ConfigBuildHandler.DEFAULT_ARMOR.get());
            player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(ConfigBuildHandler.DEFAULT_ARMOR_TOUGHNESS.get());
            player.getAttribute(SharedMonsterAttributes.LUCK).setBaseValue(ConfigBuildHandler.DEFAULT_LUCK.get());
            player.getAttribute(LivingEntity.SWIM_SPEED).setBaseValue(ConfigBuildHandler.DEFAULT_SWIM_SPEED.get());
            player.getAttribute(LivingEntity.ENTITY_GRAVITY).setBaseValue(ConfigBuildHandler.DEFAULT_GRAVITY.get());
            // default value is for creative mode
            player.getAttribute(PlayerEntity.REACH_DISTANCE).setBaseValue(ConfigBuildHandler.DEFAULT_REACH_DISTANCE.get() + RegisterAttributeHandler.REACH_DISTANCE_CREATIVE_BOOST);
            player.getAttribute(MaterialMasterReference.ATTACK_REACH).setBaseValue(ConfigBuildHandler.DEFAULT_ATTACK_REACH.get() + RegisterAttributeHandler.ATTACK_REACH_CREATIVE_BOOST);
        }
    }

}
