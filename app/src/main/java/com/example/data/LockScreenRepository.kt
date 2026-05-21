package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LockScreenRepository(private val dao: LockScreenDao) {
    
    // Provide a continuous flow, defaulting to an initial LockScreenConfig if record doesn't exist yet
    val configFlow: Flow<LockScreenConfig> = dao.getConfigFlow().map { it ?: LockScreenConfig() }

    suspend fun saveConfig(config: LockScreenConfig) {
        dao.saveConfig(config)
    }
}
