/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.dialog.DialogBadgeV7;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewDescriptionDisplayable.class})
public class AppViewDescriptionWidget extends Widget<AppViewDescriptionDisplayable> {

	private static String TAG = "badgeDialog";
	// Description Badges
	View badgeLayout;
	private TextView descriptionTextView;
	// Badges
	private ImageView badgeMarketImage;
	private ImageView badgeSignatureImage;
	private ImageView badgeFlagImage;
	private ImageView badgeAntivirusImage;
	// See more
	private View seeMoreLayout;
	private TextView seeMoreTextView;
	private ImageView arrowImageView;

	public AppViewDescriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		descriptionTextView = (TextView) itemView.findViewById(R.id.description);

		badgeMarketImage = (ImageView) itemView.findViewById(R.id.iv_market_badge);
		badgeSignatureImage = (ImageView) itemView.findViewById(R.id.iv_signature_badge);
		badgeFlagImage = (ImageView) itemView.findViewById(R.id.iv_flag_badge);
		badgeAntivirusImage = (ImageView) itemView.findViewById(R.id.iv_antivirus_badge);

		seeMoreLayout = itemView.findViewById(R.id.see_more_layout);
		if(seeMoreLayout!=null){
			seeMoreTextView = (TextView) seeMoreLayout.findViewById(R.id.see_more_button);
			arrowImageView = (ImageView) seeMoreLayout.findViewById(R.id.iv_arrow);
		}

		badgeLayout = itemView.findViewById(R.id.badge_layout);
	}

	@Override
	public void bindView(AppViewDescriptionDisplayable displayable) {
		final GetAppMeta.App app = displayable.getPojo().getNodes().getMeta().getData();
		final GetAppMeta.Media media = app.getMedia();

		if(!TextUtils.isEmpty(media.getDescription())) {
			descriptionTextView.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
		}

		handleSeeMore(app);
		handleDescriptionBadges(app);
	}

	private void handleDescriptionBadges(GetAppMeta.App app) {
		Malware malware = app.getFile().getMalware();
		if (malware != null && malware.getReason() != null) {

			if (malware.getReason().getThirdpartyValidated() != null && Malware.GOOGLE_PLAY
					.equalsIgnoreCase(malware.getReason().getThirdpartyValidated().getStore())) {
				badgeMarketImage.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getSignatureValidated() != null && Malware.Reason.Status.passed
					.equals(malware.getReason().getSignatureValidated().getStatus())) {
				badgeSignatureImage.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getScanned() != null && Malware.Reason.Status.passed.equals
					(malware.getReason().getScanned().getStatus())) {
				badgeAntivirusImage.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getManual() != null && Malware.Reason.Status.passed.equals
					(malware.getReason().getManual().getStatus())) {
				badgeFlagImage.setVisibility(View.VISIBLE);
			}
		}

		badgeLayout.setOnClickListener(v -> {
			DialogBadgeV7.newInstance(malware, app.getName(), app.getFile().getMalware().getRank())
					.show(getContext().getSupportFragmentManager(), TAG);
		});
	}

	private void handleSeeMore(GetAppMeta.App app) {
		seeMoreLayout.setOnClickListener(new View.OnClickListener() {

			private boolean extended = false;

			@Override
			public void onClick(View v) {
				/** The MAX_LINES parameter cannot be too long, or the animation will not be seen
				 * smoothly */
				final int MAX_LINES = 200;
				final int ANIMATION_DELAY = 0;
				int maxLines;
				int[] values;
				String text;
				int descriptionMaxLines = AptoideUtils.ResourseU.getInt(R.integer.minimum_description_lines);

				if (extended) {
					maxLines = descriptionMaxLines;
					values = new int[]{MAX_LINES, maxLines};
					text = AptoideUtils.StringU.getResString(R.string.see_more);
					arrowImageView.setImageDrawable(AptoideUtils.ResourseU.getDrawable(R.drawable.ic_down_arrow));
				} else {
					maxLines = MAX_LINES;
					values = new int[]{descriptionMaxLines, maxLines};
					text = AptoideUtils.ResourseU.getString(R.string.see_less);
					arrowImageView.setImageDrawable(AptoideUtils.ResourseU.getDrawable(R.drawable.ic_up_arrow));
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					ObjectAnimator animation = ObjectAnimator.ofInt(descriptionTextView, "maxLines", values);
					animation.setDuration(ANIMATION_DELAY).start();
				} else {
					descriptionTextView.setMaxLines(maxLines);
				}

				seeMoreTextView.setText(text);
				extended = !extended;
			}
		});
	}
}
