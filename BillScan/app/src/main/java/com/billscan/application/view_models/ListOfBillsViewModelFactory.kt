package com.billscan.application.view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.billscan.application.database.BillDao

class ListOfBillsViewModelFactory(
    private var application: Application,
    private var billDao: BillDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListOfBillsViewModel::class.java)) {
            return ListOfBillsViewModel(application, billDao) as T
        }

        throw IllegalArgumentException("Unknown View Model Class.")
    }

}