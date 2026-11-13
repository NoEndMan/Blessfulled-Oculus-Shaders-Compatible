package net.flameslight.blessfulled_oculus_shaders_compatible.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.flameslight.blessfulled_oculus_shaders_compatible.compat.OculusCompat;
import net.flameslight.blessfulled_oculus_shaders_compatible.mixins.ParticleAccessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.aqutheseal.blessfulled.BSClientConfig;
import org.aqutheseal.blessfulled.particle.DamagePopupParticle;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Draws a wrapped Blessfulled damage popup the same way the particle did (same position, animation, colors, outline and config),
 * but through the entity PoseStack and buffer source, which shader packs render correctly.
 */
public class DamagePopupEntityRenderer extends EntityRenderer<DamagePopupEntity> {
    /**
     * Builds the renderer.
     *
     * @param context the renderer provider context
     */
    public DamagePopupEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0F;
    }

    /**
     * Renders while the entity wraps a particle, at any distance, like the particle.
     *
     * @param entity  the popup entity
     * @param frustum the camera frustum
     * @param camX    the camera x
     * @param camY    the camera y
     * @param camZ    the camera z
     * @return true if the entity has a particle
     */
    @Override
    public boolean shouldRender(DamagePopupEntity entity, @NotNull Frustum frustum, double camX, double camY, double camZ) {
        return entity.getParticle() != null;
    }

    /**
     * Draws the damage number and the onomatopoeia like Blessfulled's renderCounter and renderText, from the entity's render cache.
     *
     * @param entity      the popup entity
     * @param entityYaw   the entity yaw, unused
     * @param partialTick the partial tick
     * @param poseStack   the pose stack, at the entity's position
     * @param buffer      the buffer source
     * @param packedLight the entity light, unused: popups are full bright
     */
    @Override
    public void render(DamagePopupEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight) {
        DamagePopupParticle particle = entity.getParticle();

        if (particle == null || OculusCompat.isRenderingShadowPass()) {
            return;
        }

        boolean showNumber = BSClientConfig.showExpression;
        ParticleAccessor fields = (ParticleAccessor) particle;
        int lifetime = particle.getLifetime();
        float age = fields.blessfulledShaderCombability$getAge() + partialTick;
        boolean showWord = BSClientConfig.showDamage && fields.blessfulledShaderCombability$getAge() <= lifetime - 5;

        if (!showNumber && !showWord) {
            return;
        }

        /*
            The entity moves before the particle in a client tick, so it lags one tick behind. Offset to the particle's own
            interpolated position, so the popup is drawn exactly where the particle would have drawn it.
         */
        double offsetX = Mth.lerp(partialTick, fields.blessfulledShaderCombability$getXo(), fields.blessfulledShaderCombability$getX())
                - Mth.lerp(partialTick, entity.xo, entity.getX());
        double offsetY = Mth.lerp(partialTick, fields.blessfulledShaderCombability$getYo(), fields.blessfulledShaderCombability$getY())
                - Mth.lerp(partialTick, entity.yo, entity.getY());
        double offsetZ = Mth.lerp(partialTick, fields.blessfulledShaderCombability$getZo(), fields.blessfulledShaderCombability$getZ())
                - Mth.lerp(partialTick, entity.zo, entity.getZ());

        Font font = getFont();
        float sizeFactor = particle.getQuadSize(partialTick) * (float) BSClientConfig.popupSizeMultiplier;
        boolean outline = BSClientConfig.showOutline;

        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());

        if (showNumber) {
            entity.updateNumberText(font);
            float scale = DamagePopupParticle.easeOut(age, 0F, 0.001F, lifetime, (particle.shouldScale ? 250 : 200) * sizeFactor);
            poseStack.pushPose();
            poseStack.scale(-scale, -scale, scale);
            poseStack.translate(0F, 6F, -1F);
            drawText(font, entity.getNumberText(), entity.getNumberHalfWidth(), entity.getNumberColor(), entity.getNumberOutlineColor(),
                    outline, poseStack.last().pose(), buffer);
            poseStack.popPose();
        }

        if (showWord) {
            float scale = DamagePopupParticle.easeOut(age, 0F, 0.001F, lifetime - 5, (particle.shouldScale ? 150 : 100) * sizeFactor);
            poseStack.pushPose();
            poseStack.scale(-scale, -scale, scale);
            drawText(font, entity.getWordText(), entity.getWordHalfWidth(), entity.getWordColor(), entity.getWordOutlineColor(),
                    outline, poseStack.last().pose(), buffer);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    /**
     * Draws one centered text, outlined or plain, full bright.
     *
     * @param font         the font
     * @param text         the text
     * @param halfWidth    half the text width
     * @param color        the text color
     * @param outlineColor the outline color
     * @param outline      true to draw the 8 times outline
     * @param matrix       the pose matrix
     * @param buffer       the buffer source
     */
    private static void drawText(Font font, FormattedCharSequence text, float halfWidth, int color, int outlineColor, boolean outline,
                                 Matrix4f matrix, MultiBufferSource buffer) {
        if (outline) {
            font.drawInBatch8xOutline(text, -halfWidth, 0F, color, outlineColor, matrix, buffer, LightTexture.FULL_BRIGHT);
        } else {
            font.drawInBatch(text, -halfWidth, 0F, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        }
    }

    /**
     * Required by EntityRenderer, unused.
     *
     * @param entity the popup entity
     * @return the block atlas
     */
    @Override
    public ResourceLocation getTextureLocation(DamagePopupEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
