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
package com.doctoror.particleswallpaper.presentation.base

import android.content.Intent
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnActrivityResultCallbackHostImplTest {

    @Test
    fun testCallbacksAreEmptyByDefault() {
        val target = OnActivityResultCallbackHostImpl()
        assertTrue { target.callbacks.isEmpty() }
    }

    @Test
    fun testRegisterCallback() {
        val callback = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback)

        assertEquals(1, target.callbacks.size)
    }

    @Test
    fun testRegisterSameCallbackTwice() {
        val callback = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback)
        registerCallbackAndAssert(target, callback)

        assertEquals(1, target.callbacks.size)
    }

    @Test
    fun testRegisterMultipleCallbacks() {
        val callback1 = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val callback2 = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback1)
        registerCallbackAndAssert(target, callback2)

        assertEquals(2, target.callbacks.size)
    }

    private fun registerCallbackAndAssert(
            target: OnActivityResultCallbackHostImpl,
            callback: OnActivityResultCallback) {
        target.registerCallback(callback)
        assertTrue { target.callbacks.contains(callback) }
    }

    @Test
    fun testUnregisterCallback() {
        val callback = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback)

        target.unregsiterCallback(callback)

        assertTrue { target.callbacks.isEmpty() }
    }

    @Test
    fun testUnregisterSameCallbackTwice() {
        val callback = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback)

        target.unregsiterCallback(callback)
        assertTrue { target.callbacks.isEmpty() }

        target.unregsiterCallback(callback)
        assertTrue { target.callbacks.isEmpty() }
    }

    @Test
    fun testUnregisterMultipleCallbacks() {
        val callback1 = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val callback2 = object : OnActivityResultCallback() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            }
        }

        val target = OnActivityResultCallbackHostImpl()
        registerCallbackAndAssert(target, callback1)
        registerCallbackAndAssert(target, callback2)

        assertEquals(2, target.callbacks.size)

        target.unregsiterCallback(callback1)

        assertEquals(1, target.callbacks.size)

        target.unregsiterCallback(callback2)

        assertTrue { target.callbacks.isEmpty() }
    }

}