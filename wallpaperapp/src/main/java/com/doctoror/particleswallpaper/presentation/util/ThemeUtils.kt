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
package com.doctoror.particleswallpaper.presentation.util

import android.content.res.Resources.Theme
import android.support.annotation.AttrRes
import android.support.annotation.Dimension

/**
 * Utils for obtaining [Theme] attributes
 */
class ThemeUtils private constructor() {

    init {
        throw UnsupportedOperationException()
    }

    companion object {

        /**
         * Returns dimension for attr from the [Theme]

         * @param theme [Theme] to get dimension from
         * *
         * @param attr  Attribute of the dimension
         * *
         * @return dimension for attr from the [Theme]
         */
        @Dimension fun getDimension(theme: Theme, @AttrRes attr: Int): Float {
            val array = theme.obtainStyledAttributes(intArrayOf(attr))
            try {
                return array.getDimension(0, 0f)
            } finally {
                array.recycle()
            }
        }
    }
}
