package cm.aptoide.pt.v8engine.social.data;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import java.util.Set;
import rx.Observable;
import rx.functions.Func1;

public class TimelineCardFilter {
  private final TimelineCardDuplicateFilter duplicateFilter;
  private final InstalledRepository installedRepository;

  public TimelineCardFilter(TimelineCardDuplicateFilter duplicateFilter,
      InstalledRepository installedRepository) {
    this.duplicateFilter = duplicateFilter;
    this.installedRepository = installedRepository;
  }

  public void clear() {
    this.duplicateFilter.clear();
  }

  public Observable<TimelineItem<TimelineCard>> filter(TimelineItem<TimelineCard> item) {
    return Observable.just(item)
        .filter(timelineItem -> timelineItem != null)
        .filter(duplicateFilter)
        .flatMap(timelineCard -> filterInstalledRecommendation(timelineCard))
        .flatMap(timelineCard -> filterAlreadyDoneUpdates(timelineCard));
  }

  private Observable<TimelineItem<TimelineCard>> filterInstalledRecommendation(
      TimelineItem<TimelineCard> timelineItem) {
    String packageName = getPackageNameFrom(timelineItem);
    if (!TextUtils.isEmpty(packageName)) {
      return installedRepository.isInstalled(packageName)
          .firstOrDefault(false)
          .flatMap(installed -> {
            if (!installed) {
              return Observable.just(timelineItem);
            }
            return Observable.empty();
          });
    }
    return Observable.just(timelineItem);
  }

  private String getPackageNameFrom(TimelineItem<TimelineCard> timelineItem) {
    final TimelineCard card = timelineItem.getData();
    if (card instanceof Recommendation) {
      return ((Recommendation) card).getRecommendedApp()
          .getPackageName();
    }
    if (card instanceof AppUpdate) {
      return ((AppUpdate) card).getPackageName();
    }
    return null;
  }

  private Observable<TimelineItem<TimelineCard>> filterAlreadyDoneUpdates(
      TimelineItem<TimelineCard> timelineItem) {
    String packageName = getPackageNameFrom(timelineItem);
    if (!TextUtils.isEmpty(packageName)) {
      return installedRepository.getInstalled(packageName)
          .firstOrDefault(null)
          .flatMap(installed -> {
            if (installed != null
                && installed.getVersionCode() == ((AppUpdate) timelineItem).getFile()
                .getVercode()) {
              return Observable.empty();
            }
            return Observable.just(timelineItem);
          });
    }
    return Observable.just(timelineItem);
  }

  public static class TimelineCardDuplicateFilter
      implements Func1<TimelineItem<TimelineCard>, Boolean> {

    private final Set<String> cardIds;

    public TimelineCardDuplicateFilter(Set<String> cardIds) {
      this.cardIds = cardIds;
    }

    public void clear() {
      cardIds.clear();
    }

    @Override public Boolean call(TimelineItem<TimelineCard> card) {
      return cardIds.add(card.getData()
          .getCardId());
    }
  }
}
