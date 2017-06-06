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

import android.app.AlertDialog
import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.doctoror.particleswallpaper.presentation.presenter.PerformanceTipsPreferencePresenter
import com.doctoror.particleswallpaper.presentation.view.PerformanceTipsPreferenceView
import javax.inject.Inject

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 */
class PerformanceTipsPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(context, attrs),
        PerformanceTipsPreferenceView {

    @Inject lateinit var presenter: PerformanceTipsPreferencePresenter

    init {
        Injector.configComponent.inject(this)
        presenter.onTakeView(this)
        isPersistent = false
    }

    override fun onClick() {
        presenter.onClick()
    }

    override fun showDialog() {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(R.string.performance_tips)
                .setPositiveButton(R.string.Close, null)
                .show()
    }
}
