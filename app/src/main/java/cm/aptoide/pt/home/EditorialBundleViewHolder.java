package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorial.ReactionsHomeEvent;
import cm.aptoide.pt.editorialList.CurationCard;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.TopReaction;
import cm.aptoide.pt.reactions.ui.ReactionsPopup;
import cm.aptoide.pt.view.Translator;
import java.util.List;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.editorial.ViewsFormatter.formatNumberOfViews;
import static cm.aptoide.pt.reactions.ReactionMapper.mapReaction;

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
  private final ImageButton reactButton;
  private final ImageView firstReaction;
  private final ImageView secondReaction;
  private final ImageView thirdReaction;
  private final TextView numberOfReactions;
  private boolean firstCreation;

  public EditorialBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.editorialCard = view.findViewById(R.id.editorial_card);
    this.editorialTitle = (TextView) view.findViewById(R.id.editorial_title);
    this.editorialSubtitle = (TextView) view.findViewById(R.id.editorial_subtitle);
    this.editorialViews = view.findViewById(R.id.editorial_views);
    this.backgroundImage = (ImageView) view.findViewById(R.id.background_image);
    this.reactButton = view.findViewById(R.id.add_reactions);
    this.firstReaction = view.findViewById(R.id.reaction_1);
    this.secondReaction = view.findViewById(R.id.reaction_2);
    this.thirdReaction = view.findViewById(R.id.reaction_3);
    this.numberOfReactions = view.findViewById(R.id.number_of_reactions);
    firstCreation = true;
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();

    setBundleInformation(actionItem.getIcon(), actionItem.getTitle(), actionItem.getSubTitle(),
        actionItem.getCardId(), actionItem.getNumberOfViews(), actionItem.getType(), position,
        homeBundle);
  }

  private void setBundleInformation(String icon, String title, String subTitle, String cardId,
      String numberOfViews, String type, int position, HomeBundle homeBundle) {
    ImageLoader.with(itemView.getContext())
        .load(icon, backgroundImage);
    editorialTitle.setText(Translator.translate(title, itemView.getContext(), ""));
    editorialSubtitle.setText(Translator.translate(subTitle, itemView.getContext(), ""));
    editorialViews.setText(String.format(itemView.getContext()
            .getString(R.string.editorial_card_short_number_views),
        formatNumberOfViews(numberOfViews)));
    reactButton.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, type, homeBundle, position,
            HomeEvent.Type.REACTION_BUTTON)));
    editorialCard.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, type, homeBundle, position, HomeEvent.Type.EDITORIAL)));
    if (firstCreation) {
      firstCreation = false;
      uiEventsListener.onNext(new EditorialHomeEvent(cardId, type, homeBundle, position,
          HomeEvent.Type.EDITORIAL_CREATED));
    }
  }

  public void setReactions(List<TopReaction> reactions, int numberOfReactions,
      String userReaction) {
    if (userReaction != null) {
      setUserReaction(userReaction);
    }
    ImageView[] imageViews = { firstReaction, secondReaction, thirdReaction };
    for (int i = 0; i < reactions.size(); i++) {
      if (i < imageViews.length) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(mapReaction(reactions.get(i)
                .getType()), imageViews[i]);
        imageViews[i].setVisibility(View.VISIBLE);
      }
    }
    if (numberOfReactions > 0) {
      this.numberOfReactions.setText(String.valueOf(numberOfReactions));
      this.numberOfReactions.setVisibility(View.VISIBLE);
    }
  }

  public void setEditorialCard(CurationCard curationCard, int position) {
    setBundleInformation(curationCard.getIcon(), curationCard.getTitle(),
        curationCard.getSubTitle(), curationCard.getId(), curationCard.getViews(),
        curationCard.getType(), position, null);
  }

  public void showReactions(String cardId, String groupId, int position) {
    ReactionsPopup reactionsPopup = new ReactionsPopup(itemView.getContext(), reactButton);
    reactionsPopup.show();
    reactionsPopup.setOnReactionsItemClickListener(item -> {
      uiEventsListener.onNext(
          new ReactionsHomeEvent(cardId, groupId, null, position, HomeEvent.Type.REACTION,
              item.toString()
                  .toLowerCase()));
      reactionsPopup.dismiss();
      reactionsPopup.setOnReactionsItemClickListener(null);
    });
  }

  public void setUserReaction(String reaction) {
    if (!reaction.equals("")) {
      reactButton.setImageResource(mapReaction(reaction));
    }
  }
}