package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorialList.CurationCard;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import cm.aptoide.pt.reactions.data.TopReaction;
import cm.aptoide.pt.reactions.ui.ReactionsPopup;
import java.util.List;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.editorial.ViewsFormatter.formatNumberOfViews;
import static cm.aptoide.pt.reactions.ReactionMapper.mapReaction;
import static cm.aptoide.pt.reactions.ReactionMapper.mapUserReaction;

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
  private final TextView numberOfReactions;
  private ImageView[] imageViews;

  public EditorialBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.editorialCard = view.findViewById(R.id.editorial_card);
    this.editorialTitle = (TextView) view.findViewById(R.id.editorial_title);
    this.editorialSubtitle = (TextView) view.findViewById(R.id.editorial_subtitle);
    this.editorialViews = view.findViewById(R.id.editorial_views);
    this.backgroundImage = (ImageView) view.findViewById(R.id.background_image);
    this.reactButton = view.findViewById(R.id.add_reactions);
    ImageView firstReaction = view.findViewById(R.id.reaction_1);
    ImageView secondReaction = view.findViewById(R.id.reaction_2);
    ImageView thirdReaction = view.findViewById(R.id.reaction_3);
    imageViews = new ImageView[] { firstReaction, secondReaction, thirdReaction };
    this.numberOfReactions = view.findViewById(R.id.number_of_reactions);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();

    setBundleInformation(actionItem.getIcon(), actionItem.getTitle(), actionItem.getSubTitle(),
        actionItem.getCardId(), actionItem.getNumberOfViews(), actionItem.getType(), position,
        homeBundle, actionItem.getReactionList(), actionItem.getTotal(),
        actionItem.getUserReaction());
  }

  private void setBundleInformation(String icon, String title, String subTitle, String cardId,
      String numberOfViews, String type, int position, HomeBundle homeBundle,
      List<TopReaction> reactions, int numberOfReactions, String userReaction) {
    clearReactions();
    setReactions(reactions, numberOfReactions, userReaction);
    ImageLoader.with(itemView.getContext())
        .load(icon, backgroundImage);
    editorialTitle.setText(title);
    editorialSubtitle.setText(subTitle);
    editorialViews.setText(String.format(itemView.getContext()
            .getString(R.string.editorial_card_short_number_views),
        formatNumberOfViews(numberOfViews)));
    reactButton.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, type, homeBundle, position,
            HomeEvent.Type.REACT_SINGLE_PRESS)));
    reactButton.setOnLongClickListener(view -> {
      uiEventsListener.onNext(new EditorialHomeEvent(cardId, type, homeBundle, position,
          HomeEvent.Type.REACT_LONG_PRESS));
      return true;
    });
    editorialCard.setOnClickListener(view -> uiEventsListener.onNext(
        new EditorialHomeEvent(cardId, type, homeBundle, position, HomeEvent.Type.EDITORIAL)));
  }

  public void setReactions(List<TopReaction> reactions, int numberOfReactions,
      String userReaction) {
    setUserReaction(userReaction);
    int validReactions = 0;
    for (int i = 0; i < imageViews.length; i++) {
      if (i < reactions.size() && isReactionValid(reactions.get(i)
          .getType())) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(mapReaction(reactions.get(i)
                .getType()), imageViews[i]);
        imageViews[i].setVisibility(View.VISIBLE);
        validReactions++;
      } else {
        imageViews[i].setVisibility(View.GONE);
      }
    }
    if (numberOfReactions > 0 && validReactions > 0) {
      this.numberOfReactions.setText(String.valueOf(numberOfReactions));
      this.numberOfReactions.setVisibility(View.VISIBLE);
    } else {
      this.numberOfReactions.setVisibility(View.GONE);
    }
  }

  public void setEditorialCard(CurationCard curationCard, int position) {
    setBundleInformation(curationCard.getIcon(), curationCard.getTitle(),
        curationCard.getSubTitle(), curationCard.getId(), curationCard.getViews(),
        curationCard.getType(), position, null, curationCard.getReactions(),
        curationCard.getNumberOfReactions(), curationCard.getUserReaction());
  }

  public void showReactions(String cardId, String groupId, int position) {
    ReactionsPopup reactionsPopup = new ReactionsPopup(itemView.getContext(), reactButton);
    reactionsPopup.show();
    reactionsPopup.setOnReactionsItemClickListener(item -> {
      uiEventsListener.onNext(
          new ReactionsHomeEvent(cardId, groupId, null, position, HomeEvent.Type.REACTION,
              mapUserReaction(item)));
      reactionsPopup.dismiss();
      reactionsPopup.setOnReactionsItemClickListener(null);
    });
  }

  private void setUserReaction(String reaction) {
    if (!reaction.equals("") && isReactionValid(reaction)) {
      reactButton.setImageResource(mapReaction(reaction));
    } else {
      reactButton.setImageResource(R.drawable.ic_reaction_emoticon);
    }
  }

  private boolean isReactionValid(String reaction) {
    return mapReaction(reaction) != -1;
  }

  private void clearReactions() {
    reactButton.setImageResource(R.drawable.ic_reaction_emoticon);
    for (ImageView imageView : imageViews) {
      imageView.setVisibility(View.GONE);
    }
    this.numberOfReactions.setVisibility(View.GONE);
  }
}