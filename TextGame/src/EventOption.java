public class EventOption {
  private String buttonText;
  private String choiceText;
  private String resultText;
  private int goldCost;
  private int heal;
  private int goldPerHeal;
  private String reqItemId;
  private String itemGet;
  private String itemLose;
  private String equip;
  private String fight;

  public EventOption(String buttonText, String choiceText, String resultText, int goldCost, int heal, int goldPerHeal,
      String reqItemId, String itemGet, String itemLose, String equip, String fight) {
    this.buttonText = buttonText;
    this.choiceText = choiceText;
    this.resultText = resultText;
    this.goldCost = goldCost;
    this.heal = heal;
    this.goldPerHeal = goldPerHeal;
    this.reqItemId = reqItemId;
    this.itemGet = itemGet;
    this.itemLose = itemLose;
    this.equip = equip;
    this.fight = fight;
  }

  // Getters
  public String getButtonText() {
    return buttonText;
  }

  public String getChoiceText() {
    return choiceText;
  }

  public String getResultText() {
    return resultText;
  }

  public int getGoldCost() {
    return goldCost;
  }

  public int getHeal() {
    return heal;
  }

  public int getGoldPerHeal() {
    return goldPerHeal;
  }

  public String getReqItemId() {
    return reqItemId;
  }

  public String getItemGet() {
    return itemGet;
  }

  public String getItemLose() {
    return itemLose;
  }

  public String getEquip() {
    return equip;
  }

  public String getFight() {
    return fight;
  }
}
