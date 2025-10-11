package com.yanakudrinskaya.bookshelf.auth.data.mappers

import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.auth.domain.models.User

class UserSharedPrefsMapper(private val gson: Gson) {

    fun userToJson(user: User): String {
        return gson.toJson(user)
    }

    fun jsonToUser(json: String): User? {
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}