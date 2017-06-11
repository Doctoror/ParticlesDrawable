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
import static org.mockito.Mockito.*;

import android.support.annotation.NonNull;

import static org.junit.Assert.*;

public final class SceneControllerTest {

    @NonNull
    private SceneController newSceneController() {
        return new SceneController(mock(IParticlesView.class), mock(SceneScheduler.class));
    }

    @Test
    public void testIsRunningByDefault() {
        assertFalse(newSceneController().isRunning());
    }

    @Test
    public void testIsRunningWhenStarted() {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 10, 10);
        c.start();
        try {
            assertTrue(c.isRunning());
        } finally {
            c.stop();
        }
    }

    @Test
    public void testIsStopedWhenStopped() {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 10, 10);
        c.start();
        c.stop();
        assertFalse(c.isRunning());
    }

    @Test
    public void testSetBoundsWhenRunning() {
        final SceneController c = newSceneController();
        c.start();
        c.setBounds(0, 0, 10, 10);
        c.stop();
    }

    @Test
    public void testWithZeroBounds() {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 0, 0);
        c.start();
        assertTrue(c.isRunning());
        c.stop();
        assertFalse(c.isRunning());
    }

    @Test
    public void testMakeBrandNewFrameWithZeroBounds() {
        final SceneController c = newSceneController();
        c.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrame() {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 10, 10);
        c.makeBrandNewFrame();
    }

    @Test
    public void testMakeBrandNewFrameWhenRunning() {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 10, 10);
        c.start();
        c.makeBrandNewFrame();
        c.stop();
    }

    @Test
    public void testDotRadiusRangeThatRoundsUpToTheSameInt() throws Throwable {
        final SceneController c = newSceneController();
        c.setBounds(0, 0, 10, 10);
        c.setDotRadiusRange(0.5f, 0.6f);
        c.makeBrandNewFrame();
    }
}
