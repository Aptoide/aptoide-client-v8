package cm.aptoide.pt.comments.refactor.data

data class User(val id: Long, val avatar: String, val name: String) {

  constructor(avatar: String, name: String) : this(-1, avatar, name)
}