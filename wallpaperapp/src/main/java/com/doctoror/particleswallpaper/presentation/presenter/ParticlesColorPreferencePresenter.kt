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
package com.doctoror.particleswallpaper.presentation.presenter

import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import com.doctoror.particleswallpaper.presentation.view.ParticlesColorPreferenceView
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 */
class ParticlesColorPreferencePresenter @Inject constructor(
        val settings: MutableSettingsRepository,
        @Named(ConfigModule.DEFAULT) val defaults: SettingsRepository)
    : Presenter<ParticlesColorPreferenceView> {

    private lateinit var view: ParticlesColorPreferenceView

    private var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            view.setColor(t)
        }
    }

    override fun onTakeView(view: ParticlesColorPreferenceView) {
        this.view = view
    }

    fun onPreferenceChange(v: Int?) {
        val color = v ?: defaults.getParticlesColor().blockingFirst()
        settings.setParticlesColor(color)
    }

    fun onClick() {
        view.showPreferenceDialog()
    }

    override fun onStart() {
        disposable = settings.getParticlesColor().subscribe(changeAction)
    }

    override fun onStop() {
        disposable?.dispose()
    }
}