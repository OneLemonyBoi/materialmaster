package com.fuzs.materialmaster.common;

import com.fuzs.materialmaster.core.PropertySyncManager;
import com.fuzs.materialmaster.core.property.SimpleItemProperty;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DigSpeedHandler {

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onBreakSpeed(final PlayerEvent.BreakSpeed evt) {

        PlayerEntity player = evt.getPlayer();
        float f = player.inventory.getDestroySpeed(evt.getState());
        float ratio = 1.0F;

        ItemStack itemstack = player.getHeldItemMainhand();
        if (!itemstack.isEmpty()) {

            float speed = ((SimpleItemProperty) PropertySyncManager.getInstance().getProperty("dig_speed")).getValue(itemstack.getItem(), (double) f).floatValue();
            // won't apply when the tool is not effective on the current block, but will allow disabling dig speed for any block
            if (speed == 0.0F || f > 1.0F) {

                ratio = speed / f;
                f = speed;
            }

            if (f > 1.0F) {

                int level = EnchantmentHelper.getEfficiencyModifier(player);
                if (level > 0) {

                    f += (float) (level * level + 1);
                }
            }
        }

        if (EffectUtils.hasMiningSpeedup(player)) {

            f *= 1.0F + (float) (EffectUtils.getMiningSpeedup(player) + 1) * 0.2F;
        }

        if (player.isPotionActive(Effects.MINING_FATIGUE)) {

            switch(player.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {

                case 0:
                    f *= 0.3F;
                    break;
                case 1:
                    f *= 0.09F;
                    break;
                case 2:
                    f *= 0.0027F;
                    break;
                default:
                    f *= 8.1E-4F;
            }
        }

        if (player.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {

            f /= 5.0F;
        }

        if (!player.onGround) {

            f /= 5.0F;
        }

        // scale previous changes for attempted mod compatibility
        evt.setNewSpeed((evt.getNewSpeed() - evt.getOriginalSpeed()) * ratio + f);
    }

}
