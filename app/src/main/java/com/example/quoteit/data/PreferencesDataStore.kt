package com.example.quoteit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
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
    private val userToken = stringPreferencesKey("user_token")
    private val userName = stringPreferencesKey("user_name")
    private val userId = longPreferencesKey("user_id")
    private val vibratePref = booleanPreferencesKey("allow_vibration")

    val preferenceFlow: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { mapUserPreferences(it) }

    val preferenceToken: Flow<String> = dataStore.data
        .map { it[userToken] ?: "" }

    val preferenceVibration: Flow<Boolean> = dataStore.data
        .map { it[vibratePref] ?: false }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences{
        val loginPref = preferences[isUserLoggedIn] ?: false
        val emailPref = preferences[userLoginEmail] ?: ""
        val usernamePref = preferences[userName] ?: ""
        val passwordPref = preferences[userLoginPassword] ?: ""
        val userId = preferences[userId] ?: 0L
        val userToken = preferences[userToken] ?: ""
        val allowVibration = preferences[vibratePref] ?: false
        return UserPreferences(loginPref, emailPref, passwordPref, userId, usernamePref, userToken, allowVibration)
    }

    suspend fun saveLogInPreference(isLoggedIn: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[isUserLoggedIn] = isLoggedIn
        }
    }

    suspend fun saveVibrationPref(allow: Boolean, context: Context){
        context.dataStore.edit { preferences ->
            preferences[vibratePref] = allow
        }
    }

    suspend fun saveToken(token: String, context: Context){
        context.dataStore.edit { preferences ->
            preferences[userToken] = token
        }
    }

    suspend fun saveLogInCredentials(
        email: String,
        username: String,
        password: String,
        id: Long,
        context: Context
    ){
        context.dataStore.edit { preferences ->
            preferences[userLoginEmail] = email
            preferences[userName] = username
            preferences[userLoginPassword] = password
            preferences[userId] = id
        }
    }

    suspend fun clearDataStore(context: Context){
        context.dataStore.edit { it.clear() }
    }
}

data class UserPreferences(
    val isUserLoggedIn: Boolean = false,
    val userEmail: String = "",
    val userPassword: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val token: String = "",
    val allowVibration: Boolean = false
)