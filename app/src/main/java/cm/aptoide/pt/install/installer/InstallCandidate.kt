package cm.aptoide.pt.install.installer

data class InstallCandidate(val md5: String, val forceDefaultInstall: Boolean,
                            val shouldSetPackageInstaller: Boolean)