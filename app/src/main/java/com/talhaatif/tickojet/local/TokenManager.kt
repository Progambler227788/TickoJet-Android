package com.talhaatif.tickojet.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "token_store")


class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    // 1. Kept your exact suspend function
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // 2. Preserved your Flow return type
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    // 3. Unchanged clear function
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    // NEW: Only added this helper for Retrofit
    suspend fun getTokenForRequest(): String? {
        return getToken().first()
    }


    fun saveFcmToken(token: String) {

        val sharedPref = context.getSharedPreferences("fcm", Context.MODE_PRIVATE)
        sharedPref.edit().putString("fcm_token", token).apply()
    }

    fun getFcmToken(): String? {
        val sharedPref = context.getSharedPreferences("fcm", Context.MODE_PRIVATE)
        return sharedPref.getString("fcm_token", null)
    }

}