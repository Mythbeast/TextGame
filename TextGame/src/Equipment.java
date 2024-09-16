import java.util.*;

public class Equipment extends AttackStats{
  private DatabaseManager db;
  private String name;
  private String type;
  private int HP;
  private  HashMap<String, Integer> combatStats;

  Equipment(DatabaseManager db, String equipmentID) {
    this.db = db;
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
  
}
