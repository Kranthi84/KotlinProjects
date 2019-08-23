package com.billscan.application.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillEntity
import kotlinx.coroutines.*


class ListOfBillsViewModel(application: Application, private var billDao: BillDao) :
    AndroidViewModel(application) {

    init {

    }


    private var viewModelJob = Job()
    private var _bill = MutableLiveData<BillEntity>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _bills = MutableLiveData<List<BillEntity>>()

    val bills: LiveData<List<BillEntity>>
        get() = _bills

    val bill: LiveData<BillEntity>
        get() = _bill


    fun initializeTopBill() {


        uiScope.launch {
            _bill.value = getTopBillFromDatabase()
        }


    }

    private suspend fun getTopBillFromDatabase(): BillEntity? {

        return withContext(Dispatchers.IO) {
            billDao.getTopBill()
        }

    }

    fun getAllBills() {


        uiScope.launch {
            _bills.value = getAllBillsfromDatabase()

        }


    }

    private suspend fun getAllBillsfromDatabase(): List<BillEntity> {
        return withContext(Dispatchers.IO) {
            billDao.getAllBills()
        }
    }

    fun clearBill(id: Long) {
        uiScope.launch {
            clearBillFromDatabase(id)
        }
    }

    private suspend fun clearBillFromDatabase(id: Long) {
        return withContext(Dispatchers.IO) {
            billDao.clearBill(id)
        }
    }



}
