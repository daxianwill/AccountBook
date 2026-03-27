package com.sym.accountbook.data

import androidx.room.TypeConverter
import com.sym.accountbook.data.entity.TransactionType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(name: String): TransactionType {
        return TransactionType.valueOf(name)
    }
}
