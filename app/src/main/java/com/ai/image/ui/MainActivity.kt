package com.ai.image.ui

import com.ai.image.utility.PaperBook.setLastFragmentMain
import com.ai.image.utility.PaperBook.lastFragmentMain
import com.ai.image.ui.base.BaseActivity
import android.app.Activity
import com.ai.image.utility.ViewPagerFragmentAdapter
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.ai.image.R
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener
import com.ai.image.utility.SendPushToken
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.ai.image.ui.main.HomeFragment
import com.ai.image.ui.main.SettingsFragment
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import com.ai.image.BuildConfig
import com.ai.image.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

import com.ai.image.model.EventModel
import com.ai.image.model.OnActivityResultModel
import com.ai.image.ui.main.SelectFileFragment
import com.ai.image.utility.PaperBook
import com.ai.image.utility.PaperBook.setFirebaseToken
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {
    private var contInst: Activity? = null
    private var myAdapter: ViewPagerFragmentAdapter? = null
    private val fragmentList = ArrayList<Fragment>()
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding: ActivityMainBinding
    private val currentFragment = 0

    /*@JvmField
    var valueType: String? = null
    @JvmField
    var actionType: String? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        contInst = this
        setLastFragmentMain(currentFragment)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token
            Log.d("TAG", msg)
            Log.d("TAG", PaperBook.firebaseToken)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        /*val intent = intent
        if (intent != null && intent.extras != null) {
            val extras = intent.extras
            valueType = extras!!.getString(VALUE_TYPE)
            actionType = extras.getString(ACTION_TYPE)
        }*/
        initiClick()
        initiPager()
        //Log.i("TAG", "onCreate: " + Global.checkPermissionStorage(contInst));


        //fcm config
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.FCM_TOPIC)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("SendPushToken", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                setFirebaseToken(token)
                SendPushToken(token)
            })


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(`object`: Any?) {
        if (`object` is EventModel) {
            val model = `object`
            if (model.action == EventModel.VARIABLE.ACTION_FILTER) {
                llCamera()
                // EventBus.getDefault().post(`object`)
            } else if (model.action == EventModel.VARIABLE.ACTION_BACK) {
                onBackPressed()
            }
        }
    }

    private fun initiClick() {
        binding.lbb.ivHome.setOnClickListener {
            llHome()
        }
        binding.lbb.ivCamera.setOnClickListener {
            llCamera()
        }
        binding.lbb.ivSettings.setOnClickListener {
            llSettings()
        }
    }

    override fun onBackPressed() {
        if (binding.viewPager2.currentItem != currentFragment) {
            binding.viewPager2.setCurrentItem(currentFragment, true)
            setActiveView(binding.lbb.ivHome)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Snackbar.make(
                findViewById(R.id.root),
                Html.fromHtml(contInst!!.resources.getString(R.string.textExit)),
                Snackbar.LENGTH_SHORT
            ).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    private fun initiPager() {
        fragmentList.add(HomeFragment.newInstance(""))
        fragmentList.add(SelectFileFragment.newInstance(""))
        fragmentList.add(SettingsFragment.newInstance(""))
        myAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        for (i in fragmentList.indices) {
            myAdapter!!.addFrag(fragmentList[i])
        }
        // set Orientation in your ViewPager2
        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager2.adapter = myAdapter
        binding.viewPager2.offscreenPageLimit = fragmentList.size
        //viewPager2.setPageTransformer(new MarginPageTransformer(1500));
        binding.viewPager2.setCurrentItem(currentFragment, false)
        binding.viewPager2.isUserInputEnabled = false
        setActiveView(binding.lbb.ivHome)
    }

    private fun setActiveView(ivView: ImageView) {
        /*binding.lbb.ivSettings.setImageDrawable(resources.getDrawable(R.drawable.ic_settings))
        binding.lbb.ivCamera.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
        binding.lbb.ivHome.setImageDrawable(resources.getDrawable(R.drawable.ic_chat))
       */
        /*when {
            ivView === binding.lbb.ivCamera -> {
                binding.lbb.ivHome.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_nav_gallery))
                binding.lbb.ivCamera.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_nav_shot))
                binding.lbb.ivSettings.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_nav_rotate))
            }
            else -> { //setting or home
                binding.lbb.ivHome.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_home_nav))
                binding.lbb.ivCamera.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_scan))
                binding.lbb.ivSettings.setImageDrawable(ContextCompat.getDrawable(contInst!!,
                    R.drawable.ic_more_nav))
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        when (lastFragmentMain) {
            0 -> {
                llHome()
            }
            1 -> {
                llCamera()
            }
            2 -> {
                llSettings()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            EventBus.getDefault().post(OnActivityResultModel(requestCode, resultCode,
                data!!))
        }
    }

    override fun onPause() {
        super.onPause()
        setLastFragmentMain(binding.viewPager2.currentItem)
    }

    private fun llHome() {
        binding.viewPager2.currentItem = 0
        setActiveView(binding.lbb.ivHome)
    }

    private fun llCamera() {
        binding.viewPager2.currentItem = 1
        setActiveView(binding.lbb.ivCamera)
    }

    private fun llSettings() {
        binding.viewPager2.currentItem = 2
        setActiveView(binding.lbb.ivSettings)
    }


}