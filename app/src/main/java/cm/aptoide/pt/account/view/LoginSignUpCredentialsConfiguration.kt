package cm.aptoide.pt.account.view

data class LoginSignUpCredentialsConfiguration(val dismissToNavigateToMainView: Boolean,
                                               val cleanBackStack: Boolean,
                                               val hasMagicLinkError: Boolean,
                                               val magicLinkErrorMessage: String)