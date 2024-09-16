import java.util.*;

public class Equipment extends AttackStats{
  private DatabaseManager db;
  private String name;
  private String ID;
  private String type;
  private int HP;
  private  HashMap<String, Integer> combatStats;

  Equipment(DatabaseManager db, String equipmentID) {
    this.db = db;
    this.ID = equipmentID;
    // equipmentInfo = {name, type, HP, attack, defence, critChance, critDamage}
    List<Object> stats = db.getEquipmentInfo(equipmentID);
    this.name = (String) stats.get(0);
    this.type = (String) stats.get(1);
    this.HP = (int) stats.get(2);
    this.attack = (int) stats.get(3);
    this.defence = (int) stats.get(4);
    this.critChance = (int) stats.get(5);
    this.critDamage = (int) stats.get(6);

    this.combatStats = new HashMap<String, Integer>();

    if (this.HP > 0) {
      this.combatStats.put("HP: ", this.HP);
    }

    if (this.attack > 0) {
      this.combatStats.put("Attack: ", this.attack);
    }
    if (this.defence > 0) {
    this.combatStats.put("Defence: ", this.defence);
    }
    if (this.critChance > 0) {
    this.combatStats.put("Crit Chance: ", this.critChance);
    }
    if (this.critDamage > 0) {
    this.combatStats.put("Crit Damage: ", this.critDamage);
    }
  }

  public String getID() {
    return this.ID;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public int getHP(){
  return this.HP;
  }

  public HashMap<String, Integer> getCombatStats() {
    return this.combatStats;
  }

  // override the .equals() method to be able to compare equipment with .contains()
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
        Equipment that = (Equipment) o;
        return Objects.equals(this.ID, that.ID) && Objects.equals(name, that.name);
  }

  // Override hashCode to maintain consistency with equals
  @Override
  public int hashCode() {
    return Objects.hash(name, ID);
  }

  @Override
    public String toString() {
        return "Equipment{name='" + name + "', ID=" + ID + "}";
    }
  
}
