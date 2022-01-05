package cm.aptoide.pt.downloads;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.download.AppValidationAnalytics;
import cm.aptoide.pt.download.AppValidator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class AppValidatorTest {

  @Mock AppValidationAnalytics appValidationAnalytics;
  private AppValidator appValidator;
  private Obb validObb;
  private Obb invalidObbMain;
  private Obb invalidObbPatch;
  private List<Split> splits;
  private List<String> requiredSplits;

  @Before public void setupAppValidator() {
    MockitoAnnotations.initMocks(this);
    appValidator = new AppValidator(appValidationAnalytics);
    validObb = configureValidObb();
    invalidObbMain = configureInvalidObbMain();
    invalidObbPatch = configureInvalidObbPatch();
    splits = new ArrayList<>();
    requiredSplits = new ArrayList<>();
  }

  @Test public void validateValidAppWithObb() {
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "filepath",
            "filepathAlt", splits, requiredSplits), AppValidator.AppValidationResult.VALID_APP);
  }

  @Test public void validateValidAppWithNoObb() {
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "filepath",
            "filepathAlt", splits, requiredSplits), AppValidator.AppValidationResult.VALID_APP);
  }

  @Test public void validateInvalidMd5App() {
    assertEquals(
        appValidator.validateApp(null, null, "cm.aptoide.pt", "aptoide", "filepath", "filepathAlt",
            splits, requiredSplits), AppValidator.AppValidationResult.INVALID_MD5);
  }

  @Test public void validateAppWithNoPackageName() {
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "", "aptoide", "filepath", "filepathAlt",
            splits, requiredSplits), AppValidator.AppValidationResult.NO_PACKAGE_NAME_SPECIFIED);
  }

  @Test public void validateAppWithNoAppName() {
    assertEquals(appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "", "filepath",
        "filepathAlt", splits, requiredSplits),
        AppValidator.AppValidationResult.NO_APP_NAME_SPECIFIED);
  }

  @Test public void validateInvalidMainObbApp() {
    assertEquals(
        appValidator.validateApp("validmd5", invalidObbMain, "cm.aptoide.pt", "aptoide", "filepath",
            "filepathAlt", splits, requiredSplits),
        AppValidator.AppValidationResult.NO_MAIN_OBB_DOWNLOAD_LINK);
  }

  @Test public void validateInvalidPatchObbApp() {
    assertEquals(appValidator.validateApp("validmd5", invalidObbPatch, "cm.aptoide.pt", "aptoide",
        "filepath", "filepathAlt", splits, requiredSplits),
        AppValidator.AppValidationResult.NO_PATCH_OBB_DOWNLOAD_LINK);
  }

  @Test public void validateAppWithoutMainPath() {
    assertEquals(appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "",
        "filepathAlt", splits, requiredSplits),
        AppValidator.AppValidationResult.NO_MAIN_DOWNLOAD_LINK);
  }

  @Test public void validateAppWithoutAlternativePath() {
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "filepath", null,
            splits, requiredSplits), AppValidator.AppValidationResult.NO_ALTERNATIVE_DOWNLOAD_LINK);
  }

  @Test public void validateAppWith_requiredSplits() {
    splits.add(loadValidABISplit());
    requiredSplits.add("ABI");
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "filepath",
            "filepathAlt", splits, requiredSplits), AppValidator.AppValidationResult.VALID_APP);
  }

  @Test public void validateApp_noRequiredSplits() {
    splits.add(loadValidABISplit());
    requiredSplits.add("DENSITY");
    assertEquals(
        appValidator.validateApp("validmd5", validObb, "cm.aptoide.pt", "aptoide", "filepath",
            "filepathAlt", splits, requiredSplits),
        AppValidator.AppValidationResult.REQUIRED_SPLITS_NOT_FOUND);
  }

  private Split loadValidABISplit() {
    return new Split("config.arm64_v8a", "ABI", "path/", 4234324, "md5");
  }

  private Obb.ObbItem configureValidObbItem() {
    Obb.ObbItem obbItem = new Obb.ObbItem();
    obbItem.setPath("obb random path");
    obbItem.setFilename("filename");
    obbItem.setMd5sum("md5");
    obbItem.setFilesize(1231313);
    return obbItem;
  }

  private Obb.ObbItem configureInvalidObbItem() {
    Obb.ObbItem obbItem = new Obb.ObbItem();
    obbItem.setPath("");
    obbItem.setFilename("filename");
    obbItem.setMd5sum("md5");
    obbItem.setFilesize(1231313);
    return obbItem;
  }

  private Obb configureValidObb() {
    Obb obb = new Obb();
    obb.setMain(configureValidObbItem());
    obb.setPatch(configureValidObbItem());
    return obb;
  }

  private Obb configureInvalidObbMain() {
    Obb obb = new Obb();
    obb.setMain(configureInvalidObbItem());
    obb.setPatch(configureValidObbItem());
    return obb;
  }

  private Obb configureInvalidObbPatch() {
    Obb obb = new Obb();
    obb.setMain(configureValidObbItem());
    obb.setPatch(configureInvalidObbItem());
    return obb;
  }
}
