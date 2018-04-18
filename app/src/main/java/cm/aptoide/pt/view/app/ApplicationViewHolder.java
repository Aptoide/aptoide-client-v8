package cm.aptoide.pt.view.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 18/10/2017.
 */

public class ApplicationViewHolder extends ListStoreAppViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView downloadsTextView;
  private final RatingBar rating;
  private final PublishSubject<Application> appClicks;

  public ApplicationViewHolder(View itemView, PublishSubject<Application> appClicks) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    downloadsTextView = ((TextView) itemView.findViewById(R.id.downloads));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = ((RatingBar) itemView.findViewById(R.id.ratingbar));
    this.appClicks = appClicks;
  }

  public void setApp(Application app) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .load(app.getIcon(), iconView);
    downloadsTextView.setText(
        AptoideUtils.StringU.withSuffix(app.getDownloads()) + itemView.getContext()
            .getString(R.string._downloads));
    rating.setRating(app.getRating());
    itemView.setOnClickListener(v -> appClicks.onNext(app));
  }
}
