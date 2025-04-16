package com.yanakudrinskaya.bookshelf.domain.repository

interface NetworkMonitorRepository {
    suspend fun isNetworkAvailable(): Boolean
}