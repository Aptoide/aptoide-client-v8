package cm.aptoide.pt.presenter

abstract class ViewPresenter<V : Any, C : Any> : Presenter {

  protected lateinit var configuration: C
  protected lateinit var view: V
  protected lateinit var lifecycleView: View

  fun initialize(lifecycleView: View, view: V, configuration: C) {
    this.lifecycleView = lifecycleView
    this.view = view
    this.configuration = configuration
  }
}