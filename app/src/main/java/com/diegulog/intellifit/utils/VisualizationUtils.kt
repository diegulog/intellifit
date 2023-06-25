/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package com.diegulog.intellifit.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.diegulog.intellifit.domain.entity.BodyPart
import com.diegulog.intellifit.domain.entity.Sample


object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 7f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 5f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f
    val aqua = Color.rgb(0, 255, 255)
    val yellow = Color.rgb(255, 255, 0)
    val white = Color.rgb(232, 232, 232)
    val pink = Color.rgb(255, 20, 148)
    val black = Color.rgb(0, 0, 0)

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Triple(BodyPart.NOSE, BodyPart.LEFT_EYE, aqua),
        Triple(BodyPart.NOSE, BodyPart.RIGHT_EYE, yellow),
        Triple(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR, aqua),
        Triple(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR, yellow),
        Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, aqua),
        Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, yellow),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW, aqua),
        Triple(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST, aqua),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW, yellow),
        Triple(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST, yellow),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, white),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP, aqua),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP, yellow),
        Triple(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP, white),
        Triple(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE, aqua),
        Triple(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE, aqua),
        Triple(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE, yellow),
        Triple(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE, yellow)
    )


    // Draw line and point indicate body pose
    fun drawBodyKeypoints(samples: List<Sample>, input: Bitmap?=null): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = pink
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }

        val output = if (input != null) {
            input.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            Bitmap.createBitmap(samples.first().width, samples.first().height, Bitmap.Config.ARGB_8888)
        }
        val originalSizeCanvas = Canvas(output)
        samples.forEach { person ->
            // draw person id if tracker is enable
            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                paintLine.color = it.third
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }
        }
        return output
    }
}
