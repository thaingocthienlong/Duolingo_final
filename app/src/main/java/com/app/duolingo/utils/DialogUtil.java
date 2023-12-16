package com.app.duolingo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.duolingo.R;

import java.util.Objects;

public class DialogUtil {

    private static final String TAG = "DialogUtil";
    private static Dialog dialog;

    public static void progressDlgShow(Activity act, String mes) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (act.isFinishing()) {
            Log.d(TAG, "Owner Activity isFinishing!!!");
            return;
        }

        dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(
                LayoutInflater.from(act)
                        .inflate(R.layout.dialog_loading, null)
        );
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(mes);
    }

    public static void progressDlgHide() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
