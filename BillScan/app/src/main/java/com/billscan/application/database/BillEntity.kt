package com.billscan.application.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bill_table", indices = [Index(value = ["bill_image_path"], unique = true)])
data class BillEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bill_id")
    var billNum: Long = 0L,

    @ColumnInfo(name = "bill_date")
    var billDate: String = "",

    @ColumnInfo(name = "bill_name")
    var billName: String = "",

    @ColumnInfo(name = "bill_image_path")
    var billImagePath: String = ""

)