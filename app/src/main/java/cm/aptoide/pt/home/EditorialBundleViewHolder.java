package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.Translator;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 29/08/2018.
 */

public class EditorialBundleViewHolder extends AppBundleViewHolder {
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final View editorialCard;
  private final TextView editorialTitle;
  private final TextView editorialSubtitle;
  private final ImageView backgroundImage;
  private final TextView editorialViews;

  public EditorialBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.editorialCard = view.findViewById(R.id.editorial_card);
    this.editorialTitle = (TextView) view.findViewById(R.id.editorial_title);
    this.editorialSubtitle = (TextView) view.findViewById(R.id.editorial_subtitle);
    this.editorialViews = view.findViewById(R.id.editorial_views);
    this.backgroundImage = (ImageView) view.findViewById(R.id.background_image);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();

    ImageLoader.with(itemView.getContext())
        .load(actionItem.getIcon(), backgroundImage);
    editorialTitle.setText(Translator.translate(actionItem.getTitle(), itemView.getContext(), ""));
    editorialSubtitle.setText(
        Translator.translate(actionItem.getSubTitle(), itemView.getContext(), ""));
    editorialViews.setText(String.format(itemView.getContext()
        .getString(R.string.editorial_card_short_number_views), actionItem.getNumberOfViews()));
    editorialCard.setOnClickListener(view -> {
      uiEventsListener.onNext(new EditorialHomeEvent(actionItem.getCardId(), homeBundle, position,
          HomeEvent.Type.EDITORIAL));
    });
  }
}
