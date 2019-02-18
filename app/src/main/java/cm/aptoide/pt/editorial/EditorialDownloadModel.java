package cm.aptoide.pt.editorial;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;

public class EditorialDownloadModel extends DownloadModel {

  private final int position;

  public EditorialDownloadModel(DownloadModel.Action action, int progress,
      DownloadModel.DownloadState downloadState, GetAppMeta.Pay pay, int position) {
    super(action, progress, downloadState, pay);
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}
