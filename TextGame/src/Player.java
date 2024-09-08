import java.util.*;

// TODO: clean up Area functions using class elements
// TODO: make itemList and use db?


public class Player extends CombatEntity{
  private DatabaseManager db;
  
  // ArrayLists used as size is mutable
  // variable used to hold areaIDs for discover checks and quick indexing
  private ArrayList<String> discoveredAreaIDs;
  // variable used to hold entire Areas to avoid recreating each time
  private ArrayList<Area> discoveredAreas;
  // variable used purely for GUI purposes
  private ArrayList<String> discoveredAreaNames;

  // private List<Object> currentAreaInfo;
  
  // variables used to explore
  private String currentAreaID;
  private Area currentArea;
  // itemStats could contain the following:
  // hpBonus, attackBonus, defenceBonus, critChanceBonus, critDamageBonus 
  // goldMultiplierBonus???, dropRateIncrease???, 
  private int[] itemStats;
  
  

  Player(DatabaseManager db) {
    this.db = db;
    
    this.level = 1;
    this.xp = 0;
    this.maxHP = 10;
    this.currentHP = 10;
    this.attack = 3;
    this.defence = 1;
    this.gold = 500;
    this.critChance = 20;
    this.critDamage = 50;
    this.discoveredAreaNames = new ArrayList<String>();
    this.discoveredAreaIDs = new ArrayList<String>();
    this.discoveredAreas = new ArrayList<Area>();
    // add initially discovered areaIDs
    discoverArea("Gate");
    discoverArea("Farm");

    this.currentAreaID = this.discoveredAreaIDs.get(0);
    this.currentArea = this.discoveredAreas.get(0);
    // this.currentAreaInfo = db.getAreaInfo(this.currentAreaID);
    
  }

  public Area getCurrentArea() {
    return currentArea;
  }

  // public List<Object> getCurrentAreaInfo() {
  //   return this.currentAreaInfo;
  // }

  public void setCurrentArea(String areaID) {
    this.currentAreaID = areaID;
    int discoveryIndex = discoveredAreaIDs.indexOf(areaID);
    this.currentArea = discoveredAreas.get(discoveryIndex);
  }

  public void gainGold(int amount) {
    this.gold += amount;
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
