package cm.aptoide.pt.social.data;

import android.text.TextUtils;
import cm.aptoide.pt.PackageRepository;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineItem;
import java.util.Set;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

public class TimelineCardFilter {
  private final TimelineCardDuplicateFilter duplicateFilter;
  private final PackageRepository packageRepository;

  public TimelineCardFilter(TimelineCardDuplicateFilter duplicateFilter,
      PackageRepository packageRepository) {
    this.duplicateFilter = duplicateFilter;
    this.packageRepository = packageRepository;
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
    final TimelineCard card = timelineItem.getData();
    String packageName = null;
    if (card instanceof Recommendation) {
      packageName = ((Recommendation) card).getRecommendedApp()
          .getPackageName();
    }
    if (!TextUtils.isEmpty(packageName)) {
      return packageRepository.isPackageInstalled(packageName)
          .flatMapObservable(installed -> {
            if (!installed) {
              return Observable.just(timelineItem);
            }
            return Observable.empty();
          });
    }
    return Observable.just(timelineItem);
  }

  private Observable<TimelineItem<TimelineCard>> filterAlreadyDoneUpdates(
      TimelineItem<TimelineCard> timelineItem) {
    final TimelineCard card = timelineItem.getData();
    String packageName = null;
    if (card instanceof AppUpdate) {
      packageName = ((AppUpdate) card).getPackageName();
    }
    if (!TextUtils.isEmpty(packageName)) {
      return packageRepository.getPackageVersionCode(packageName)
          .onErrorResumeNext(err -> Single.just(null))
          .flatMapObservable(versionCode -> {
            if (versionCode != null && versionCode >= ((AppUpdate) timelineItem.getData()).getFile()
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
      return card.getData()
          .getCardId() == null || cardIds.add(card.getData()
          .getCardId());
    }
  }
}
