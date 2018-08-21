package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

class InfoBundleViewHolder extends AppBundleViewHolder {
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final View knowMoreButton;
  private final View dismissButton;
  private final ImageView icon;
  private final TextView title;
  private final TextView message;

  public InfoBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.knowMoreButton = view.findViewById(R.id.know_more_button);
    this.dismissButton = view.findViewById(R.id.dismiss_button);
    this.icon = (ImageView) view.findViewById(R.id.icon);
    this.title = (TextView) view.findViewById(R.id.title);
    this.message = (TextView) view.findViewById(R.id.message);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();
    ImageLoader.with(itemView.getContext())
        .load(actionItem.getIcon(), icon);
    title.setText(actionItem.getTitle());
    message.setText(actionItem.getMessage());
    knowMoreButton.setOnClickListener(view -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.KNOW_MORE)));
    dismissButton.setOnClickListener(itemView -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.DISMISS_BUNDLE)));
  }
}
