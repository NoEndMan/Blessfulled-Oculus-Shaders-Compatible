package net.flameslight.blessfulled_shader_combability.events;

import net.flameslight.blessfulled_shader_combability.BlessfulledShaderCombability;
import net.flameslight.blessfulled_shader_combability.config.ClientConfig;
import net.flameslight.blessfulled_shader_combability.entity.DamagePopupEntityRenderer;
import net.flameslight.blessfulled_shader_combability.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Mod bus listeners of the client. Each one only forwards to its handler.
 */
@Mod.EventBusSubscriber(modid = BlessfulledShaderCombability.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {
    /**
     * Config load, reload and unload.
     *
     * @param event any mod config event
     */
    @SubscribeEvent
    public static void onConfigLoaded(ModConfigEvent event) {
        ClientConfig.onConfigLoaded(event.getConfig(), event instanceof ModConfigEvent.Unloading);
    }

    /**
     * Entity renderer registration.
     *
     * @param event the register renderers event
     */
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Grab the object out of the registry while it is still valid and cache it
        ModEntities.CACHED_DAMAGE_POPUP = ModEntities.DAMAGE_POPUP.get();

        // Register the renderer using our cached version
        event.registerEntityRenderer(
                ModEntities.CACHED_DAMAGE_POPUP,
                DamagePopupEntityRenderer::new
        );
    }
}
