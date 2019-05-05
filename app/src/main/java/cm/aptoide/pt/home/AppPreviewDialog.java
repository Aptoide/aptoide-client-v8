package cm.aptoide.pt.home;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;

public class AppPreviewDialog extends DialogFragment {

  private static final String APP_NAME = "app_name";
  private static final String APP_ICON = "app_icon";
  private ImageView appPreviewImage;
  private TextView appPreviewName;
  private String appName;
  private String appIcon;

  public AppPreviewDialog() {

  }

  public static AppPreviewDialog newInstance(String packageName, String appIcon) {
    Bundle args = new Bundle();
    AppPreviewDialog fragment = new AppPreviewDialog();
    args.putString(APP_NAME, packageName);
    args.putString(APP_ICON, appIcon);
    fragment.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, 0);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appIcon = getArguments().getString(APP_ICON);
    appName = getArguments().getString(APP_NAME);
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    View preview = getActivity().getLayoutInflater()
        .inflate(R.layout.app_preview, null);
    appPreviewImage = preview.findViewById(R.id.app_preview_icon);
    appPreviewName = preview.findViewById(R.id.app_preview_name);

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

    builder.setView(preview);
    ImageLoader.with(this.getActivity()
        .getApplicationContext())
        .loadWithRoundCorners(appIcon, 8, appPreviewImage, R.drawable.placeholder_square);

    appPreviewName.setText(appName);

    return builder.create();
  }
}
