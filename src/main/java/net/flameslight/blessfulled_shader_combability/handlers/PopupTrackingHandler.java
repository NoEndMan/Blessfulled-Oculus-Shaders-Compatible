package net.flameslight.blessfulled_shader_combability.handlers;

import net.flameslight.blessfulled_shader_combability.compat.OculusCompat;
import net.flameslight.blessfulled_shader_combability.entity.DamagePopupEntity;
import net.flameslight.blessfulled_shader_combability.logger.ModLogger;
import net.flameslight.blessfulled_shader_combability.mixins.ParticleAccessor;
import net.flameslight.blessfulled_shader_combability.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.aqutheseal.blessfulled.particle.DamagePopupParticle;

/**
 * Spawns, follows and despawns the client side only entities that wrap Blessfulled damage popups.
 * <p>
 * Client side only. Keeps no collection of popups: the level's entity storage already holds them, and each entity holds a
 * direct reference to its particle.
 */
public final class PopupTrackingHandler {
    /** Fail safe: a popup entity despawns after this many of its own ticks (3 seconds). */
    public static final int MAX_AGE_TICKS = 60;

    private static int livePopups = 0;

    /**
     * Spawns the entity that wraps a newly built popup particle, only while a shader pack is in use. Without shaders the
     * particle draws itself as Blessfulled made it.
     *
     * @param level    the client level of the particle
     * @param particle the popup particle
     * @param x        the spawn x
     * @param y        the spawn y
     * @param z        the spawn z
     * @return true if the entity was spawned and now draws the popup
     */
    public static boolean spawn(ClientLevel level, DamagePopupParticle particle, double x, double y, double z) {
        if (!OculusCompat.isShaderPackInUse()) {
            return false;
        }

        try {
            DamagePopupEntity entity = new DamagePopupEntity(ModEntities.CACHED_DAMAGE_POPUP, level);
            entity.wrap(particle, Minecraft.getInstance().font);
            entity.setPos(x, y, z);
            entity.setOldPosAndRot();

            level.putNonPlayerEntity(entity.getId(), entity);
            livePopups++;

            ModLogger.debug("Spawned popup entity {}: damage {}, type {}, lifetime {}, at {} {} {}.",
                    entity.getId(), particle.damage, particle.popupType, particle.getLifetime(), x, y, z);
            return true;
        } catch (Exception e) {
            ModLogger.error("Could not spawn the damage popup entity, the popup draws itself instead.", e);
            return false;
        }
    }

    /**
     * Follows the particle, or discards the entity when its particle is gone or when it reached MAX_AGE_TICKS.
     *
     * @param entity the popup entity
     */
    public static void tickPopup(DamagePopupEntity entity) {
        DamagePopupParticle particle = entity.getParticle();

        if (particle == null || !particle.isAlive()) {
            ModLogger.debug("Discarded popup entity {}: particle expired.", entity.getId());
            entity.discard();
            return;
        }

        if (entity.tickCount >= MAX_AGE_TICKS) {
            ModLogger.debug("Discarded popup entity {}: max age reached.", entity.getId());
            entity.discard();
            return;
        }

        ParticleAccessor position = (ParticleAccessor) particle;
        entity.setPos(position.blessfulledShaderCombability$getX(), position.blessfulledShaderCombability$getY(),
                position.blessfulledShaderCombability$getZ());
    }

    /**
     * a new client level was entered (server, world or dimension). A new level starts without entities, and the old
     * level was dropped with its popups without removing them, so only the count and the id counter are reset.
     */
    public static void onLevelEntered() {
        if (livePopups != 0) {
            ModLogger.debug("Entered a new level, {} popup entities were dropped with the old one.", livePopups);
        }
        livePopups = 0;
    }

    /**
     * A popup entity was removed.
     */
    public static void onDiscarded() {
        if (livePopups > 0) {
            livePopups--;
        }
    }
}
