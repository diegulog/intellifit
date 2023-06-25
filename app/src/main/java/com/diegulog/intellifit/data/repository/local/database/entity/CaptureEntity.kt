package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.DomainTranslatable
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType

@Entity(
    tableName = "capture"
)
data class CaptureEntity(
    @PrimaryKey
    val id: String,
    val videoPath: String = "",
    val moveType: MoveType,
    val timestamp: Long = 0,
    var exerciseId: String,
    var modelId: String
): DomainTranslatable<Capture> {
    @Ignore
    var samples: List<SampleEntity> = emptyList()
    override fun toDomain(): Capture {
        return Capture(
            id = id,
            samples = samples.map { it.toDomain() },
            videoPath = videoPath,
            moveType = moveType,
            timestamp = timestamp,
            exerciseId = exerciseId,
            modelId = modelId
        )
    }

    companion object {
        fun fromDomain(capture: Capture): CaptureEntity {
            return CaptureEntity(
                id = capture.id,
                videoPath = capture.videoPath,
                moveType = capture.moveType,
                timestamp = capture.timestamp,
                exerciseId = capture.exerciseId,
                modelId = capture.modelId
            ).apply {
                samples = capture.samples.map { SampleEntity.fromDomain(it) }
            }
        }
    }
}
