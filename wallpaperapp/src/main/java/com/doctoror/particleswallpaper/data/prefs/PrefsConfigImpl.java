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
package com.doctoror.particleswallpaper.data.prefs;

import android.graphics.Color;

import ds.gendalf.PrefsConfig;

/**
 * Created by Yaroslav Mytkalyk on 28.05.17.
 */
@SuppressWarnings("unused")
@PrefsConfig("Prefs")
public interface PrefsConfigImpl {

    int numDots = -1;
    int frameDelay = -1;
    float stepMultiplier = -1f;

    float dotScale = -1f;
    float lineScale = -1f;
    float lineDistance = -1f;

    int particlesColor = Color.WHITE;
    int backgroundColor = Color.BLACK;
    String backgroundUri = "";
}
