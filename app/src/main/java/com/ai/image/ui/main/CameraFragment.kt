package com.ai.image.ui.main

import android.Manifest
import com.ai.image.utility.PageStatusView.pageStatusView
import com.ai.image.ui.base.BaseFragment
import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ai.image.R
import com.camerakit.CameraKitView.PreviewListener
import com.camerakit.CameraKitView.PermissionsListener
import com.camerakit.CameraKitView.CameraListener
import android.content.Intent
import android.provider.MediaStore
import com.camerakit.CameraKitView
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import com.camerakit.CameraKit
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.ai.image.model.api.UploadApi
import com.ai.image.component.DialogCustom
import android.widget.Toast
import android.widget.TextView
import com.karumi.dexter.listener.PermissionRequest
import com.ai.image.BuildConfig
import com.ai.image.adapter.SpinnerAdapter
import com.ai.image.component.Global
import com.ai.image.databinding.FragmentCameraBinding
import org.greenrobot.eventbus.ThreadMode
import com.ai.image.model.EventModel
import com.ai.image.model.HomeModel
import com.ai.image.model.OnActivityResultModel
import com.ai.image.utility.*
import com.soundcloud.android.crop.Crop
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CameraFragment : BaseFragment() {
    private lateinit var adapterSpinner: SpinnerAdapter
    private var contInst: FragmentActivity? = null
    private lateinit var binding: FragmentCameraBinding
    private val isNeedReset = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_camera, container, false)
        contInst = activity
        permissionRead()
        binding.cameraKitView.previewListener = object : PreviewListener {
            override fun onStart() {
                Log.v("CameraKitView", "PreviewListener: onStart()")
                updateInfoText()
            }

            override fun onStop() {
                Log.v("CameraKitView", "PreviewListener: onStop()")
            }
        }
        binding.ivBack.setOnClickListener {
            val eventModel = EventModel()
            eventModel.action = EventModel.VARIABLE.ACTION_BACK
            EventBus.getDefault().post(eventModel)
        }
        binding.ivReset.setOnClickListener {
            binding.cameraKitView.onStop()
            binding.cameraKitView.onStart()
            binding.cameraKitView.onResume()
        }
        binding.flashText.setOnClickListener(flashOnOnClickListener)
        binding.facingText.setOnClickListener(facingOnClickListener)
        binding.cameraKitView.setPermissionsListener(object : PermissionsListener {
            override fun onPermissionsSuccess() {
                binding.permissionsButton.visibility = View.GONE
            }

            override fun onPermissionsFailure() {
                binding.permissionsButton.visibility = View.VISIBLE
            }
        })
        binding.cameraKitView.cameraListener = object : CameraListener {
            override fun onOpened() {
                Log.v("CameraKitView", "CameraListener: onOpened()")
            }

            override fun onClosed() {
                Log.v("CameraKitView", "CameraListener: onClosed()")
            }
        }
        binding.cameraKitView.previewListener = object : PreviewListener {
            override fun onStart() {
                Log.v("CameraKitView", "PreviewListener: onStart()")
                updateInfoText()
            }

            override fun onStop() {
                Log.v("CameraKitView", "PreviewListener: onStop()")
            }
        }
        setupSpinner()
        return binding.root
    }

    private val imageFromGallery: Unit
        private get() {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivity(intent)
        }

    private fun takeCapture() {
        permissionRead()
        binding.cameraKitView.captureImage { cameraKitView: CameraKitView?, capturedImage: ByteArray? ->
            /* new Thread(new Runnable() {
                @Override
                public void run() {
                    final Jpeg jpeg = new Jpeg(capturedImage);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setJpeg(jpeg);
                        }
                    });
                }
            });*/
            // capturedImage contains the image from the CameraKitView.
            //save file
            val savedPhoto = File(contInst!!.cacheDir, "cropped_" + "selfie")
            try {
                val outputStream = FileOutputStream(savedPhoto.path)
                outputStream.write(capturedImage)
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //crop file
            val ii = Date().time
            val destination = Uri.fromFile(File(contInst!!.cacheDir, "cropped_selfie" + ".jpg"))
            Crop.of(Uri.fromFile(savedPhoto), destination).asSquare().withMaxSize(1000, 1000)
                .start(contInst as Activity?)
        }
    }

    private val flashOnOnClickListener = View.OnClickListener {
        if (binding.cameraKitView.flash != CameraKit.FLASH_ON) {
            binding.cameraKitView.flash = CameraKit.FLASH_ON
        } else {
            binding.cameraKitView.flash = CameraKit.FLASH_OFF
        }
        updateInfoText()
    }
    private val facingOnClickListener = View.OnClickListener { toggleFacing() }
    private fun toggleFacing() {
        if (binding.cameraKitView.facing != CameraKit.FACING_BACK) {
            binding.cameraKitView.facing = CameraKit.FACING_BACK
        } else {
            binding.cameraKitView.facing = CameraKit.FACING_FRONT
        }
        updateInfoText()
    }

    private fun updateInfoText() {
        if (BuildConfig.DEBUG) {
            //binding.llInfo.visibility = View.VISIBLE
            val facingValue =
                if (binding.cameraKitView.facing == CameraKit.FACING_BACK) "BACK" else "FRONT"
            binding.facingText.text = Html.fromHtml("<b>Facing:</b> $facingValue")
            var flashValue = "OFF"
            when (binding.cameraKitView.flash) {
                CameraKit.FLASH_OFF -> {
                    flashValue = "OFF"
                }
                CameraKit.FLASH_ON -> {
                    flashValue = "ON"
                }
                CameraKit.FLASH_AUTO -> {
                    flashValue = "AUTO"
                }
                CameraKit.FLASH_TORCH -> {
                    flashValue = "TORCH"
                }
            }
            binding.flashText.text = Html.fromHtml("<b>Flash:</b> $flashValue")
            val previewSize = binding.cameraKitView.previewResolution
            if (previewSize != null) {
                binding.previewSizeText.text =
                    Html.fromHtml(String.format("<b>Preview Resolution:</b> %d x %d",
                        previewSize.width,
                        previewSize.height))
            }
            val photoSize = binding.cameraKitView.photoResolution
            if (photoSize != null) {
                binding.photoSizeText.text =
                    Html.fromHtml(String.format("<b>Photo Resolution:</b> %d x %d",
                        photoSize.width,
                        photoSize.height))
            }
        } else {
            binding.llInfo.visibility = View.GONE
        }
    }

    private fun permissionRead() {
        Dexter.withActivity(contInst)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showToast(resources.getString(R.string.pleaseAccessNeed))
                        Global.openSettings(contInst)
                        // permission is denied permenantly, navigate user to app settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken,
                ) {
                    token.continuePermissionRequest()
                }
            })
            .withErrorListener {
                //showToast("Error occurred! $error");
            }
            .onSameThread()
            .check()
    }

    private var callMain: Call<UploadApi>? = null
    var uploadingFile = false
    var dialogText: DialogCustom? = null
    /*fun setData(uri: Uri?) {
        Toast.makeText(contInst, "amadeh ersal", Toast.LENGTH_SHORT).show()
        if (true) return
        if (Global.NetworkConnection()) {
            // show dialog or progress
            var file: File? = File("")
            //uri
            if (uri != null) {
                file = FileUtils.getFile(contInst, uri)
                if (file == null) {
                    return
                }
                //file = Global.compressFile_File(file);
            } else {
                Toast.makeText(contInst, "error: uri null", Toast.LENGTH_SHORT).show()
                return
            }
            if (file == null) {
                return
            }
            val dialog: Dialog
            dialog = showDialogProgressBar(contInst)
            uploadingFile = true

            // create RequestBody instance from file
            if (file != null) {
                val type = URLConnection.guessContentTypeFromName(file.name)
            }
            var body: MultipartBody.Part? = null
            val requestFile: RequestBody =
                ProgressRequestBody(MediaType.parse("image/jpg"), file, object : UploadCallbacks {
                    override fun onProgressUpdate(percentage: Int, uploaded: Long, total: Long) {
                        progressBarGlobal!!.progress = percentage
                        tvPercentageGlobal!!.text =
                            percentage.toString() + "%" + "  " + uploaded / 1000 + "Kb/" + total / 1000 + "Kb"
                    }

                    override fun onError() {}
                    override fun onFinish() {
                        if (dialog != null) {
                            dialog.dismiss()
                        }
                    }
                })
            var fileName: String? = ""
            if (file != null) {
                fileName = file.name
            }
            body = MultipartBody.Part.createFormData("photo", fileName, requestFile)
            // MultipartBody.Part is used to send also the actual file name

            // add another part within the multipart request
            val apiService = ApiService()
            callMain = apiService.getApi().profileVerify(body)
            // call is running
            callMain!!.enqueue(object : Callback<UploadApi?> {
                override fun onResponse(call: Call<UploadApi?>, response: Response<UploadApi?>) {
                    val activity = contInst as Activity?
                    if (activity!!.isFinishing) return
                    if (response.body() != null) {
                        Toast.makeText(activity,
                            resources.getString(R.string.textVerifyAfterUpload),
                            Toast.LENGTH_SHORT).show()
                        try {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        *//*RequestOptions options = new RequestOptions()
                                    .error(R.drawable.ic_profile_user)
                                    .circleCropTransform()
                                    .priority(Priority.HIGH);
                            Glide.with(contInst)
                                    .asBitmap()
                                    .load(uri)
                                    .apply(options)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(new SimpleTarget<Bitmap>() {
                                              @Override
                                              public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                  ivImage.setImageBitmap(resource);
                                              }
                                          }
                                    );*//*
                    } else {
                        // show error connection
                        handelErrorBody(contInst!!, response.errorBody())
                        //Toast.makeText(contInst, getResources().getString(R.string.errorserver), Toast.LENGTH_LONG).show();
                    }
                    // dimis progress
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                    uploadingFile = false
                }

                override fun onFailure(call: Call<UploadApi?>, t: Throwable) {
                    // dimis progress
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                    if (!callMain!!.isCanceled) {
                        showToast(resources.getString(R.string.errorserver))
                    } else {
                        showToast(resources.getString(R.string.stopedUpload))
                    }
                    uploadingFile = false
                }
            })
        } else {
            Toast.makeText(contInst,
                "" + resources.getString(R.string.errorconnection),
                Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun showToast(data: String) {
        Toast.makeText(contInst, data + "", Toast.LENGTH_SHORT).show()
    }

    private fun dialogCancelUpload(dialogUpload: Dialog) {
        dialogText = DialogCustom(contInst, { v: View? ->
            dialogUpload.dismiss()
            dialogText!!.dismiss()
            callMain!!.cancel()
        }) { v: View? -> dialogText!!.dismiss() }
        dialogText!!.setMessag(resources.getString(R.string.textStopUpload))
        dialogText!!.setOkText(resources.getString(R.string.yes))
        dialogText!!.setCancelText(resources.getString(R.string.no))
        dialogText!!.title = resources.getString(R.string.stopUpload)
        dialogText!!.show()
    }

    private var tvPercentageGlobal: TextView? = null
    private var progressBarGlobal: ProgressBar? = null
    fun showDialogProgressBar(context: Context?): Dialog {
        val mDialog = DialogHandler().createMDialogUnClose(R.layout.dialog_progress, context)
        //TextView tvTitle = mDialog.findViewById(R.id.tvTitle);
        val tvPercentage = mDialog.findViewById<TextView>(R.id.tvPercentage)
        val tvClose = mDialog.findViewById<TextView>(R.id.tvClose)
        val progressBar = mDialog.findViewById<ProgressBar>(R.id.progressBar)
        //tvTitle.setText(title + "");
        tvPercentageGlobal = tvPercentage
        progressBarGlobal = progressBar
        tvClose.setOnClickListener { v: View? -> dialogCancelUpload(mDialog) }
        return mDialog
    }

    override fun onStart() {
        super.onStart()
        binding.cameraKitView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.cameraKitView.onResume()
    }

    override fun onPause() {
        binding.cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(`object`: Any?) {
        if (`object` is EventModel) {
            val model = `object`
            if (model.action == EventModel.VARIABLE.ACTION_CAPTURE) {
                takeCapture()
            } else if (model.action == EventModel.VARIABLE.ACTION_GALLERY) {
                imageFromGallery
            } else if (model.action == EventModel.VARIABLE.ACTION_FACEING) {
                toggleFacing()
            } else if (model.action == EventModel.VARIABLE.ACTION_FILTER) {
                val hModel: HomeModel = model.model as HomeModel
                var pos = 0
                for (i in adapterSpinner.data.indices) {
                    if (adapterSpinner.data[i].actionValue == hModel.actionValue) {
                        pos = i
                    }
                }
                binding.spinner.setSelection(pos)
            }
        } else if (`object` is OnActivityResultModel) {
            val model = `object`
            if (model.resultCode == Activity.RESULT_OK) {
                if (model.requestCode == Crop.REQUEST_CROP) {
                    binding.cameraKitView.onStop()
                    //setData(Crop.getOutput(model.intent))
                }
            }
        }
    }

    private fun setupSpinner() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val countryList: ArrayList<HomeModel> = MyConstants.getFilters()
        adapterSpinner = SpinnerAdapter(context, countryList)
        binding.spinner.adapter = adapterSpinner
    }

    private fun pageStatus(vararg visibleView: View?) {
        //if show main view pass null for params
        val viewsGone = arrayOf<View>(
            binding.layoutWatinig.rlLoading,
            binding.layoutWatinig.rlNoWifi,
            binding.layoutWatinig.rlNoData,
            binding.layoutWatinig.rlNoData2,
            binding.layoutWatinig.rlNoData3,
            binding.layoutWatinig.rlNoData4,
            binding.layoutWatinig.rlPvLoading,
            binding.layoutWatinig.rlRetry)
        pageStatusView(visibleView, viewsGone)
        /*
        binding.layoutWatinig.tvRetry.setOnClickListener(v -> getData());
        binding.layoutWatinig.tvAllTryconnection.setOnClickListener(v -> getData());
        */
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        fun newInstance(strClsCat: String?): CameraFragment {
            val fragment = CameraFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, strClsCat)
            fragment.arguments = args
            return fragment
        }
    }
}