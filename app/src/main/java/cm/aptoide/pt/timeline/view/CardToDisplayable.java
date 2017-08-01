package cm.aptoide.pt.timeline.view;

import android.support.annotation.UiThread;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;

public interface CardToDisplayable {
  @UiThread Displayable convert(TimelineCard card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, DownloadFactory downloadFactory,
      LinksHandlerFactory linksHandlerFactory);
}
