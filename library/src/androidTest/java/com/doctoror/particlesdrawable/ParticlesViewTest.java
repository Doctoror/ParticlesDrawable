/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particlesdrawable;

import org.junit.Test;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.view.View;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Yaroslav Mytkalyk on 12.05.17.
 */
public final class ParticlesViewTest {

    @NonNull
    private ParticlesView newParticlesView() {
        final ParticlesView v = new ParticlesView(InstrumentationRegistry.getContext());
        v.setEmulateOnAttachToWindow(true);
        return v;
    }

    @NonNull
    private ParticlesView newAttachedAndVisibleParticlesView() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.VISIBLE);
        v.onAttachedToWindow();
        return v;
    }

    @Test
    public void testIsRunningByDefault() {
        assertFalse(newParticlesView().isRunning());
    }

    @Test
    public void testIsRunningWhenVisibleAndAttached() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        try {
            assertTrue(v.isRunning());
        } finally {
            v.stopInternal();
        }
    }

    @Test
    public void testIsStopedWhenStopped() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stopInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testIsStopedWhenDetachedFromWindow() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.onDetachedFromWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenInvisible() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenGone() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.setVisibility(View.GONE);
        v.onVisibilityChanged(v, View.GONE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenNotVisibleBeforeStart() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        v.startInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenNotVisibleBeforeAttachedToWindow() {
        final ParticlesView v = newParticlesView();
        v.setVisibility(View.INVISIBLE);
        v.onVisibilityChanged(v, View.INVISIBLE);
        v.onAttachedToWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedAndAttachedToWindow() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.onAttachedToWindow();
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedAndVisible() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.setVisibility(View.VISIBLE);
        v.onVisibilityChanged(v, View.VISIBLE);
        assertFalse(v.isRunning());
    }

    @Test
    public void testNotRunningWhenExplicitlyStoppedButStartedInternally() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.startInternal();
        assertFalse(v.isRunning());
    }

    @Test
    public void testRunningWhenExplicitlyStartedAfterExplicitStop() {
        final ParticlesView v = newAttachedAndVisibleParticlesView();
        v.stop();
        v.start();
        try {
            assertTrue(v.isRunning());
        } finally {
            v.stopInternal();
        }
    }
}
