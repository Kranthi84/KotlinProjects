package com.billscan.application.view_models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillEntity
import com.billscan.application.support_classes.BillImage
import com.billscan.application.utils.PictureUtils
import kotlinx.coroutines.*
import java.util.*

class CameraViewModel(context: Context, application: Application, private var billDao: BillDao) :
    AndroidViewModel(application) {

    private var _isImageVisible = MutableLiveData<Boolean>()
    private var _billImage = MutableLiveData<BillImage>()
    private var mContext: Context = context
    private var _canTakePhoto = MutableLiveData<Boolean>()
    private var _isPermissionGranted = MutableLiveData<Boolean>()
    private var _bitMapImage = MutableLiveData<Bitmap>()


    private var viewModelJob = Job()
    private var _bill = MutableLiveData<BillEntity>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _bills = MutableLiveData<List<BillEntity>>()

    private var _billWithId = MutableLiveData<BillEntity>()

    val isImageVisible: LiveData<Boolean>
        get() = _isImageVisible

    val billWithId: LiveData<BillEntity>
        get() = _billWithId

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

    val bitMapImage: LiveData<Bitmap>
        get() = _bitMapImage

    init {
        _canTakePhoto.value = false
        _isPermissionGranted.value = false
        _isImageVisible.value = false
        getAllBills()
        initializeTopBill()
    }

    fun initializeTopBill() {

        uiScope.launch {
            _bill.value = getTopBillFromDatabase()
        }

    }

    fun setImageVisible(flag: Boolean) {
        _isImageVisible.value = flag
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


    fun updateBill(flag: Boolean, name: String) {
        uiScope.launch {
            _bill.value?.let {
                it.billFlag = flag
                it.billName = name
                updateBillinDatabase(it)
                _bill.value = getTopBillFromDatabase()
            }

            _bill.value = getTopBillFromDatabase()
        }
    }

    private suspend fun updateBillinDatabase(billEntity: BillEntity) {
        return withContext(Dispatchers.IO) {
            billDao.updateBill(billEntity)
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

    fun updateBitmap(bitmap: Bitmap) {
        _bitMapImage.value = PictureUtils.run { rotateTheImage(bitmap, 90f) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
