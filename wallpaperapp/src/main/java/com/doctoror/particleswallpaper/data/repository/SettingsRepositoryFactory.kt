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
package com.doctoror.particleswallpaper.data.repository

import android.content.Context
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository

/**
 * Created by Yaroslav Mytkalyk on 29.05.17.
 */
class SettingsRepositoryFactory {

    companion object {
        @JvmStatic private var settingsRepository: MutableSettingsRepository? = null
        @JvmStatic private var settingsRepositoryDefault: SettingsRepository? = null

        @JvmStatic fun provideMutable(context: Context): MutableSettingsRepository {
            if (settingsRepository == null) {
                settingsRepository = SettingsRepositoryImpl(context.applicationContext!!)
            }
            return settingsRepository!!
        }

        @JvmStatic fun provide(context: Context): SettingsRepository
                = provideMutable(context)

        @JvmStatic fun provideDefault(): SettingsRepository {
            if (settingsRepositoryDefault == null) {
                settingsRepositoryDefault = SettingsRepositoryDefault()
            }
            return settingsRepositoryDefault!!
        }
    }
}