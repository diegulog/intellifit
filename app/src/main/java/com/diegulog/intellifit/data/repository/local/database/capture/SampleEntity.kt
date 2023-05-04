package com.diegulog.intellifit.data.repository.local.database.capture

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.local.database.DomainTranslatable
import com.diegulog.intellifit.domain.entity.KeyPoint
import com.diegulog.intellifit.domain.entity.Sample

@Entity(
    tableName = "sample",
    foreignKeys = [
        ForeignKey(
            entity = CaptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["captureId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val keyPoints: List<KeyPoint>,
    var captureId: Long? = null,
) : DomainTranslatable<Sample> {
    @Ignore
    override fun toDomain(): Sample {
        return Sample(
            id = id,
            score = score,
            timestamp = timestamp,
            keyPoints = keyPoints,
        )
    }

    companion object {
        fun fromDomain(sample: Sample): SampleEntity {
            return SampleEntity(
                id = sample.id,
                score = sample.score,
                timestamp = sample.timestamp,
                keyPoints = sample.keyPoints
            )
        }
    }
}