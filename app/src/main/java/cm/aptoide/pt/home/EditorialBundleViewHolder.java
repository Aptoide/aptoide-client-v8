package cm.aptoide.pt.home;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorialList.CurationCard;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import cm.aptoide.pt.reactions.TopReactionsPreview;
import cm.aptoide.pt.reactions.data.TopReaction;
import cm.aptoide.pt.reactions.ui.ReactionsPopup;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  private final TextView editorialDate;
  private final ImageView backgroundImage;
  private final TextView editorialViews;
  private final ImageButton reactButton;
  private final CardView curationTypeBubble;
  private final TextView curationTypeBubbleText;
  private TopReactionsPreview topReactionsPreview;

  public EditorialBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.editorialCard = view.findViewById(R.id.editorial_card);
    this.editorialTitle = (TextView) view.findViewById(R.id.editorial_title);
    this.editorialDate = (TextView) view.findViewById(R.id.editorial_date);
    this.editorialViews = view.findViewById(R.id.editorial_views);
    this.backgroundImage = (ImageView) view.findViewById(R.id.background_image);
    this.reactButton = view.findViewById(R.id.add_reactions);
    this.curationTypeBubble = view.findViewById(R.id.curation_type_bubble);
    this.curationTypeBubbleText = view.findViewById(R.id.curation_type_bubble_text);
    topReactionsPreview = new TopReactionsPreview();
    topReactionsPreview.initialReactionsSetup(view);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();

    setBundleInformation(actionItem.getIcon(), actionItem.getTitle(), actionItem.getSubTitle(),
        actionItem.getCardId(), actionItem.getNumberOfViews(), actionItem.getType(),
        actionItem.getDate(), position, homeBundle, actionItem.getReactionList(),
        actionItem.getTotal(), actionItem.getUserReaction());
  }

  private void setBundleInformation(String icon, String title, String subTitle, String cardId,
      String numberOfViews, String type, String date, int position, HomeBundle homeBundle,
      List<TopReaction> reactions, int numberOfReactions, String userReaction) {
    clearReactions();
    setReactions(reactions, numberOfReactions, userReaction);
    ImageLoader.with(itemView.getContext())
        .load(icon, backgroundImage);
    editorialTitle.setText(title);
    editorialViews.setText(String.format(itemView.getContext()
            .getString(R.string.editorial_card_short_number_views),
        formatNumberOfViews(numberOfViews)));
    setCurationCardBubble(subTitle);
    setupCalendarDateString(date);
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

  private void setupCalendarDateString(String date) {
    String[] dateSplitted = date.split(" ");
    String newFormatDate = dateSplitted[0].replace("-", "/");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date newDate = null;
    String formattedDate;
    try {
      newDate = dateFormat.parse(newFormatDate);
    } catch (ParseException parseException) {
      Snackbar.make(editorialCard, itemView.getContext()
          .getString(R.string.unknown_error), Snackbar.LENGTH_SHORT)
          .show();
    }
    if (newDate != null) {
      formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
          .format(newDate);
      editorialDate.setText(formattedDate);
    }
  }

  private void setReactions(List<TopReaction> reactions, int numberOfReactions,
      String userReaction) {
    setUserReaction(userReaction);
    topReactionsPreview.setReactions(reactions, numberOfReactions, itemView.getContext());
  }

  public void setEditorialCard(CurationCard curationCard, int position) {
    setBundleInformation(curationCard.getIcon(), curationCard.getTitle(),
        curationCard.getSubTitle(), curationCard.getId(), curationCard.getViews(),
        curationCard.getType(), curationCard.getDate(), position, null, curationCard.getReactions(),
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
    reactionsPopup.setOnDismissListener(item -> {
      uiEventsListener.onNext(new EditorialHomeEvent(cardId, groupId, null, position,
          HomeEvent.Type.POPUP_DISMISS));

      reactionsPopup.setOnDismissListener(null);
    });
  }

  private void setUserReaction(String reaction) {
    if (topReactionsPreview.isReactionValid(reaction)) {
      reactButton.setImageResource(mapReaction(reaction));
    } else {
      reactButton.setImageResource(R.drawable.ic_reaction_emoticon);
    }
  }

  private void clearReactions() {
    reactButton.setImageResource(R.drawable.ic_reaction_emoticon);
    topReactionsPreview.clearReactions();
  }

  private void setCurationCardBubble(String caption) {
    curationTypeBubbleText.setText(caption);
    switch (caption) {
      case "Game of the Week":
        curationTypeBubble.setCardBackgroundColor(itemView.getContext()
            .getResources()
            .getColor(R.color.curation_grey));
        break;

      case "App of the Week":
        curationTypeBubble.setCardBackgroundColor(itemView.getContext()
            .getResources()
            .getColor(R.color.curation_blue));
        break;

      case "Collections":
        curationTypeBubble.setCardBackgroundColor(itemView.getContext()
            .getResources()
            .getColor(R.color.curation_green));
        break;

      default:
        curationTypeBubble.setCardBackgroundColor(itemView.getContext()
            .getResources()
            .getColor(R.color.curation_default));
        break;
    }
  }
}