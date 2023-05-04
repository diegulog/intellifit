package com.diegulog.intellifit.data.repository.local.database

import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegulog.intellifit.data.*
import com.diegulog.intellifit.di.appModuleTest
import com.diegulog.intellifit.domain.entity.*
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class DataBaseRepositoryImplTest : KoinTest {

    val dataBaseRepository by inject<DataBaseRepository>()


    @Before
    fun initDependencies() {
        loadKoinModules(appModuleTest)
    }


    @Test
    fun saveCapture() = runTest {

        (0 until 10).forEach { _ ->
            dataBaseRepository.saveCapture(sampleCapture())
        }
        val saveCaptureFLow = dataBaseRepository.getCaptures()

        assertTrue(saveCaptureFLow.first().isLoading)
        val saveCapture = saveCaptureFLow.drop(1).first().valueOrNull
        assertNotNull(saveCapture)
        assertEquals(10, saveCapture!!.size)
        assertEquals(MoveType.CORRECT, saveCapture!![0].moveType)
        assertEquals(12, saveCapture[0].samples.size)
        assertEquals(17, saveCapture[0].samples[0].keyPoints.size)
    }

    @Test
    fun deleteCapture() = runTest {
        dataBaseRepository.saveCapture(sampleCapture())
        var saveCapture = dataBaseRepository.getCaptures().drop(1).first().valueOrNull
        assertEquals(1, saveCapture!!.size)
        dataBaseRepository.deleteCapture(saveCapture!![0])
        saveCapture = dataBaseRepository.getCaptures().drop(1).first().valueOrNull
        assertEquals(0, saveCapture!!.size)
    }

    @Test
    fun getCaptures()  = runTest {
        val size = 100
        (0 until size).forEach { _ ->
            dataBaseRepository.saveCapture(sampleCapture())
        }

        val saveCapture = dataBaseRepository.getCaptures().drop(1).first().valueOrNull
        assertEquals(size, saveCapture!!.size)
    }

    @Test
    fun getCapture() = runTest {
        var capture = sampleCapture()
        dataBaseRepository.saveCapture(capture)
        val saved = dataBaseRepository.getCaptures().drop(1).first().valueOrNull!![0]
        capture = dataBaseRepository.getCapture(saved.id).drop(1).first().valueOrNull!!
        assertEquals(saved, capture)
    }

    private fun sampleCapture(): Capture {
        val keyPoints = BodyPart.values().map { KeyPoint(bodyPart = it, PointF(0f, 0f), 0f) }

        //12 samples por captura
        val samples = (0..11).map {
            Sample(keyPoints = keyPoints, score = 0f, timestamp = it * 100L)
        }

        return Capture(
            samples = samples,
            videoPath = "/sdcard/",
            moveType = MoveType.CORRECT,
            timestamp = System.currentTimeMillis()
        )

    }
    @After
    fun tearDown() {
        unloadKoinModules(appModuleTest)
    }
}