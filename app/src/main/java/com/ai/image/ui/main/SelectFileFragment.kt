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
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.ai.image.component.DialogCustom
import android.widget.Toast
import com.karumi.dexter.listener.PermissionRequest
import com.ai.image.adapter.SpinnerAdapter
import com.ai.image.component.Global
import com.ai.image.databinding.FragmentSelectFileBinding
import com.ai.image.model.EventModel
import com.ai.image.model.HomeModel
import com.ai.image.ui.ShowResultActivity
import com.ai.image.utility.*
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList

class SelectFileFragment : BaseFragment() {
    private lateinit var adapterSpinner: SpinnerAdapter
    private var contInst: FragmentActivity? = null
    private lateinit var binding: FragmentSelectFileBinding
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
            inflater, R.layout.fragment_select_file, container, false)
        contInst = activity
        permissionRead()
        binding.ivBack.setOnClickListener {
            val eventModel = EventModel()
            eventModel.action = EventModel.VARIABLE.ACTION_BACK
            EventBus.getDefault().post(eventModel)
        }
        setupSpinner()
        binding.cvCamera.setOnClickListener {
            startDialogForSelectImage(10001)
        }
        binding.cvGallery.setOnClickListener {
            startDialogForSelectImage(10002)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //عکس اپلود شد
            val myUriImage = data!!.data
            dialogNotif()
        }
    }

    private lateinit var dialogText2: DialogCustom
    private fun dialogNotif() {
        dialogText2 = DialogCustom(contInst, {
            val intent = Intent(contInst, ShowResultActivity::class.java)
            intent.putExtra("filterName", "morph filter")
            intent.putExtra("image1",
                "https://www.gettyimages.pt/gi-resources/images/Homepage/Hero/PT/PT_hero_42_153645159.jpg")
            intent.putExtra("image2", "https://tinypng.com/images/social/website.jpg")
            startActivity(intent)
            dialogText2.dismiss()
        }) {
            dialogText2.dismiss()
        }
        dialogText2.setMessag(resources.getString(R.string.textUploadedImageAndWaitForResponse))
        dialogText2.setOkText(resources.getString(R.string.OK))
        dialogText2.setCancelText("")
        dialogText2.title = resources.getString(R.string.pleaswait)
        dialogText2.show()
    }

    private fun startDialogForSelectImage(REQUEST_CODE: Int) {
        val intent = Intent(contInst, ImportLoaderDialog::class.java)
        intent.putExtra("request", REQUEST_CODE)
        intent.putExtra("filter",
            adapterSpinner.data[binding.spinner.selectedItemPosition].actionValue)
        startActivityForResult(intent, REQUEST_CODE)
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

    /*private var callMain: Call<UploadApi>? = null
    var uploadingFile = false
    private lateinit var dialogText: DialogCustom
    fun setData(uri: Uri?) {
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
    }
    private fun dialogCancelUpload(dialogUpload: Dialog) {
        dialogText = DialogCustom(contInst, { v: View? ->
            dialogUpload.dismiss()
            dialogText.dismiss()
            callMain!!.cancel()
        }) { v: View? -> dialogText!!.dismiss() }
        dialogText.setMessag(resources.getString(R.string.textStopUpload))
        dialogText.setOkText(resources.getString(R.string.yes))
        dialogText.setCancelText(resources.getString(R.string.no))
        dialogText.title = resources.getString(R.string.stopUpload)
        dialogText.show()
    }

    private var tvPercentageGlobal: TextView? = null
    private var progressBarGlobal: ProgressBar? = null
    private fun showDialogProgressBar(context: Context?): Dialog {
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
    }*/


    private fun showToast(data: String) {
        Toast.makeText(contInst, data + "", Toast.LENGTH_SHORT).show()
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
        fun newInstance(strClsCat: String?): SelectFileFragment {
            val fragment = SelectFileFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, strClsCat)
            fragment.arguments = args
            return fragment
        }
    }
}