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
package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.rarepebble.colorpicker.ColorPreference

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 */
open class ColorPreferenceNoPreview @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ColorPreference(context, attrs) {

    override fun onBindView(view: View?) {
        super.onBindView(view)
        if (view != null) {
            val widgetFrameView: ViewGroup = view.findViewById(android.R.id.widget_frame) as ViewGroup
            widgetFrameView.visibility = View.GONE
            widgetFrameView.removeAllViews()
        }
    }
}