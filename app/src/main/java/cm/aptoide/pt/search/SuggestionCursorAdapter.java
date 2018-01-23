package cm.aptoide.pt.search;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cm.aptoide.pt.R;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SuggestionCursorAdapter extends CursorAdapter {

  private static final int ITEM_LAYOUT = R.layout.simple_dropdown_item;
  private static final String[] COLUMN_NAMES = {
      "_id", android.app.SearchManager.SUGGEST_COLUMN_TEXT_1
  };

  private final List<String> suggestions;

  public SuggestionCursorAdapter(Context context, List<String> initialSuggestions) {
    super(context, null, false);
    this.suggestions = new LinkedList<>();
    if (initialSuggestions != null && !initialSuggestions.isEmpty()) {
      setData(initialSuggestions);
    }
  }

  public SuggestionCursorAdapter(Context context) {
    this(context, Collections.emptyList());
  }

  public void setData(@NonNull List<String> newSuggestions) {
    suggestions.clear();
    suggestions.addAll(newSuggestions);
    changeCursor(getCursorFor(suggestions));
  }

  @NonNull private MatrixCursor getCursorFor(@NonNull List<String> data) {
    final MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES, data.size());
    int index = 0;
    for (String item : data) {
      cursor.newRow()
          .add(Integer.toString(index++))
          .add(item);
    }
    return cursor;
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context)
        .inflate(ITEM_LAYOUT, parent, false);
  }

  @Override public void bindView(View view, Context context, Cursor cursor) {
    TextView textView = (TextView) view.findViewById(R.id.dropdown_text);
    textView.setText(getSuggestionAtCurrentPosition(cursor));
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public CharSequence getSuggestionAt(int position) {
    final Cursor cursor = getCursor();
    if (cursor.moveToPosition(position)) {
      return getSuggestionAtCurrentPosition(cursor);
    }
    throw new UnsupportedOperationException("Unable to find query at position " + position);
  }

  private String getSuggestionAtCurrentPosition(Cursor cursor) {
    return cursor.getString(cursor.getColumnIndex(android.app.SearchManager.SUGGEST_COLUMN_TEXT_1));
  }
}
