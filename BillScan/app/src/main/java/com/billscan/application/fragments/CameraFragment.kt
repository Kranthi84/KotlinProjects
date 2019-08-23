package com.billscan.application.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.billscan.application.R
import com.billscan.application.database.BillDatabase
import com.billscan.application.databinding.CameraFragmentBinding
import com.billscan.application.utils.PictureUtils
import com.billscan.application.view_models.CameraViewModel
import com.billscan.application.view_models.CameraViewModelFactory
import java.io.File

class CameraFragment : Fragment() {

    private lateinit var binding: CameraFragmentBinding
    private var file: File? = null
    private lateinit var uri: Uri
    private val CAMERA_REQUEST_CODE = 1
    private val TAKE_PHOTO = 2
    private val MY_PERMISSIONS_REQUEST_CAMERA = 3

    companion object {
        fun newInstance() = CameraFragment()
    }

    private lateinit var viewModel: CameraViewModel
    private lateinit var viewModelFactory: CameraViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        //View Model Created
        val application = requireNotNull(this.activity!!.application)
        val billDao = BillDatabase.getInstance(application).billDao
        viewModelFactory = CameraViewModelFactory(context!!, application, billDao)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CameraViewModel::class.java)
        binding.lifecycleOwner = this

        viewModel.billImage.observe(this, Observer {
            this.file = it.photoFile()!!
            this.uri = FileProvider.getUriForFile(
                context!!,
                "com.billscan.application.fileprovider",
                this.file!!
            )
            if (this.file != null && intent.resolveActivity(activity!!.packageManager) != null) {
                viewModel.updateCanTakePhoto(true)
            } else {
                viewModel.updateCanTakePhoto(false)
            }
        })

        viewModel.bitMapImage.observe(this, Observer {
            it?.let {
                binding.imageView.setImageBitmap(it)
            }
        })

        viewModel.bills.observe(this, Observer {
            it?.let {
                for (i in it) {
                    Log.d("The path for bill# " + i.billNum, i.billImagePath)
                }
            }
        })

        binding.cameraButton.setOnClickListener {
            viewModel.createBillImage()

            viewModel.canTakePhoto.value?.let {
                if (it) {
                    takeAnImage(intent)
                }
            }
        }


        setHasOptionsMenu(true)

        return binding.root
    }


    private fun takeAnImage(intent: Intent) {

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val cameraActivities: List<ResolveInfo> =
            activity!!.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

        for (resolveInfo in cameraActivities) activity!!.grantUriPermission(
            activity!!.applicationInfo.packageName,
            this.uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        viewModel.isPermissionGranted.observe(this, Observer {

            if (it) {
                startActivityForResult(intent, TAKE_PHOTO)
            } else
                grantPermissions()

        })

        viewModel.bill.observe(this, Observer {
            it?.let {

            }
        })
    }

    private fun pickAnImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                showImage(resultCode, data)
            }

            TAKE_PHOTO -> {
                showImageFromCamera(resultCode, data)
            }
        }
    }

    private fun showImage(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                val selectedImage = getSelectedImage(it)
                binding.imageView.setImageBitmap(selectedImage)
            }
        }
    }

    private fun showImageFromCamera(resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            this.uri.let {
                activity!!.revokeUriPermission(it, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            viewModel.insertBill(this.file!!.path)
            val selectedImage = PictureUtils.getScaledBitmap(this.file!!.path, activity!!)
            viewModel.updateBitmap(selectedImage)

            /*     val rotatedImage = PictureUtils.rotateTheImage(this.file!!.path, selectedImage)
                 rotatedImage?.let {
                     binding.imageView.setImageBitmap(selectedImage)
                 }*/

        }
    }

    private fun getSelectedImage(it: Uri): Bitmap? {
        return getBitmap(it)?.let { it1 ->
            val scaleFactor = Math.max(
                it1.width.toFloat() / binding.imageView.width.toFloat(),
                it1.height.toFloat() / binding.imageView.width.toFloat()
            )
            Bitmap.createScaledBitmap(
                it1,
                (it1.width / scaleFactor).toInt(),
                (it1.height / scaleFactor).toInt(),
                true
            )
        }
    }

    private fun getBitmap(it: Uri) =
        MediaStore.Images.Media.getBitmap(activity?.contentResolver, it)


    private fun grantPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        } else {
            viewModel.updatePermission(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    viewModel.updatePermission(true)
                else
                    viewModel.updatePermission(false)

                return
            }

            else -> {
                viewModel.updatePermission(false)
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.camera_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                viewModel.updateBill(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}


