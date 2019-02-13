package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorialList.CurationCard;
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

  public EditorialBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.editorialCard = view.findViewById(R.id.editorial_card);
    this.editorialTitle = (TextView) view.findViewById(R.id.editorial_title);
    this.editorialSubtitle = (TextView) view.findViewById(R.id.editorial_subtitle);
    this.backgroundImage = (ImageView) view.findViewById(R.id.background_image);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();

    setBundleInformation(actionItem.getIcon(), actionItem.getTitle(), actionItem.getSubTitle(),
        actionItem.getCardId(), position, homeBundle);
  }

  private void setBundleInformation(String icon, String title, String subTitle, String cardId,
      int position, HomeBundle homeBundle) {
    ImageLoader.with(itemView.getContext())
        .load(icon, backgroundImage);
    editorialTitle.setText(Translator.translate(title, itemView.getContext(), ""));
    editorialSubtitle.setText(Translator.translate(subTitle, itemView.getContext(), ""));
    editorialCard.setOnClickListener(view -> {
      uiEventsListener.onNext(
          new EditorialHomeEvent(cardId, homeBundle, position, HomeEvent.Type.EDITORIAL));
    });
  }

  public void setEditorialCard(CurationCard curationCard, int position) {
    setBundleInformation(curationCard.getIcon(), curationCard.getTitle(),
        curationCard.getSubTitle(), curationCard.getId(), position, null);
  }
}