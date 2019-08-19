package com.billscan.application

import android.util.Log
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillDatabase
import com.billscan.application.database.BillEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var billDb: BillDatabase
    private lateinit var billDao: BillDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        billDb = Room.inMemoryDatabaseBuilder(context, BillDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        billDao = billDb.billDao
    }

    @Test
    @Throws(IOException::class)
    fun insertAndGet() {
        val bill = BillEntity()
        billDao.insertBill(bill)
        val billFromDb = billDao.getTopBill()
        Log.i("Test", "${billFromDb?.billnum}")
        assertEquals(billFromDb?.billdate, "")

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        billDb.close()
    }


}
