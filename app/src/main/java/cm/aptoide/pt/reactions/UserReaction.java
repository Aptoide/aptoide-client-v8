package cm.aptoide.pt.reactions;

class UserReaction {

  private final String userId;
  private final String reaction;

  public UserReaction(String userId, String reaction) {

    this.userId = userId;
    this.reaction = reaction;
  }

  public String getUserId() {
    return userId;
  }

  public String getReaction() {
    return reaction;
  }
}
