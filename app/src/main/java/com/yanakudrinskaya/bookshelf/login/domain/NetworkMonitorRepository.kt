package com.yanakudrinskaya.bookshelf.login.domain

interface NetworkMonitorRepository {
    suspend fun isNetworkAvailable(): Boolean
}