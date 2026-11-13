package net.flameslight.blessfulled_oculus_shaders_compatible.events;

import net.flameslight.blessfulled_oculus_shaders_compatible.BlessfulledOculusShadersCompatible;
import net.flameslight.blessfulled_oculus_shaders_compatible.handlers.PopupTrackingHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge bus listeners of the client. Each one only forwards to a handler.
 */
@Mod.EventBusSubscriber(modid = BlessfulledOculusShadersCompatible.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    /**
     * A level is loaded. Only a new client level (entering a server, a world or a dimension) is forwarded.
     *
     * @param event the level load event
     */
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel) {
            PopupTrackingHandler.onLevelEntered();
        }
    }
}
