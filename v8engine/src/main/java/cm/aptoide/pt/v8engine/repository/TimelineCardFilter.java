package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import java.util.Set;
import rx.Observable;
import rx.functions.Func1;

public class TimelineCardFilter {
  private final TimelineCardDuplicateFilter duplicateFilter;
  private final InstalledAccessor installedAccessor;

  public TimelineCardFilter(TimelineCardDuplicateFilter duplicateFilter,
      InstalledAccessor installedAccessor) {
    this.duplicateFilter = duplicateFilter;
    this.installedAccessor = installedAccessor;
  }

  public void clear() {
    this.duplicateFilter.clear();
  }

  public Observable<TimelineCard> filter(TimelineItem<TimelineCard> item) {
    return Observable.just(item).filter(timelineItem -> timelineItem != null).<TimelineCard>map(
        timelineItem -> timelineItem.getData())
        .filter(duplicateFilter)
        .flatMap(timelineItem -> filterInstalledRecommendation(timelineItem));
  }

  private Observable<? extends TimelineCard> filterInstalledRecommendation(
      TimelineCard timelineItem) {
    if (timelineItem instanceof Recommendation) {
      return installedAccessor.isInstalled(
          ((Recommendation) timelineItem).getRecommendedApp().getPackageName())
          .flatMap(installed -> {
            if (!installed) {
              return Observable.just(timelineItem);
            }
            return Observable.empty();
          });
    }
    return Observable.just(timelineItem);
  }

  public static class TimelineCardDuplicateFilter implements Func1<TimelineCard, Boolean> {

    private final Set<String> cardIds;

    public TimelineCardDuplicateFilter(Set<String> cardIds) {
      this.cardIds = cardIds;
    }

    public void clear() {
      cardIds.clear();
    }

    @Override public Boolean call(TimelineCard card) {
      cardIds.toString();
      boolean result = cardIds.add(card.getCardId());
      return result;
    }
  }
}