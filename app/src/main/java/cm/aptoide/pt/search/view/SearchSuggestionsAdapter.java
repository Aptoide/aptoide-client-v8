package cm.aptoide.pt.search.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.search.view.item.SuggestionViewHolder;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 07/03/18.
 */

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SuggestionViewHolder> {

  private List<Suggestion> suggestions;
  private PublishSubject<SearchQueryEvent> suggestionsPublishSubject;

  public SearchSuggestionsAdapter(List<Suggestion> suggestions,
      PublishSubject<SearchQueryEvent> suggestionsPublishSubject) {
    this.suggestions = suggestions;
    this.suggestionsPublishSubject = suggestionsPublishSubject;
  }

  @Override public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int i) {
    return new SuggestionViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.search_suggestion_item, parent, false), suggestionsPublishSubject);
  }

  @Override public void onBindViewHolder(SuggestionViewHolder suggestionViewHolder, int position) {
    suggestionViewHolder.setSuggestion(suggestions.get(position), position);
  }

  @Override public int getItemCount() {
    return suggestions.size();
  }

  public void addSuggestions(List<Suggestion> suggestions) {
    this.suggestions = suggestions;
    notifyDataSetChanged();
  }

  public List<Suggestion> getSuggestions() {
    return suggestions;
  }

  public void addSuggestionsFromString(List<String> suggestions) {
    List<Suggestion> result = new ArrayList<>();

    for (String suggestion : suggestions)
      result.add(new Suggestion(suggestion, null));

    this.suggestions = result;
    notifyDataSetChanged();
  }
}
