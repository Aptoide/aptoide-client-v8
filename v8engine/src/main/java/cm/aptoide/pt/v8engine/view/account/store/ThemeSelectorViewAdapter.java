package cm.aptoide.pt.v8engine.view.account.store;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import rx.Observable;

public class ThemeSelectorViewAdapter
    extends RecyclerView.Adapter<ThemeSelectorViewAdapter.ViewHolder> {

  private final PublishRelay<StoreTheme> storeThemePublishRelay;
  private final List<StoreTheme> themes;
  private StoreTheme selectedStoreTheme;

  public ThemeSelectorViewAdapter(PublishRelay<StoreTheme> storeThemePublishRelay,
      List<StoreTheme> themes) {
    this.storeThemePublishRelay = storeThemePublishRelay;
    this.themes = themes;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT, parent, false);
    return new ViewHolder(itemView, storeThemePublishRelay);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.update(themes.get(position), selectedStoreTheme);
  }

  @Override public int getItemCount() {
    return themes != null ? themes.size() : 0;
  }

  public void selectTheme(StoreTheme selectedStoreTheme) {
    this.selectedStoreTheme = selectedStoreTheme;
    this.notifyDataSetChanged();
  }

  public StoreTheme getSelectedTheme() {
    return this.selectedStoreTheme;
  }

  public Observable<StoreTheme> storeThemeSelection() {
    return storeThemePublishRelay.doOnNext(storeTheme -> selectTheme(storeTheme));
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private static final int LAYOUT = R.layout.partial_store_theme_round_item;
    private final PublishRelay<StoreTheme> storeThemePublishRelay;
    private ImageView storeThemeImage;
    private ImageView storeThemeCheckMark;
    private StoreTheme storeTheme;

    public ViewHolder(View itemView, PublishRelay<StoreTheme> storeThemePublishRelay) {
      super(itemView);
      this.storeThemePublishRelay = storeThemePublishRelay;
      bind(itemView);
    }

    private void bind(View view) {
      storeThemeImage = (ImageView) view.findViewById(R.id.theme_color);
      storeThemeCheckMark = (ImageView) view.findViewById(R.id.theme_checked);
      RxView.clicks(view)
          .doOnNext(__ -> storeThemePublishRelay.call(storeTheme))
          .subscribe();
    }

    public void update(StoreTheme storeTheme, StoreTheme selectedStoreTheme) {
      this.storeTheme = storeTheme;
      storeThemeImage.setBackgroundResource(storeTheme.getRoundDrawable());
      if (storeTheme == selectedStoreTheme) {
        storeThemeCheckMark.setVisibility(View.VISIBLE);
      } else {
        storeThemeCheckMark.setVisibility(View.GONE);
      }
    }
  }
}
