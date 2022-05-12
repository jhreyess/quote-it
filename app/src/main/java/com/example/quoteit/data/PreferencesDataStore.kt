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

class PreferencesDataStore(dataStore: DataStore<Preferences>) {

    private val isUserLoggedIn = booleanPreferencesKey("is_user_logged_in")
    private val userLoginEmail = stringPreferencesKey("user_login_email")
    private val userLoginPassword = stringPreferencesKey("user_login_password")

    val preferenceFlow: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            mapUserPreferences(it)
        }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences{
        val loginPref = preferences[isUserLoggedIn] ?: false
        val emailPref = preferences[userLoginEmail] ?: ""
        val passwordPref = preferences[userLoginPassword] ?: ""
        return UserPreferences(loginPref, emailPref, passwordPref)
    }

    suspend fun saveLogInPreference(isLoggedIn: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[isUserLoggedIn] = isLoggedIn
        }
    }

    suspend fun saveLogInCredentials(email: String, password: String, context: Context){
        context.dataStore.edit { preferences ->
            preferences[userLoginEmail] = email
            preferences[userLoginPassword] = password
        }
    }
}

data class UserPreferences(
    val isUserLoggedIn: Boolean = false,
    val userEmail: String = "",
    val userPassword: String = "",
)