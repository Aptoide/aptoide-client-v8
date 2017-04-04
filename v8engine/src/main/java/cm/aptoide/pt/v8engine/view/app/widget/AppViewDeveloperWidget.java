/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.permission.DialogPermissions;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({ AppViewDeveloperDisplayable.class }) public class AppViewDeveloperWidget
    extends Widget<AppViewDeveloperDisplayable> {

  private static final String TAG = AppViewDeveloperWidget.class.getSimpleName();

  //	private TextView additionalInfo;
  //	private View additionalInfoLayout;

  private TextView websiteLabel;
  private TextView emailLabel;
  private TextView privacyPolicyLabel;
  private TextView permissionsLabel;

  public AppViewDeveloperWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    //		additionalInfo = (TextView) itemView.findViewById(R.id.additional_info);
    //		additionalInfoLayout = itemView.findViewById(R.id.additional_info_layout);
    websiteLabel = (TextView) itemView.findViewById(R.id.website_label);
    emailLabel = (TextView) itemView.findViewById(R.id.email_label);
    privacyPolicyLabel = (TextView) itemView.findViewById(R.id.privacy_policy_label);
    permissionsLabel = (TextView) itemView.findViewById(R.id.permissions_label);
  }

  @Override public void bindView(AppViewDeveloperDisplayable displayable) {
    final GetAppMeta.App app = displayable.getPojo().getNodes().getMeta().getData();
    final Context ctx = getContext();

		/*
    final FragmentShower fragmentShower = (FragmentShower) ctx;
		final Resources.Theme theme = ctx.getTheme();
		final Resources res = ctx.getResources();
		additionalInfo.setOnClickListener(v -> {

			if (additionalInfoLayout.getVisibility() == View.GONE) {
				additionalInfoLayout.setVisibility(View.VISIBLE);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					additionalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_up_arrow, theme), null);
				} else {
					additionalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_up_arrow), null);
				}
			} else {

				additionalInfoLayout.setVisibility(View.GONE);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					additionalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_down_arrow, theme), null);
				} else {
					additionalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_down_arrow), null);
				}
			}

			Scrollable scrollable = null;
			try {
				scrollable = ((Scrollable) fragmentShower.getLastV4());
			} catch (ClassCastException ex) {
				Logger.e(TAG, ex);
			}

			if (scrollable != null) {
				scrollable.scroll(Scrollable.Position.LAST);
			}
		});
		*/

    if (!TextUtils.isEmpty(app.getDeveloper().getWebsite())) {
      websiteLabel.setText(String.format(ctx.getString(R.string.developer_website),
          app.getDeveloper().getWebsite()));
    } else {
      websiteLabel.setText(String.format(ctx.getString(R.string.developer_website),
          ctx.getString(R.string.not_available)));
    }
    websiteLabel.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(app.getDeveloper().getWebsite())) {
        Intent browserIntent =
            new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper().getWebsite()));
        ctx.startActivity(browserIntent);
      }
    });

    if (!TextUtils.isEmpty(app.getDeveloper().getEmail())) {
      emailLabel.setText(
          String.format(ctx.getString(R.string.developer_email), app.getDeveloper().getEmail()));
    } else {
      emailLabel.setText(String.format(ctx.getString(R.string.developer_email),
          ctx.getString(R.string.not_available)));
    }
    emailLabel.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(app.getDeveloper().getEmail())) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(
            "mailto:" + app.getDeveloper().getEmail() + "?subject=" + "Feedback" + "&body=" + "");
        intent.setData(data);
        ctx.startActivity(intent);
      }
    });

    if (!TextUtils.isEmpty(app.getDeveloper().getPrivacy())) {
      privacyPolicyLabel.setText(String.format(ctx.getString(R.string.developer_privacy_policy),
          app.getDeveloper().getPrivacy()));
    } else {
      privacyPolicyLabel.setText(String.format(ctx.getString(R.string.developer_privacy_policy),
          ctx.getString(R.string.not_available)));
    }
    privacyPolicyLabel.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(app.getDeveloper().getPrivacy())) {
        Intent browserIntent =
            new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper().getPrivacy()));
        ctx.startActivity(browserIntent);
      }
    });

    permissionsLabel.setOnClickListener(v -> {
      DialogPermissions dialogPermissions = DialogPermissions.newInstance(displayable.getPojo());
      dialogPermissions.show(getContext().getSupportFragmentManager(), "");
    });
  }
}
