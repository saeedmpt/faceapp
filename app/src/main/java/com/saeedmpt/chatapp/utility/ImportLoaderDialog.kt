package com.saeedmpt.chatapp.utility

import android.Manifest
import com.saeedmpt.chatapp.utility.MyUtils.handelErrorBody
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.saeedmpt.chatapp.R
import android.content.Intent
import android.view.ViewGroup
import android.graphics.drawable.ColorDrawable
import pl.aprilapps.easyphotopicker.EasyImage
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.MultiplePermissionsReport
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import com.karumi.dexter.PermissionToken
import android.widget.Toast
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import com.saeedmpt.chatapp.model.api.UploadApi
import com.saeedmpt.chatapp.component.DialogCustom
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.saeedmpt.chatapp.component.ProgressRequestBody
import com.saeedmpt.chatapp.component.ProgressRequestBody.UploadCallbacks
import com.saeedmpt.chatapp.net.ApiService
import android.widget.TextView
import com.karumi.dexter.listener.PermissionRequest
import com.saeedmpt.chatapp.component.FileUtils
import com.saeedmpt.chatapp.component.Global
import com.saeedmpt.chatapp.databinding.DialogImportLoaderBinding
import com.soundcloud.android.crop.Crop
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.net.URLConnection
import java.util.*

class ImportLoaderDialog : AppCompatActivity() {
    var bitmap: Bitmap? = null
    var contInst: Context? = null
    private var requestCode = 0
    private var filter: String = ""
    private lateinit var binding: DialogImportLoaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_import_loader)
        contInst = this
        val intent = intent
        requestCode = intent.getIntExtra("request", -1)
        filter = intent.getStringExtra("filter").toString()
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val wmlp = window.attributes
        wmlp.gravity = Gravity.CENTER
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        EasyImage.configuration(contInst)
        //initi()
        if (requestCode == 10001) {
            getAccess(1)
        } else if (requestCode == 10002) {
            getAccess(2)
        } else {
            finish()
        }
    }

    /* private fun initi() {
         if (!hasImage || requestCode == REQUEST_CODE_IMAGE1) {
             binding.tvRemovePhoto.visibility = View.GONE
             binding.vRemovePhoto.visibility = View.GONE
             binding.tvNewPhoto.visibility = View.GONE
             binding.vNewPhoto.visibility = View.GONE
             binding.tvChooseFromGallery.visibility = View.VISIBLE
             binding.vChooseFromGallery.visibility = View.VISIBLE
             binding.tvTakePhoto.visibility = View.VISIBLE
             binding.vTakePhoto.visibility = View.VISIBLE
         } else {
             binding.tvChooseFromGallery.visibility = View.GONE
             binding.vChooseFromGallery.visibility = View.GONE
             binding.tvTakePhoto.visibility = View.GONE
             binding.vTakePhoto.visibility = View.GONE
         }
     }
 */
    private fun getAccess(type: Int) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (type == 1) {
                            openCamera()
                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, TAKE_PICTURE);*/
                        } else {
                            Crop.pickImage(contInst as Activity?)
                        }
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
            .withErrorListener { showToast("Error occurred! \$error") }
            .onSameThread()
            .check()
    }

    private fun openCamera() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        EasyImage.openCamera(contInst as Activity?, 0)
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
            .withErrorListener { showToast("Error occurred! \$error") }
            .onSameThread()
            .check()
    }

    private fun showToast(data: String) {
        Toast.makeText(contInst, data + "", Toast.LENGTH_SHORT).show()
    }

    fun onClick_take_ImageLoader(v: View?) {
        getAccess(1)
    }

    fun onClick_new_photo(v: View?) {
        //hasImage = false
        //initi()
    }

    fun onClick_remove_photo(v: View?) {
        setData(null, null, -1)
    }

    fun onClick_finish(v: View?) {
        onBackPressed()
    }

    fun onClick_import_ImageLoader(v: View?) {
        /*try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/ *");
            startActivityForResult(photoPickerIntent, SELECT_PICTURE);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setType("image/ *");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }*/
        getAccess(2)
    }

    //font
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode == RESULT_OK) {
            val ii = Date().time
            if (requestCode == TAKE_PICTURE) {
                //EasyImage.openCamera(this, 0);
                try {
                    val photo = result!!.extras!!["data"] as Bitmap?
                    val tempUri = getImageUri(contInst, photo)
                    val destination = Uri.fromFile(File(cacheDir, "cropped_Take_Photo$ii"))
                    Crop.of(tempUri, destination).asSquare().withMaxSize(1000, 1000).start(this)
                } catch (e: Exception) {
                }
            }
            if (requestCode == Crop.REQUEST_PICK) {
                val destination = Uri.fromFile(File(cacheDir, "cropped_REQUEST_PICK$ii"))
                Crop.of(result!!.data, destination).asSquare().withMaxSize(1000, 1000).start(this)
            } else if (requestCode == Crop.REQUEST_CROP) {
                //Toast.makeText(conInst, "ok", Toast.LENGTH_SHORT).show();
                /*Intent returnIntent = new Intent();
                returnIntent.setData(Crop.getOutput(result));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();*/
                setData(Crop.getOutput(result), null, 1)
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
        } else if (resultCode == RESULT_CANCELED) {
            finish()
        }
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            result,
            this,
            object : DefaultCallback() {
                override fun onImagePickerError(e: Exception, source: ImageSource, type: Int) {
                    //Some error handling
                    e.printStackTrace()
                }

                override fun onImagePicked(imageFile: File, source: ImageSource, type: Int) {
                    //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                    //ImageView iv = findViewById(R.id.ivImage);
                    //iv.setImageBitmap(bitmap);
                    val vall = FileUtils.getUri(imageFile)
                    //setData(uri.toString(), imageFile, 1);
                    val ii = Date().time
                    val destination = Uri.fromFile(File(cacheDir, "cropped_photo$ii"))
                    Crop.of(vall, destination).withMaxSize(1000, 1000).asSquare()
                        .start(contInst as Activity?)
                }

                override fun onCanceled(source: ImageSource, type: Int) {
                    //Cancel handling, you might wanna remove taken photo if it was canceled
                    if (source == ImageSource.CAMERA) {
                        val photoFile = EasyImage.lastlyTakenButCanceledPhoto(contInst)
                        photoFile?.delete()
                    }
                }
            })
    }


    private fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext!!.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private var callMain: Call<UploadApi>? = null
    private val pathImage = ""
    var uploadingFile = false
    var dialogText: DialogCustom? = null
    private fun setData(uri: Uri?, fileee: File?, type2: Int) {
        if (Global.NetworkConnection()) {
            // show dialog or progress
            var file: File? = File("")
            if (type2 == 1) {
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
            } else {
                //file
                file = fileee
            }
            val dialog: Dialog = showDialogProgressBar(contInst, type2)
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
                            finish()
                        }
                    }
                })

            assert(filter != null)
            val filterParam = RequestBody.create(
                MediaType.parse("multipart/form-data"), filter
            )
            val notificationToken = RequestBody.create(
                MediaType.parse("multipart/form-data"), PaperBook.firebaseToken
            )
            var fileName: String? = ""
            if (file != null) {
                fileName = file.name
            }
            body = MultipartBody.Part.createFormData(MyConstants.param_file, fileName, requestFile)
            // MultipartBody.Part is used to send also the actual file name

            // add another part within the multipart request
            val apiService = ApiService()
            callMain = apiService.getApi().profileUpload(body, filterParam, notificationToken)
            // call is running
            callMain!!.enqueue(object : Callback<UploadApi> {
                override fun onResponse(call: Call<UploadApi>, response: Response<UploadApi>) {
                    val activity = contInst as Activity?
                    if (activity!!.isFinishing) return
                    if (response != null) {
                        if (type2 == -1) {
                            //isRemoveImage
                            val returnIntent = Intent()
                            returnIntent.putExtra("type", TYPE_REMOVE)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        } else {
                            if (response.body()?.path != null) {
                                //Toast.makeText(activity, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                val returnIntent = Intent()
                                returnIntent.data = uri
                                returnIntent.putExtra("path", response.body()!!.path)
                                setResult(RESULT_OK, returnIntent)
                                finish()
                                /*RequestOptions options = new RequestOptions()
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
                                    );*/
                            } else {
                                // error webwervice connection
                                handelErrorBody(contInst!!, response.errorBody())

                                //Toast.makeText(contInst, response.body().getMsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        // show error connection
                        handelErrorBody(contInst!!, response.errorBody())
                        //Toast.makeText(contInst, getResources().getString(R.string.errorserver), Toast.LENGTH_LONG).show();
                    }
                    // dimis progress
                    if (dialog != null) {
                        dialog.dismiss()
                        finish()
                    }
                    uploadingFile = false
                }

                override fun onFailure(call: Call<UploadApi>, t: Throwable) {
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
                    finish()
                }
            })
        } else {
            Toast.makeText(
                contInst,
                "" + resources.getString(R.string.errorconnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun dialogCancelUpload(dialogUpload: Dialog) {
        dialogText = DialogCustom(contInst, { v: View? ->
            dialogUpload.dismiss()
            dialogText!!.dismiss()
            callMain!!.cancel()
            finish()
        }) { v: View? ->
            dialogText!!.dismiss()
        }
        dialogText!!.setMessag(resources.getString(R.string.textStopUpload))
        dialogText!!.setOkText(resources.getString(R.string.yes))
        dialogText!!.setCancelText(resources.getString(R.string.no))
        dialogText!!.title = resources.getString(R.string.stopUpload)
        dialogText!!.show()
    }

    private var tvPercentageGlobal: TextView? = null
    private var progressBarGlobal: ProgressBar? = null
    fun showDialogProgressBar(context: Context?, type2: Int): Dialog {
        val mDialog: Dialog
        mDialog = if (type2 == -1) {
            DialogHandler().createMDialogUnClose(R.layout.dialog_loading, context)
        } else {
            DialogHandler().createMDialogUnClose(R.layout.dialog_progress, context)
        }
        //TextView tvTitle = mDialog.findViewById(R.id.tvTitle);
        val tvPercentage = mDialog.findViewById<TextView>(R.id.tvPercentage)
        val tvClose = mDialog.findViewById<TextView>(R.id.tvClose)
        val progressBar = mDialog.findViewById<ProgressBar>(R.id.progressBar)
        tvPercentageGlobal = tvPercentage
        progressBarGlobal = progressBar
        tvClose.setOnClickListener { v: View? -> dialogCancelUpload(mDialog) }
        return mDialog
    }

    companion object {
        private const val SELECT_PICTURE = 1
        private const val TAKE_PICTURE = 2
        const val TYPE_REMOVE = "remove_photo"
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }
}