package com.billscan.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.billscan.application.R
import com.billscan.application.databinding.ListOfBillsFragmentBinding
import com.billscan.application.view_models.ListOfBillsViewModel

class ListOfBillsFragment : Fragment() {

    private lateinit var binding: ListOfBillsFragmentBinding
    private lateinit var viewModel: ListOfBillsViewModel

    companion object {
        fun newInstance() = ListOfBillsFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.list_of_bills_fragment, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListOfBillsViewModel::class.java)
        binding.floatingActionButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_listOfBillsFragment_to_cameraFragment))
    }

}
