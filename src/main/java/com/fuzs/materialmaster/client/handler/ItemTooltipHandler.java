package com.fuzs.materialmaster.client.handler;

import com.fuzs.materialmaster.common.handler.RegisterAttributeHandler;
import com.fuzs.materialmaster.core.PropertySyncManager;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ItemTooltipHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent evt) {

        List<ITextComponent> tooltip = evt.getToolTip();
        PlayerEntity player = evt.getPlayer();
        ItemStack stack = evt.getItemStack();

        // respect hide flag tag
        if (stack.hasTag() && stack.getTag().contains("HideFlags", 99)) {

            if ((stack.getTag().getInt("HideFlags") & 2) != 0) {

                return;
            }
        }

        int start = -1, end = -1;

        // find vanilla attribute list start
        for (int j = 0; j < tooltip.size(); j++) {

            ITextComponent component = tooltip.get(j);
            if (component instanceof TranslationTextComponent && ((TranslationTextComponent) component).getKey().contains("item.modifiers.")) {

                start = --j;
                break;
            }
        }

        // find vanilla attribute list end
        for (int j = 0; j < tooltip.size(); j++) {

            ITextComponent component = tooltip.get(j);
            if (component.toString().contains("attribute.modifier.")) {

                end = j;
            }
        }

        // remove vanilla attribute list
        if (start != -1) {

            tooltip.subList(start, end != -1 ? ++end : tooltip.size()).clear();
        } else {

            start = tooltip.size();
        }

        // add own attributes
        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {

            Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslottype);
            if (!multimap.isEmpty()) {

                tooltip.add(start++, new StringTextComponent(""));
                tooltip.add(start++, (new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).applyTextStyle(TextFormatting.GRAY));

                int realStart = start;
                // use tree map for default sorting
                Map<String, Double> stats = Maps.newTreeMap();
                for (Map.Entry<String, AttributeModifier> entry : multimap.entries()) {

                    AttributeModifier attributemodifier = entry.getValue();
                    double amount = attributemodifier.getAmount();
                    if (equipmentslottype == EquipmentSlotType.MAINHAND && player != null && PropertySyncManager.getInstance().isKnownAttributeId(attributemodifier.getID())) {

                        // collect known attributes in separate map for adding later collectively
                        stats.merge(entry.getKey(), amount, Double::sum);
                    } else {

                        if (attributemodifier.getOperation() != AttributeModifier.Operation.ADDITION) {

                            amount *= 100.0;
                        }

                        if (amount > 0.0) {

                            tooltip.add(start++, (new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(amount), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.BLUE));
                        } else if (amount < 0.0) {

                            amount *= -1.0;
                            tooltip.add(start++, (new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(amount), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.RED));
                        }
                    }

                }

                if (!stats.isEmpty()) {

                    if (player != null) {

                        stats.replaceAll((name, value) -> {

                            IAttributeInstance attributeInstance = player.getAttributes().getAttributeInstanceByName(name);
                            if (attributeInstance != null) {

                                value += attributeInstance.getBaseValue();
                                // reach attributes are handled differently depending on game mode
                                if (!player.abilities.isCreativeMode && (attributeInstance.getAttribute() == PlayerEntity.REACH_DISTANCE
                                        || attributeInstance.getAttribute() == RegisterAttributeHandler.ATTACK_REACH)) {

                                    value -= 0.5;
                                }

                                if (attributeInstance.getAttribute() == SharedMonsterAttributes.ATTACK_DAMAGE) {

                                    value += EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED);
                                }
                            }

                            return value;
                        });
                    }

                    for (Map.Entry<String, Double> entry : stats.entrySet()) {

                        tooltip.add(realStart++, (new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.equals." + AttributeModifier.Operation.ADDITION.getId(), ItemStack.DECIMALFORMAT.format(entry.getValue()), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN));
                        // need to update start as well as everything is shifted by inserting somewhere in the middle
                        // value might be needed again for another equipment slot
                        start++;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS || this.mc.player == null || this.mc.playerController == null) {

            return;
        }

        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.thirdPersonView == 0) {

            if (this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR || this.mc.ingameGUI.isTargetNamedMenuProvider(this.mc.objectMouseOver)) {

                if (!gamesettings.showDebugInfo || gamesettings.hideGUI || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo) {

                    if (gamesettings.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {

                        float f = this.mc.player.getCooledAttackStrength(0.0F);
                        if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof LivingEntity && f >= 1.0F) {

                            // show attack indicator for everything that doesn't have default attack speed (4.0)
                            if (this.mc.player.getCooldownPeriod() < 5.0F && this.mc.pointedEntity.isAlive()) {

                                this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                                RenderSystem.enableBlend();
                                RenderSystem.enableAlphaTest();
                                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                                int k = this.mc.getMainWindow().getScaledWidth() / 2 - 8;
                                int j = this.mc.getMainWindow().getScaledHeight() / 2 - 7 + 16;
                                AbstractGui.blit(k, j, 68, 94, 16, 16, 256, 256);
                            }
                        }
                    }
                }
            }
        }
    }

}
