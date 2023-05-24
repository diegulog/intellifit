package com.diegulog.intellifit.data.repository.local.database

import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegulog.intellifit.data.*
import com.diegulog.intellifit.data.repository.remote.ApiServiceMockInterceptor.Companion.trainingsMock
import com.diegulog.intellifit.di.appModuleTest
import com.diegulog.intellifit.domain.entity.*
import com.diegulog.intellifit.domain.repository.local.LocalDataSource
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
class LocalLocalDataSourceImplTest : KoinTest {

    private val localDataSource by inject<LocalDataSource>()


    @Before
    fun initDependencies() = runTest {
        loadKoinModules(appModuleTest)
        localDataSource.saveTraining(trainingsMock[0])
    }


    @Test
    fun saveCapture() = runTest {

        (0 until 10).forEach { _ ->
            localDataSource.saveCapture(sampleCapture())
        }
        val saveCaptures= localDataSource.getCaptures()

        assertEquals(10, saveCaptures.size)
        assertEquals(MoveType.CORRECT, saveCaptures[0].moveType)
        assertEquals(12, saveCaptures[0].samples.size)
        assertEquals(17, saveCaptures[0].samples[0].keyPoints.size)
    }

    @Test
    fun deleteCapture() = runTest {
        localDataSource.saveCapture(sampleCapture())
        var saveCapture = localDataSource.getCaptures()
        assertEquals(1, saveCapture.size)
        localDataSource.deleteCapture(saveCapture[0].id)
        saveCapture = localDataSource.getCaptures()
        assertEquals(0, saveCapture.size)
    }

    @Test
    fun getCaptures()  = runTest {
        val size = 100
        (0 until size).forEach { _ ->
            localDataSource.saveCapture(sampleCapture())
        }

        val saveCapture = localDataSource.getCaptures()
        assertEquals(size, saveCapture.size)
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
            timestamp = System.currentTimeMillis(),
            exerciseId = trainingsMock[0].exercises[0].id
        )

    }
    @After
    fun tearDown() {
        unloadKoinModules(appModuleTest)
    }
}