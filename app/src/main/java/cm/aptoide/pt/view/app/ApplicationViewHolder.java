package cm.aptoide.pt.view.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 18/10/2017.
 */

public class ApplicationViewHolder extends AppViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView downloadsTextView;
  private final TextView rating;
  private final PublishSubject<Application> appClicks;

  public ApplicationViewHolder(View itemView, PublishSubject<Application> appClicks) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    downloadsTextView = ((TextView) itemView.findViewById(R.id.downloads));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = ((TextView) itemView.findViewById(R.id.rating_label));
    this.appClicks = appClicks;
  }

  public void setApp(Application app) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .load(app.getIcon(), iconView);
    downloadsTextView.setText(itemView.getContext()
        .getString(R.string.downloads_count_text,
            AptoideUtils.StringU.withSuffix(app.getDownloads())));
    rating.setText(app.getRating() + "");
    itemView.setOnClickListener(v -> appClicks.onNext(app));
  }
}
