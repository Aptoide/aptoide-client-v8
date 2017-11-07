package cm.aptoide.pt.search.suggestionsprovider;

import android.content.SearchRecentSuggestionsProvider;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.R;

public class DefaultSuggestions extends SearchRecentSuggestionsProvider {

  // TODO

  @Override public boolean onCreate() {
    setupSuggestions(getSearchProvider(getContext().getResources()), DATABASE_MODE_QUERIES);
    return super.onCreate();
  }

  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {

    // TODO implement this

    return super.query(uri, projection, selection, selectionArgs, sortOrder);
  }

  private String getSearchProvider(Resources resources) {
    return resources.getString(R.string.search_suggestion_provider_authority);
  }

  @Nullable @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder,
      @Nullable CancellationSignal cancellationSignal) {

    // TODO implement this and use the CancellationSignal to cancel the query

    return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
  }
}
