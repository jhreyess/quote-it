package com.example.quoteit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_LOGIN_PREFS = "login_prefs"

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = USER_LOGIN_PREFS)

class PreferencesDataStore(context: Context) {
    private val isUserLoggedIn = booleanPreferencesKey("is_user_logged_in")
    val preferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[isUserLoggedIn] ?: false
        }

    suspend fun saveLogInPreference(isLoggedIn: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[isUserLoggedIn] = isLoggedIn
        }
    }
}