package net.flameslight.blessfulled_oculus_shaders_compatible.mixins;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reads the protected position and age fields of a particle, used to draw a wrapped popup where its particle is.
 */
@Mixin(Particle.class)
public interface ParticleAccessor {
    /**
     * Reads the current x.
     *
     * @return the particle's x
     */
    @Accessor("x")
    double blessfulledShaderCombability$getX();

    /**
     * Reads the current y.
     *
     * @return the particle's y
     */
    @Accessor("y")
    double blessfulledShaderCombability$getY();

    /**
     * Reads the current z.
     *
     * @return the particle's z
     */
    @Accessor("z")
    double blessfulledShaderCombability$getZ();

    /**
     * Reads the x of the previous tick.
     *
     * @return the particle's previous x
     */
    @Accessor("xo")
    double blessfulledShaderCombability$getXo();

    /**
     * Reads the y of the previous tick.
     *
     * @return the particle's previous y
     */
    @Accessor("yo")
    double blessfulledShaderCombability$getYo();

    /**
     * Reads the z of the previous tick.
     *
     * @return the particle's previous z
     */
    @Accessor("zo")
    double blessfulledShaderCombability$getZo();

    /**
     * Reads the age.
     *
     * @return the particle's age in ticks
     */
    @Accessor("age")
    int blessfulledShaderCombability$getAge();
}
