package com.billscan.application.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillEntity
import kotlinx.coroutines.*

class ImageViewModel(application: Application, private val billDao: BillDao) :
    AndroidViewModel(application) {



    private var _billWithId = MutableLiveData<BillEntity>()
    val billWithId: LiveData<BillEntity>
        get() = _billWithId
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    fun getBillWithId(id: Long) {
        uiScope.launch {
            _billWithId.value = getBillWithIdfromDatabase(id)
        }
    }

    private suspend fun getBillWithIdfromDatabase(id: Long): BillEntity? {
        return withContext(Dispatchers.IO) {
            billDao.getBill(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}