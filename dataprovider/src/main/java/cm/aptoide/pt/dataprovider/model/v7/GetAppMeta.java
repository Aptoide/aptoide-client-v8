/*
 * Copyright (c) 2016.
 * Modified on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetAppMeta extends BaseV7Response {

  private App data;

  public GetAppMeta() {
  }

  public App getData() {
    return this.data;
  }

  public void setData(App data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetAppMeta;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetAppMeta)) return false;
    final GetAppMeta other = (GetAppMeta) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    return this$data == null ? other$data == null : this$data.equals(other$data);
  }

  public String toString() {
    return "GetAppMeta(data=" + this.getData() + ")";
  }

  public static class App {

    private long id;
    private String name;
    @JsonProperty("package") private String packageName;
    private long size;
    private String icon;
    private String graphic;
    private String added;
    private String modified;
    private Developer developer;
    private Store store;
    private GetAppMetaFile file;
    private Media media;
    private Urls urls;
    private Stats stats;
    private Obb obb;
    private Pay pay;

    public App() {
    }

    public boolean isPaid() {
      return (pay != null && pay.getPrice() > 0.0f);
    }

    public String getMd5() {
      return file == null ? "" : file.getMd5sum();
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackageName() {
      return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public long getSize() {
      return this.size;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public String getIcon() {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getGraphic() {
      return this.graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
    }

    public String getAdded() {
      return this.added;
    }

    public void setAdded(String added) {
      this.added = added;
    }

    public String getModified() {
      return this.modified;
    }

    public void setModified(String modified) {
      this.modified = modified;
    }

    public Developer getDeveloper() {
      return this.developer;
    }

    public void setDeveloper(Developer developer) {
      this.developer = developer;
    }

    public Store getStore() {
      return this.store;
    }

    public void setStore(Store store) {
      this.store = store;
    }

    public GetAppMetaFile getFile() {
      return this.file;
    }

    public void setFile(GetAppMetaFile file) {
      this.file = file;
    }

    public Media getMedia() {
      return this.media;
    }

    public void setMedia(Media media) {
      this.media = media;
    }

    public Urls getUrls() {
      return this.urls;
    }

    public void setUrls(Urls urls) {
      this.urls = urls;
    }

    public Stats getStats() {
      return this.stats;
    }

    public void setStats(Stats stats) {
      this.stats = stats;
    }

    public Obb getObb() {
      return this.obb;
    }

    public void setObb(Obb obb) {
      this.obb = obb;
    }

    public Pay getPay() {
      return this.pay;
    }

    public void setPay(Pay pay) {
      this.pay = pay;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $packageName = this.getPackageName();
      result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
      final long $size = this.getSize();
      result = result * PRIME + (int) ($size >>> 32 ^ $size);
      final Object $icon = this.getIcon();
      result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
      final Object $graphic = this.getGraphic();
      result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
      final Object $added = this.getAdded();
      result = result * PRIME + ($added == null ? 43 : $added.hashCode());
      final Object $modified = this.getModified();
      result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
      final Object $developer = this.getDeveloper();
      result = result * PRIME + ($developer == null ? 43 : $developer.hashCode());
      final Object $store = this.getStore();
      result = result * PRIME + ($store == null ? 43 : $store.hashCode());
      final Object $file = this.getFile();
      result = result * PRIME + ($file == null ? 43 : $file.hashCode());
      final Object $media = this.getMedia();
      result = result * PRIME + ($media == null ? 43 : $media.hashCode());
      final Object $urls = this.getUrls();
      result = result * PRIME + ($urls == null ? 43 : $urls.hashCode());
      final Object $stats = this.getStats();
      result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
      final Object $obb = this.getObb();
      result = result * PRIME + ($obb == null ? 43 : $obb.hashCode());
      final Object $pay = this.getPay();
      result = result * PRIME + ($pay == null ? 43 : $pay.hashCode());
      return result;
    }

    protected boolean canEqual(Object other) {
      return other instanceof App;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof App)) return false;
      final App other = (App) o;
      if (!other.canEqual(this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$packageName = this.getPackageName();
      final Object other$packageName = other.getPackageName();
      if (this$packageName == null ? other$packageName != null
          : !this$packageName.equals(other$packageName)) {
        return false;
      }
      if (this.getSize() != other.getSize()) return false;
      final Object this$icon = this.getIcon();
      final Object other$icon = other.getIcon();
      if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
      final Object this$graphic = this.getGraphic();
      final Object other$graphic = other.getGraphic();
      if (this$graphic == null ? other$graphic != null : !this$graphic.equals(other$graphic)) {
        return false;
      }
      final Object this$added = this.getAdded();
      final Object other$added = other.getAdded();
      if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
      final Object this$modified = this.getModified();
      final Object other$modified = other.getModified();
      if (this$modified == null ? other$modified != null : !this$modified.equals(other$modified)) {
        return false;
      }
      final Object this$developer = this.getDeveloper();
      final Object other$developer = other.getDeveloper();
      if (this$developer == null ? other$developer != null
          : !this$developer.equals(other$developer)) {
        return false;
      }
      final Object this$store = this.getStore();
      final Object other$store = other.getStore();
      if (this$store == null ? other$store != null : !this$store.equals(other$store)) return false;
      final Object this$file = this.getFile();
      final Object other$file = other.getFile();
      if (this$file == null ? other$file != null : !this$file.equals(other$file)) return false;
      final Object this$media = this.getMedia();
      final Object other$media = other.getMedia();
      if (this$media == null ? other$media != null : !this$media.equals(other$media)) return false;
      final Object this$urls = this.getUrls();
      final Object other$urls = other.getUrls();
      if (this$urls == null ? other$urls != null : !this$urls.equals(other$urls)) return false;
      final Object this$stats = this.getStats();
      final Object other$stats = other.getStats();
      if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
      final Object this$obb = this.getObb();
      final Object other$obb = other.getObb();
      if (this$obb == null ? other$obb != null : !this$obb.equals(other$obb)) return false;
      final Object this$pay = this.getPay();
      final Object other$pay = other.getPay();
      return this$pay == null ? other$pay == null : this$pay.equals(other$pay);
    }

    public String toString() {
      return "GetAppMeta.App(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", packageName="
          + this.getPackageName()
          + ", size="
          + this.getSize()
          + ", icon="
          + this.getIcon()
          + ", graphic="
          + this.getGraphic()
          + ", added="
          + this.getAdded()
          + ", modified="
          + this.getModified()
          + ", developer="
          + this.getDeveloper()
          + ", store="
          + this.getStore()
          + ", file="
          + this.getFile()
          + ", media="
          + this.getMedia()
          + ", urls="
          + this.getUrls()
          + ", stats="
          + this.getStats()
          + ", obb="
          + this.getObb()
          + ", pay="
          + this.getPay()
          + ")";
    }
  }

  public static class Pay {

    private double price;
    private String symbol;
    private String currency;
    private String status;

    public Pay() {
    }

    public boolean isPaid() {
      return "OK".equalsIgnoreCase(status);
    }

    public void setPaid() {
      status = "OK";
    }

    public double getPrice() {
      return this.price;
    }

    public void setPrice(double price) {
      this.price = price;
    }

    public String getSymbol() {
      return this.symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public String getCurrency() {
      return this.currency;
    }

    public void setCurrency(String currency) {
      this.currency = currency;
    }

    public String getStatus() {
      return this.status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Pay;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Pay)) return false;
      final Pay other = (Pay) o;
      if (!other.canEqual(this)) return false;
      if (Double.compare(this.getPrice(), other.getPrice()) != 0) return false;
      final Object this$symbol = this.getSymbol();
      final Object other$symbol = other.getSymbol();
      if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) {
        return false;
      }
      final Object this$currency = this.getCurrency();
      final Object other$currency = other.getCurrency();
      if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
        return false;
      }
      final Object this$status = this.getStatus();
      final Object other$status = other.getStatus();
      return this$status == null ? other$status == null : this$status.equals(other$status);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $price = Double.doubleToLongBits(this.getPrice());
      result = result * PRIME + (int) ($price >>> 32 ^ $price);
      final Object $symbol = this.getSymbol();
      result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
      final Object $currency = this.getCurrency();
      result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
      final Object $status = this.getStatus();
      result = result * PRIME + ($status == null ? 43 : $status.hashCode());
      return result;
    }

    public String toString() {
      return "GetAppMeta.Pay(price="
          + this.getPrice()
          + ", symbol="
          + this.getSymbol()
          + ", currency="
          + this.getCurrency()
          + ", status="
          + this.getStatus()
          + ")";
    }
  }

  public static class Developer {

    private String name;
    private String website;
    private String email;
    private String privacy;

    public Developer() {
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getWebsite() {
      return this.website;
    }

    public void setWebsite(String website) {
      this.website = website;
    }

    public String getEmail() {
      return this.email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPrivacy() {
      return this.privacy;
    }

    public void setPrivacy(String privacy) {
      this.privacy = privacy;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Developer;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Developer)) return false;
      final Developer other = (Developer) o;
      if (!other.canEqual(this)) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$website = this.getWebsite();
      final Object other$website = other.getWebsite();
      if (this$website == null ? other$website != null : !this$website.equals(other$website)) {
        return false;
      }
      final Object this$email = this.getEmail();
      final Object other$email = other.getEmail();
      if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
      final Object this$privacy = this.getPrivacy();
      final Object other$privacy = other.getPrivacy();
      return this$privacy == null ? other$privacy == null : this$privacy.equals(other$privacy);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $website = this.getWebsite();
      result = result * PRIME + ($website == null ? 43 : $website.hashCode());
      final Object $email = this.getEmail();
      result = result * PRIME + ($email == null ? 43 : $email.hashCode());
      final Object $privacy = this.getPrivacy();
      result = result * PRIME + ($privacy == null ? 43 : $privacy.hashCode());
      return result;
    }

    public String toString() {
      return "GetAppMeta.Developer(name="
          + this.getName()
          + ", website="
          + this.getWebsite()
          + ", email="
          + this.getEmail()
          + ", privacy="
          + this.getPrivacy()
          + ")";
    }
  }

  public static class GetAppMetaFile extends File {

    private GetAppMetaFile.Signature signature;
    private GetAppMetaFile.Hardware hardware;
    private Malware malware;
    private GetAppMetaFile.Flags flags;
    private List<String> usedFeatures;
    private List<String> usedPermissions;
    private List<String> tags;

    public GetAppMetaFile() {
    }

    public boolean isGoodApp() {
      return this.flags != null && flags.review != null && flags.review.equalsIgnoreCase(
          Flags.GOOD);
    }

    public Signature getSignature() {
      return this.signature;
    }

    public void setSignature(Signature signature) {
      this.signature = signature;
    }

    public Hardware getHardware() {
      return this.hardware;
    }

    public void setHardware(Hardware hardware) {
      this.hardware = hardware;
    }

    public Malware getMalware() {
      return this.malware;
    }

    public void setMalware(Malware malware) {
      this.malware = malware;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + super.hashCode();
      final Object $signature = this.getSignature();
      result = result * PRIME + ($signature == null ? 43 : $signature.hashCode());
      final Object $hardware = this.getHardware();
      result = result * PRIME + ($hardware == null ? 43 : $hardware.hashCode());
      final Object $malware = this.getMalware();
      result = result * PRIME + ($malware == null ? 43 : $malware.hashCode());
      final Object $flags = this.getFlags();
      result = result * PRIME + ($flags == null ? 43 : $flags.hashCode());
      final Object $usedFeatures = this.getUsedFeatures();
      result = result * PRIME + ($usedFeatures == null ? 43 : $usedFeatures.hashCode());
      final Object $usedPermissions = this.getUsedPermissions();
      result = result * PRIME + ($usedPermissions == null ? 43 : $usedPermissions.hashCode());
      final Object $tags = this.getTags();
      result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof GetAppMetaFile)) return false;
      final GetAppMetaFile other = (GetAppMetaFile) o;
      if (!other.canEqual(this)) return false;
      if (!super.equals(o)) return false;
      final Object this$signature = this.getSignature();
      final Object other$signature = other.getSignature();
      if (this$signature == null ? other$signature != null
          : !this$signature.equals(other$signature)) {
        return false;
      }
      final Object this$hardware = this.getHardware();
      final Object other$hardware = other.getHardware();
      if (this$hardware == null ? other$hardware != null : !this$hardware.equals(other$hardware)) {
        return false;
      }
      final Object this$malware = this.getMalware();
      final Object other$malware = other.getMalware();
      if (this$malware == null ? other$malware != null : !this$malware.equals(other$malware)) {
        return false;
      }
      final Object this$flags = this.getFlags();
      final Object other$flags = other.getFlags();
      if (this$flags == null ? other$flags != null : !this$flags.equals(other$flags)) return false;
      final Object this$usedFeatures = this.getUsedFeatures();
      final Object other$usedFeatures = other.getUsedFeatures();
      if (this$usedFeatures == null ? other$usedFeatures != null
          : !this$usedFeatures.equals(other$usedFeatures)) {
        return false;
      }
      final Object this$usedPermissions = this.getUsedPermissions();
      final Object other$usedPermissions = other.getUsedPermissions();
      if (this$usedPermissions == null ? other$usedPermissions != null
          : !this$usedPermissions.equals(other$usedPermissions)) {
        return false;
      }
      final Object this$tags = this.getTags();
      final Object other$tags = other.getTags();
      return this$tags == null ? other$tags == null : this$tags.equals(other$tags);
    }

    public String toString() {
      return "GetAppMeta.GetAppMetaFile(signature="
          + this.getSignature()
          + ", hardware="
          + this.getHardware()
          + ", malware="
          + this.getMalware()
          + ", flags="
          + this.getFlags()
          + ", usedFeatures="
          + this.getUsedFeatures()
          + ", usedPermissions="
          + this.getUsedPermissions()
          + ", tags="
          + this.getTags()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof GetAppMetaFile;
    }

    public Flags getFlags() {
      return this.flags;
    }

    public void setFlags(Flags flags) {
      this.flags = flags;
    }

    public List<String> getUsedFeatures() {
      return this.usedFeatures;
    }

    public void setUsedFeatures(List<String> usedFeatures) {
      this.usedFeatures = usedFeatures;
    }

    public List<String> getUsedPermissions() {
      return this.usedPermissions;
    }

    public void setUsedPermissions(List<String> usedPermissions) {
      this.usedPermissions = usedPermissions;
    }

    public List<String> getTags() {
      return this.tags;
    }

    public void setTags(List<String> tags) {
      this.tags = tags;
    }

    public static class Signature {

      private String sha1;
      private String owner;

      public Signature() {
      }

      public String getSha1() {
        return this.sha1;
      }

      public void setSha1(String sha1) {
        this.sha1 = sha1;
      }

      public String getOwner() {
        return this.owner;
      }

      public void setOwner(String owner) {
        this.owner = owner;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Signature;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Signature)) return false;
        final Signature other = (Signature) o;
        if (!other.canEqual(this)) return false;
        final Object this$sha1 = this.getSha1();
        final Object other$sha1 = other.getSha1();
        if (this$sha1 == null ? other$sha1 != null : !this$sha1.equals(other$sha1)) return false;
        final Object this$owner = this.getOwner();
        final Object other$owner = other.getOwner();
        return this$owner == null ? other$owner == null : this$owner.equals(other$owner);
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $sha1 = this.getSha1();
        result = result * PRIME + ($sha1 == null ? 43 : $sha1.hashCode());
        final Object $owner = this.getOwner();
        result = result * PRIME + ($owner == null ? 43 : $owner.hashCode());
        return result;
      }

      public String toString() {
        return "GetAppMeta.GetAppMetaFile.Signature(sha1="
            + this.getSha1()
            + ", owner="
            + this.getOwner()
            + ")";
      }
    }

    public static class Hardware {

      private int sdk;
      private String screen;
      private int gles;
      private List<String> cpus;
      /**
       * Second array contains only two values: First value is the screen, second value is
       * the
       * density
       */
      private List<List<Integer>> densities;

      public Hardware() {
      }

      public int getSdk() {
        return this.sdk;
      }

      public void setSdk(int sdk) {
        this.sdk = sdk;
      }

      public String getScreen() {
        return this.screen;
      }

      public void setScreen(String screen) {
        this.screen = screen;
      }

      public int getGles() {
        return this.gles;
      }

      public void setGles(int gles) {
        this.gles = gles;
      }

      public List<String> getCpus() {
        return this.cpus;
      }

      public void setCpus(List<String> cpus) {
        this.cpus = cpus;
      }

      public List<List<Integer>> getDensities() {
        return this.densities;
      }

      public void setDensities(List<List<Integer>> densities) {
        this.densities = densities;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Hardware;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Hardware)) return false;
        final Hardware other = (Hardware) o;
        if (!other.canEqual(this)) return false;
        if (this.getSdk() != other.getSdk()) return false;
        final Object this$screen = this.getScreen();
        final Object other$screen = other.getScreen();
        if (this$screen == null ? other$screen != null : !this$screen.equals(other$screen)) {
          return false;
        }
        if (this.getGles() != other.getGles()) return false;
        final Object this$cpus = this.getCpus();
        final Object other$cpus = other.getCpus();
        if (this$cpus == null ? other$cpus != null : !this$cpus.equals(other$cpus)) return false;
        final Object this$densities = this.getDensities();
        final Object other$densities = other.getDensities();
        return this$densities == null ? other$densities == null
            : this$densities.equals(other$densities);
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSdk();
        final Object $screen = this.getScreen();
        result = result * PRIME + ($screen == null ? 43 : $screen.hashCode());
        result = result * PRIME + this.getGles();
        final Object $cpus = this.getCpus();
        result = result * PRIME + ($cpus == null ? 43 : $cpus.hashCode());
        final Object $densities = this.getDensities();
        result = result * PRIME + ($densities == null ? 43 : $densities.hashCode());
        return result;
      }

      public String toString() {
        return "GetAppMeta.GetAppMetaFile.Hardware(sdk="
            + this.getSdk()
            + ", screen="
            + this.getScreen()
            + ", gles="
            + this.getGles()
            + ", cpus="
            + this.getCpus()
            + ", densities="
            + this.getDensities()
            + ")";
      }
    }

    public static class Flags {

      public static final String GOOD = "GOOD";
      /**
       * When there's a review, there are no votes
       * <p>
       * flags: { review": "GOOD" },
       */
      public String review;
      private List<GetAppMetaFile.Flags.Vote> votes;

      public Flags() {
      }

      public String getReview() {
        return this.review;
      }

      public void setReview(String review) {
        this.review = review;
      }

      public List<Vote> getVotes() {
        return this.votes;
      }

      public void setVotes(List<Vote> votes) {
        this.votes = votes;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Flags;
      }

      public static class Vote {

        /**
         * type can be:
         * <p>
         * FAKE, FREEZE, GOOD, LICENSE, VIRUS
         */
        private GetAppMetaFile.Flags.Vote.Type type;
        private int count;

        public Vote() {
        }

        public Type getType() {
          return this.type;
        }

        public void setType(Type type) {
          this.type = type;
        }

        public int getCount() {
          return this.count;
        }

        public void setCount(int count) {
          this.count = count;
        }

        protected boolean canEqual(Object other) {
          return other instanceof Vote;
        }

        public enum Type {
          FAKE, FREEZE, GOOD, LICENSE, VIRUS
        }

        public boolean equals(Object o) {
          if (o == this) return true;
          if (!(o instanceof Vote)) return false;
          final Vote other = (Vote) o;
          if (!other.canEqual(this)) return false;
          final Object this$type = this.getType();
          final Object other$type = other.getType();
          if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
          return this.getCount() == other.getCount();
        }

        public int hashCode() {
          final int PRIME = 59;
          int result = 1;
          final Object $type = this.getType();
          result = result * PRIME + ($type == null ? 43 : $type.hashCode());
          result = result * PRIME + this.getCount();
          return result;
        }

        public String toString() {
          return "GetAppMeta.GetAppMetaFile.Flags.Vote(type="
              + this.getType()
              + ", count="
              + this.getCount()
              + ")";
        }
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Flags)) return false;
        final Flags other = (Flags) o;
        if (!other.canEqual(this)) return false;
        final Object this$review = this.getReview();
        final Object other$review = other.getReview();
        if (this$review == null ? other$review != null : !this$review.equals(other$review)) {
          return false;
        }
        final Object this$votes = this.getVotes();
        final Object other$votes = other.getVotes();
        return this$votes == null ? other$votes == null : this$votes.equals(other$votes);
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $review = this.getReview();
        result = result * PRIME + ($review == null ? 43 : $review.hashCode());
        final Object $votes = this.getVotes();
        result = result * PRIME + ($votes == null ? 43 : $votes.hashCode());
        return result;
      }

      public String toString() {
        return "GetAppMeta.GetAppMetaFile.Flags(review="
            + this.getReview()
            + ", votes="
            + this.getVotes()
            + ")";
      }
    }
  }

  public static class Media {

    private List<String> keywords;
    private String description;
    private String news;
    private List<Media.Screenshot> screenshots;
    private List<Media.Video> videos;

    public Media() {
    }

    public List<String> getKeywords() {
      return this.keywords;
    }

    public void setKeywords(List<String> keywords) {
      this.keywords = keywords;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getNews() {
      return this.news;
    }

    public void setNews(String news) {
      this.news = news;
    }

    public List<Screenshot> getScreenshots() {
      return this.screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
      this.screenshots = screenshots;
    }

    public List<Video> getVideos() {
      return this.videos;
    }

    public void setVideos(List<Video> videos) {
      this.videos = videos;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Media;
    }

    public static class Video {

      private String type;
      private String url;
      private String thumbnail;

      public Video() {
      }

      public String getType() {
        return this.type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getUrl() {
        return this.url;
      }

      public void setUrl(String url) {
        this.url = url;
      }

      public String getThumbnail() {
        return this.thumbnail;
      }

      public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Video;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Video)) return false;
        final Video other = (Video) o;
        if (!other.canEqual(this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
        final Object this$thumbnail = this.getThumbnail();
        final Object other$thumbnail = other.getThumbnail();
        return this$thumbnail == null ? other$thumbnail == null
            : this$thumbnail.equals(other$thumbnail);
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        final Object $thumbnail = this.getThumbnail();
        result = result * PRIME + ($thumbnail == null ? 43 : $thumbnail.hashCode());
        return result;
      }

      public String toString() {
        return "GetAppMeta.Media.Video(type="
            + this.getType()
            + ", url="
            + this.getUrl()
            + ", thumbnail="
            + this.getThumbnail()
            + ")";
      }
    }

    public static class Screenshot {

      private String url;
      private int height;
      private int width;

      public Screenshot() {
      }

      public String getOrientation() {
        return height > width ? "portrait" : "landscape";
      }

      public String getUrl() {
        return this.url;
      }

      public void setUrl(String url) {
        this.url = url;
      }

      public int getHeight() {
        return this.height;
      }

      public void setHeight(int height) {
        this.height = height;
      }

      public int getWidth() {
        return this.width;
      }

      public void setWidth(int width) {
        this.width = width;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Screenshot;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Screenshot)) return false;
        final Screenshot other = (Screenshot) o;
        if (!other.canEqual(this)) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
        if (this.getHeight() != other.getHeight()) return false;
        return this.getWidth() == other.getWidth();
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        result = result * PRIME + this.getHeight();
        result = result * PRIME + this.getWidth();
        return result;
      }

      public String toString() {
        return "GetAppMeta.Media.Screenshot(url="
            + this.getUrl()
            + ", height="
            + this.getHeight()
            + ", width="
            + this.getWidth()
            + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Media)) return false;
      final Media other = (Media) o;
      if (!other.canEqual(this)) return false;
      final Object this$keywords = this.getKeywords();
      final Object other$keywords = other.getKeywords();
      if (this$keywords == null ? other$keywords != null : !this$keywords.equals(other$keywords)) {
        return false;
      }
      final Object this$description = this.getDescription();
      final Object other$description = other.getDescription();
      if (this$description == null ? other$description != null
          : !this$description.equals(other$description)) {
        return false;
      }
      final Object this$news = this.getNews();
      final Object other$news = other.getNews();
      if (this$news == null ? other$news != null : !this$news.equals(other$news)) return false;
      final Object this$screenshots = this.getScreenshots();
      final Object other$screenshots = other.getScreenshots();
      if (this$screenshots == null ? other$screenshots != null
          : !this$screenshots.equals(other$screenshots)) {
        return false;
      }
      final Object this$videos = this.getVideos();
      final Object other$videos = other.getVideos();
      return this$videos == null ? other$videos == null : this$videos.equals(other$videos);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $keywords = this.getKeywords();
      result = result * PRIME + ($keywords == null ? 43 : $keywords.hashCode());
      final Object $description = this.getDescription();
      result = result * PRIME + ($description == null ? 43 : $description.hashCode());
      final Object $news = this.getNews();
      result = result * PRIME + ($news == null ? 43 : $news.hashCode());
      final Object $screenshots = this.getScreenshots();
      result = result * PRIME + ($screenshots == null ? 43 : $screenshots.hashCode());
      final Object $videos = this.getVideos();
      result = result * PRIME + ($videos == null ? 43 : $videos.hashCode());
      return result;
    }

    public String toString() {
      return "GetAppMeta.Media(keywords="
          + this.getKeywords()
          + ", description="
          + this.getDescription()
          + ", news="
          + this.getNews()
          + ", screenshots="
          + this.getScreenshots()
          + ", videos="
          + this.getVideos()
          + ")";
    }
  }

  public static class Urls {

    private String w;
    private String m;

    public Urls() {
    }

    public String getW() {
      return this.w;
    }

    public void setW(String w) {
      this.w = w;
    }

    public String getM() {
      return this.m;
    }

    public void setM(String m) {
      this.m = m;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Urls;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Urls)) return false;
      final Urls other = (Urls) o;
      if (!other.canEqual(this)) return false;
      final Object this$w = this.getW();
      final Object other$w = other.getW();
      if (this$w == null ? other$w != null : !this$w.equals(other$w)) return false;
      final Object this$m = this.getM();
      final Object other$m = other.getM();
      return this$m == null ? other$m == null : this$m.equals(other$m);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $w = this.getW();
      result = result * PRIME + ($w == null ? 43 : $w.hashCode());
      final Object $m = this.getM();
      result = result * PRIME + ($m == null ? 43 : $m.hashCode());
      return result;
    }

    public String toString() {
      return "GetAppMeta.Urls(w=" + this.getW() + ", m=" + this.getM() + ")";
    }
  }

  public static class Stats {

    private Stats.Rating rating;
    @JsonProperty("prating") private Stats.Rating globalRating;
    private int downloads;
    private int pdownloads;

    public Stats() {
    }

    public Rating getRating() {
      return this.rating;
    }

    public void setRating(Rating rating) {
      this.rating = rating;
    }

    public Rating getGlobalRating() {
      return this.globalRating;
    }

    public void setGlobalRating(Rating globalRating) {
      this.globalRating = globalRating;
    }

    public int getDownloads() {
      return this.downloads;
    }

    public void setDownloads(int downloads) {
      this.downloads = downloads;
    }

    public int getPdownloads() {
      return this.pdownloads;
    }

    public void setPdownloads(int pdownloads) {
      this.pdownloads = pdownloads;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Stats;
    }

    public static class Rating {

      private float avg;
      private int total;
      private List<Stats.Rating.Vote> votes;

      public Rating() {
      }

      public float getAvg() {
        return this.avg;
      }

      public void setAvg(float avg) {
        this.avg = avg;
      }

      public int getTotal() {
        return this.total;
      }

      public void setTotal(int total) {
        this.total = total;
      }

      public List<Vote> getVotes() {
        return this.votes;
      }

      public void setVotes(List<Vote> votes) {
        this.votes = votes;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Rating;
      }

      public static class Vote {

        private int value;
        private int count;

        public Vote() {
        }

        public int getValue() {
          return this.value;
        }

        public void setValue(int value) {
          this.value = value;
        }

        public int getCount() {
          return this.count;
        }

        public void setCount(int count) {
          this.count = count;
        }

        protected boolean canEqual(Object other) {
          return other instanceof Vote;
        }

        public boolean equals(Object o) {
          if (o == this) return true;
          if (!(o instanceof Vote)) return false;
          final Vote other = (Vote) o;
          if (!other.canEqual(this)) return false;
          if (this.getValue() != other.getValue()) return false;
          return this.getCount() == other.getCount();
        }

        public int hashCode() {
          final int PRIME = 59;
          int result = 1;
          result = result * PRIME + this.getValue();
          result = result * PRIME + this.getCount();
          return result;
        }

        public String toString() {
          return "GetAppMeta.Stats.Rating.Vote(value="
              + this.getValue()
              + ", count="
              + this.getCount()
              + ")";
        }
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Rating)) return false;
        final Rating other = (Rating) o;
        if (!other.canEqual(this)) return false;
        if (Float.compare(this.getAvg(), other.getAvg()) != 0) return false;
        if (this.getTotal() != other.getTotal()) return false;
        final Object this$votes = this.getVotes();
        final Object other$votes = other.getVotes();
        return this$votes == null ? other$votes == null : this$votes.equals(other$votes);
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Float.floatToIntBits(this.getAvg());
        result = result * PRIME + this.getTotal();
        final Object $votes = this.getVotes();
        result = result * PRIME + ($votes == null ? 43 : $votes.hashCode());
        return result;
      }

      public String toString() {
        return "GetAppMeta.Stats.Rating(avg="
            + this.getAvg()
            + ", total="
            + this.getTotal()
            + ", votes="
            + this.getVotes()
            + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Stats)) return false;
      final Stats other = (Stats) o;
      if (!other.canEqual(this)) return false;
      final Object this$rating = this.getRating();
      final Object other$rating = other.getRating();
      if (this$rating == null ? other$rating != null : !this$rating.equals(other$rating)) {
        return false;
      }
      final Object this$globalRating = this.getGlobalRating();
      final Object other$globalRating = other.getGlobalRating();
      if (this$globalRating == null ? other$globalRating != null
          : !this$globalRating.equals(other$globalRating)) {
        return false;
      }
      if (this.getDownloads() != other.getDownloads()) return false;
      return this.getPdownloads() == other.getPdownloads();
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $rating = this.getRating();
      result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
      final Object $globalRating = this.getGlobalRating();
      result = result * PRIME + ($globalRating == null ? 43 : $globalRating.hashCode());
      result = result * PRIME + this.getDownloads();
      result = result * PRIME + this.getPdownloads();
      return result;
    }

    public String toString() {
      return "GetAppMeta.Stats(rating="
          + this.getRating()
          + ", globalRating="
          + this.getGlobalRating()
          + ", downloads="
          + this.getDownloads()
          + ", pdownloads="
          + this.getPdownloads()
          + ")";
    }
  }
}
