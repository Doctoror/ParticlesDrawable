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

    private fun <T> assertObserverHasValueCount(o: TestObserver<T>, count: Int) {
        o.assertNoErrors()
        o.assertNotComplete()
        o.assertSubscribed()
        o.assertValueCount(count)
    }

    private fun <T> assertObservableHasValueCount(o: Observable<T>, valueCount: Int): TestObserver<T> {
        val observer = TestObserver.create<T>()
        o.subscribe(observer)
        assertObserverHasValueCount(observer, valueCount)

        return observer
    }

    private fun <T> assertObservableHasValue(o: Observable<T>, value: T) {
        val testObserver = assertObservableHasValueCount(o, 1)
        testObserver.assertValue(value)
    }

    private fun <T> assertObservableHasValueWithCount(o: Observable<T>, value: T, count: Int) {
        val testObserver = assertObservableHasValueCount(o, count)
        testObserver.assertValue(value)
    }

    @Test
    fun testGetParticlesColor() {
        assertObservableHasValueCount(settings.getColor(), 1)
    }

    @Test
    fun testSetParticlesColor() {
        val value = 0xff000000.toInt()
        settings.setColor(value)
        assertObservableHasValue(settings.getColor(), value)
    }

    @Test
    fun testSetParticlesColorMonitorsChanges() {
        val value = 0xfa000000.toInt()
        val observer = TestObserver.create<Int>()
        settings.getColor().subscribe(observer)
        assertObserverHasValueCount(observer, 1)

        settings.setColor(value)

        assertObserverHasValueCount(observer, 2)
    }

    @Test
    fun testGetBackgroundColor() {
        assertObservableHasValueCount(settings.getBackgroundColor(), 1)
    }

    @Test
    fun testSetBackgroundColor() {
        val value = 0xff0000ff.toInt()
        settings.setBackgroundColor(value)
        assertObservableHasValue(settings.getBackgroundColor(), value)
    }

    @Test
    fun testGetBackgroundUri() {
        assertObservableHasValueCount(settings.getBackgroundUri(), 1)
    }

    @Test
    fun testSetBackgroundUri() {
        val value = "Someshit"
        settings.setBackgroundUri(value)
        assertObservableHasValue(settings.getBackgroundUri(), value)
    }

    @Test
    fun testGetNumDots() {
        assertObservableHasValueCount(settings.getNumDots(), 1)
    }

    @Test
    fun testSetNumDots() {
        val value = 22
        settings.setNumDots(value)
        assertObservableHasValue(settings.getNumDots(), value)
    }

    @Test
    fun testGetFrameDelay() {
        assertObservableHasValueCount(settings.getFrameDelay(), 1)
    }

    @Test
    fun testSetFrameDelay() {
        val value = 18
        settings.setFrameDelay(value)
        assertObservableHasValue(settings.getFrameDelay(), value)
    }

    @Test
    fun testGetStepMultiplier() {
        assertObservableHasValueCount(settings.getStepMultiplier(), 1)
    }

    @Test
    fun testSetStepMultiplier() {
        val value = 2.6f
        settings.setStepMultiplier(value)
        assertObservableHasValue(settings.getStepMultiplier(), value)
    }

    @Test
    fun testGetDotScale() {
        assertObservableHasValueCount(settings.getDotScale(), 1)
    }

    @Test
    fun testSetDotScale() {
        val value = 2.7f
        settings.setDotScale(value)
        assertObservableHasValue(settings.getDotScale(), value)
    }

    @Test
    fun testGetLineScale() {
        assertObservableHasValueCount(settings.getLineScale(), 1)
    }

    @Test
    fun testSetLineScale() {
        val value = 2.7f
        settings.setLineScale(value)
        assertObservableHasValue(settings.getLineScale(), value)
    }

    @Test
    fun testGetLineDistance() {
        assertObservableHasValueCount(settings.getLineDistance(), 1)
    }

    @Test
    fun testSetLineDistance() {
        val value = 2.7f
        settings.setLineDistance(value)
        assertObservableHasValue(settings.getLineDistance(), value)
    }

}