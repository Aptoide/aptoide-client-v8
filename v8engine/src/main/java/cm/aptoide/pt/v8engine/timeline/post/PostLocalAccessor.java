package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import java.util.List;
import rx.Single;

public class PostLocalAccessor implements PostAccessor {

  private final InstalledRepository installedRepository;

  public PostLocalAccessor(InstalledRepository installedRepository) {
    this.installedRepository = installedRepository;
  }

  @Override public Single<String> postOnTimeline(String url, String content, String packageName) {
    return Single.error(new NoSuchMethodException());
  }

  @Override public Single<List<PostRemoteAccessor.RelatedApp>> getRelatedApps(String url) {
    return installedRepository.getAllInstalledSorted()
        .first()
        .flatMapIterable(list -> list)
        .filter(app -> !app.isSystemApp())
        .map(installed -> convertInstalledToRelatedApp(installed))
        .toList()
        .toSingle();
  }

  @Override public Single<PostView.PostPreview> getCardPreview(String url) {
    return Single.error(new NoSuchMethodException());
  }

  private PostRemoteAccessor.RelatedApp convertInstalledToRelatedApp(Installed installed) {
    return new PostRemoteAccessor.RelatedApp(installed.getIcon(), installed.getName(),
        PostManager.Origin.Installed, false, installed.getPackageName());
  }
}
