
public class CombatEntity extends AttackStats {
  protected int level;
  protected double xp;
  protected int maxHp;
  protected int currentHp;
  protected int gold;

  public CombatEntity() {

  }

  // getters and setters

  public int getLevel() {
    return this.level;
  }

  public int getCurrentHp() {
    return this.currentHp;
  }

  public void setCurrentHp(int hp) {
    this.currentHp = MathUtils.clamp(hp, 0, this.maxHp);
  }

  public void heal(int heal) {
    this.currentHp = MathUtils.clamp(this.currentHp + heal, 0, this.maxHp);
  }

  public int getMaxHp() {
    return this.maxHp;
  }

  public int getGold() {
    return this.gold;
  }

  public int[] getCombatStats() {
    int[] stats = { maxHp, currentHp, attack, defence, critChance, critDamage, 0 };
    return stats;
  }

  public double getXp() {
    return this.xp;
  }

}