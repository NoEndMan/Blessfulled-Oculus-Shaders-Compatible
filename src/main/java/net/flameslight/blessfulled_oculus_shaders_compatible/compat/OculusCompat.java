package net.flameslight.blessfulled_oculus_shaders_compatible.compat;

import net.irisshaders.iris.api.v0.IrisApi;

/**
 * Oculus state queries. Oculus is a required dependency, so its API is always present.
 */
public final class OculusCompat {
    /**
     * Tells whether a shader pack is rendering right now.
     *
     * @return true if a shader pack is in use
     */
    public static boolean isShaderPackInUse() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    /**
     * Tells whether Oculus is rendering its shadow pass right now.
     *
     * @return true during the shadow pass
     */
    public static boolean isRenderingShadowPass() {
        return IrisApi.getInstance().isRenderingShadowPass();
    }
}
