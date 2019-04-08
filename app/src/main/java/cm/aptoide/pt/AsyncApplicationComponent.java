package cm.aptoide.pt;

import cm.aptoide.pt.ads.MoPubAdsService;
import com.google.common.util.concurrent.ListenableFuture;
import dagger.producers.ProductionComponent;

@ProductionComponent(modules = AsyncApplicationModule.class, dependencies = ApplicationComponent.class)

public interface AsyncApplicationComponent {
  ListenableFuture<MoPubAdsService> moPubAdsService();
}
