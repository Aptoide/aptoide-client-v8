package cm.aptoide.accountmanager;

public class Store {
  private final long downloadCount;
  private final String avatar;
  private final long id;
  private final String name;
  private final String theme;
  private final String username;
  private final String password;

  public Store(long downloadCount, String avatar, long id, String name, String theme,
      String username, String password) {
    this.downloadCount = downloadCount;
    this.avatar = avatar;
    this.id = id;
    this.name = name;
    this.theme = theme;
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public long getDownloadCount() {
    return downloadCount;
  }

  public String getAvatar() {
    return avatar;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTheme() {
    return theme;
  }
}
