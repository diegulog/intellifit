package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.DomainTranslatable
import com.diegulog.intellifit.domain.entity.KeyPoint
import com.diegulog.intellifit.domain.entity.Sample
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "sample",
    foreignKeys = [
        ForeignKey(
            entity = CaptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["captureId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
            )
    ]
)
class SampleEntity(
    @PrimaryKey
    val id: String,
    val score: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val keyPoints: List<KeyPoint>,
    val width: Int,
    val height: Int,
    var captureId: String,
) : DomainTranslatable<Sample> {
    @Ignore
    override fun toDomain(): Sample {
        return Sample(
            id = id,
            score = score,
            timestamp = timestamp,
            keyPoints = keyPoints,
            width = width,
            height = height,
            captureId = captureId
        )
    }

    companion object {
        fun fromDomain(sample: Sample): SampleEntity {
            return SampleEntity(
                id = sample.id,
                score = sample.score,
                timestamp = sample.timestamp,
                keyPoints = sample.keyPoints,
                width = sample.width,
                height = sample.height,
                captureId = sample.captureId
            )
        }
    }
}