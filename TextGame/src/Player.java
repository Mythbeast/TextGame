import java.util.HashMap;
import java.util.ArrayList;
import javafx.scene.paint.Color;

// TODO: clean up Area functions using class elements
// TODO: create private backpack/key item functions that handle both

public class Player extends CombatEntity {
  private int saveNumber = 1;
  private DatabaseManager db;
  private Gui gui;

  // ArrayLists used as size is mutable
  // variable used to hold discovered non repeatable events
  private ArrayList<String> discoveredEventIds;
  // variable used to hold areaIds for discover checks and quick indexing
  private ArrayList<String> discoveredAreaIds;
  // variable used to hold entire Areas to avoid recreating each time
  private ArrayList<Area> discoveredAreas = new ArrayList<Area>();
  // variable used purely for Gui purposes
  private ArrayList<String> discoveredAreaNames = new ArrayList<String>();

  // variables used to explore
  private String currentAreaId;
  private Area currentArea;

  // equipment is {Weapon, Shield, ...}
  private ArrayList<Equipment> currentEquipment = new ArrayList<Equipment>();
  // itemStats are HP, attack, defence, critChance and critDamage
  private ArrayList<Equipment> backpack = new ArrayList<Equipment>();
  private ArrayList<KeyItem> keyItems = new ArrayList<KeyItem>();
  private int shards = 0;

  private HashMap<String, Integer> itemStats;

  private double xpForNextLevel;

  public Player(DatabaseManager db, Gui gui, int saveNumber, int gold, double xp, ArrayList<String> areaIds,
      ArrayList<String> eventIds, ArrayList<String> currentEquipment, ArrayList<String> backpack,
      ArrayList<String> keyItems) {

    this.db = db;
    this.gui = gui;
    this.xp = xp;
    this.gold = gold;
    this.discoveredAreaIds = areaIds;
    this.discoveredEventIds = eventIds;
    this.itemStats = new HashMap<>();
    this.itemStats.put("HP: ", 0);
    this.itemStats.put("Attack: ", 0);
    this.itemStats.put("Defence: ", 0);
    this.itemStats.put("Crit Chance: ", 0);
    this.itemStats.put("Crit Damage: ", 0);

    initialLevelCheck();
    loadPlayerStats(this.level);
    this.currentHp = this.maxHp;

    for (String equipId : currentEquipment) {
      Equipment equipment = new Equipment(db, equipId);
      equip(equipment);
    }

    for (String equipId : backpack) {
      Equipment equipment = new Equipment(db, equipId);
      addToBackpack(equipment);
    }

    for (String key : keyItems) {
      addKeyItem(key);
    }

  }

  public void setSaveNumber(int number) {
    this.saveNumber = number;
  }

  public void saveGame() {
    db.saveGame(this.saveNumber, this.xp, this.gold, this.discoveredAreaIds, this.currentEquipment, this.backpack,
        this.keyItems, this.discoveredEventIds);
  }

  public void guiSetUp() {
    for (Equipment equipment : currentEquipment) {
      gui.newEquip(equipment, equipment.getType());
    }

    for (Equipment equipment : backpack) {
      gui.addToBackpack(equipment);
    }
    gui.ItemStatUpdate();

    for (KeyItem item : keyItems) {
      gui.addToKeyItems(item);
    }

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
    // levelCheck();

    // // test2:
    // addKeyItem("Genesis");

    // add initially discovered areaIds
    discoverArea("Gate", db.getArea("Gate", this.discoveredAreaIds));
    discoverArea("Farm", db.getArea("Farm", this.discoveredAreaIds));

    this.currentAreaId = this.discoveredAreaIds.get(0);
    this.currentArea = this.discoveredAreas.get(0);
    levelCheck();

    gui.setPlayerGui();
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

  public ArrayList<Area> getDiscoveredAreas() {
    return this.discoveredAreas;
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
    int[] stats = MathUtils.intArrayAdd(playerStats, itemCombatStats);
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

  public boolean checkIfOwned(String itemId) {
    // method to check whether an item is owned by the player
    boolean owned = false;
    // check if key item or equipment
    if (db.keyItemCheck(itemId)) {
      // check key items
      KeyItem check = new KeyItem(db, itemId);
      if (getKeyItems().contains(check))
        owned = true;
    } else {
      // check backpack
      Equipment check = new Equipment(db, itemId);
      if (getBackpack().contains(check)) {
        owned = true;
      }
    }
    return owned;
  }

  public ArrayList<KeyItem> getKeyItems() {
    return this.keyItems;
  }

  public ArrayList<Equipment> getEquipmentList() {
    return this.currentEquipment;
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
    if (this.currentEquipment.contains(equipment)) {
      currentEquipment.remove(equipment);
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

    // messy code due to the size of currentEquipment changing when an item is
    // removed
    // if 2Handed weapon - iterate through currentEquipment and check for weapons
    // and
    // shields
    if (type.equals("weapon2")) {
      HashMap<String, Integer> oldStats;
      // variables used to hold index to avoid removing during for loop, -1 used so
      // that all indices are greater
      int oldWeaponIndex = -1;
      int oldShieldIndex = -1;
      for (int i = 0; i < currentEquipment.size(); i++) {
        // .contains used to get weapon1 and weapon2
        if (currentEquipment.get(i).getType().contains("weapon")) {
          // if so, remove stats from itemstats and save index
          Equipment oldWeapon = currentEquipment.get(i);
          oldStats = oldWeapon.getCombatStats();
          this.itemStats = MathUtils.hashMapSubtract(this.itemStats, oldStats);
          oldWeaponIndex = i;
        }
        if (currentEquipment.get(i).getType().equals("shield")) {
          // if so, remove stats from itemstats and save index
          Equipment oldShield = currentEquipment.get(i);
          oldStats = oldShield.getCombatStats();
          this.itemStats = MathUtils.hashMapSubtract(this.itemStats, oldStats);
          oldShieldIndex = i;
        }
      }
      // remove highest index first to avoid second index changing
      int higherIndex = Math.max(oldWeaponIndex, oldShieldIndex);
      int lowerIndex = Math.min(oldWeaponIndex, oldShieldIndex);
      if (higherIndex >= 0) {
        unequip(this.currentEquipment.get(higherIndex), higherIndex);
      }
      if (lowerIndex >= 0) {
        unequip(this.currentEquipment.get(lowerIndex), lowerIndex);
      }
    } else {
      // for all non 2H weapon, find index of old equipment and remove
      for (int i = 0; i < currentEquipment.size(); i++) {
        if (currentEquipment.get(i).getType().equals(type)) {
          // if so, remove stats from itemStats and remove equipment
          Equipment oldEquipment = currentEquipment.get(i);
          HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
          this.itemStats = MathUtils.hashMapSubtract(this.itemStats, oldStats);
          unequip(oldEquipment, i);
          break;
        }
        // case where player has a 2H weapon and wants to swap it for a shield
        if (type.equals("shield")) {
          if (currentEquipment.get(i).getType().equals("weapon2")) {
            // if so, remove stats from itemStats and remove equipment
            Equipment oldEquipment = currentEquipment.get(i);
            HashMap<String, Integer> oldStats = oldEquipment.getCombatStats();
            this.itemStats = MathUtils.hashMapSubtract(this.itemStats, oldStats);
            unequip(oldEquipment, i);
          }
        }
      }
    }
    this.currentEquipment.add(equipment);
    this.itemStats = MathUtils.hashMapAdd(this.itemStats, equipment.getCombatStats());
    gui.newEquip(equipment, equipment.getType());
    gui.removeFromBackpack(equipment);

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

  private void equipCheck(String itemId) {
    Equipment equipment = new Equipment(db, itemId);
    // check to see if equipment already obtained
    if (this.backpack.contains(equipment) || this.currentEquipment.contains(equipment)) {
      gui.print("You already have this item!", Color.BLACK, null);
    } else {
      // if not owned, add to backpack and check if player would like to equip
      addToBackpack(equipment);
      gui.equipCheck(equipment);
    }

  }

  private void unequip(Equipment equipment, int index) {
    this.currentEquipment.remove(index);
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

  private void initialLevelCheck() {
    while (this.xp >= this.xpForNextLevel) {
      this.level += 1;
      loadPlayerStats(this.level);
      this.currentHp = this.maxHp;
    }
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

  public boolean discoverArea(String newAreaId, Area newArea) {
    // if area has already been discovered, return false
    if (discoveredAreaIds.contains(newAreaId)) {
      return false;
    }
    // else return true and create the area
    else {
      this.discoveredAreaIds.add(newAreaId);
      this.discoveredAreas.add(newArea);
      this.discoveredAreaNames.add(newArea.getName());
      // if (gui != null) {
      // gui.updateArea(newArea);
      // }
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
    if (repeatable == 0 && discoveredEventIds.contains(eventId)) {
      return false;
    }
    // if event is new, add to discoveredEventIds
    if (!discoveredEventIds.contains(eventId)) {

      discoveredEventIds.add(eventId);
    }
    // if event can happen, activate the event
    return true;
  }

  private void victory() {
    String victoryText = "As you obtain the last essence, the five elemental essences float gracefully into the air, they begin to swirl and merge, their individual energies intertwining in a mesmerizing dance of light and color. Fire’s fierce radiance, air’s delicate wisps, earth’s steadfast strength, water’s fluid clarity, and ice’s shimmering cold blend seamlessly together. The swirling amalgamation crystallizes into the Genesis Crystal, a breathtaking gem that pulses with the combined power of all elements, embodying the profound essence of creation.";
    gui.print(victoryText, Color.BLACK, "bold");
    gui.print("You have won the game!", Color.BLACK, "bold");
  }

}
