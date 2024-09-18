
import java.util.HashMap;
import java.util.List;

import javafx.scene.paint.Color;

public class Monster extends CombatEntity {
  private DatabaseManager db;
  private GUI gui;
  private String name;
  private String deathText;
  private HashMap<String, Integer> drops;

  public Monster(DatabaseManager db, GUI gui, String monsterID) {
    this.db = db;
    this.gui = gui;
    List<Object> monsterInfo = db.getMonsterInfo(monsterID);
    this.name = (String) monsterInfo.get(0);
    this.level = (Integer) monsterInfo.get(1);
    this.xp = (Double) monsterInfo.get(2);
    this.maxHp = (Integer) monsterInfo.get(3);
    this.currentHp = (Integer) monsterInfo.get(3);
    this.attack = (Integer) monsterInfo.get(4);
    this.defence = (Integer) monsterInfo.get(5);
    this.critChance = (Integer) monsterInfo.get(6);
    this.critDamage = (Integer) monsterInfo.get(7);
    this.gold = (Integer) monsterInfo.get(8);
    this.deathText = (String) monsterInfo.get(9);
    this.drops = db.getMonsterDrops(monsterID);

    // HashMap<String, Integer> drop = new HashMap<>();
    // drop.put("dagger", 35);
    // drops.add(drop);

  }

  public String getName() {
    return this.name;
  }

  public void printDeathText() {
    gui.print(this.deathText, Color.BLACK, "italic");
  }

  public HashMap<String, Integer> getDrops() {
    return this.drops;
  }

}