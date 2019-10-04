package cm.aptoide.pt.home.bundles.info;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.aptoideviews.skeleton.Skeleton;
import cm.aptoide.aptoideviews.skeleton.SkeletonUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.bundles.base.ActionBundle;
import cm.aptoide.pt.home.bundles.base.ActionItem;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.Translator;
import rx.subjects.PublishSubject;

public class InfoBundleViewHolder extends AppBundleViewHolder {
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final Button knowMoreButton;
  private final Button dismissButton;
  private final ImageView icon;
  private final TextView title;
  private final TextView message;

  private View infoLayout;
  private Skeleton skeleton;

  public InfoBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.knowMoreButton = view.findViewById(R.id.action_button);
    this.dismissButton = view.findViewById(R.id.dismiss_button);
    this.icon = view.findViewById(R.id.icon);
    this.title = view.findViewById(R.id.title);
    this.message = view.findViewById(R.id.message);
    this.infoLayout = view.findViewById(R.id.card_info_layout);

    this.skeleton = SkeletonUtils.applySkeleton(infoLayout, view.findViewById(R.id.root_layout),
        R.layout.info_action_item_card_skeleton);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();
    if (actionItem == null) {
      skeleton.showSkeleton();
    } else {
      skeleton.showOriginal();
      ImageLoader.with(itemView.getContext())
          .load(actionItem.getIcon(), icon);
      knowMoreButton.setText(R.string.all_button_know_more);
      dismissButton.setText(R.string.all_button_got_it);
      title.setText(Translator.translate(actionItem.getTitle(), itemView.getContext(), ""));
      message.setText(Translator.translate(actionItem.getSubTitle(), itemView.getContext(), ""));
      knowMoreButton.setOnClickListener(view -> uiEventsListener.onNext(
          new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.APPC_KNOW_MORE)));
      dismissButton.setOnClickListener(itemView -> uiEventsListener.onNext(
          new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.DISMISS_BUNDLE)));
    }
  }
}
