package com.diegulog.intellifit.data.repository.local.database

import androidx.room.TypeConverter
import com.diegulog.intellifit.domain.entity.KeyPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun keyPointsFromString(value: String): List<KeyPoint> {
        val listType: Type = object : TypeToken<List<KeyPoint>>(){}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun stringFromKeyPoints(list: List<KeyPoint>): String {
        return Gson().toJson(list)
    }

}