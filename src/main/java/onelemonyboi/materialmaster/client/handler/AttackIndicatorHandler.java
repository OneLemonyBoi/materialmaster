package onelemonyboi.materialmaster.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AttackIndicatorHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS || this.mc.player == null || this.mc.playerController == null) {

            return;
        }

        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.getPointOfView() == PointOfView.FIRST_PERSON) {

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
                                AbstractGui.blit(evt.getMatrixStack(), k, j, 68, 94, 16, 16, 256, 256);
                            }
                        }
                    }
                }
            }
        }
    }

}
