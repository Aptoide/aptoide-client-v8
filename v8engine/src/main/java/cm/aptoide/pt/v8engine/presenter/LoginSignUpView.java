package cm.aptoide.pt.v8engine.presenter;

public interface LoginSignUpView extends View {
  void collapseBottomSheet();

  void expandBottomSheet();

  boolean bottomSheetIsExpanded();

  void setBottomSheetState(int stateCollapsed);
}
