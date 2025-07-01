package com.yanakudrinskaya.bookshelf.auth.domain

interface NetworkMonitorRepository {
    suspend fun isNetworkAvailable(): Boolean
}