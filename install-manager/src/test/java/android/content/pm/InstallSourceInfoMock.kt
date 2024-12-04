package android.content.pm

fun createInstallSource(vc: Long): InstallSourceInfo {
  // This constructor is package private
  val sourceInfo = InstallSourceInfo()
  // Some troubles with compiled in class
  //return new InstallSourceInfo(
  //    vc % 2 == 1L ? "my.package" : "other.package",
  //    new SigningInfo(),
  //    "origin.package",
  //    vc % 2 == 1L ? "my.package" : "other.package",
  //    vc % 2 == 1L ? "my.package" : "other.package",
  //    PACKAGE_SOURCE_UNSPECIFIED
  //);
  return sourceInfo
}
