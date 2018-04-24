package cm.aptoide.pt.view.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 18/10/2017.
 */

public class ApplicationViewHolder extends ListStoreAppViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView rating;
  private final PublishSubject<Application> appClicks;

  public ApplicationViewHolder(View itemView, PublishSubject<Application> appClicks) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = ((TextView) itemView.findViewById(R.id.rating_label));
    this.appClicks = appClicks;
  }

  public void setApp(Application app) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .load(app.getIcon(), iconView);
    float rating = app.getRating();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(new DecimalFormat("#.#").format(rating));
    }
    itemView.setOnClickListener(v -> appClicks.onNext(app));
  }
}
