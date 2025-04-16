package com.yanakudrinskaya.bookshelf.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.domain.models.Result
import com.yanakudrinskaya.bookshelf.domain.models.User
import com.yanakudrinskaya.bookshelf.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val APP_PREFERENCES = "app_prefs"
const val IS_FIRST_LAUNCH = "is_first_launch"
const val KEY_USER_PROFILE = "user_profile"

class UserProfileRepositoryImpl(
    private val context: Context
) : UserProfileRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }
    private val gson = Gson()

    override suspend fun saveLocalUserProfile(user: User) {
        withContext(Dispatchers.IO) {
            try {
                val userJson = gson.toJson(user)
                sharedPreferences.edit()
                    .putString(KEY_USER_PROFILE, userJson)
                    .apply()
                Log.d("Myregister", "User profile saved successfully")

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
                } ?: Result.Failure(NoSuchElementException("User profile not found in local storage"))
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
}