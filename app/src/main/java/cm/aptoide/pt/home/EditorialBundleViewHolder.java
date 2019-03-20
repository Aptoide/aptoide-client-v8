package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorial.ReactionsHomeEvent;
import cm.aptoide.pt.editorialList.CurationCard;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.data.ReactionType;
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
        actionItem.getCardId(), actionItem.getNumberOfViews(), position, homeBundle,
        actionBundle.getReactionTypes(), actionBundle.getNumberOfReactions(),
        actionBundle.getUserReaction());
  }

  private void setBundleInformation(String icon, String title, String subTitle, String cardId,
      String numberOfViews, int position, HomeBundle homeBundle, List<ReactionType> reactionTypes,
      String numberOfReactions, ReactionType userReaction) {
    ImageLoader.with(itemView.getContext())
        .load(icon, backgroundImage);
    editorialTitle.setText(Translator.translate(title, itemView.getContext(), ""));
    editorialSubtitle.setText(Translator.translate(subTitle, itemView.getContext(), ""));
    editorialViews.setText(String.format(itemView.getContext()
            .getString(R.string.editorial_card_short_number_views),
        formatNumberOfViews(numberOfViews)));
    setReactions(reactionTypes, numberOfReactions, userReaction);
    reactButton.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, homeBundle, position, HomeEvent.Type.REACTION_BUTTON)));
    editorialCard.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, homeBundle, position, HomeEvent.Type.EDITORIAL)));
    if (firstCreation) {
      firstCreation = false;
      uiEventsListener.onNext(
          new EditorialHomeEvent(cardId, homeBundle, position, HomeEvent.Type.EDITORIAL_CREATED));
    }
  }

  public void setReactions(List<ReactionType> reactions, String numberOfReactions,
      ReactionType userReaction) {
    if (userReaction != null) {
      setUserReaction(userReaction);
    }
    ImageView[] imageViews = { firstReaction, secondReaction, thirdReaction };
    for (int i = 0; i < reactions.size(); i++) {
      if (i < imageViews.length) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(mapReaction(reactions.get(i)), imageViews[i]);
        imageViews[i].setVisibility(View.VISIBLE);
      }
    }
    if (!numberOfReactions.equals("0")) {
      this.numberOfReactions.setText(numberOfReactions);
      this.numberOfReactions.setVisibility(View.VISIBLE);
    }
  }

  public void setEditorialCard(CurationCard curationCard, int position) {
    setBundleInformation(curationCard.getIcon(), curationCard.getTitle(),
        curationCard.getSubTitle(), curationCard.getId(), curationCard.getViews(), position, null,
        curationCard.getReactionTypes(), curationCard.getNumberOfReactions(),
        curationCard.getUserReaction());
  }

  public void showReactions(String cardId, int position) {
    ReactionsPopup reactionsPopup = new ReactionsPopup(itemView.getContext(), reactButton);
    reactionsPopup.show();
    reactionsPopup.setOnReactionsItemClickListener(item -> {
      uiEventsListener.onNext(
          new ReactionsHomeEvent(cardId, null, position, HomeEvent.Type.REACTION, item));
      reactionsPopup.dismiss();
      reactionsPopup.setOnReactionsItemClickListener(null);
    });
  }

  public void setUserReaction(ReactionType reaction) {
    reactButton.setImageResource(mapReaction(reaction));
  }
}