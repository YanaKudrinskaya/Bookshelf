package com.yanakudrinskaya.bookshelf.auth.data

import android.content.SharedPreferences
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserSharedPrefsMapper
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository

const val APP_PREFERENCES = "app_prefs"
const val KEY_USER_PROFILE = "user_profile"

class UserProfileRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val userSharedPrefsMapper: UserSharedPrefsMapper
) : UserProfileRepository {

    private val userFlow = MutableStateFlow<Result<User>>(getLocalUserProfile())
    fun getUserFlow(): StateFlow<Result<User>> = userFlow

    override fun saveLocalUserProfile(user: User) {
        val userJson = userSharedPrefsMapper.userToJson(user)
        sharedPreferences.edit {
            putString(KEY_USER_PROFILE, userJson)
        }

        userFlow.value = Result.Success(user)
    }

    override fun getLocalUserProfile(): Result<User> {
        val userJson = sharedPreferences.getString(KEY_USER_PROFILE, null)
        return userJson?.let { json ->
            userSharedPrefsMapper.jsonToUser(json)?.let { user ->
                Result.Success(user)
            }
        } ?: run {
            Result.Error(ResponseStatus.NOT_FOUND, "User not found in local storage")
        }
    }
    override fun getLocalUserProfileStream(): Flow<Result<User>> = getUserFlow()


    override fun deleteProfile() {
        sharedPreferences.edit {
            remove(KEY_USER_PROFILE)
        }
        userFlow.value = Result.Error(ResponseStatus.NOT_FOUND, "User profile deleted")
    }
}