package net.flameslight.blessfulled_oculus_shaders_compatible;

import com.mojang.logging.LogUtils;
import net.flameslight.blessfulled_oculus_shaders_compatible.config.ClientConfig;
import net.flameslight.blessfulled_oculus_shaders_compatible.logger.ModLogger;
import net.flameslight.blessfulled_oculus_shaders_compatible.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(BlessfulledOculusShadersCompatible.MOD_ID)
public class BlessfulledOculusShadersCompatible {
    /** The mod id. */
    public static final String MOD_ID = "blessfulled_oculus_shaders_compatible";
    /** The mod's logger. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Registers the popup entity type and the client config. Does nothing on a dedicated server.
     *
     * @param context the mod loading context, given by Forge
     */
    public BlessfulledOculusShadersCompatible(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            ModLogger.warn("Client side only mod, it does nothing on a dedicated server.");
            return;
        }

        ModEntities.ENTITY_TYPES.register(context.getModEventBus());
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC);
    }
}
