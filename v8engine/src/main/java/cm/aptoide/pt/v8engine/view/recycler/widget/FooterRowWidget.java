/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.FooterRowDisplayable;

/**
 * Created by sithengineer on 27/06/16.
 */
@Displayables({ FooterRowDisplayable.class }) public class FooterRowWidget
    extends Widget<FooterRowDisplayable> {
  private TextView title;

  public FooterRowWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    title = (TextView) itemView.findViewById(R.id.title);
  }

  @Override public void bindView(FooterRowDisplayable displayable) {
    String pojo = displayable.getPojo();
    title.setText(pojo);
    title.setVisibility(View.VISIBLE);
  }
}
