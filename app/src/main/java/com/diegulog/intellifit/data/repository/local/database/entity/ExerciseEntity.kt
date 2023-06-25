package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.DomainTranslatable
import com.diegulog.intellifit.domain.entity.Exercise

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = TrainingEntity::class,
            parentColumns = ["id"],
            childColumns = ["trainingId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String = "",
    val urlImage: String = "",
    val urlVideo: String = "",
    val duration: Int = 1,
    val idModel: String = "",
    var trainingId: String,
    val isPublic: Boolean = false,
    val description: String = "",
    val repeat: Int = 0
) : DomainTranslatable<Exercise> {

    @Ignore
    var captures: List<CaptureEntity> = emptyList()

    override fun toDomain(): Exercise {
        return Exercise(
            id = id,
            name = name,
            urlImage = urlImage,
            urlVideo = urlVideo,
            duration = duration,
            idModel = idModel,
            captures = captures.map { it.toDomain() },
            isPublic = isPublic,
            description = description,
            trainingId = trainingId,
            repeat = repeat
        )
    }

    companion object {
        fun fromDomain(exercise: Exercise): ExerciseEntity {
            return ExerciseEntity(
                id = exercise.id,
                name = exercise.name,
                urlImage = exercise.urlImage,
                urlVideo = exercise.urlVideo,
                duration = exercise.duration,
                idModel = exercise.idModel,
                isPublic = exercise.isPublic,
                description = exercise.description,
                trainingId = exercise.trainingId,
                repeat = exercise.repeat
            ).apply {
                captures = exercise.captures.map { CaptureEntity.fromDomain(it) }
            }
        }
    }
}