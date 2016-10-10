/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.annotation.LayoutRes;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickDisplayable extends DisplayablePojo<App> {

  @Getter private String tag;

  public AppBrickDisplayable() {
  }

  public AppBrickDisplayable(App pojo, String tag) {
    super(pojo);
    this.tag = tag;
  }

  @Override public Type getType() {
    return Type.APP_BRICK;
  }

	/*
  @Override
	public boolean isFixedPerLineCount() {
		return true;
	}
	*/

  @LayoutRes @Override public int getViewLayout() {
    return R.layout.brick_app_item;
  }

  @Override protected Configs getConfig() {
    return new Configs(2, true);
  }
}
