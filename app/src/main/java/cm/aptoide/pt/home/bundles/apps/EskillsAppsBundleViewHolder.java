package cm.aptoide.pt.home.bundles.apps;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.aptoideviews.skeleton.Skeleton;
import cm.aptoide.aptoideviews.skeleton.SkeletonUtils;
import cm.aptoide.pt.ApplicationComponent;
import cm.aptoide.pt.ApplicationModule;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DaggerApplicationComponent;
import cm.aptoide.pt.FlavourApplicationModule;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.home.bundles.base.AppBundle;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.rx2.RxSingleKt;
import okhttp3.OkHttpClient;
import rx.subjects.PublishSubject;

import static android.content.Context.WINDOW_SERVICE;

import com.aptoide.aptoide_ab_testing.AptoideFlagr;
import com.aptoide.aptoide_ab_testing.model.EvalContext;
import com.aptoide.aptoide_ab_testing.model.Flag;
import com.aptoide.aptoide_ab_testing.model.PostEvaluationResponseJson;
import com.aptoide.aptoide_ab_testing.model.Tag;
import com.aptoide.aptoide_ab_testing.network.FlagrApiService;
import com.google.gson.JsonObject;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class EskillsAppsBundleViewHolder extends AppBundleViewHolder implements ExperimentClicked {
  private final Button moreButton;
  private final AppsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView appsList;

  private final Skeleton skeleton;

  // AB Testing - Experiment 1
  private static final String INDICATIVE_EVENT_PARTICIPATE = "vanilla_ab_test_1923_eskills_bundle_background_participate";
  private static final String INDICATIVE_EVENT_CONVERT = "vanilla_ab_test_1923_eskills_bundle_background_convert";
  private static final String FLAGR_URL = "https://flagr.aptoide.com/#/";
  private final LinearLayout ll;
  private Map<String, Object> properties;
  private boolean flagEnabled;
  private ApplicationComponent applicationComponent;
  @Inject IdsRepository idsRepository;
  @Inject @Named("default") OkHttpClient defaultClient;
  @Inject @Named("indicativeEventLogger") EventLogger eventLogger;
  @Inject NavigationTracker navigationTracker;


  public EskillsAppsBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter) {
    super(view);

    this.uiEventsListener = uiEventsListener;
    moreButton = view.findViewById(R.id.bundle_more);
    appsList = view.findViewById(R.id.apps_list);
    appsInBundleAdapter =
        new AppsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener, this);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);
    appsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });

    appsList.setLayoutManager(layoutManager);
    appsList.setAdapter(appsInBundleAdapter);
    appsList.setNestedScrollingEnabled(false);


    Resources resources = view.getContext()
        .getResources();
    WindowManager windowManager = (WindowManager) view.getContext()
        .getSystemService(WINDOW_SERVICE);
    skeleton = SkeletonUtils.applySkeleton(appsList, R.layout.app_home_item_skeleton,
        Type.APPS_GROUP.getPerLineCount(resources, windowManager) * 3);


    // -- EXPERIMENT 1 ----------------
    ll = view.findViewById(R.id.eskills_apps_bundle_item_linear_layout);

    // Dagger
    getApplicationComponent(view).inject(this);

    // Properties for Indicative
    properties = new HashMap<>();

    // Get user id
    String userId = idsRepository.getUniqueIdentifier().toBlocking().value();

    // Get Flagr service
    AptoideFlagr flagr = new AptoideFlagr(new FlagrApiService(FLAGR_URL, defaultClient));

    // Id of flag in Flagr
    int flagId = 1;

    try {
      // Get flag information
      Single<Flag> getFlag = RxSingleKt.rxSingle(
              EmptyCoroutineContext.INSTANCE,
              ((coroutineScope, continuation) -> flagr.getFlag(String.valueOf(flagId), continuation))
      );
      Flag getFlagResult = getFlag.blockingGet();

      // If flag is enabled,
      flagEnabled = getFlagResult.getEnabled();
      if (flagEnabled) {

        // Get some information from flag necessary for posting evaluation
        String flagKey = getFlagResult.getKey();

        List<Tag> flagTags = getFlagResult.getTags();
        List<String> flagTagsValue = new ArrayList<>();
        for (Tag tag : flagTags) {
          flagTagsValue.add(tag.getValue());
        }

        JsonObject entityContext = new JsonObject(); // Following Flagr's example

        EvalContext body = new EvalContext(
                userId,
                "user",
                entityContext,
                true,
                flagId,
                flagKey,
                flagTagsValue,
                "ANY"
        );

        // Evaluate the flag
        Single<PostEvaluationResponseJson> postEvaluation = RxSingleKt.rxSingle(
                EmptyCoroutineContext.INSTANCE,
                ((coroutineScope, continuation) -> flagr.postEvaluation(body, continuation))
        );
        PostEvaluationResponseJson postEvaluationResult = postEvaluation.blockingGet();

        // See which variant we got
        String variant = postEvaluationResult.getVariantKey();
        // Put in properties (for Indicative)
        properties.put("group", variant);

        if (variant.equals("a")) {
          // Do nothing
        } else if (variant.equals("b")) {
          // Change background
          ll.setBackgroundResource(R.drawable.eskills_apps_bundle_gradient);
        }

        // Record participation (impression) in Indicative
        eventLogger.log(INDICATIVE_EVENT_PARTICIPATE, properties, AnalyticsManager.Action.IMPRESSION, navigationTracker.getCurrentViewName());
      }

    } catch (Exception e) {
      Log.d("EXPERIMENT_ERROR", e.toString());
    }

  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }

    if (homeBundle.getContent() == null) {
      skeleton.showSkeleton();
    } else {
      appsInBundleAdapter.updateBundle(homeBundle, position);
      appsInBundleAdapter.update((List<Application>) homeBundle.getContent());
      skeleton.showOriginal();
      appsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dx > 0) {
            uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.SCROLL_RIGHT));
          }
        }
      });

      itemView.setOnClickListener(v -> uiEventsListener.onNext(
          new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.ESKILLS_KNOW_MORE)));
      moreButton.setOnClickListener(v -> {
        uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.ESKILLS_KNOW_MORE));

        // If flag is enabled (experiment is on)
        if (flagEnabled) {
          // Record conversion in Indicative
          eventLogger.log(INDICATIVE_EVENT_CONVERT, properties, AnalyticsManager.Action.CLICK, navigationTracker.getCurrentViewName());
        }
      });

    }
  }

  /**
   * For dependency injection
   *
   * @param view
   * @return
   */
  public ApplicationComponent getApplicationComponent(View view) {

    AptoideApplication application = (AptoideApplication) view.getContext().getApplicationContext();

    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
              .applicationModule(new ApplicationModule(application))
              .flavourApplicationModule(new FlavourApplicationModule(application))
              .build();
    }
    return applicationComponent;
  }

  @Override
  public boolean onClicked() {
    // If flag is enabled (experiment is on)
    if (flagEnabled) {
      // Record conversion in Indicative
      eventLogger.log(INDICATIVE_EVENT_CONVERT, properties, AnalyticsManager.Action.CLICK, navigationTracker.getCurrentViewName());
    }
    return false;
  }

}
