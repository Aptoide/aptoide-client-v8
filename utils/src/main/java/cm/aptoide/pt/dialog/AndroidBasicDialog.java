/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.utils.R;
import rx.functions.Action0;

/**
 * Created by sithengineer on 16/08/16.
 */
// TODO: 16/08/16 sithengineer Instead of being a wrapper for the AlertDialog, this should be a extension of AlertDialog and AlertDialog.Builder
public class AndroidBasicDialog {

  private AlertDialog.Builder builder;
  private AlertDialog alertDialog;

  private View view;
  private Button positive;
  private Button negative;

  private TextView title;
  private TextView message;

  private AndroidBasicDialog(Context context, @LayoutRes int dialogLayout) {
    this.builder = new AlertDialog.Builder(context);
    this.view = LayoutInflater.from(context).inflate(dialogLayout, null, false);
    builder.setView(view);
    bindView();
  }

  private AndroidBasicDialog(Context context, View dialogLayout) {
    this.view = dialogLayout;
    this.builder = new AlertDialog.Builder(context);
    builder.setView(dialogLayout);
    bindView();
  }

  public static AndroidBasicDialog build(Context context) {
    AndroidBasicDialog androidBasicDialog = new AndroidBasicDialog(context, R.layout.dialog_basic);
    androidBasicDialog.getCreatedDialog();
    return androidBasicDialog;
  }

  public static AndroidBasicDialog build(Context context, @LayoutRes int dialogLayout) {
    AndroidBasicDialog androidBasicDialog = new AndroidBasicDialog(context, dialogLayout);
    androidBasicDialog.getCreatedDialog();
    return androidBasicDialog;
  }

  public static AndroidBasicDialog build(Context context, View dialogLayout) {
    AndroidBasicDialog androidBasicDialog = new AndroidBasicDialog(context, dialogLayout);
    androidBasicDialog.getCreatedDialog();
    return androidBasicDialog;
  }

  private void bindView() {
    positive = (Button) view.findViewById(R.id.positive_button);
    negative = (Button) view.findViewById(R.id.negative_button);
    title = (TextView) view.findViewById(R.id.title);
    message = (TextView) view.findViewById(R.id.message);
  }

  public AndroidBasicDialog setPositiveButton(@StringRes int title, View.OnClickListener listener) {
    positive.setVisibility(View.VISIBLE);
    positive.setOnClickListener(listener);
    positive.setText(title);
    return this;
  }

  public AndroidBasicDialog setPositiveButton(String title, View.OnClickListener listener) {
    positive.setVisibility(View.VISIBLE);
    positive.setOnClickListener(listener);
    positive.setText(title);
    return this;
  }

  public AndroidBasicDialog setNegativeButton(@StringRes int title, View.OnClickListener listener) {
    negative.setVisibility(View.VISIBLE);
    negative.setOnClickListener(listener);
    negative.setText(title);
    return this;
  }

  public AndroidBasicDialog setNegativeButton(String title, View.OnClickListener listener) {
    negative.setVisibility(View.VISIBLE);
    negative.setOnClickListener(listener);
    negative.setText(title);
    return this;
  }

  public AndroidBasicDialog setMessage(@StringRes int message) {
    if(this.message!=null) {
      this.message.setText(message);
      this.message.setVisibility(View.VISIBLE);
    }
    return this;
  }

  public AndroidBasicDialog setMessage(String message) {
    if(this.message!=null) {
      this.message.setText(message);
      this.message.setVisibility(View.VISIBLE);
    }
    return this;
  }

  public AndroidBasicDialog setTitle(@StringRes int title) {
    if(this.title!=null) {
      this.title.setText(title);
      this.title.setVisibility(View.VISIBLE);
    }
    return this;
  }

  public AndroidBasicDialog setTitle(String title) {
    if(this.title!=null) {
      this.title.setText(title);
      this.title.setVisibility(View.VISIBLE);
    }
    return this;
  }

  public void dismiss() {
    if (alertDialog == null) {
      throw new IllegalStateException("Alert dialog wasn't shown");
    }
    alertDialog.dismiss();
  }

  public void show() {
    getCreatedDialog();
    if(!alertDialog.isShowing()){
      alertDialog.show();
    }
  }

  public void setOnCancelListener(Action0 action) {
    alertDialog.setOnCancelListener(dialog -> {
      action.call();
      dialog.dismiss();
    });
  }

  public Dialog getCreatedDialog() {
    if (alertDialog == null) {
      alertDialog = this.builder.create();
    }
    return alertDialog;
  }
}
