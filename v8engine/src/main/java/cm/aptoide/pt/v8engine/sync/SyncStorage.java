package cm.aptoide.pt.v8engine.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncStorage {

  private final Map<String, Sync> syncs;

  public SyncStorage(Map<String, Sync> syncs) {
    this.syncs = syncs;
  }

  public void save(Sync sync) {
    syncs.put(sync.getId(), sync);
  }

  public Sync get(String syncId) {
    return syncs.get(syncId);
  }

  public List<Sync> getAll() {
    return new ArrayList<>(syncs.values());
  }

  public void remove(String syncId) {
    syncs.remove(syncId);
  }
}
