package net.flameslight.blessfulled_oculus_shaders_compatible.registry;

import net.flameslight.blessfulled_oculus_shaders_compatible.BlessfulledOculusShadersCompatible;
import net.flameslight.blessfulled_oculus_shaders_compatible.entity.DamagePopupEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * The mod's entity registry. Registered on the client only.
 */
public final class ModEntities {
    /** The entity type register, attached to the mod bus by the mod constructor. */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BlessfulledOculusShadersCompatible.MOD_ID);

    /** The client side only entity that wraps a Blessfulled damage popup particle. */
    public static final RegistryObject<EntityType<DamagePopupEntity>> DAMAGE_POPUP = ENTITY_TYPES.register("damage_popup",
            () -> EntityType.Builder.<DamagePopupEntity>of(DamagePopupEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .setShouldReceiveVelocityUpdates(false)
                    .build("damage_popup"));

    // This variable will hold our permanent reference, surviving the server sync wipe
    public static EntityType<DamagePopupEntity> CACHED_DAMAGE_POPUP;
}
