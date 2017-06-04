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

import android.app.Activity
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.presentation.presenter.PreviewPreferencePresenter

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class PreviewPreference @JvmOverloads constructor
(contextParam: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(contextParam, attrs) {

    private val presenter = PreviewPreferencePresenter(contextParam as Activity)

    init {
        isPersistent = false
    }

    override fun onClick() {
        presenter.onClick()
    }
}
