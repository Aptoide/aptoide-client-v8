package cm.aptoide.pt.profile.data.model

data class UserProfile(
  val username: String,
  val userImage: String,
  val joinedData: String,
  val userStore: String = "",
)
