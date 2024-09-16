
import java.util.ArrayList;

public class CombatEntity extends AttackStats{
  protected int level;
  protected int xp;
  protected int maxHP;
  protected int currentHP;
  protected int gold;
  

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

public int getGold() {
  return this.gold;
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