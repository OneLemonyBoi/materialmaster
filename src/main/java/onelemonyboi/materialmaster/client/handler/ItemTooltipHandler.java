package onelemonyboi.materialmaster.client.handler;

import onelemonyboi.materialmaster.api.MaterialMasterReference;
import onelemonyboi.materialmaster.common.handler.RegisterAttributeHandler;
import onelemonyboi.materialmaster.api.core.PropertySyncManager;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ItemTooltipHandler {

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent evt) {

        List<ITextComponent> tooltip = evt.getToolTip();
        PlayerEntity player = evt.getPlayer();
        ItemStack stack = evt.getItemStack();

        // respect hide flag tag
        boolean flag = stack.hasTag() && stack.getTag().contains("HideFlags", 99)
                && (stack.getTag().getInt("HideFlags") & 2) != 0;
        if (flag || player == null) {

            return;
        }

        int start = this.getStartingLine(tooltip);
        if (start == -1) {

            // search should always be successful, meaning another mod is messing around so don't do anything
            return;
        }

        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {

            Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslottype);
            if (!multimap.isEmpty()) {

                start = this.addModifiersForSlot(tooltip, player, stack, start, equipmentslottype, multimap);
            }
        }
    }

    private int getStartingLine(List<ITextComponent> tooltip) {

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

        if (start != -1) {

            if (end != -1) {

                // remove vanilla attribute list
                tooltip.subList(start, ++end).clear();
            } else {

                // another mod is messing around, so don't do anything
                start = -1;
            }
        }

        return start;
    }

    private int addModifiersForSlot(List<ITextComponent> tooltip, PlayerEntity player, ItemStack stack, int start, EquipmentSlotType equipmentslottype, Multimap<String, AttributeModifier> multimap) {

        tooltip.add(start++, new StringTextComponent(""));
        tooltip.add(start++, (new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).applyTextStyle(TextFormatting.GRAY));

        // use tree map for default sorting
        Map<String, Double> stats = Maps.newTreeMap();
        // insert known attributes at the top
        int knownAttributesStart = start;
        start = this.processAttributeMap(tooltip, start, equipmentslottype == EquipmentSlotType.MAINHAND, multimap, stats);
        if (!stats.isEmpty()) {

            this.adjustStatValues(stats, stack, player);
            for (Map.Entry<String, Double> entry : stats.entrySet()) {

                tooltip.add(knownAttributesStart++, (new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.equals." + AttributeModifier.Operation.ADDITION.getId(), ItemStack.DECIMALFORMAT.format(entry.getValue()), new TranslationTextComponent("attribute.name." + entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN));
                // need to update start as well as everything is shifted by inserting somewhere in the middle
                // value might be used again for another equipment slot
                start++;
            }
        }

        return start;
    }

    private int processAttributeMap(List<ITextComponent> tooltip, int start, boolean mainhand, Multimap<String, AttributeModifier> multimap, Map<String, Double> stats) {

        for (Map.Entry<String, AttributeModifier> entry : multimap.entries()) {

            AttributeModifier attributemodifier = entry.getValue();
            double amount = attributemodifier.getAmount();
            if (mainhand && PropertySyncManager.getInstance().isKnownAttributeId(attributemodifier.getID())) {

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

        return start;
    }

    private void adjustStatValues(Map<String, Double> stats, ItemStack stack, PlayerEntity player) {

        stats.replaceAll((name, value) -> {

            IAttributeInstance attributeInstance = player.getAttributes().getAttributeInstanceByName(name);
            if (attributeInstance != null) {

                value += attributeInstance.getBaseValue();

                if (attributeInstance.getAttribute() == SharedMonsterAttributes.ATTACK_DAMAGE) {

                    value += EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED);
                } else if (!player.abilities.isCreativeMode) {

                    // reach attributes are handled differently depending on game mode
                    value -= attributeInstance.getAttribute() == PlayerEntity.REACH_DISTANCE ? RegisterAttributeHandler.REACH_DISTANCE_CREATIVE_BOOST : 0.0;
                    value -= attributeInstance.getAttribute() == MaterialMasterReference.ATTACK_REACH ? RegisterAttributeHandler.ATTACK_REACH_CREATIVE_BOOST : 0.0;
                }
            }

            return value;
        });
    }

}
