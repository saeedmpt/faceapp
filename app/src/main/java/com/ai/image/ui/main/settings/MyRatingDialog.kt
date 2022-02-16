package com.ai.chatapp.ui.main.settings

import android.app.Dialog
import com.ai.chatapp.utility.PaperBook.setRate
import androidx.databinding.DataBindingUtil
import com.ai.chatapp.R
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.RatingBar
import android.content.Intent
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.view.*
import com.ai.chatapp.component.Global
import com.ai.chatapp.databinding.DialogRatingBinding

class MyRatingDialog(private val contInst: Context, private val type: String) : Dialog(
    contInst) {
    var dialog: Dialog
    var binding: DialogRatingBinding
    private fun setShowFeedbackDialog(context: Context) {
        if (binding.dialogRatingFeedback.visibility == View.VISIBLE) {
            if (binding.dialogRatingFeedback.text.toString().length > 0) {
                setRate(true)
                Global.sendEmail(context, binding.dialogRatingFeedback.text.toString())
                dialog.dismiss()
            }
            return
        }
        // feedback
        binding.dialogRatingIcon.visibility = View.GONE
        binding.dialogRatingTitle.visibility = View.GONE
        binding.dialogRatingRatingBar.visibility = View.GONE
        binding.dialogRatingButtonPositive.visibility = View.VISIBLE
        binding.dialogRatingFeedbackTitle.visibility = View.VISIBLE
        binding.dialogRatingFeedback.visibility = View.VISIBLE
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_rating,
            null,
            false)
        setContentView(binding.root)
        dialog = this
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes.gravity = Gravity.CENTER
        binding.dialogRatingButtonNegative.setOnClickListener { v: View? -> dialog.dismiss() }
        binding.dialogRatingRatingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar: RatingBar?, rating: Float, fromUser: Boolean ->
                if (rating > 0 && fromUser) {
                    binding.dialogRatingButtonPositive.visibility = View.VISIBLE
                } else {
                    binding.dialogRatingButtonPositive.visibility = View.GONE
                }
            }
        binding.dialogRatingButtonPositive.setOnClickListener { v: View? ->
            val rate = binding.dialogRatingRatingBar.rating.toInt()
            if (rate == 5) {
                setRate(true)
                //goto googleplay
                val uri = Uri.parse("market://details?id=" + contInst.packageName)
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    contInst.startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    contInst.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + contInst.packageName)))
                }
                dialog.dismiss()
            } else {
                setShowFeedbackDialog(contInst)
            }
        }
        if (type == "report") {
            binding.dialogRatingFeedbackTitle.text = "REPORT"
            setShowFeedbackDialog(contInst)
        }
    }
}