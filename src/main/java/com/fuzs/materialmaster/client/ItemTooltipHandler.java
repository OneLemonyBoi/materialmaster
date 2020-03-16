package com.fuzs.materialmaster.client;

import com.fuzs.materialmaster.core.PropertySyncManager;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
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
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class ItemTooltipHandler {

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

                if (player != null && !stats.isEmpty()) {

                    stats.replaceAll((key, value) -> {

                        IAttributeInstance attributeInstance = player.getAttributes().getAttributeInstanceByName(key);
                        return attributeInstance != null ? value + attributeInstance.getBaseValue() : value;
                    });

                    if (stats.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {

                        stats.merge(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), (double) EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED), Double::sum);
                    }

                    for (Map.Entry<String, Double> entry : stats.entrySet()) {

                        tooltip.add(realStart++, (new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.equals." + AttributeModifier.Operation.ADDITION.getId(), ItemStack.DECIMALFORMAT.format(entry.getValue()), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN));
                    }
                }
            }
        }
    }

}
