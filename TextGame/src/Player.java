import java.util.*;
import javafx.scene.paint.Color;


// TODO: clean up Area functions using class elements
// TODO: make itemList and use db?


public class Player extends CombatEntity{
  private DatabaseManager db;
  private GUI gui;
    
  // ArrayLists used as size is mutable
  // variable used to hold discovered non repeatable events
  private ArrayList<String> discoveredEventIDs;
  // variable used to hold areaIDs for discover checks and quick indexing
  private ArrayList<String> discoveredAreaIDs;
  // variable used to hold entire Areas to avoid recreating each time
  private ArrayList<Area> discoveredAreas;
  // variable used purely for GUI purposes
  private ArrayList<String> discoveredAreaNames;

    // variables used to explore
  private String currentAreaID;
  private Area currentArea;

  // equipment is {Weapon, Shield, ...}
  private ArrayList<Equipment> equipmentList;
    // itemStats are HP, attack, defence, critChance and critDamage 
  
  private HashMap<String, Integer> itemStats;

  private int xpForNextLevel;
  
  

  Player(DatabaseManager db, GUI gui) {
    this.db = db;
    this.gui = gui;
    this.level = 1;
    this.xp = 0;
    this.gold = 500;
    loadPlayerStats(this.level);
    this.currentHP = this.maxHP;
    this.equipmentList = new ArrayList();
    this.itemStats = new HashMap<>();

    this.discoveredEventIDs = new ArrayList<String>();


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

  public int[] getCombatStats() {
    int[] playerStats = {maxHP, currentHP, attack, defence, critChance, critDamage, 0};
    int[] itemCombatStats = {
        0, 
        0, 
        this.itemStats.getOrDefault("Attack: ", 0), 
        this.itemStats.getOrDefault("Defence: ", 0), 
        this.itemStats.getOrDefault("Crit Chance: ", 0), 
        this.itemStats.getOrDefault("Crit Damage: ", 0)};
    int[] stats = intArrayAdd(playerStats, itemCombatStats);
    return stats;
  }

  public HashMap<String, Integer> getItemStats() {
    return this.itemStats;
  }

  public void gainGold(int amount) {
    this.gold += amount;
  }

  public int getXPForNextLevel() {
    return this.xpForNextLevel;
  }

  public void gainXP(int amount) {
    this.xp += amount;
    if (this.xp >= this.xpForNextLevel) {
      levelUp();
    }
  }

  public void equip(Equipment equipment, String type) {
    // TODO: condense terrible duplicated code
    // messy code due to the size of equipmentList changing when an item is removed
    // if 2Handed weapon - iterate through equipmentList and check for weapons and shields
    if (type.equals("weapon2")) {
      HashMap<String, Integer> oldStats;
      // variables used to hold index to avoid removing during for loop, -1 used so that all indices are greater
      int oldWeaponIndex = -1;
      int oldShieldIndex = -1;
      for (int i=0; i < equipmentList.size(); i++) {
        // .contains used to get weapon1 and weapon2
        if (equipmentList.get(i).getType().contains("weapon")) {
          // if so, remove stats from itemstats and save index
          Equipment oldWeapon = equipmentList.get(i);
          oldStats = oldWeapon.getCombatStats();
          this.itemStats = hashMapSubtract(this.itemStats, oldStats);
          oldWeaponIndex = i;
        }
        if (equipmentList.get(i).getType().equals("shield")) {
          // if so, remove stats from itemstats and save index
          Equipment oldShield = equipmentList.get(i);
          oldStats = oldShield.getCombatStats();
          this.itemStats = hashMapSubtract(this.itemStats, oldStats);
          oldShieldIndex = i;
        }
      }
      // remove highest index first to avoid second index changing
      int higherIndex = Math.max(oldWeaponIndex, oldShieldIndex);
      int lowerIndex = Math.min(oldWeaponIndex, oldShieldIndex);
      if (higherIndex >= 0) {
        equipmentList.remove(higherIndex);
      }
      if (lowerIndex >= 0) {
        equipmentList.remove(lowerIndex);
      }         
    } else{
      // for all non 2H weapon, find index of old equipment and remove
      for (int i=0; i < equipmentList.size(); i++) {
        if (equipmentList.get(i).getType().equals(type)) {
          // if so, remove stats from itemStats and remove equipment
          Equipment oldEquipment = equipmentList.get(i);
          HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
          this.itemStats = hashMapSubtract(this.itemStats, oldStats);
          equipmentList.remove(i);
          break;        
        }
        // case where player has a 2H weapon and wants to swap it for a shield
        if (type.equals("shield")) {
          if (equipmentList.get(i).getType().equals("weapon2")) {
            // if so, remove stats from itemStats and remove equipment
            Equipment oldEquipment = equipmentList.get(i);
            HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
            this.itemStats = hashMapSubtract(this.itemStats, oldStats);
            equipmentList.remove(i);
          }
        }
      }
    }
    this.equipmentList.add(equipment);
    this.itemStats = hashMapAdd(equipment.getCombatStats(), this.itemStats);    
  }

  



  private void loadPlayerStats(int level) {
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
    loadPlayerStats(this.level);
    this.currentHP = this.maxHP;
    gui.levelUp(this.level, this.maxHP);
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

  public boolean discoverEvent(String eventID, int repeatable) {
    // if event is not repeatable and has already been discovered, return false
    if (discoveredEventIDs.contains(eventID)) {
      return false;
    }
    
    else {
      if (repeatable == 0) {
        discoveredEventIDs.add(eventID);
      }
      return true;

    }
  }

  private HashMap<String, Integer> hashMapSubtract(HashMap<String, Integer> hashMap1, HashMap<String, Integer>hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.get(stat);
      hashMap1.replace(stat, value1, value1-value2);
    }
    return hashMap1;
  }

  private HashMap<String, Integer> hashMapAdd(HashMap<String, Integer> hashMap1, HashMap<String, Integer>hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.get(stat);
      hashMap1.replace(stat, value1, value1+value2);
    }
    return hashMap1;
  }

  private int[] intArrayAdd(int[] array1, int[] array2) {
    int[] result = new int[array1.length];
    for (int i = 0; i< array1.length - 1; i++) {
      result[i] = array1[i] + array2[i];
    }
    return result;
  }
  
  

}

  
  

