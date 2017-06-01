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
import android.os.Parcelable
import android.util.AttributeSet
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
class BackgroundColorPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ColorPreferenceNoPreview(context, attrs) {

    val settings: MutableSettingsRepository
            = SettingsRepositoryFactory.provideMutable(context)

    val defaults: SettingsRepository = SettingsRepositoryFactory.provideDefault()

    var disposable: Disposable? = null

    private val changeAction = Consumer<Int> { t ->
        if (t != null) {
            color = t
        }
    }

    init {
        isPersistent = false
        setOnPreferenceChangeListener({ _, v ->
            val color = v as? Int ?: defaults.getBackgroundColor().blockingFirst()
            settings.setBackgroundColor(color)
            settings.setBackgroundUri("")
            true
        })

        subscribe()
    }

    override fun onClick() {
        if (settings.getBackgroundUri().blockingFirst() == "") {
            super.onClick()
        } else {
            AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(R.string.This_will_replace_background_image)
                    .setPositiveButton(R.string.Continue, { _, _ -> super.onClick() })
                    .setNegativeButton(R.string.Cancel, null)
                    .show()
        }
    }

    private fun subscribe() {
        disposable = settings.getBackgroundColor().subscribe(changeAction)
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
        return settings.getBackgroundColor().blockingFirst()
    }
}