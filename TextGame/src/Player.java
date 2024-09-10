import java.util.*;


// TODO: clean up Area functions using class elements
// TODO: make itemList and use db?


public class Player extends CombatEntity{
  private DatabaseManager db;
  private GUI gui;
    
  // ArrayLists used as size is mutable
  // variable used to hold areaIDs for discover checks and quick indexing
  private ArrayList<String> discoveredAreaIDs;
  // variable used to hold entire Areas to avoid recreating each time
  private ArrayList<Area> discoveredAreas;
  // variable used purely for GUI purposes
  private ArrayList<String> discoveredAreaNames;

  
  // variables used to explore
  private String currentAreaID;
  private Area currentArea;
  // itemStats could contain the following:
  // hpBonus, attackBonus, defenceBonus, critChanceBonus, critDamageBonus 
  // goldMultiplierBonus???, dropRateIncrease???, 
  private int[] itemStats;

  private int xpForNextLevel;
  
  

  Player(DatabaseManager db, GUI gui) {
    this.db = db;
    this.gui = gui;
    this.level = 1;
    this.xp = 0;
    this.gold = 500;
    loadStats(this.level);
    this.currentHP = this.maxHP;
    System.out.println(xpForNextLevel);


    this.discoveredAreaNames = new ArrayList<String>();
    this.discoveredAreaIDs = new ArrayList<String>();
    this.discoveredAreas = new ArrayList<Area>();
    // add initially discovered areaIDs
    discoverArea("Gate");
    discoverArea("Farm");

    this.currentAreaID = this.discoveredAreaIDs.get(0);
    this.currentArea = this.discoveredAreas.get(0);

    
  }

  public void setGUI(GUI gui) {
    this.gui=gui;
  }

  public Area getCurrentArea() {
    return currentArea;
  }

  public String getCurrentAreaID() {
    return this.currentAreaID;
  }

  public ArrayList<String> getDiscoveredAreaIDs() {
    return this.discoveredAreaIDs;
  }

  public ArrayList<String> getDiscoveredAreaNames() {
    return this.discoveredAreaNames;
  }

  public void setCurrentArea(String areaID) {
    this.currentAreaID = areaID;
    int discoveryIndex = discoveredAreaIDs.indexOf(areaID);
    this.currentArea = discoveredAreas.get(discoveryIndex);
  }

  public void gainGold(int amount) {
    this.gold += amount;
  }

  public void gainXP(int amount) {
    this.xp += amount;
    if (this.xp >= this.xpForNextLevel) {
      levelUp();
    }
  }

  private void loadStats(int level) {
    // stats are {xpNeeded, maxHP, attack, defence, critChance, critDamage}
    ArrayList<Integer> stats = db.getPlayerStats(this.level);
    this.xpForNextLevel = stats.get(0);
    this.maxHP = stats.get(1);
    this.attack = stats.get(2);
    this.defence = stats.get(3);
    this.critChance = stats.get(4);
    this.critDamage = stats.get(5);
  }

  private void levelUp() {
    this.level += 1;
    loadStats(this.level);
    this.currentHP = this.maxHP;
    gui.levelUp(this.level);
  }

  public boolean discoverArea(String areaID) {
    // if area has already been discovered, return false
    if (discoveredAreaIDs.contains(areaID)) {
      return false;
    }
    // else return true and create the area
    else {
      Area area = new Area(this.db, areaID);
      this.discoveredAreaIDs.add(areaID);
      this.discoveredAreas.add(area);
      this.discoveredAreaNames.add(area.getName());
      return true;
    }
    

  }

  
  

}
