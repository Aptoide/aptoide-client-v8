package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import rx.Observable;

/**
 * Created by jdandrade on 25/10/2016.
 */

public class AnalyticsEventRequest extends V7<BaseV7Response, AnalyticsEventRequest.Body> {

  private final String action;
  private final String name;
  private final String context;

  private AnalyticsEventRequest(Body body, String baseHost, String action, String name,
      String context) {
    super(body, baseHost);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static AnalyticsEventRequest of(String accessToken, Body.Data data, String eventName,
      String uniqueIdentifier) {
    final BaseBodyDecorator decorator = new BaseBodyDecorator(uniqueIdentifier);
    final AnalyticsEventRequest.Body body =
        new AnalyticsEventRequest.Body(data, DataProvider.getConfiguration().getAppId());

    return new AnalyticsEventRequest((Body) decorator.decorate(body, accessToken), BASE_HOST, "CLICK",
        eventName, "TIMELINE");
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }

  public static class Body extends AnalyticsBaseBody {

    private final Data data;

    public Body(Data data, String packageName) {
      super(packageName);
      this.data = data;
    }

    public Data getData() {
      return data;
    }

    public static class Data {
      private String cardType;
      private String source;
      private Specific specific;

      public Data(String cardType, String source, Specific specific) {
        this.cardType = cardType;
        this.source = source;
        this.specific = specific;
      }

      public static DataBuilder builder() {
        return new DataBuilder();
      }

      public String getCardType() {
        return cardType;
      }

      public String getSource() {
        return source;
      }

      public Specific getSpecific() {
        return specific;
      }

      public static class DataBuilder {
        private String cardType;
        private String source;
        private Specific specific;

        DataBuilder() {
        }

        public Data.DataBuilder cardType(String cardType) {
          this.cardType = cardType;
          return this;
        }

        public Data.DataBuilder source(String source) {
          this.source = source;
          return this;
        }

        public Data.DataBuilder specific(Specific specific) {
          this.specific = specific;
          return this;
        }

        public Data build() {
          return new Data(cardType, source, specific);
        }
      }
    }

    public static class Specific {
      private String store;
      private String app;
      private String url;
      private String similarTo;
      private String basedOn;

      Specific(String store, String app, String url, String similarTo, String basedOn) {
        this.store = store;
        this.app = app;
        this.url = url;
        this.similarTo = similarTo;
        this.basedOn = basedOn;
      }

      public static SpecificBuilder builder() {
        return new SpecificBuilder();
      }

      public String getStore() {
        return store;
      }

      public String getApp() {
        return app;
      }

      public String getUrl() {
        return url;
      }

      public String getSimilarTo() {
        return similarTo;
      }

      public String getBasedOn() {
        return basedOn;
      }

      public static class SpecificBuilder {
        private String store;
        private String app;
        private String url;
        private String similarTo;
        private String basedOn;

        SpecificBuilder() {
        }

        public Specific.SpecificBuilder store(String store) {
          this.store = store;
          return this;
        }

        public Specific.SpecificBuilder app(String app) {
          this.app = app;
          return this;
        }

        public Specific.SpecificBuilder url(String url) {
          this.url = url;
          return this;
        }

        public Specific.SpecificBuilder similarTo(String similarTo) {
          this.similarTo = similarTo;
          return this;
        }

        public Specific.SpecificBuilder basedOn(String basedOn) {
          this.basedOn = basedOn;
          return this;
        }

        public Specific build() {
          return new Specific(store, app, url, similarTo, basedOn);
        }
      }
    }
  }
}
