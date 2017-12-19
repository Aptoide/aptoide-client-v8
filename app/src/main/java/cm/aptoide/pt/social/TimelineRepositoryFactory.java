package cm.aptoide.pt.social;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.social.data.TimelineCardFilter;
import cm.aptoide.pt.social.data.TimelineRemoteDataSource;
import cm.aptoide.pt.social.data.TimelineRepository;
import cm.aptoide.pt.social.data.TimelineResponseCardMapper;
import cm.aptoide.pt.updates.UpdateRepository;
import java.util.HashSet;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by jdandrade on 02/08/2017.
 */

public class TimelineRepositoryFactory {

  private final Map<String, TimelineRepository> repositories;

  private BodyInterceptor<BaseBody> baseBodyInterceptorV7;
  private OkHttpClient defaultClient;
  private SharedPreferences defaultSharedPreferences;
  private TokenInvalidator tokenInvalidator;
  private LinksHandlerFactory linksHandlerFactory;
  private PackageRepository packageRepository;
  private Converter.Factory defaultConverter;
  private TimelineResponseCardMapper mapper;
  private UpdateRepository updateRepository;

  public TimelineRepositoryFactory(Map<String, TimelineRepository> repositories,
      BodyInterceptor<BaseBody> baseBodyInterceptorV7, OkHttpClient defaultClient,
      SharedPreferences defaultSharedPreferences, TokenInvalidator tokenInvalidator,
      LinksHandlerFactory linksHandlerFactory, PackageRepository packageRepository,
      Converter.Factory defaultConverter, TimelineResponseCardMapper mapper,
      UpdateRepository updateRepository) {
    this.repositories = repositories;
    this.baseBodyInterceptorV7 = baseBodyInterceptorV7;
    this.defaultClient = defaultClient;
    this.defaultSharedPreferences = defaultSharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.linksHandlerFactory = linksHandlerFactory;
    this.packageRepository = packageRepository;
    this.defaultConverter = defaultConverter;
    this.mapper = mapper;
    this.updateRepository = updateRepository;
  }

  public TimelineRepository create(String action) {
    if (!repositories.containsKey(action)) {

      final TimelineCardFilter.TimelineCardDuplicateFilter duplicateFilter =
          new TimelineCardFilter.TimelineCardDuplicateFilter(new HashSet<>());
      final TimelineCardFilter postFilter =
          new TimelineCardFilter(duplicateFilter, packageRepository, updateRepository);

      repositories.put(action, new TimelineRepository(
          new TimelineRemoteDataSource(action, baseBodyInterceptorV7, defaultClient,
              defaultConverter, packageRepository, 20, 10, mapper, linksHandlerFactory, 20, 0,
              Integer.MAX_VALUE, tokenInvalidator, defaultSharedPreferences, postFilter)));
    }
    return repositories.get(action);
  }
}
