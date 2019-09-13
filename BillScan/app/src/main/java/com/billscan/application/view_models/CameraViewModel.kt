package com.billscan.application.view_models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.billscan.application.database.BillDao
import com.billscan.application.database.BillEntity
import com.billscan.application.support_classes.BillImage
import com.billscan.application.support_classes.ImageText
import com.billscan.application.utils.PictureUtils
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class CameraViewModel(context: Context, application: Application, private var billDao: BillDao) :
    AndroidViewModel(application) {

    private var _isImageVisible = MutableLiveData<Boolean>()
    private var _billImage = MutableLiveData<BillImage>()
    private var mContext: Context = context
    private var _canTakePhoto = MutableLiveData<Boolean>()
    private var _isPermissionGranted = MutableLiveData<Boolean>()
    private var _bitMapImage = MutableLiveData<Bitmap>()
    private var _listOfTexts = MutableLiveData<MutableList<String>>()
    private var _listOfWebsites = MutableLiveData<MutableList<String>>()
    private var viewModelJob = Job()
    private var _bill = MutableLiveData<BillEntity>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _bills = MutableLiveData<List<BillEntity>>()

    private var _billWithId = MutableLiveData<BillEntity>()

    private var _imageText = MutableLiveData<ImageText>()

    private var _isImageGalleryPicked = MutableLiveData<Boolean>()

    val isImageGalleryPicked: LiveData<Boolean>
        get() = _isImageGalleryPicked

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

    val listOfTexts: LiveData<MutableList<String>>
        get() = _listOfTexts

    val imageText: LiveData<ImageText>
        get() = _imageText

    val listOfWebSites: LiveData<MutableList<String>>
        get() = _listOfWebsites

    init {
        _canTakePhoto.value = false
        _isPermissionGranted.value = false
        _isImageVisible.value = false
        _listOfTexts.value = ArrayList()
        _listOfWebsites.value = ArrayList()
        _isImageGalleryPicked.value = false
        getAllBills()
        initializeTopBill()
    }

    fun addToTheList(textList: List<String>) {

        _listOfTexts.value?.clear()
        _listOfWebsites.value?.clear()
        _listOfTexts.value?.addAll(textList)

        textList.forEach {
            if (looksLikeHandle(it)) {
                _listOfWebsites.value?.add(it)
            }


        }

        createImageText()

    }

    fun createImageText() {
        var finalList = ArrayList<String>()
        _listOfWebsites.value?.let {
            if (it.isNotEmpty()) {
                // Fetch the text after last website link in the text.
                val indexOfLastWebsiteLink = _listOfTexts.value?.indexOf(it.last())
                val sBuilder = StringBuilder()
                val indexOflastString = _listOfTexts.value?.indexOf(_listOfTexts.value?.last())

                var i = indexOfLastWebsiteLink?.plus(1)

                while (i!! <= indexOflastString!!) {
                    sBuilder.append(_listOfTexts.value!![i])
                    i = i.plus(1)
                }
                //  val lastWebsite = it[it.size.minus(1)]
                val surveyText = sBuilder.toString()


                for (website in it) {
                    if (!URLUtil.isValidUrl(website)) {
                        var authority = website
                        var pathParam = ""
                        if (website.contains("/")) {
                            val arr = website.split("/")

                            authority = arr[0]

                            if (arr.size == 2)
                                pathParam = arr[1]
                        }
                        val url =
                            Uri.Builder().scheme("https").authority(authority).appendPath(pathParam)
                                .build().toString()
                        finalList.add(url)
                    } else {
                        finalList.add(website)
                    }
                }



                _imageText.value = ImageText(finalList, surveyText, 0.00)
            } else {
                _imageText.value = ImageText(_listOfWebsites.value!!, "No Websites", 0.00)
            }

        }
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

    fun updateBitmapAndRotate(bitmap: Bitmap) {
        _bitMapImage.value = PictureUtils.run { rotateTheImage(bitmap, 90f) }
    }

    fun updateBitmap(bitmap: Bitmap) {
        _bitMapImage.value = bitmap
    }

    fun setGalleryImagePicked(flag: Boolean) {
        _isImageGalleryPicked.value = flag
    }

    private fun looksLikeHandle(text: String) =
        text.matches(Regex("^(https?://)?(www\\.)?([\\w]+\\.)+[‌​\\w]{2,63}?/?[\\w]{0,100}?$"))

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
