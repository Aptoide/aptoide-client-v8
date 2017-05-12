/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.v8engine.view.permission;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.permissions.ApkPermission;
import cm.aptoide.pt.permissions.ApkPermissionGroup;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.AppUtils;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 18/11/15.
 * <p>
 * TODO: fix size of scrollview to shrink when not enough permissions on screen
 * <p>
 */
public class DialogPermissions extends DialogFragment {

  private GetApp getApp;
  private String appName;
  private String versionName;
  private String icon;
  private String size;

  public static DialogPermissions newInstance(GetApp getApp) {
    GetAppMeta.App app = getApp.getNodes()
        .getMeta()
        .getData();
    DialogPermissions dialog = new DialogPermissions();
    dialog.getApp = getApp;
    dialog.appName = app.getName();
    dialog.versionName = app.getFile()
        .getVername();
    dialog.icon = app.getIcon();
    dialog.size = AptoideUtils.StringU.formatBytes(AppUtils.sumFileSizes(app.getFile()
        .getFilesize(), app.getObb()), false);
    return dialog;
  }

  @Override public void onPause() {
    dismiss();
    super.onPause();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
    } else {
      setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    @SuppressLint("InflateParams") final View v = LayoutInflater.from(getActivity())
        .inflate(R.layout.layout_dialog_permissions, null);
    AlertDialog builder = new AlertDialog.Builder(getActivity()).setView(v)
        .create();

    v.findViewById(R.id.dialog_ok_button)
        .setOnClickListener(v1 -> dismiss());

    TextView tvAppInfo = (TextView) v.findViewById(R.id.dialog_app_info);
    tvAppInfo.setText(getString(R.string.dialog_version_size, versionName, size));

    TextView tvAppName = (TextView) v.findViewById(R.id.dialog_app_name);
    tvAppName.setText(appName);

    Glide.with(this)
        .load(icon)
        .into((ImageView) v.findViewById(R.id.dialog_appview_icon));

    final TableLayout tableLayout = (TableLayout) v.findViewById(R.id.dialog_table_permissions);

    final List<String> usedPermissions = getApp.getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getUsedPermissions();

    List<ApkPermission> apkPermissions =
        AptoideUtils.SystemU.parsePermissions(getContext(), usedPermissions);
    final ArrayList<ApkPermissionGroup> apkPermissionsGroup =
        AppUtils.fillPermissionsGroups(apkPermissions);

    if (apkPermissionsGroup.size() == 0) {
      TextView noPermissions = new TextView(getContext());
      noPermissions.setText(getString(R.string.no_permissions_required));
      noPermissions.setPadding(5, 5, 5, 5);
    } else {
      AppUtils.fillPermissionsForTableLayout(getContext(), tableLayout, apkPermissionsGroup);
    }

    builder.getWindow()
        .setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(android.R.color.transparent)));

    return builder;
  }
}
