package cm.aptoide.pt.editorial;

import cm.aptoide.pt.app.DownloadModel;

public class EditorialDownloadModel extends DownloadModel {

  private final int position;

  public EditorialDownloadModel(Action action, int progress, DownloadState downloadState,
      int position) {
    super(action, progress, downloadState);
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}
