package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import lombok.Data;

/**
 * Created by trinkes on 30/12/2016.
 */
public @Data class Event<T> {
  String action;
  T data;
  String name;
  String context;
}
