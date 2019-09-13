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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billscan.application.R
import com.billscan.application.adapters.TextViewRecyclerAdapter
import com.billscan.application.database.BillDatabase
import com.billscan.application.database.BillEntity
import com.billscan.application.databinding.CameraFragmentBinding
import com.billscan.application.support_classes.ImageText
import com.billscan.application.utils.FirebaseUtil
import com.billscan.application.utils.PictureUtils
import com.billscan.application.view_models.CameraViewModel
import com.billscan.application.view_models.CameraViewModelFactory
import kotlinx.android.synthetic.main.dialog_find_text_layout.view.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import java.io.File

class CameraFragment : Fragment(), FirebaseUtil.View {


    private lateinit var binding: CameraFragmentBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var file: File? = null
    private lateinit var uri: Uri
    private val CAMERA_REQUEST_CODE = 1
    private val TAKE_PHOTO = 2
    private val MY_PERMISSIONS_REQUEST_CAMERA = 3
    private var myMenu: Menu? = null


    companion object {
        fun newInstance() = CameraFragment()
    }

    private lateinit var viewModel: CameraViewModel
    private lateinit var viewModelFactory: CameraViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_cameraFragment_to_listOfBillsFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
        firebaseUtil = FirebaseUtil(this)


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
        grantPermissions()
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
                viewModel.setImageVisible(true)

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
            viewModel.setGalleryImagePicked(false)
            viewModel.canTakePhoto.value?.let {
                if (it) {
                    takeAnImage(intent)
                }
            }
        }



        binding.galleryButton.setOnClickListener {
            viewModel.createBillImage()
            viewModel.setGalleryImagePicked(true)
            pickAnImage()
        }

        binding.findTextButton.setOnClickListener {
            it?.let {
                val billEntity = viewModel.billWithId.value
                billEntity?.let { bill ->
                    val bitMap = createBitmapFrompath(bill)
                    firebaseUtil.runTextRecogmition(bitMap)
                }

                if (billEntity == null) {
                    val bitmap = viewModel.bitMapImage.value
                    bitmap?.let { image ->
                        firebaseUtil.runTextRecogmition(image)
                    }
                }
            }
        }

        arguments?.let {

            if (!it.isEmpty) {
                val args = CameraFragmentArgs.fromBundle(it)
                viewModel.getBillWithId(args.billId)
            }

        }

        viewModel.billWithId.observe(this, Observer {
            it?.let {
                val bitmap = createBitmapFrompath(it)
                binding.imageView.setImageBitmap(bitmap)
                viewModel.setImageVisible(true)
            }
        })

        viewModel.isImageVisible.observe(this, Observer {
            it?.let {
                when {
                    it -> {
                        binding.findTextButton.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.findTextButton.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.imageText.observe(this, Observer {
            alertDialogBuilderForFindingText(it)?.let { builder ->
                builder.create().apply {
                    this.setCanceledOnTouchOutside(false)
                    this.show()
                }
            }

        })


        viewModel.isImageGalleryPicked.observe(this, Observer { bool ->
            bool?.let { internalBool ->
                myMenu?.let {
                    it.findItem(R.id.save_menu).isVisible = !internalBool
                }

            }
        })


        setHasOptionsMenu(true)

        return binding.root
    }

    private fun createBitmapFrompath(billEntity: BillEntity): Bitmap {
        var selectedImage =
            PictureUtils.getScaledBitmap(billEntity.billImagePath, requireActivity())
        selectedImage = PictureUtils.rotateTheImage(selectedImage, 90f)!!
        return selectedImage
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
                selectedImage?.let {
                    viewModel.updateBitmap(selectedImage)
                }
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
            viewModel.updateBitmapAndRotate(selectedImage)
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


    private fun grantPermissions() =// Here, thisActivity is the current activity
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
        myMenu = menu
        inflater.inflate(R.menu.camera_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                val bill = viewModel.bill.value
                bill?.let {
                    alertDialogBuilder()?.let {
                        it.create()?.apply {
                            this.setCanceledOnTouchOutside(false)
                            this.show()
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun alertDialogBuilder(): AlertDialog.Builder? {

        val view = layoutInflater.inflate(R.layout.dialog_layout, null)
        var builder: AlertDialog.Builder? = null
        activity?.let {
            builder = AlertDialog.Builder(it)
            builder?.apply {
                setView(view)
                setPositiveButton(
                    R.string.save_string
                ) { dialogInterface, i ->

                    val name = view.edit_text_bill_name.text.toString()
                    viewModel.updateBill(true, name)

                }

                setNegativeButton(
                    R.string.cancel_string
                )
                { dialoginterface, i ->
                    dialoginterface.cancel()
                }
            }
        }
        return builder
    }

    fun alertDialogBuilderForFindingText(imageText: ImageText): AlertDialog.Builder? {

        val view = layoutInflater.inflate(R.layout.dialog_find_text_layout, null)
        view.tv_survey.text = imageText.surveyText

        if (imageText.websites.isNotEmpty()) {
            view.tv_info.visibility = View.VISIBLE
        } else {
            view.tv_info.visibility = View.GONE
        }

        val textViewAdapter = TextViewRecyclerAdapter(imageText.websites,
            TextViewRecyclerAdapter.TextViewListener { website ->
                val uri = Uri.parse(website)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.resolveActivity(activity!!.packageManager!!)?.let {
                    startActivity(intent)
                }
            })
        view.textView_Recycler.layoutManager = LinearLayoutManager(context)
        view.textView_Recycler.adapter = textViewAdapter

        var builder: AlertDialog.Builder? = null
        activity?.let {
            builder = AlertDialog.Builder(it)
            builder?.apply {
                setView(view)
                setPositiveButton(
                    R.string.ok_string
                ) { dialogInterface, i ->

                    dialogInterface.dismiss()

                }

            }
        }
        return builder
    }

    override fun showNoTextMessage() {
        Toast.makeText(activity, "No Text", Toast.LENGTH_SHORT).show()
    }

    override fun showHandle(textList: List<String>) {

        viewModel.addToTheList(textList)
    }


    override fun showProgress() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressCircular.visibility = View.GONE
    }
}


