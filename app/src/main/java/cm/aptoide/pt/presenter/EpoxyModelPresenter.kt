package cm.aptoide.pt.presenter

interface EpoxyModelPresenter<V : EpoxyModelView<*>> {
  fun present(view: V)
}