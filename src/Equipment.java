
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Equipment extends AttackStats {
  private DatabaseManager db;
  private String name;
  private String id;
  private String type;
  private int hp;
  private HashMap<String, Integer> combatStats;

  public Equipment(DatabaseManager db, String equipmentId) {
    this.db = db;
    this.id = equipmentId;
    // equipmentInfo = {name, type, hp, attack, defence, critChance, critDamage}
    List<Object> stats = db.getEquipmentInfo(equipmentId);
    this.name = (String) stats.get(0);
    this.type = (String) stats.get(1);
    this.hp = (int) stats.get(2);
    this.attack = (int) stats.get(3);
    this.defence = (int) stats.get(4);
    this.critChance = (int) stats.get(5);
    this.critDamage = (int) stats.get(6);

    this.combatStats = new HashMap<String, Integer>();

    if (this.hp > 0) {
      this.combatStats.put("hp: ", this.hp);
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

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public int getHP() {
    return this.hp;
  }

  public HashMap<String, Integer> getCombatStats() {
    return this.combatStats;
  }

  // override the .equals() method to be able to compare equipment with
  // .contains()
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    Equipment that = (Equipment) o;
    return Objects.equals(this.id, that.id) && Objects.equals(name, that.name);
  }

  // Override hashCode to maintain consistency with equals
  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }

  @Override
  public String toString() {
    return "Equipment{name='" + name + "', id=" + id + "}";
  }

}
