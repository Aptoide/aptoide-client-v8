/*
 * Copyright (c) 2016.
 * Modified on 21/07/2016.
 */

package cm.aptoide.pt.permission;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.permissions.ApkPermission;
import cm.aptoide.pt.permissions.ApkPermissionGroup;
import cm.aptoide.pt.util.AppUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class DialogPermissions extends DialogFragment {

  private String appName;
  private String versionName;
  private String icon;
  private String size;
  private List<String> usedPermissions;

  public static DialogPermissions newInstance(String appName, String versionName, String icon,
      String size, List<String> usedPermissions) {
    DialogPermissions dialog = new DialogPermissions();
    dialog.appName = appName;
    dialog.versionName = versionName;
    dialog.icon = icon;
    dialog.size = size;
    dialog.usedPermissions = usedPermissions;
    return dialog;
  }

  @Override public void onPause() {
    dismiss();
    super.onPause();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    @SuppressLint("InflateParams") final View v = LayoutInflater.from(getActivity())
        .inflate(R.layout.layout_dialog_permissions, null);
    AlertDialog builder = new AlertDialog.Builder(getActivity()).setView(v)
        .create();

    v.findViewById(R.id.dialog_ok_button)
        .setOnClickListener(v1 -> dismiss());

    TextView tvAppInfo = v.findViewById(R.id.dialog_app_info);
    tvAppInfo.setText(getString(R.string.dialog_version_size, versionName, size));

    TextView tvAppName = v.findViewById(R.id.dialog_app_name);
    tvAppName.setText(appName);

    Glide.with(this)
        .load(icon)
        .into((ImageView) v.findViewById(R.id.dialog_appview_icon));

    final TableLayout tableLayout = v.findViewById(R.id.dialog_table_permissions);

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

    return builder;
  }
}
