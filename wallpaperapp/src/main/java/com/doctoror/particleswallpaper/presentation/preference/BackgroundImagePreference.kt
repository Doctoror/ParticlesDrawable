package com.doctoror.particleswallpaper.presentation.preference

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.Preference
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.Toast
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryFactory
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallback
import com.doctoror.particleswallpaper.presentation.base.OnActivityResultCallbackHost

/**
 * Created by Yaroslav Mytkalyk on 31.05.17.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
@RequiresApi(Build.VERSION_CODES.KITKAT)
class BackgroundImagePreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(context, attrs) {

    val settings: MutableSettingsRepository
            = SettingsRepositoryFactory.provideMutable(context)

    val defaults: SettingsRepository
            = SettingsRepositoryFactory.provideDefault()

    val requestCodePick = 1

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

    init {
        isPersistent = false
        setDefaultValue(settings.getBackgroundUri().blockingFirst())
        setOnPreferenceClickListener {
            _ ->
            onPrefClicked()
            true
        }
    }

    private fun onPrefClicked() {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(R.string.Pick, { _, _ -> pickDocument() })
                .setNegativeButton(R.string.Clear, { _, _ -> clearBackground() })
                .setNeutralButton(R.string.Cancel, null)
                .show()
    }

    private fun clearBackground() {
        defaults.getBackgroundUri().subscribe({ u -> settings.setBackgroundUri(u) })
    }

    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.type = "image/*"
        try {
            host?.startActivityForResult(intent, requestCodePick)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.Failed_to_open_image_picker, Toast.LENGTH_SHORT).show()
        }
    }

    val onActivityResultCallback = object : OnActivityResultCallback {

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