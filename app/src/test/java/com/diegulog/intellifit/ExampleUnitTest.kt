package com.diegulog.intellifit

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.math.absoluteValue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        for (i in 1..20) {
            System.out.println(UUID.randomUUID().mostSignificantBits.absoluteValue)
        }


    }
}