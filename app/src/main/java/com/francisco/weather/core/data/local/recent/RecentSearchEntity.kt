package com.francisco.weather.core.data.local.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val name: String,
    val region: String,
    val country: String,
    val savedAt: Long,
)
