/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.DescriptionFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewDescriptionDisplayable.class})
public class AppViewDescriptionWidget extends Widget<AppViewDescriptionDisplayable> {

	private TextView descriptionTextView;
	private Button readMoreBtn;
	private String storeName;

	public AppViewDescriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		descriptionTextView = (TextView) itemView.findViewById(R.id.description);
		readMoreBtn = (Button) itemView.findViewById(R.id.read_more_button);
	}

	@Override
	public void bindView(AppViewDescriptionDisplayable displayable) {
		final GetAppMeta.App app = displayable.getPojo().getNodes().getMeta().getData();
		final GetAppMeta.Media media = app.getMedia();
		this.storeName = app.getStore().getName();

		if(!TextUtils.isEmpty(media.getDescription())) {
			descriptionTextView.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
			readMoreBtn.setOnClickListener(seeMoreHandler(app.getId()));
		} else {
			// only show "default" description if the app doesn't have one
			descriptionTextView.setText(R.string.description_not_available);
			readMoreBtn.setVisibility(View.GONE);
		}
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}

	private View.OnClickListener seeMoreHandler(final long appId) {
		return v -> {
			((FragmentShower) getContext()).pushFragmentV4(DescriptionFragment.newInstance(appId, storeName));
		};
	}
}
