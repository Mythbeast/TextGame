import java.util.HashMap;
import java.util.ArrayList;
import javafx.scene.paint.Color;

// TODO: clean up Area functions using class elements
// TODO: create private backpack/key item functions that handle both

public class Player extends CombatEntity {
  private DatabaseManager db;
  private Gui gui;

  // ArrayLists used as size is mutable
  // variable used to hold discovered non repeatable events
  private ArrayList<String> discoveredEventIds;
  // variable used to hold areaIds for discover checks and quick indexing
  private ArrayList<String> discoveredAreaIds;
  // variable used to hold entire Areas to avoid recreating each time
  private ArrayList<Area> discoveredAreas;
  // variable used purely for Gui purposes
  private ArrayList<String> discoveredAreaNames;

  // variables used to explore
  private String currentAreaId;
  private Area currentArea;

  // equipment is {Weapon, Shield, ...}
  private ArrayList<Equipment> equipmentList;
  // itemStats are HP, attack, defence, critChance and critDamage
  private ArrayList<Equipment> backpack;
  private ArrayList<KeyItem> keyItems;
  private int shards = 0;

  private HashMap<String, Integer> itemStats;

  private double xpForNextLevel;

  public Player(DatabaseManager db, Gui gui) {
    this.db = db;
    this.gui = gui;
    this.level = 1;
    this.xp = 0;
    this.gold = 500;
    loadPlayerStats(this.level);
    this.currentHp = this.maxHp;
    this.keyItems = new ArrayList<>();
    this.equipmentList = new ArrayList();
    this.backpack = new ArrayList<>();
    this.itemStats = new HashMap<>();
    this.itemStats.put("HP: ", 0);
    this.itemStats.put("Attack: ", 0);
    this.itemStats.put("Defence: ", 0);
    this.itemStats.put("Crit Chance: ", 0);
    this.itemStats.put("Crit Damage: ", 0);

    this.discoveredEventIds = new ArrayList<String>();

    this.discoveredAreaNames = new ArrayList<String>();
    this.discoveredAreaIds = new ArrayList<String>();
    this.discoveredAreas = new ArrayList<Area>();
    // add initially discovered areaIds
    discoverArea("Gate");
    discoverArea("Farm");

    // // test1:
    // discoverArea("VolH");
    // discoverArea("CryC");
    // discoverArea("IceC");
    // discoverArea("Trog");
    // discoverArea("Prim");
    // discoverArea("Isle");
    // discoverArea("Vict");
    // discoverArea("Clou");
    // discoverArea("Drag");
    // this.xp = 50000000000000000000000000.0;

    // // test2:
    // addKeyItem("Genesis");

    this.currentAreaId = this.discoveredAreaIds.get(0);
    this.currentArea = this.discoveredAreas.get(0);
    levelCheck();
  }

  // event handler
  public void onEquipBoolean(Boolean bool, Equipment equipment) {
    if (bool) {
      equip(equipment);
    }
    gui.removeEvent();
  }

  // getters and setters
  public void setGui(Gui gui) {
    this.gui = gui;
  }

  public Area getCurrentArea() {
    return currentArea;
  }

  public String getCurrentAreaId() {
    return this.currentAreaId;
  }

  public ArrayList<String> getDiscoveredAreaIds() {
    return this.discoveredAreaIds;
  }

  public ArrayList<String> getDiscoveredAreaNames() {
    return this.discoveredAreaNames;
  }

  public void setCurrentArea(String areaId) {
    this.currentAreaId = areaId;
    int discoveryIndex = discoveredAreaIds.indexOf(areaId);
    this.currentArea = discoveredAreas.get(discoveryIndex);
  }

  public int[] getCombatStats() {
    int[] playerStats = { maxHp, currentHp, attack, defence, critChance, critDamage, 0 };
    int[] itemCombatStats = {
        0,
        0,
        this.itemStats.getOrDefault("Attack: ", 0),
        this.itemStats.getOrDefault("Defence: ", 0),
        this.itemStats.getOrDefault("Crit Chance: ", 0),
        this.itemStats.getOrDefault("Crit Damage: ", 0) };
    int[] stats = intArrayAdd(playerStats, itemCombatStats);
    return stats;
  }

  public HashMap<String, Integer> getItemStats() {
    return this.itemStats;
  }

  public void gainGold(int amount) {
    this.gold += amount;
  }

  public double getXpForNextLevel() {
    return this.xpForNextLevel;
  }

  public void gainXp(double amount) {
    this.xp += amount;
    levelCheck();
  }

  private void addKeyItem(String keyItemId) {
    KeyItem keyItem = new KeyItem(this.db, keyItemId);
    gui.print("You obtained the " + keyItem.getName() + "!", Color.BLACK, "bold");
    if (keyItems.contains(keyItem)) {
      gui.print("You already have this key item", Color.RED, null);
      return;
    }
    if (keyItemId.contains("Shard")) {
      this.shards += 1;
    }
    this.keyItems.add(keyItem);
    gui.addToKeyItems(keyItem);
    if (this.shards == 5) {
      KeyItem genesis = new KeyItem(db, "Genesis");
      this.keyItems.add(genesis);
      victory();
    }
  }

  private void removeKeyItem(String keyItemId) {
    KeyItem keyItem = new KeyItem(this.db, keyItemId);
    if (keyItems.contains(keyItem)) {
      // don't remove key item from player inventory so that events won't retrigger,
      // but remove from UI
      // keyItems.remove(keyItem);
      gui.removeFromKeyItems(keyItem);
    }
  }

  public ArrayList<KeyItem> getKeyItems() {
    return this.keyItems;
  }

  public ArrayList<Equipment> getEquipmentList() {
    return this.equipmentList;
  }

  public ArrayList<Equipment> getBackpack() {
    return this.backpack;
  }

  public void addItem(String itemId) {
    // check to see if item is a key item or equipment
    if (db.keyItemCheck(itemId)) {
      addKeyItem(itemId);
    } else {
      // check whether to equip immediately
      equipCheck(itemId);
    }
  }

  private void equipCheck(String itemId) {
    Equipment equipment = new Equipment(db, itemId);
    // check to see if equipment already obtained
    if (this.backpack.contains(equipment) || this.equipmentList.contains(equipment)) {
      gui.print("You already have this item!", Color.BLACK, null);
    } else {
      // if not owned, add to backpack and check if player would like to equip
      addToBackpack(equipment);
      gui.equipCheck(equipment);
    }

  }

  public void removeItem(String itemId) {
    // check to see if item is a key item or equipment
    if (db.keyItemCheck(itemId)) {
      removeKeyItem(itemId);
    } else {
      removeEquipment(itemId);
    }
  }

  public void addToBackpack(Equipment equipment) {
    // add item to backpack and UI
    this.backpack.add(equipment);
    gui.addToBackpack(equipment);
  }

  private void removeEquipment(String itemId) {
    Equipment equipment = new Equipment(db, itemId);
    if (this.backpack.contains(equipment)) {
      backpack.remove(equipment);
    }
    if (this.equipmentList.contains(equipment)) {
      equipmentList.remove(equipment);
    }
    gui.removeFromBackpack(equipment);
  }

  public void equip(Equipment equipment) {
    // TODO: condense terrible duplicated code
    String type = equipment.getType();

    // if item was in backpack, remove it
    if (this.backpack.contains(equipment)) {
      backpack.remove(equipment);
    }

    // messy code due to the size of equipmentList changing when an item is removed
    // if 2Handed weapon - iterate through equipmentList and check for weapons and
    // shields
    if (type.equals("weapon2")) {
      HashMap<String, Integer> oldStats;
      // variables used to hold index to avoid removing during for loop, -1 used so
      // that all indices are greater
      int oldWeaponIndex = -1;
      int oldShieldIndex = -1;
      for (int i = 0; i < equipmentList.size(); i++) {
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
        unequip(this.equipmentList.get(higherIndex), higherIndex);
      }
      if (lowerIndex >= 0) {
        unequip(this.equipmentList.get(lowerIndex), lowerIndex);
      }
    } else {
      // for all non 2H weapon, find index of old equipment and remove
      for (int i = 0; i < equipmentList.size(); i++) {
        if (equipmentList.get(i).getType().equals(type)) {
          // if so, remove stats from itemStats and remove equipment
          Equipment oldEquipment = equipmentList.get(i);
          HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
          this.itemStats = hashMapSubtract(this.itemStats, oldStats);
          unequip(oldEquipment, i);
          break;
        }
        // case where player has a 2H weapon and wants to swap it for a shield
        if (type.equals("shield")) {
          if (equipmentList.get(i).getType().equals("weapon2")) {
            // if so, remove stats from itemStats and remove equipment
            Equipment oldEquipment = equipmentList.get(i);
            HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
            this.itemStats = hashMapSubtract(this.itemStats, oldStats);
            unequip(oldEquipment, i);
          }
        }
      }
    }
    this.equipmentList.add(equipment);
    this.itemStats = hashMapAdd(this.itemStats, equipment.getCombatStats());
  }

  private void unequip(Equipment equipment, int index) {
    this.equipmentList.remove(index);
    addToBackpack(equipment);
  }

  private void loadPlayerStats(int level) {
    // stats are {xpNeeded, maxHp, attack, defence, critChance, critDamage}
    ArrayList<Object> stats = db.getPlayerStats(this.level);
    this.xpForNextLevel = (double) stats.get(0);
    this.maxHp = (int) stats.get(1);
    this.attack = (int) stats.get(2);
    this.defence = (int) stats.get(3);
    this.critChance = (int) stats.get(4);
    this.critDamage = (int) stats.get(5);
  }

  private void levelCheck() {
    while (this.xp >= this.xpForNextLevel) {
      levelUp();
    }
  }

  private void levelUp() {
    this.level += 1;
    loadPlayerStats(this.level);
    this.currentHp = this.maxHp;
    if (this.gui != null) {
      gui.levelUp(this.level, this.maxHp);
    }
  }

  public boolean discoverArea(String areaId) {
    // if area has already been discovered, return false
    if (discoveredAreaIds.contains(areaId)) {
      return false;
    }
    // else return true and create the area
    else {
      Area area = new Area(this.db, areaId);
      this.discoveredAreaIds.add(areaId);
      this.discoveredAreas.add(area);
      this.discoveredAreaNames.add(area.getName());
      return true;
    }
  }

  public boolean discoverEvent(String eventId, int repeatable) {
    // access prerequisite item and item that triggers the event to stop repeating
    ArrayList<String> items = db.getEventTriggerItems(eventId);
    String reqItem = items.get(0);
    String stopRepeat = items.get(1);
    // if you own the item that stops the event from occuring, return false and do
    // not activate the event
    if (stopRepeat != null) {
      if (db.keyItemCheck(stopRepeat)) {
        KeyItem check = new KeyItem(db, stopRepeat);
        if (this.keyItems.contains(check)) {
          return false;
        }

      } else {
        Equipment check = new Equipment(db, stopRepeat);
        if (this.backpack.contains(check)) {
          return false;
        }
      }
    }

    // if (stopRepeat != null) {
    // if (this.keyItems.contains(reqItem) || this.backpack.contains(reqItem)) {
    // return false;
    // }
    // }

    // if the event requires an item and the player does not have it, return false
    // and do not activate the event
    if (reqItem != null) {
      if (db.keyItemCheck(reqItem)) {
        KeyItem check = new KeyItem(db, reqItem);
        if (!this.keyItems.contains(check)) {
          return false;
        }

      } else {
        Equipment check = new Equipment(db, reqItem);
        if (!this.backpack.contains(check)) {
          return false;
        }
      }
    }
    // if event is not repeatable and has already been discovered, return false and
    // do not activate the event
    if (discoveredEventIds.contains(eventId)) {
      return false;
    }
    // if event is not repeatable and new, discover it and activate the event
    if (repeatable == 0) {
      discoveredEventIds.add(eventId);
      return true;
    }
    // if event is repeatable
    if (repeatable == 1) {
      return true;
    }
    return false;
  }

  private void victory() {
    String victoryText = "As you obtain the last essence, the five elemental essences float gracefully into the air, they begin to swirl and merge, their individual energies intertwining in a mesmerizing dance of light and color. Fire’s fierce radiance, air’s delicate wisps, earth’s steadfast strength, water’s fluid clarity, and ice’s shimmering cold blend seamlessly together. The swirling amalgamation crystallizes into the Genesis Crystal, a breathtaking gem that pulses with the combined power of all elements, embodying the profound essence of creation.";
    gui.print(victoryText, Color.BLACK, "bold");
    gui.print("You have won the game!", Color.BLACK, "bold");
  }

  private HashMap<String, Integer> hashMapSubtract(HashMap<String, Integer> hashMap1,
      HashMap<String, Integer> hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.getOrDefault(stat, 0);
      hashMap1.replace(stat, value1, value1 - value2);
    }
    return hashMap1;
  }

  private HashMap<String, Integer> hashMapAdd(HashMap<String, Integer> hashMap1, HashMap<String, Integer> hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.getOrDefault(stat, 0);
      hashMap1.replace(stat, value1, value1 + value2);
    }
    return hashMap1;
  }

  private int[] intArrayAdd(int[] array1, int[] array2) {
    int[] result = new int[array1.length];
    for (int i = 0; i < array1.length - 1; i++) {
      result[i] = array1[i] + array2[i];
    }
    return result;
  }

}
