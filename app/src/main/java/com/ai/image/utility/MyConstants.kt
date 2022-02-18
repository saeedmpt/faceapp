package com.ai.image.utility

import com.ai.image.R
import com.ai.image.model.HomeModel
import java.util.ArrayList

object MyConstants {


    //link address
    const val BASE_URL = "http://206.189.5.209:5001/"
    const val EMAIL_FEEDBACK = "feedback@hoora.uk"

    //configs

    //route & params
    const val route_upload = "file-upload"
    const val param_file = "file"
    const val param_filter = "filter"

    const val route_firebase = "firebase"
    const val param_firebase_token = "token";

    //intent code
    const val VALUE_TYPE = "valueType"
    const val ACTION_TYPE = "actionType"


    fun getFilters(): ArrayList<HomeModel> {
        val listModel = ArrayList<HomeModel>()
        listModel.add(HomeModel(R.drawable.img_face_morphing,
            "Face Morphing",
            "Please check your fortune report today",
            "1"))
        listModel.add(HomeModel(R.drawable.img_image_translation, "Image Translation", "", "2"))
        listModel.add(HomeModel(R.drawable.img_old_video_restore, "Old video restore", "", "3"))
        listModel.add(HomeModel(R.drawable.img_super_resolaution, "Super resolation", "", "4"))
        listModel.add(HomeModel(R.drawable.img_face_cartoonization,
            "Face catoonization",
            "Please check your fortune report today",
            "5"))
        return listModel;
    }
}
