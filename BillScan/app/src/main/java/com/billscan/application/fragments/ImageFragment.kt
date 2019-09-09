package com.billscan.application.fragments

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.billscan.application.R
import com.billscan.application.database.BillDatabase
import com.billscan.application.database.BillEntity
import com.billscan.application.utils.FirebaseUtil
import com.billscan.application.utils.GraphicOverlay
import com.billscan.application.utils.PictureUtils
import com.billscan.application.view_models.ImageViewModel
import com.billscan.application.view_models.ImageViewModelFactory
import kotlinx.android.synthetic.main.image_layout.*

class ImageFragment : Fragment() {

    private lateinit var overlay: GraphicOverlay
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Model Created
        val application = requireNotNull(this.activity!!.application)
        val billDao = BillDatabase.getInstance(application).billDao
        val viewModelFactory = ImageViewModelFactory(application, billDao)

        imageViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ImageViewModel::class.java)
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_imageFragment_to_cameraFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.image_layout, container, false)
        overlay = view.findViewById(R.id.overlay)

        progressBar = view.findViewById(R.id.progressBar)
        
        arguments?.let {

            if (!it.isEmpty) {
                val args = ImageFragmentArgs.fromBundle(it)
                imageViewModel.getBillWithId(args.billNum)
            }

        }

        imageViewModel.billWithId.observe(this, Observer {
            it?.let {
                overlay.clear()

                val bitmap = createBitmapFrompath(it)
                justImageView.setImageBitmap(bitmap)
                firebaseUtil.runTextRecogmition(bitmap)
            }


        })

        return view
    }



    override fun onResume() {
        super.onResume()
        /*overlay.clear()
        val billEntity = viewModel.billWithId.value
        billEntity?.let { billEntity ->
            val bitmap = createBitmapFrompath(billEntity)
            firebaseUtil.runTextRecogmition(bitmap)
        }*/
    }

    private fun createBitmapFrompath(billEntity: BillEntity): Bitmap {
        var selectedImage =
            PictureUtils.getScaledBitmap(billEntity.billImagePath, requireActivity())
        selectedImage = PictureUtils.rotateTheImage(selectedImage, 90f)!!
        return selectedImage
    }
}