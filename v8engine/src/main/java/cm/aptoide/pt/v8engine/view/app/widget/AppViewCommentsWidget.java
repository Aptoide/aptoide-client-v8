/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.view.View;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Deprecated @Displayables({ AppViewCommentsDisplayable.class }) public class AppViewCommentsWidget
    extends Widget<AppViewCommentsDisplayable> {

  private View writeCommentView;

  public AppViewCommentsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    writeCommentView = itemView.findViewById(R.id.write_comment);
  }

  @Override public void bindView(AppViewCommentsDisplayable displayable) {
    // TODO

    writeCommentView.setOnClickListener(v -> ShowMessage.asSnack(v, "TO DO"));
  }
}
