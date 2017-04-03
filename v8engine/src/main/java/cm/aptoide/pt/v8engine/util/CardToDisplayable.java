package cm.aptoide.pt.v8engine.util;

import android.support.annotation.UiThread;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.timeline.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;

public interface CardToDisplayable {
  @UiThread Displayable convert(TimelineCard card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, DownloadFactory downloadFactory,
      LinksHandlerFactory linksHandlerFactory);
}
