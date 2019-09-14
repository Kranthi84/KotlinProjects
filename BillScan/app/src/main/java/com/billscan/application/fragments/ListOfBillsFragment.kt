package com.billscan.application.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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


class ListOfBillsFragment : Fragment(), SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchText: String?): Boolean {
        val newText = searchText?.toLowerCase()
        newText?.let {
            viewModel.searchText(newText)
            adapter?.let {
                it.updateList(viewModel.searchedBills.value)
            }

        }
        return true
    }

    private lateinit var binding: ListOfBillsFragmentBinding
    private lateinit var viewModel: ListOfBillsViewModel
    var recyclerView: RecyclerView? = null
    var adapter: ImageRecyclerAdapter? = null
    var searchView: SearchView? = null

    companion object {
        fun newInstance() = ListOfBillsFragment()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllBills()
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
        setHasOptionsMenu(true)
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
                                ListOfBillsFragmentDirections.actionListOfBillsFragmentToCameraFragment(
                                    billId
                                )
                            )
                        }
                    }, ImageRecyclerAdapter.BillsSelectListener { billId ->
                        viewModel.clearBill(billId)
                    })

            }

            recyclerView?.let {
                it.adapter = adapter
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        menu.findItem(R.id.search_view).actionView?.let {
            searchView = it as SearchView
            val sView = searchView
            sView?.maxWidth = Int.MAX_VALUE
            sView?.setOnQueryTextListener(this)
        }


    }


}
