package onelemonyboi.materialmaster.api;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaterialMasterReference {

    public static final String MODID = "materialmaster";
    public static final String NAME = "Material Master";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final String REGISTER_SYNC_PROVIDER = "register_sync_provider";
    public static final String REGISTER_CONFIG_PROVIDER = "register_config_provider";
    public static final String RETURN_CONFIG_EVENT = "return_config_event";

    public static final IAttribute ATTACK_REACH = new RangedAttribute(null,  MODID + ".attackReach", 5.0, 0.0, 1024.0).setShouldWatch(true);

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityConstructing(final EntityEvent.EntityConstructing evt) {

        if (evt.getEntity() instanceof PlayerEntity) {

            ((PlayerEntity) evt.getEntity()).getAttributes().registerAttribute(ATTACK_REACH);
        }
    }

}
