package com.diegulog.intellifit.data.repository.local.preferences
import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.diegulog.intellifit.domain.repository.local.AppPreferences

class AppPreferencesImpl(context: Context): AppPreferences {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs_"+ context.packageName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveSessionToken(token: String?) = sharedPreferences.edit(true) { putString(SESSION_TOKEN, token) }
    override fun getSessionToken() = sharedPreferences.getString(SESSION_TOKEN, null)

    companion object{
        const val SESSION_TOKEN = "session_token"
    }

}