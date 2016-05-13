/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewDescriptionDisplayable.class})
public class AppViewDescriptionWidget extends Widget<AppViewDescriptionDisplayable> {

	private TextView description;

	private ImageView badgeMarket;
	private ImageView badgeSignature;
	private ImageView badgeFlag;
	private ImageView badgeAntivirus;

	private View seeMoreLayout;
	private TextView seeMoreButton;
	private ImageView arrowImageView;

	public AppViewDescriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		description = (TextView) itemView.findViewById(R.id.description);

		badgeMarket = (ImageView) itemView.findViewById(R.id.iv_market_badge);
		badgeSignature = (ImageView) itemView.findViewById(R.id.iv_signature_badge);
		badgeFlag = (ImageView) itemView.findViewById(R.id.iv_flag_badge);
		badgeAntivirus = (ImageView) itemView.findViewById(R.id.iv_antivirus_badge);

		seeMoreLayout = itemView.findViewById(R.id.see_more_layout);
		if(seeMoreLayout!=null) {
			seeMoreButton = (TextView) seeMoreLayout.findViewById(R.id.see_more_button);
			arrowImageView = (ImageView) seeMoreLayout.findViewById(R.id.iv_arrow);
			seeMoreLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void bindView(AppViewDescriptionDisplayable displayable) {
		final GetAppMeta.App pojo = displayable.getPojo();
		final GetAppMeta.Media media = pojo.getMedia();

		if(!TextUtils.isEmpty(media.getDescription())) {
			description.setText(media.getDescription());
		}

		seeMoreLayout.setOnClickListener(
			new View.OnClickListener() {
					boolean open=false;
					@Override
					public void onClick(View v) {

						if(open) {
							int maxLines = v.getContext().getResources().getInteger
									(R.integer.minimum_description_lines);
							description.setMaxLines(maxLines);
							seeMoreButton.setText(R.string.see_more);
							arrowImageView.setImageResource(R.drawable.ic_down_arrow);

						} else {
							description.setMaxLines(Integer.MAX_VALUE);
							seeMoreButton.setText(R.string.see_less);
							arrowImageView.setImageResource(R.drawable.ic_up_arrow);
						}

						open = !open;
					}
				}
		);

		GetAppMeta.GetAppMetaFile.Malware malware = pojo.getFile().getMalware();
		if(malware!=null) {

			if (malware.getReason().getThirdpartyValidated() != null && GetAppMeta.GetAppMetaFile.Malware.GOOGLE_PLAY
					.equalsIgnoreCase(malware.getReason().getThirdpartyValidated().getStore())) {
				badgeMarket.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getSignatureValidated() != null && GetAppMeta.GetAppMetaFile
					.Malware.PASSED
					.equals(malware.getReason().getSignatureValidated().getStatus())) {
				badgeSignature.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getScanned() != null && GetAppMeta.GetAppMetaFile.Malware.PASSED.equals
					(malware.getReason().getScanned().getStatus())) {
				badgeAntivirus.setVisibility(View.VISIBLE);
			}

			if (malware.getReason().getManual() != null && GetAppMeta.GetAppMetaFile.Malware.PASSED.equals
					(malware.getReason().getManual().getStatus())) {
				badgeFlag.setVisibility(View.VISIBLE);
			}
		}
	}
}
