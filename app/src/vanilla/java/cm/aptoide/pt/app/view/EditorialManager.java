package cm.aptoide.pt.app.view;

import rx.Single;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialManager {

  private final EditorialRepository editorialRepository;
  private final String cardId;
  private final String editorialName;

  public EditorialManager(EditorialRepository editorialRepository, String cardId,
      String editorialName) {

    this.editorialRepository = editorialRepository;
    this.cardId = cardId;
    this.editorialName = editorialName;
  }

  public Single<EditorialViewModel> loadEditorialViewModel() {
    return editorialRepository.loadEditorialViewModel(cardId);
  }

  public String getEditorialName() {
    return editorialName;
  }
}
