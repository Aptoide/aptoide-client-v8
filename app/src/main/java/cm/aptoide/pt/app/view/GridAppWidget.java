/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.app.view;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.text.DecimalFormat;

public class GridAppWidget<T extends GridAppDisplayable> extends Widget<T> {

  private TextView name;
  private ImageView icon;
  private TextView ratingBar;

  public GridAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(@NonNull View view) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    ratingBar = (TextView) itemView.findViewById(R.id.rating_label);
  }

  @Override public void bindView(T displayable) {
    final App pojo = displayable.getPojo();
    final long appId = pojo.getId();
    final FragmentActivity context = getContext();

    ImageLoader.with(context)
        .load(pojo.getIcon(), icon);

    name.setText(pojo.getName());
    DecimalFormat oneDecimalFormatter = new DecimalFormat("#.#");

    //ratingBar.setText(oneDecimalFormatter.format(pojo.getStats()
    //  .getRating()
    //.getAvg()));
    ratingBar.setText("3");
  }
}
