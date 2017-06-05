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

import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import com.doctoror.particleswallpaper.presentation.view.BackgroundImagePreferenceView
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Yaroslav Mytkalyk on 03.06.17.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
@RequiresApi(Build.VERSION_CODES.KITKAT)
class BackgroundImagePreferencePresenter @Inject constructor(
        val context: Context,
        val settings: MutableSettingsRepository,
        @Named(ConfigModule.DEFAULT) val defaults: SettingsRepository)
    : Presenter<BackgroundImagePreferenceView> {

    private lateinit var view: BackgroundImagePreferenceView

    private val requestCodePick = 1

    var host: Fragment? = null
        set(f) {
            val prevHost = host
            if (prevHost !== f) {
                if (prevHost is OnActivityResultCallbackHost) {
                    prevHost.unregsiterCallback(onActivityResultCallback)
                }
                if (f is OnActivityResultCallbackHost) {
                    f.registerCallback(onActivityResultCallback)
                }
                field = f
            }
        }

    override fun onTakeView(view: BackgroundImagePreferenceView) {
        this.view = view
    }

    override fun onStart() {
        // Stub
    }

    override fun onStop() {
        // Stub
    }

    fun onClick() {
        view.showActionDialog()
    }

    fun clearBackground() {
        val uri = settings.getBackgroundUri().blockingFirst()
        if (uri != "") {
            context.contentResolver?.releasePersistableUriPermission(Uri.parse(uri),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        defaults.getBackgroundUri().take(1).subscribe({ u -> settings.setBackgroundUri(u) })
    }

    fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.type = "image/*"
        try {
            host?.startActivityForResult(intent, requestCodePick)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.Failed_to_open_image_picker, Toast.LENGTH_SHORT).show()
        }
    }

    val onActivityResultCallback = object : OnActivityResultCallback() {

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == requestCodePick && resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                if (uri != null) {
                    context.contentResolver?.takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    settings.setBackgroundUri(uri.toString())
                }
            }
        }
    }
}