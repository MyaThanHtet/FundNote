package com.mth.bettingnote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "noteTbl")
data class Note(
    @PrimaryKey
    var id: Int?,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "amount")
    var amount: String,

    @ColumnInfo(name = "ispaid")
    var isPaid: Boolean,

    @ColumnInfo(name = "desc")
    var description: String
)
