package com.doctoror.particleswallpaper.presentation.config

import android.annotation.TargetApi
import android.app.ActionBar
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.domain.interactor.SetWallpaperUseCase

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConfigActivityLollipop : ConfigActivity() {

    private val requestCodeSetWallpaper = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root: ViewGroup = findViewById(R.id.toolbarContainer) as ViewGroup
        val toolbar = layoutInflater.inflate(R.layout.activity_config_toolbar, root, false) as Toolbar
        root.addView(toolbar, 0)
        setActionBar(toolbar)
        actionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_HOME
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_config, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.actionPreview -> {
                SetWallpaperUseCase(this, requestCodeSetWallpaper).useCase().subscribe({
                    v ->
                    if (!v) onPreviewStartFailed()
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onPreviewStartFailed() {
        Toast.makeText(this, R.string.Failed_to_start_preview, Toast.LENGTH_LONG).show()
    }
}