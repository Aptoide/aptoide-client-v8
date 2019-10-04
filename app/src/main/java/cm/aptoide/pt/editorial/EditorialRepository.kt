package cm.aptoide.pt.editorial

import rx.Single

class EditorialRepository(private val editorialService: EditorialService) {

  private var cachedEditorialViewModel: EditorialViewModel? = null;

  fun loadEditorialViewModel(editorialLoadSource: EditorialLoadSource): Single<EditorialViewModel> {
    return if (cachedEditorialViewModel != null)
      Single.just(cachedEditorialViewModel)
    else
      when (editorialLoadSource) {
        is CardId -> editorialService.loadEditorialViewModel(
            editorialLoadSource.cardId).doOnError { throwable -> throwable.printStackTrace() }.map { editorialViewModel ->
          saveResponse(editorialViewModel)
        }
        is Slug -> editorialService.loadEditorialViewModelWithSlug(
            editorialLoadSource.slug).doOnError { throwable -> throwable.printStackTrace() }.map { editorialViewModel ->
          saveResponse(editorialViewModel)
        }
      }
  }

  private fun saveResponse(editorialViewModel: EditorialViewModel): EditorialViewModel {
    if (!editorialViewModel.hasError() && !editorialViewModel.isLoading) {
      cachedEditorialViewModel = editorialViewModel
    }
    return editorialViewModel
  }
}