package net.flameslight.blessfulled_shader_combability.entity;

import net.flameslight.blessfulled_shader_combability.handlers.PopupTrackingHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.aqutheseal.blessfulled.BSClientConfig;
import org.aqutheseal.blessfulled.particle.DamagePopupParticle;
import org.aqutheseal.blessfulled.util.BSColor;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

/**
 * Client side only entity that wraps one Blessfulled damage popup particle, so the popup is drawn through the entity render path.
 * <p>
 * Holds a direct reference to its particle and a render cache built once at spawn: the particle's damage, word and color are
 * final, so the texts, their widths and the colors never need to be rebuilt per frame. Only the number is rebuilt, if Blessfulled's
 * decimal format setting changes while the popup lives.
 */
public class DamagePopupEntity extends Entity {
    private static final int WHITE = new BSColor(255, 255, 255, 255).asNum();
    private static final int WHITE_OUTLINE = new BSColor(255, 255, 255, 255).shadeWithBlueShift(0.4F, 0.2F).asNum();

    @Nullable
    private DamagePopupParticle particle;

    private FormattedCharSequence numberText = FormattedCharSequence.EMPTY;
    private float numberHalfWidth;
    private int numberColor;
    private int numberOutlineColor;
    @Nullable
    private String formatUsed;

    private FormattedCharSequence wordText = FormattedCharSequence.EMPTY;
    private float wordHalfWidth;

    /**
     * Builds an empty popup entity. Without a particle it discards itself on its first tick.
     *
     * @param type  the entity type
     * @param level the level
     */
    public DamagePopupEntity(EntityType<?> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
        setInvisible(true);
        setSilent(true);
    }

    /**
     * Sets the wrapped particle and builds the render cache from it.
     *
     * @param particle the wrapped particle
     * @param font     the font used to measure the texts
     */
    public void wrap(DamagePopupParticle particle, Font font) {
        this.particle = particle;
        numberColor = particle.uniqueColor.asNum();
        numberOutlineColor = particle.uniqueColor.shadeWithBlueShift(0.4F, 0.2F).asNum();
        Component word = Component.literal(particle.onomatopoeia);
        wordText = word.getVisualOrderText();
        wordHalfWidth = font.width(word) / 2F;
        updateNumberText(font);
    }

    /**
     * Re-formats the number if Blessfulled's decimal format setting changed since it was formatted. A reference compare, so it
     * costs nothing while the setting is unchanged.
     *
     * @param font the font used to measure the number
     */
    public void updateNumberText(Font font) {
        String format = BSClientConfig.damageDecimalFormat;
        if (format == formatUsed || particle == null) {
            return;
        }
        formatUsed = format;
        String number = format == null ? Float.toString(particle.damage) : new DecimalFormat(format).format(particle.damage);
        Component numberComponent = Component.literal(number);
        numberText = numberComponent.getVisualOrderText();
        numberHalfWidth = font.width(numberComponent) / 2F;
    }

    /**
     * Gets the wrapped particle.
     *
     * @return the particle, null if none was set
     */
    @Nullable
    public DamagePopupParticle getParticle() {
        return particle;
    }

    /**
     * Gets the cached damage number text.
     *
     * @return the number text
     */
    public FormattedCharSequence getNumberText() {
        return numberText;
    }

    /**
     * Gets half the width of the damage number.
     *
     * @return the half width in font pixels
     */
    public float getNumberHalfWidth() {
        return numberHalfWidth;
    }

    /**
     * Gets the damage number color.
     *
     * @return the color as ARGB
     */
    public int getNumberColor() {
        return numberColor;
    }

    /**
     * Gets the damage number outline color.
     *
     * @return the outline color as ARGB
     */
    public int getNumberOutlineColor() {
        return numberOutlineColor;
    }

    /**
     * Gets the cached onomatopoeia text.
     *
     * @return the word text
     */
    public FormattedCharSequence getWordText() {
        return wordText;
    }

    /**
     * Gets half the width of the onomatopoeia.
     *
     * @return the half width in font pixels
     */
    public float getWordHalfWidth() {
        return wordHalfWidth;
    }

    /**
     * Gets the onomatopoeia color, white like Blessfulled's.
     *
     * @return the color as ARGB
     */
    public int getWordColor() {
        return WHITE;
    }

    /**
     * Gets the onomatopoeia outline color.
     *
     * @return the outline color as ARGB
     */
    public int getWordOutlineColor() {
        return WHITE_OUTLINE;
    }

    /**
     * Follows the particle or despawns, see PopupTrackingHandler. Skips Entity's base tick: no fire, fluid, portal or block checks.
     */
    @Override
    public void tick() {
        PopupTrackingHandler.tickPopup(this);
    }

    /**
     * Removes the entity and updates the live popup count once.
     *
     * @param reason the removal reason
     */
    @Override
    public void remove(RemovalReason reason) {
        boolean wasRemoved = isRemoved();
        super.remove(reason);
        if (!wasRemoved) {
            PopupTrackingHandler.onDiscarded();
        }
    }

    /**
     * Always renders regardless of distance, like the particle.
     *
     * @param distance the squared distance to the camera
     * @return true
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    /**
     * Never targeted by the crosshair.
     *
     * @return false
     */
    @Override
    public boolean isPickable() {
        return false;
    }

    /**
     * Never pushed by other entities.
     *
     * @return false
     */
    @Override
    public boolean isPushable() {
        return false;
    }

    /**
     * Never collides.
     *
     * @return false
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Never attacked.
     *
     * @return false
     */
    @Override
    public boolean isAttackable() {
        return false;
    }

    /**
     * Never saved.
     *
     * @return false
     */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    /**
     * No synched data: the entity never reaches the server and reads no locked data.
     */
    @Override
    protected void defineSynchedData() {
    }

    /**
     * Nothing to load.
     *
     * @param tag the save tag
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    /**
     * Nothing to save.
     *
     * @param tag the save tag
     */
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    /**
     * Required by Entity, never used: this entity only exists on the client.
     *
     * @return a standard add entity packet
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
