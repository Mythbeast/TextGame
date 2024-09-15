
import java.util.ArrayList;

public class CombatEntity {
  protected int level;
  protected int xp;
  protected int maxHP;
  protected int currentHP;
  protected int attack;
  protected int defence;
  protected int gold;
  protected int critChance;
  protected int critDamage;

CombatEntity() {

}

// getters and setters

public int getLevel() {
  return this.level;
}

public int getCurrentHP() {
  return this.currentHP;
}

public void setCurrentHP(int HP) {
  this.currentHP = clamp(HP, 0, this.maxHP);
}

public void heal(int heal) {
  this.currentHP = clamp(this.currentHP + heal, 0, this.maxHP);
}

public int getMaxHP() {
  return this.maxHP;
}

public int getAttack() {
  return this.attack;
}

public int getDefence() {
  return this.defence;
}

public int getGold() {
  return this.gold;
}

public int getCritChance() {
  return this.critChance;
}

public int getCritDamage() {
  return this.critDamage;
}

public int[] getCombatStats() {
  int[] stats = {maxHP, currentHP, attack, defence, critChance, critDamage, 0};
  return stats;
}

public int getXP() {
  return this.xp;
}


public static int clamp(int value, int min, int max) {
  return Math.max(min, Math.min(value, max));
}

}