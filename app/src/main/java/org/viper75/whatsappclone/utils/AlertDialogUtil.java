package org.viper75.whatsappclone.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import org.viper75.whatsappclone.databinding.ProgressDialogLayoutBinding;

public class AlertDialogUtil {

    public static AlertDialog getProgressDialog(Context context, int title, int description, boolean cancelable) {
        ProgressDialogLayoutBinding progressDialogLayoutBinding = ProgressDialogLayoutBinding
                .inflate(LayoutInflater.from(context));
        progressDialogLayoutBinding.title.setText(title);
        progressDialogLayoutBinding.description.setText(description);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(progressDialogLayoutBinding.getRoot())
                .setCancelable(cancelable);

        return builder.create();
    }
}
