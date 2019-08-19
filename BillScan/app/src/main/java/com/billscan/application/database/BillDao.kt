package com.billscan.application.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BillDao {

    @Insert
    fun insertBill(bill: BillEntity)

    @Update
    fun updateBill(bill: BillEntity)

    @Query("select * from bill_table where bill_id=:key")
    fun getBill(key: Long): BillEntity?


    @Query("select * from bill_table order by bill_id desc")
    fun getAllBills(): List<BillEntity>

    @Query("delete from bill_table")
    fun clearAll()

    @Query("delete from bill_table where bill_id=:key")
    fun clearBill(key: Long)

    @Query("select * from bill_table order by bill_id desc limit 1")
    fun getTopBill(): BillEntity?


}