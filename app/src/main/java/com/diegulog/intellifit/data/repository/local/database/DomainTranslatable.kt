package com.diegulog.intellifit.data.repository.local.database

interface DomainTranslatable<out T> {
     fun toDomain(): T
}