package com.billscan.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.billscan.application.R
import com.billscan.application.adapters.ImageRecyclerAdapter
import com.billscan.application.database.BillDatabase
import com.billscan.application.databinding.ListOfBillsFragmentBinding
import com.billscan.application.view_models.ListOfBillsViewModel
import com.billscan.application.view_models.ListOfBillsViewModelFactory
import java.io.File

class ListOfBillsFragment : Fragment() {

    private lateinit var binding: ListOfBillsFragmentBinding
    private lateinit var viewModel: ListOfBillsViewModel
    var recyclerView: RecyclerView? = null
    var adapter: ImageRecyclerAdapter? = null

    companion object {
        fun newInstance() = ListOfBillsFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.list_of_bills_fragment, container, false)
        recyclerView = binding.idCameraView
        recyclerView?.let {
            it.layoutManager = LinearLayoutManager(this.context)
        }



        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireNotNull(this.activity!!.application)
        val billDao = BillDatabase.getInstance(application).billDao
        viewModel = ViewModelProviders.of(this, ListOfBillsViewModelFactory(application, billDao))
            .get(ListOfBillsViewModel::class.java)
        binding.floatingActionButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_listOfBillsFragment_to_cameraFragment))
        binding.lifecycleOwner = this

        viewModel.initializeTopBill()

        viewModel.getAllBills()

        viewModel.bill.observe(this, Observer {

            it?.let {
                if (!it.billFlag) {
                    File(it.billImagePath).delete()
                    viewModel.clearBill(it.billNum)
                }
            }
        })

        viewModel.bills.observe(this, Observer { list ->
            list?.let {
                adapter =
                    ImageRecyclerAdapter(list, ImageRecyclerAdapter.BillsSelectListener { billId ->
                        run {
                            findNavController().navigate(
                                ListOfBillsFragmentDirections.actionListOfBillsFragmentToCameraFragment(billId)
                            )
                        }
                    })

            }

            recyclerView?.let {
                it.adapter = adapter
            }
        })
    }

}
