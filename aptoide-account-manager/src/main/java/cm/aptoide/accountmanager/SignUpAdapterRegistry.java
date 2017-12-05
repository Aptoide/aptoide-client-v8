package cm.aptoide.accountmanager;

import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class SignUpAdapterRegistry {

  private final Map<String, SignUpAdapter> adapters;
  private final AccountService accountService;

  public SignUpAdapterRegistry(Map<String, SignUpAdapter> adapters, AccountService accountService) {
    this.adapters = adapters;
    this.accountService = accountService;
  }

  public void register(String type, SignUpAdapter adapter) {
    adapters.put(type, adapter);
  }

  public <T> Single<Account> signUp(String type, T data) {
    return adapters.get(type)
        .signUp(data, accountService);
  }

  public Completable logoutAll() {
    return Observable.from(adapters.values())
        .filter(adapter -> adapter.isEnabled())
        .map(adapter -> adapter.logout())
        .flatMapCompletable(logout -> logout)
        .toCompletable();
  }

  public boolean isEnabled(String type) {
    return adapters.get(type)
        .isEnabled();
  }
}
