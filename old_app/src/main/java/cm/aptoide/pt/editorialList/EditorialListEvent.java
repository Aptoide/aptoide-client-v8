package cm.aptoide.pt.editorialList;

class EditorialListEvent {

  private final String cardId;
  private final int position;

  public EditorialListEvent(String cardId, int position) {

    this.cardId = cardId;
    this.position = position;
  }

  public String getCardId() {
    return cardId;
  }

  public int getPosition() {
    return position;
  }
}
