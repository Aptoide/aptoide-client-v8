package cm.aptoide.pt.search.view.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 07/03/18.
 */

public class SuggestionViewHolder extends RecyclerView.ViewHolder {

  private PublishSubject<SearchQueryEvent> suggestionPublishSubject;
  private TextView suggestionName;
  private ImageView suggestionIcon;

  public SuggestionViewHolder(View itemView,
      PublishSubject<SearchQueryEvent> suggestionPublishSubject) {
    super(itemView);
    this.suggestionPublishSubject = suggestionPublishSubject;
    this.suggestionName = (TextView) itemView.findViewById(R.id.search_suggestion_app_name);
    this.suggestionIcon = (ImageView) itemView.findViewById(R.id.search_suggestion_app_icon);
  }

  public void setSuggestion(Suggestion suggestion, int position) {
    suggestionName.setText(suggestion.getName()
        .trim());
    if (suggestion.getIcon() != null) {
      suggestionIcon.setImageResource(R.drawable.ic_suggestions_trending);
    } else {
      suggestionIcon.setImageResource(R.drawable.ic_stat_aptoide_notification);
    }

    itemView.setOnClickListener(c -> suggestionPublishSubject.onNext(
        new SearchQueryEvent(suggestion.getName(), position, true, true)));
  }
}
