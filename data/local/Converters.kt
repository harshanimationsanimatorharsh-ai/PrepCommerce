package com.prepcommerce.app.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class Converters {
    private val json = Json { ignoreUnknownKeys = true }
    @TypeConverter fun fromList(list: List<String>): String = json.encodeToString(list)
    @TypeConverter fun toList(data: String): List<String> = json.decodeFromString(data)
}
