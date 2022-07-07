package cm.aptoide.pt.editorial;

import cm.aptoide.pt.app.DownloadModel;

public class EditorialDownloadModel extends DownloadModel {

  private final int position;

  public EditorialDownloadModel(EditorialDownloadModel editorialDownloadModel) {
    super(editorialDownloadModel.getAction(), editorialDownloadModel.getProgress(),
        editorialDownloadModel.getDownloadState());
    this.position = editorialDownloadModel.getPosition();
  }

  public EditorialDownloadModel(DownloadModel.Action action, int progress,
      DownloadModel.DownloadState downloadState, int position) {
    super(action, progress, downloadState);
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}
