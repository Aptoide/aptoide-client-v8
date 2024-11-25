package cm.aptoide.pt.downloadmanager;

public class DownloadError {

  private final Throwable errorException;
  private final String httpError;
  private final String urlError;

  public DownloadError(Throwable errorException, String httpError, String urlError) {
    this.errorException = errorException;
    this.httpError = httpError;
    this.urlError = urlError;
  }

  public Throwable getErrorException() {
    return errorException;
  }

  public String getHttpError() {
    return httpError;
  }

  public String getUrlError() {
    return urlError;
  }
}
