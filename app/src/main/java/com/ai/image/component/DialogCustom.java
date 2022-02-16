package com.ai.chatapp.component;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.ai.chatapp.R;
import com.ai.chatapp.databinding.CompDialogCustomBinding;

public class DialogCustom extends Dialog {


    public Dialog dialog;
    CompDialogCustomBinding binding;
    int intIcon;

    public void setMessag(String messParam) {
        binding.tvDes.setText(messParam);
    }


    public void setTitle(String titParam) {
        binding.tvTitle.setText(titParam);
    }

    public String getMessage() {
        return binding.tvDes.getText().toString();
    }

    public String getTitle() {
        return binding.tvTitle.getText().toString();
    }


    public void setIcon(int iconParam) {
        intIcon = iconParam;
    }


    public void setOkText(String OKTextParam) {
        binding.tvOk.setText(OKTextParam);
    }

    public void setCancelText(String CancelTextParam) {
        binding.tvCancle.setText(CancelTextParam);
        if (CancelTextParam.length() == 0)
            binding.rlClose.setVisibility(View.GONE);
    }

    public DialogCustom(Context contextParam,
                        View.OnClickListener tvOkListener,
                        View.OnClickListener tvCancleListener) {
        super(contextParam);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.comp_dialog_custom, null, false);
        setContentView(binding.getRoot());
        dialog = this;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        binding.rlClose.setVisibility(View.VISIBLE);
        binding.tvDes.setText(getMessage());
        binding.tvTitle.setText(getTitle());
        binding.tvOk.setOnClickListener(tvOkListener);
        if (tvCancleListener == null)
            binding.tvCancle.setOnClickListener(closeListener);
        else
            binding.tvCancle.setOnClickListener(tvCancleListener);

    }

    public void setlisntener(View.OnClickListener tvOkListener, View.OnClickListener tvCancleListener) {
        binding.tvOk.setOnClickListener(tvOkListener);
        if (tvCancleListener == null)
            binding.tvCancle.setOnClickListener(closeListener);
        else
            binding.tvCancle.setOnClickListener(tvCancleListener);
    }

    View.OnClickListener closeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            dialog.dismiss();
        }

    };

}

