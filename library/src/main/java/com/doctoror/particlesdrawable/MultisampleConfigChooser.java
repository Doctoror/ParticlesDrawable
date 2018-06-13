package com.doctoror.particlesdrawable;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * GLView configuration for multisampling
 * <p>
 * Used <a href="http://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java?r=2">GDC11 Multisample Example</a> as a reference.
 *
 * @author Aleksandar Kodzhabashev (d3kod)
 */
public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {
    static private final String TAG = "MultisampleConfigC";

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        mValue = new int[1];

        // Try to find a normal multisample configuration first.
        int[] configSpec = {
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_DEPTH_SIZE, 16,
                // Requires that setEGLContextClientVersion(2) is called on the view.
                EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                EGL10.EGL_SAMPLE_BUFFERS, 1 /* true */,
                EGL10.EGL_SAMPLES, 5,
                EGL10.EGL_NONE
        };

        if (!egl.eglChooseConfig(display, configSpec, null, 0,
                mValue)) {
            //            throw new IllegalArgumentException("eglChooseConfig failed");
            Log.e(TAG, "eglChooseConfig failed");
        }

        int numConfigs = mValue[0];

        if (numConfigs <= 0) {
            // No normal multisampling config was found. Try to create a
            // converage multisampling configuration, for the nVidia Tegra2.
            // See the EGL_NV_coverage_sample documentation.

            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

            configSpec = new int[]{
                    EGL10.EGL_RED_SIZE, 5,
                    EGL10.EGL_GREEN_SIZE, 6,
                    EGL10.EGL_BLUE_SIZE, 5,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                    EGL_COVERAGE_BUFFERS_NV, 1 /* true */,
                    EGL_COVERAGE_SAMPLES_NV, 5,  // always 5 in practice on tegra 2
                    EGL10.EGL_NONE
            };

            if (!egl.eglChooseConfig(display, configSpec, null, 0,
                    mValue)) {
                //                throw new IllegalArgumentException("2nd eglChooseConfig failed");
                Log.e(TAG, "2nd eglChooseConfig failed");
            } else {
                mUsesCoverageAa = true;
            }
            numConfigs = mValue[0];

            if (numConfigs <= 0) {
                // Give up, try without multisampling.
                configSpec = new int[]{
                        EGL10.EGL_RED_SIZE, 5,
                        EGL10.EGL_GREEN_SIZE, 6,
                        EGL10.EGL_BLUE_SIZE, 5,
                        EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                        EGL10.EGL_NONE
                };

                if (!egl.eglChooseConfig(display, configSpec, null, 0,
                        mValue)) {
                    //                    throw new IllegalArgumentException("3rd eglChooseConfig failed");
                    Log.e(TAG, "3rd eglChooseConfig failed");
                }
                numConfigs = mValue[0];

                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
            }
        }

        // Get all matching configurations.
        final EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs,
                mValue)) {
            throw new IllegalArgumentException("data eglChooseConfig failed");
        }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!
        int index = -1;
        for (int i = 0; i < configs.length; ++i) {
            if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == 5) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            Log.w(TAG, "Did not find sane config, using first");
        }
        final EGLConfig config = configs.length > 0 ? configs[index] : null;
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                 EGLConfig config, int attribute, int defaultValue) {
        if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            return mValue[0];
        }
        return defaultValue;
    }

    /**
     * See EGL_NV_coverage_sample documentation for more information. This is used in the OpenGL Renderer to determine if to clear the GL_COVERAGE_BUFFER_BIT_NV.
     *
     * @return if the configuration uses NVidia Tegra coverage multisampling configuration.
     */
    public static boolean usesCoverageAa() {
        return mUsesCoverageAa;
    }

    private int[] mValue;
    /**
     * Boolean to store if the graphics configuration uses NVidia Tegra coverage multisampling
     *
     * @see MultisampleConfigChooser#usesCoverageAa()
     */
    public static boolean mUsesCoverageAa;
}
