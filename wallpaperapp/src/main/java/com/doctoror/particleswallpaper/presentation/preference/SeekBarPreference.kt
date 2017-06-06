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
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.doctoror.particleswallpaper.R

/**
 * [SeekBar] [Preference]
 */
open class SeekBarPreference @JvmOverloads constructor
(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : Preference(context, attrs, defStyle), OnSeekBarChangeListener {

    private var mProgress = 0
    private var mMax = 0
    private var mTrackingTouch = false

    var max: Int
        get() = mMax
        set(max) {
            if (max != mMax) {
                mMax = max
                notifyChanged()
            }
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyle, 0)
        max = a.getInt(R.styleable.SeekBarPreference_max, mMax)
        a.recycle()
        layoutResource = R.layout.preference_widget_seekbar
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        val seekBar: SeekBar = view.findViewById(R.id.seekbar) as SeekBar
        seekBar.setOnSeekBarChangeListener(this)
        seekBar.max = mMax
        seekBar.progress = mProgress
        seekBar.isEnabled = isEnabled
    }

    override fun getSummary(): CharSequence? {
        return null
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = when {
            restoreValue -> getPersistedInt(mProgress)
            defaultValue != null -> defaultValue as Int
            else -> 0
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }

    private fun setProgress(progress: Int, notifyChanged: Boolean) {
        var progressMutable = progress
        if (progress > mMax) {
            progressMutable = mMax
        }
        if (progress < 0) {
            progressMutable = 0
        }
        if (progressMutable != mProgress) {
            mProgress = progressMutable
            persistInt(progressMutable)
            if (notifyChanged) {
                notifyChanged()
            }
        }
    }

    var progress: Int
        get() = mProgress
        set(progress) = setProgress(progress, true)

    /**
     * Persist the seekBar's progress value if callChangeListener returns true,
     * otherwise set the seekBar's progress to the stored value
     */
    private fun syncProgress(seekBar: SeekBar) {
        val progress = seekBar.progress
        if (progress != mProgress) {
            if (callChangeListener(progress)) {
                setProgress(progress, false)
            } else {
                seekBar.progress = mProgress
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && !mTrackingTouch) {
            syncProgress(seekBar)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mTrackingTouch = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mTrackingTouch = false
        if (seekBar.progress != mProgress) {
            syncProgress(seekBar)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            // No need to save instance state since it's persistent
            return superState
        }

        // Save the instance state
        val myState = SavedState(superState)
        myState.mProgress = mProgress
        myState.mMax = mMax
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state.javaClass != SavedState::class.java) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state)
            return
        }

        // Restore the instance state
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)
        mProgress = myState.mProgress
        mMax = myState.mMax
        notifyChanged()
    }

    private class SavedState : BaseSavedState {

        var mProgress = 0
        var mMax = 0

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            mProgress = source.readInt()
            mMax = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(mProgress)
            dest.writeInt(mMax)
        }

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(p: Parcel): SavedState {
                    return SavedState(p)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
