package onelemonyboi.materialmaster.api.provider;

import onelemonyboi.materialmaster.api.builder.AttributeMapBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPropertyProvider {

    /**
     * @return if this provider is enabled, should ideally be linked to a config option
     */
    public abstract boolean isEnabled();

    /**
     * @return name of the mod this provider comes from
     */
    public abstract String getName();

    /**
     * @return priority of this provider
     */
    public abstract int getPriority();

    /**
     * @return attribute map for each item, use {@link AttributeMapBuilder} to create this map
     */
    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        return Maps.newHashMap();
    }

    /**
     * @return stack size map
     */
    public Map<Item, Double> getStackSize() {

        return Maps.newHashMap();
    }

    /**
     * @return durability map
     */
    public Map<Item, Double> getDurability() {

        return Maps.newHashMap();
    }

    /**
     * @return dig speed map
     */
    public Map<Item, Double> getDigSpeed() {

        return Maps.newHashMap();
    }

    /**
     * @return harvest level map
     */
    public Map<Item, Double> getHarvestLevel() {

        return Maps.newHashMap();
    }

    /**
     * @return enchantability map
     */
    public Map<Item, Double> getEnchantability() {

        return Maps.newHashMap();
    }

    /**
     * @return single modifier id needed for registering attributes from this provider
     */
    protected abstract UUID getMainhandModifierId();

    /**
     * @return four armor modifier ids needed for registering attributes from this provider
     */
    protected abstract UUID[] getArmorModifierIds();

    /**
     * @return all attribute modifier ids used by this provider for properly displaying attributes on item tooltips
     */
    public final Set<UUID> getAttributeIds() {

        Set<UUID> ids = Sets.newHashSet(this.getArmorModifierIds());
        ids.add(this.getMainhandModifierId());

        return ids;
    }

}
