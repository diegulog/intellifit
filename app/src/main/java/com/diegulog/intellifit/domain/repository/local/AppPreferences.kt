package com.diegulog.intellifit.domain.repository.local

interface AppPreferences {
    fun saveSessionToken(token:String?)
    fun getSessionToken():String?
}