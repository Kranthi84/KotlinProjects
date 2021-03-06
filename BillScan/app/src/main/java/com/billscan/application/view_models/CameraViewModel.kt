package com.billscan.application.view_models

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillEntity
import com.billscan.application.support_classes.BillImage
import kotlinx.coroutines.*
import java.util.*

class CameraViewModel(context: Context, application: Application, private var billDao: BillDao) :
    AndroidViewModel(application) {

    private var _billImage = MutableLiveData<BillImage>()
    private var mContext: Context = context
    private var _canTakePhoto = MutableLiveData<Boolean>()
    private var _isPermissionGranted = MutableLiveData<Boolean>()


    private var viewModelJob = Job()
    private var _bill = MutableLiveData<BillEntity>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _bills = MutableLiveData<List<BillEntity>>()

    val bills: LiveData<List<BillEntity>>
        get() = _bills

    val bill: LiveData<BillEntity>
        get() = _bill

    val billImage: LiveData<BillImage>
        get() = _billImage

    val canTakePhoto: LiveData<Boolean>
        get() = _canTakePhoto

    val isPermissionGranted: LiveData<Boolean>
        get() = _isPermissionGranted

    init {
        _canTakePhoto.value = false
        _isPermissionGranted.value = false
        getAllBills()
        initializeTopBill()
    }

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


    fun insertBill(imagePath: String) {

        uiScope.launch {

            val billEntity = BillEntity()
            billEntity.billDate = Date().toString()
            billEntity.billImagePath = imagePath
            insertBillIntoDatabase(billEntity)
            _bill.value = getTopBillFromDatabase()

        }

    }

    private suspend fun insertBillIntoDatabase(bill: BillEntity) {

        return withContext(Dispatchers.IO) {
            billDao.insertBill(bill)
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

    fun createBillImage() {
        _billImage.value = BillImage(mContext)
    }

    fun updateCanTakePhoto(flag: Boolean) {
        _canTakePhoto.value = flag
    }

    fun updatePermission(flag: Boolean) {
        _isPermissionGranted.value = flag
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
