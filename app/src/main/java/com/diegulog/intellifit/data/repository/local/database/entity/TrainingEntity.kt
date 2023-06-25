package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.DomainTranslatable
import com.diegulog.intellifit.domain.entity.Training

@Entity(tableName = "training")
data class TrainingEntity(
    @PrimaryKey
    val id: String,
    val name: String = "",
    val urlImage: String = "",
    val isPublic: Boolean = false,
    val duration: Long = 0,
    val description: String = "",
    val ownerId: String
) : DomainTranslatable<Training> {

    @Ignore
    var exercises: List<ExerciseEntity> = emptyList()

    override fun toDomain(): Training {
        return Training(
            id = id,
            name = name,
            urlImage = urlImage,
            exercises = exercises.map { it.toDomain() },
            isPublic = isPublic,
            duration = duration,
            description = description,
            ownerId = ownerId
        )
    }

    companion object {
        fun fromDomain(training: Training): TrainingEntity {
            return TrainingEntity(
                id = training.id,
                name = training.name,
                urlImage = training.urlImage,
                isPublic = training.isPublic,
                duration = training.duration,
                description = training.description,
                ownerId = training.ownerId
            ).apply {
                exercises = training.exercises.map { ExerciseEntity.fromDomain(it) }
            }
        }
    }
}