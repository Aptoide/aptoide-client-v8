/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.Obb;
import cm.aptoide.pt.permissions.ApkPermission;
import cm.aptoide.pt.permissions.ApkPermissionGroup;
import cm.aptoide.pt.v8engine.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sithengineer on 21/07/16.
 */
public final class AppUtils {

  public static long sumFileSizes(long fileSize, Obb obb) {
    if (obb == null || obb.getMain() == null) {
      return fileSize;
    } else if (obb.getPatch() == null) {
      return fileSize + obb.getMain()
          .getFilesize();
    } else {
      return fileSize + obb.getMain()
          .getFilesize() + obb.getPatch()
          .getFilesize();
    }
  }

  public static ArrayList<ApkPermissionGroup> fillPermissionsGroups(
      List<ApkPermission> permissions) {
    ArrayList<ApkPermissionGroup> list = new ArrayList<>();
    String prevName = null;
    ApkPermissionGroup apkPermission = null;

    for (int i = 0; i <= permissions.size(); i++) {

      if (i >= permissions.size()) {
        if (!list.contains(apkPermission)) {
          list.add(apkPermission);
        }
      } else {

        ApkPermission permission = permissions.get(i);

        if (!permission.getName()
            .equals(prevName)) {
          prevName = permission.getName();
          apkPermission = new ApkPermissionGroup(permission.getName(), permission.getDescription());
          list.add(apkPermission);
        } else {
          apkPermission.setDescription(permission.getDescription());
        }
      }
    }

    return list;
  }

  public static void fillPermissionsForTableLayout(Context context, TableLayout mPermissionsTable,
      List<ApkPermissionGroup> apkPermissions) {
    final int ZERO_DIP = 0;
    final float WEIGHT_ONE = 1f;

    TableRow tr = new TableRow(context);
    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT));
    LinearLayout linearLayout;
    int items = 0;

    for (int i = 0; i <= apkPermissions.size(); i++) {

      if (i >= apkPermissions.size()) {
        if (tr.getChildCount() > 0) {
          // there's still a TableRow left that needs to be added
          tr.setPadding(0, 0, 0, 20);
          mPermissionsTable.addView(tr,
              new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                  TableLayout.LayoutParams.WRAP_CONTENT));
        }
      } else {
        items++;

        ApkPermissionGroup apkPermission = apkPermissions.get(i);

        if (apkPermission != null) {
          linearLayout = (LinearLayout) LayoutInflater.from(context)
              .inflate(R.layout.row_permission, tr, false);
          TextView name = (TextView) linearLayout.findViewById(R.id.permission_name);
          name.setText(apkPermission.getName());

          for (String s : apkPermission.getDescriptions()) {
            TextView description = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.row_description, linearLayout, false);
            description.setText(s);
            linearLayout.addView(description);
          }

          tr.addView(linearLayout,
              new TableRow.LayoutParams(ZERO_DIP, TableRow.LayoutParams.WRAP_CONTENT, WEIGHT_ONE));

          if (items % 2 == 0) {
            mPermissionsTable.addView(tr,
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            tr = new TableRow(context);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
          }
        }
      }
    }
  }
}
