package cm.aptoide.pt.promotions;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.install.Install;

public class PromotionViewAppMapper {

  private final DownloadStateParser downloadStateParser;

  public PromotionViewAppMapper(DownloadStateParser downloadStateParser) {
    this.downloadStateParser = downloadStateParser;
  }

  public PromotionViewApp mapInstallToPromotionApp(Install install, PromotionApp promotionApp) {
    return new PromotionViewApp(
        getDownloadModel(install.getType(), install.getProgress(), install.getState(),
            install.isIndeterminate(), install.getAppSize()), promotionApp.getName(),
        promotionApp.getPackageName(), promotionApp.getAppId(), promotionApp.getDownloadPath(),
        promotionApp.getAlternativePath(), promotionApp.getAppIcon(), promotionApp.isClaimed(),
        promotionApp.getDescription(), promotionApp.getSize(), promotionApp.getRating(),
        promotionApp.getNumberOfDownloads(), promotionApp.getMd5(), promotionApp.getVersionCode(),
        promotionApp.getVersionName(), promotionApp.getObb(), promotionApp.getAppcValue(),
        promotionApp.getSignature(), promotionApp.hasAppc(), promotionApp.getSplits(),
        promotionApp.getRequiredSplits(), promotionApp.getRank(), promotionApp.getStoreName(),
        promotionApp.getFiatValue(), promotionApp.getFiatSymbol(), promotionApp.getBdsFlags());
  }

  private DownloadModel getDownloadModel(Install.InstallationType type, int progress,
      Install.InstallationStatus state, boolean isIndeterminate, long appSize) {
    return new DownloadModel(downloadStateParser.parseDownloadType(type, false), progress,
        downloadStateParser.parseDownloadState(state, isIndeterminate), appSize);
  }
}
