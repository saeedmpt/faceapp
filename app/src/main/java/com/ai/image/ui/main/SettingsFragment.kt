package com.ai.image.ui.main

import com.ai.image.utility.PaperBook.logout
import com.ai.image.ui.base.BaseFragment
import androidx.fragment.app.FragmentActivity
import com.ai.image.component.DialogCustom
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ai.image.R
import android.content.Intent
import android.view.View
import com.ai.image.databinding.FragmentSettingBinding
import com.ai.image.ui.main.settings.MyRatingDialog
import com.ai.image.ui.main.settings.WebviewActivity

class SettingsFragment : BaseFragment() {
    private lateinit var contInst: FragmentActivity
    private lateinit var binding: FragmentSettingBinding
    private var dialogText: DialogCustom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting, container, false
        )
        contInst = requireActivity()
        binding.llPrivacy.setOnClickListener {
            startActivity(
                Intent(
                    contInst,
                    WebviewActivity::class.java
                ).putExtra("link", "https://google.com")
            )
        }
        binding.llShare.setOnClickListener { shareApp() }
        binding.llReport.setOnClickListener { rateApp("report") }
        binding.llRate.setOnClickListener { rateApp("") }
        return binding.root
    }

    private fun rateApp(type: String) {
        //show dialog rate
        val myRatingDialog = MyRatingDialog(contInst, type)
        myRatingDialog.show()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, contInst!!.resources.getString(R.string.textShare))
        startActivity(
            Intent.createChooser(
                intent,
                contInst!!.resources.getString(R.string.app_name)
            )
        )
    }

    private fun dialogReadyLogout() {
        dialogText = DialogCustom(contInst, {
            dialogText!!.dismiss()
            logout()
        }) { dialogText!!.dismiss() }
        dialogText!!.setMessag(contInst!!.resources.getString(R.string.logoutAccountText))
        dialogText!!.setOkText(contInst!!.resources.getString(R.string.yes))
        dialogText!!.setCancelText(contInst!!.resources.getString(R.string.no))
        dialogText!!.title = contInst!!.resources.getString(R.string.logoutAccount)
        dialogText!!.show()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        fun newInstance(strClsCat: String?): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, strClsCat)
            fragment.arguments = args
            return fragment
        }
    }
}