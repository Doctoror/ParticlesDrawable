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
import android.support.test.InstrumentationRegistry
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class SettingsRepositoryImplTest {

    val fakePrefsName = "TEST_PREFS"

    lateinit var settings: SettingsRepositoryImpl

    @Before
    fun setUp() {
        val fakePrefs = InstrumentationRegistry.getContext()
                .getSharedPreferences(fakePrefsName, Context.MODE_PRIVATE)

        val context = Mockito.mock<Context>(Context::class.java)
        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(fakePrefs)

        settings = SettingsRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getContext().deleteSharedPreferences(fakePrefsName)
    }

    private fun <T> assertObservableHasSingleValue(o: Observable<T>): TestObserver<T> {
        val observer = TestObserver.create<T>()
        o.subscribe(observer)

        observer.assertNoErrors()
        observer.assertNotComplete()
        observer.assertSubscribed()
        observer.assertValueCount(1)

        return observer
    }

    private fun <T> assertObservableHasSingleValue(o: Observable<T>, value: T) {
        val testObserver = assertObservableHasSingleValue(o)
        testObserver.assertValue(value)
    }

    @Test
    fun testGetParticlesColor() {
        assertObservableHasSingleValue(settings.getColor())
    }

    @Test
    fun testSetParticlesColor() {
        val value = 0xff000000.toInt()
        settings.setColor(value)
        assertObservableHasSingleValue(settings.getColor(), value)
    }

    @Test
    fun testGetBackgroundColor() {
        assertObservableHasSingleValue(settings.getBackgroundColor())
    }

    @Test
    fun testSetBackgroundColor() {
        val value = 0xff0000ff.toInt()
        settings.setBackgroundColor(value)
        assertObservableHasSingleValue(settings.getBackgroundColor(), value)
    }

    @Test
    fun testGetBackgroundUri() {
        assertObservableHasSingleValue(settings.getBackgroundUri())
    }

    @Test
    fun testSetBackgroundUri() {
        val value = "Someshit"
        settings.setBackgroundUri(value)
        assertObservableHasSingleValue(settings.getBackgroundUri(), value)
    }

    @Test
    fun testGetNumDots() {
        assertObservableHasSingleValue(settings.getNumDots())
    }

    @Test
    fun testSetNumDots() {
        val value = 22
        settings.setNumDots(value)
        assertObservableHasSingleValue(settings.getNumDots(), value)
    }

    @Test
    fun testGetFrameDelay() {
        assertObservableHasSingleValue(settings.getFrameDelay())
    }

    @Test
    fun testSetFrameDelay() {
        val value = 18
        settings.setFrameDelay(value)
        assertObservableHasSingleValue(settings.getFrameDelay(), value)
    }

    @Test
    fun testGetStepMultiplier() {
        assertObservableHasSingleValue(settings.getStepMultiplier())
    }

    @Test
    fun testSetStepMultiplier() {
        val value = 2.6f
        settings.setStepMultiplier(value)
        assertObservableHasSingleValue(settings.getStepMultiplier(), value)
    }

    @Test
    fun testGetDotScale() {
        assertObservableHasSingleValue(settings.getDotScale())
    }

    @Test
    fun testSetDotScale() {
        val value = 2.7f
        settings.setDotScale(value)
        assertObservableHasSingleValue(settings.getDotScale(), value)
    }

    @Test
    fun testGetLineScale() {
        assertObservableHasSingleValue(settings.getLineScale())
    }

    @Test
    fun testSetLineScale() {
        val value = 2.7f
        settings.setLineScale(value)
        assertObservableHasSingleValue(settings.getLineScale(), value)
    }

    @Test
    fun testGetLineDistance() {
        assertObservableHasSingleValue(settings.getLineDistance())
    }

    @Test
    fun testSetLineDistance() {
        val value = 2.7f
        settings.setLineDistance(value)
        assertObservableHasSingleValue(settings.getLineDistance(), value)
    }

}