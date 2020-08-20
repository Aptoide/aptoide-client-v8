package cm.aptoide.pt.install.installer

data class InstallationCandidate(val installation: Installation, val forceDefaultInstall: Boolean,
                                 val shouldSetPackageInstaller: Boolean)