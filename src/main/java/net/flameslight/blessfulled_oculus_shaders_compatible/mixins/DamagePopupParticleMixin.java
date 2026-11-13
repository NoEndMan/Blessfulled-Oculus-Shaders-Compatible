package net.flameslight.blessfulled_oculus_shaders_compatible.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.flameslight.blessfulled_oculus_shaders_compatible.handlers.PopupTrackingHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.aqutheseal.blessfulled.particle.DamagePopupParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wraps every Blessfulled damage popup particle with a client side only entity when it is created, and stops the particle from
 * drawing itself once it is wrapped. A popup whose entity failed to spawn keeps drawing itself as before.
 */
@Mixin(DamagePopupParticle.class)
public abstract class DamagePopupParticleMixin {
    @Unique
    private boolean blessfulledShaderCombability$isWrapped;

    /**
     * Spawns the wrapping entity once the particle is fully built.
     *
     * @param level      the client level
     * @param x          the spawn x
     * @param y          the spawn y
     * @param z          the spawn z
     * @param damage     the popup damage
     * @param popupType  the popup type
     * @param ci         the callback info
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void blessfulledShaderCombability$wrapPopup(ClientLevel level, double x, double y, double z, float damage, int popupType,
                                                       CallbackInfo ci) {
        blessfulledShaderCombability$isWrapped = PopupTrackingHandler.spawn(level, (DamagePopupParticle) (Object) this, x, y, z);
    }

    /**
     * Cancels the particle's own drawing when its entity draws it.
     *
     * @param buffer      the particle vertex consumer
     * @param camera      the camera
     * @param partialTick the partial tick
     * @param ci          the callback info
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void blessfulledShaderCombability$skipParticleRender(VertexConsumer buffer, Camera camera, float partialTick, CallbackInfo ci) {
        if (blessfulledShaderCombability$isWrapped) {
            ci.cancel();
        }
    }
}
