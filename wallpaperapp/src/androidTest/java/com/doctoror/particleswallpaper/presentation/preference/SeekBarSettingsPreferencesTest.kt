package com.doctoror.particleswallpaper.presentation.preference

import android.content.Context
import android.support.test.InstrumentationRegistry
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Yaroslav Mytkalyk on 30.05.17.
 */
class SeekBarSettingsPreferencesTest {

    @Test
    fun testDotScalePreference() {
        testMapper(DotScalePreference::class.java)
    }

    @Test
    fun testFrameDelayPreference() {
        testMapper(FrameDelayPreference::class.java)
    }

    @Test
    fun testFrameDelayPreferenceMinValue() {
        val p = newInstance(FrameDelayPreference::class.java)
        assertEquals(p.frameDelaySeekbarMin, p.transformToRealValue(p.max))
    }

    private fun <T, A> testMapper(pClass: Class<T>)
            where T : SeekBarPreference, T : MapperSeekbarPreference<A> {
        testMapperMinValue(pClass)
        testMapperMaxValue(pClass)
    }

    private fun <T, A> testMapperMinValue(pClass: Class<T>)
            where T : SeekBarPreference, T : MapperSeekbarPreference<A> {
        val p = newInstance(pClass)
        val seekBarValue = 0
        val frameDelay = p.transformToRealValue(seekBarValue)
        assertEquals(seekBarValue, p.transformToProgress(frameDelay))
    }

    private fun <T, A> testMapperMaxValue(pClass: Class<T>)
            where T : SeekBarPreference, T : MapperSeekbarPreference<A> {
        val p = newInstance(pClass)
        val seekBarValue = p.max
        val frameDelay = p.transformToRealValue(seekBarValue)
        assertEquals(seekBarValue, p.transformToProgress(frameDelay))
    }

    fun <T, A> newInstance(pClass: Class<T>): T where T : SeekBarPreference, T : MapperSeekbarPreference<A> {
        val constructor = pClass.getConstructor(Context::class.java)
        return constructor.newInstance(InstrumentationRegistry.getTargetContext())
    }

}