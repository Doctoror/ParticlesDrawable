package com.doctoror.particlesdrawable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom Keep annotation that is controlled by this project proguard config.
 * Some flavors can be built with exposing properties hidden by ConfigurableKeep.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ConfigurableKeep {
}
