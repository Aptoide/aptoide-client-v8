package cm.aptoide.pt.widget.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.navigator.TabNavigator;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by franciscocalado on 10/17/17.
 */

public class SearchWidgetFragment extends FragmentView implements SearchWidgetView {

  private TabNavigator tabNavigator;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public static SearchWidgetFragment newInstance(){return new SearchWidgetFragment();}

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_search_widget, container, false);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.tokenInvalidator = ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    this.sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final BodyInterceptor<BaseBody> baseBodyInterceptorV7 =
        ((AptoideApplication) getContext().getApplicationContext()).getBodyInterceptorWebV7();
    final OkHttpClient defaultClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory defaultConverter = WebService.getDefaultConverter();

  }

}
