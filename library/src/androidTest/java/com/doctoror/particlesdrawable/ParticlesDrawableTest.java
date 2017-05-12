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
import static org.junit.Assert.*;

public final class ParticlesDrawableTest {

    @Test
    public void testIsRunningByDefault() {
        assertFalse(new ParticlesDrawable().isRunning());
    }

    @Test
    public void testIsRunningWhenStarted() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        try {
            assertTrue(d.isRunning());
        } finally {
            d.stop();
        }
    }

    @Test
    public void testIsStopedWhenStopped() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        d.stop();
        assertFalse(d.isRunning());
    }

    @Test
    public void testSetBoundsWhenRunning() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.start();
        d.setBounds(0, 0, 10, 10);
        d.stop();
    }

    @Test
    public void testWithZeroBounds() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 0, 0);
        d.start();
        assertTrue(d.isRunning());
        d.stop();
        assertFalse(d.isRunning());
    }

    @Test
    public void testMakeBrandNewFrameWithZeroBounds() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrame() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrameWhenRunning() {
        final ParticlesDrawable d = new ParticlesDrawable();
        d.setBounds(0, 0, 10, 10);
        d.start();
        d.makeBrandNewFrame();
        d.stop();
    }

}
