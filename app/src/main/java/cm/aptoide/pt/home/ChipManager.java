package cm.aptoide.pt.home;

public class ChipManager {

  private Chip currentChip;

  public Chip getCurrentChip() {
    return currentChip;
  }

  public void setCurrentChip(Chip currentChip) {
    this.currentChip = currentChip;
  }

  public enum Chip {
    APPS("apps"), GAMES("games");

    private String name;

    Chip(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
