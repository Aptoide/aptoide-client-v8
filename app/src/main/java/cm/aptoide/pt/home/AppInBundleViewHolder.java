package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

public class AppInBundleViewHolder extends AppViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final PublishSubject<HomeEvent> appClicks;
  private AppSecondaryInfoViewHolder appInfoViewHolder;
  private LinearLayout holder;

  public AppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, oneDecimalFormatter);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    holder = itemView.findViewById(R.id.holder_test);

    this.appClicks = appClicks;
  }

  public void setApp(Application app, HomeBundle homeBundle, int bundlePosition, int position) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, iconView, R.drawable.placeholder_square);

    appInfoViewHolder.setInfo(app.hasAppcBilling(), app.getRating(), true, false);

    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.APP)));

    itemView.setOnLongClickListener(click -> {
      appClicks.onNext(
          new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.PREVIEW));
      return true;
    });



    /*
    itemView.setOnLongClickListener(v -> {
      Logger.getInstance()
          .d("dasdas", "dasda");

      View preview = LayoutInflater.from(itemView.getContext()
          .getApplicationContext())
          .inflate(R.layout.app_preview, holder, false);
      ImageView appPreviewImage = preview.findViewById(R.id.app_preview_icon);
      TextView appPreviewName = preview.findViewById(R.id.app_preview_name);

      ImageLoader.with(itemView.getContext())
          .loadWithRoundCorners(app.getIcon(), 8, appPreviewImage, R.drawable.placeholder_square);
      appPreviewName.setText("REI BRUNO");

      AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

      Dialog dialog = builder.create();

      //Dialog dialog = new Dialog(iconView.getContext());
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.getWindow()
          .requestFeature(Window.FEATURE_NO_TITLE);
      dialog.getWindow()
          .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      dialog.setContentView(preview);
      dialog.show();

      return true;
    });*/
  }
}
