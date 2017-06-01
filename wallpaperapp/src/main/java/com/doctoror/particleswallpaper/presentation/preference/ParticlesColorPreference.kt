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
import android.os.Parcelable
import android.util.AttributeSet
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class ParticlesColorPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ColorPreferenceNoPreview(context, attrs) {

    @Inject lateinit var settings: MutableSettingsRepository
    @field:[Inject Named(ConfigModule.DEFAULT)] lateinit var defaults: SettingsRepository

    var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            color = t
        }
    }

    init {
        Injector.configComponent.inject(this)
        isPersistent = false
        setOnPreferenceChangeListener({ _, v ->
            val color = v as? Int ?: defaults.getColor().blockingFirst()
            settings.setColor(color)
            true
        })

        subscribe()
    }

    private fun subscribe() {
        disposable = settings.getColor().subscribe(changeAction)
    }

    private fun unsubscribe() {
        disposable?.dispose()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        subscribe()
    }

    override fun onSaveInstanceState(): Parcelable {
        unsubscribe()
        return super.onSaveInstanceState()
    }

    override fun getPersistedInt(defaultReturnValue: Int): Int {
        return settings.getColor().blockingFirst()
    }
}