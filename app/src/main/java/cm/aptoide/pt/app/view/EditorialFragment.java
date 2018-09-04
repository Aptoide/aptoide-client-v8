package cm.aptoide.pt.app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialFragment extends NavigationTrackFragment
    implements EditorialView, NotBottomNavigationView {

  @Inject EditorialPresenter presenter;
  private Toolbar toolbar;
  private ImageView appImage;
  private TextView itemName;
  private View appCardView;
  private View genericErrorView;
  private View noNetworkErrorView;
  private ProgressBar progressBar;
  private View genericRetryButton;
  private View noNetworkRetryButton;
  private RecyclerView editorialItems;
  private EditorialItemsAdapter adapter;
  private ImageView appCardImage;
  private TextView appCardTitle;
  private Button appCardButton;
  private View editorialItemsCard;
  private View actionItemCard;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    appImage = (ImageView) view.findViewById(R.id.app_graphic);
    itemName = (TextView) view.findViewById(R.id.action_item_name);
    appCardView = view.findViewById(R.id.app_cardview);
    appCardImage = (ImageView) appCardView.findViewById(R.id.app_icon_imageview);
    appCardTitle = (TextView) appCardView.findViewById(R.id.app_title_textview);
    appCardButton = (Button) appCardView.findViewById(R.id.appview_install_button);
    actionItemCard = view.findViewById(R.id.action_item_card);
    editorialItemsCard = view.findViewById(R.id.card_layout);
    editorialItems = (RecyclerView) view.findViewById(R.id.editorial_items);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    genericRetryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    adapter = new EditorialItemsAdapter(new ArrayList<>());
    editorialItems.setLayoutManager(linearLayoutManager);
    editorialItems.setAdapter(adapter);
    editorialItems.setNestedScrollingEnabled(false);
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    toolbar = null;
    appImage = null;
    itemName = null;
    actionItemCard = null;
    appCardView = null;
    appCardImage = null;
    appCardTitle = null;
    appCardButton = null;

    editorialItemsCard = null;
    editorialItems = null;
    genericErrorView = null;
    noNetworkErrorView = null;
    progressBar = null;
    genericRetryButton = null;
    noNetworkRetryButton = null;
    adapter = null;

    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.editorial_layout, container, false);
  }

  @Override public void showLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItemsCard.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItemsCard.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<Void> retryClicked() {
    return Observable.merge(RxView.clicks(genericRetryButton), RxView.clicks(noNetworkRetryButton));
  }

  @Override public void setToolbarInfo(String title) {
    toolbar.setTitle(title);
  }

  @Override public Observable<Void> installButtonClick() {
    return RxView.clicks(appCardButton);
  }

  @Override public void populateView(EditorialViewModel editorialViewModel) {
    populateAppContent(editorialViewModel);
    populateCardContent(editorialViewModel);
  }

  @Override public void showError(EditorialViewModel.Error error) {
    switch (error) {
      case NETWORK:
        noNetworkErrorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        genericErrorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  private void populateAppContent(EditorialViewModel editorialViewModel) {
    actionItemCard.setVisibility(View.VISIBLE);
    ImageLoader.with(getContext())
        .load(editorialViewModel.getBackgroundImage(), appImage);
    appImage.setVisibility(View.VISIBLE);
    itemName.setText(editorialViewModel.getCardType());
    itemName.setVisibility(View.VISIBLE);
    appCardTitle.setText(editorialViewModel.getAppName());
    ImageLoader.with(getContext())
        .load(editorialViewModel.getIcon(), appCardImage);
    appCardView.setVisibility(View.VISIBLE);
  }

  private void populateCardContent(EditorialViewModel editorialViewModel) {
    editorialItemsCard.setVisibility(View.VISIBLE);
    adapter.add(editorialViewModel.getContentList());
  }
}
