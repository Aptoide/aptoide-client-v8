package cm.aptoide.pt.impressions;

import rx.Completable;

public class ImpressionManager {
  private final ImpressionService impressionService;

  public ImpressionManager(ImpressionService impressionService) {
    this.impressionService = impressionService;
  }

  public Completable markAsRead(String id, boolean dismiss) {
    return impressionService.markAsRead(id, dismiss);
  }
}
