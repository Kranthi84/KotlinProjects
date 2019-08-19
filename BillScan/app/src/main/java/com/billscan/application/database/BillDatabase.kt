package com.billscan.application.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BillEntity::class], version = 3, exportSchema = false)
abstract class BillDatabase : RoomDatabase() {

    abstract val billDao: BillDao

    companion object {

        @Volatile
        private var INSTANCE: BillDatabase? = null

        fun getInstance(context: Context): BillDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance =
                        Room.databaseBuilder(context.applicationContext, BillDatabase::class.java, "bill_database")
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}