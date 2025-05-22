package com.yanakudrinskaya.bookshelf.login.data

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.login.domain.models.User
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.login.domain.models.UserCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val APP_PREFERENCES = "app_prefs"
const val IS_FIRST_LAUNCH = "is_first_launch"
const val KEY_USER_PROFILE = "user_profile"

class UserProfileRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : UserProfileRepository {

    override suspend fun saveLocalUserProfile(user: User) {
        withContext(Dispatchers.IO) {
            try {
                val userJson = gson.toJson(user)
                sharedPreferences.edit()
                    .putString(KEY_USER_PROFILE, userJson)
                    .apply()
                Log.d("Myregister", "User profile saved successfully")
                saveCurrentUser(user)

            } catch (e: Exception) {
                Log.e("Myregister", "Error saving user profile", e)
            }
        }
    }

    override suspend fun getLocalUserProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val userJson = sharedPreferences.getString(KEY_USER_PROFILE, null)
                userJson?.let { json ->
                    gson.fromJson(json, User::class.java).let { user ->
                        Result.Success(user).also {
                            Log.d("Myregister", "User profile loaded successfully")
                        }
                    }
                }
                    ?: Result.Failure(NoSuchElementException("User profile not found in local storage"))
            } catch (e: Exception) {
                Log.e("Myregister", "Error loading user profile", e)
                Result.Failure(e)
            }
        }
    }

    override fun deleteProfile() {
        try {
            sharedPreferences.edit()
                .remove(KEY_USER_PROFILE)
                .apply()
            Log.d("Myregister", "User profile deleted successfully")
        } catch (e: Exception) {
            Log.e("Myregister", "Error deleting user profile", e)
        }
    }

    private fun saveCurrentUser(user: User) {
        UserCurrent.id = user.userId
        UserCurrent.name = user.name
        UserCurrent.email = user.email
    }
}