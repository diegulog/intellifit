package com.diegulog.intellifit.data.repository

interface DomainTranslatable<out T> {
     fun toDomain(): T
}