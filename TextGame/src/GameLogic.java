import java.lang.Math;
import java.util.*;

import javafx.scene.paint.Color;


public class GameLogic {
  Random rand = new Random();
  private GUI gui;
  private DatabaseManager db;
  private Player player;
  private boolean combat;
  private Monster currentMonster;
  private Equipment pendingEquipment;
  private String pendingEquipmentType;

  GameLogic(DatabaseManager db) {
    this.db = db;
    newGame();
    
         
    // while (player.getCurrentHP()>0 & monster.getCurrentHP() >0) {
    //   combatTurn(player, monster);
    // }
    
  }
  

  // event handling methods
  public void onExploreButton() {
    if (!combat) {
      Area area = player.getCurrentArea();
      explore(area);
    } else {
      combatTurn(player, currentMonster);
    }
  }

  public void onOptionButton(List<Object> option) {
    activateEventOption(option);
  }

  public void onAreaSelect(int index) {
    // take index and get areaID
    String areaID = player.getDiscoveredAreaIDs().get(index);
  
    // if area is different
    if (areaID != player.getCurrentAreaID()) {
      // change current area
      player.setCurrentArea(areaID);
      combat = false;
      gui.removeMonster();
      gui.removeEvent();
    }
  }

  public void onEquipBoolean(Boolean bool){
    if (bool) {
      equip(this.pendingEquipment);
    } else {
      this.player.addToBackpack(pendingEquipment);
    }
    gui.removeEvent();
    
  }

  public Player getPlayer() {
    return this.player;
  }

  private void newGame() {
    Player player = new Player(db, gui);
    this.player = player;
    
  }

  public void setGUI(GUI gui) {
    this.gui=gui;
    this.player.setGUI(gui);
  }
  private void explore(Area area) {
    // function to decide whether a new area is found, a monster appears or an event occurs
    // adding 1 so that it is between 1 and 100
    int result = rollThresholds(area.getExploreChance());
    switch(result) {
      case 0:
        chooseArea(area, player);
        break;

      case 1: 
        combat = true;
        this.currentMonster = chooseMonster(player.getCurrentArea());
        break;
        
      case 2:
        chooseEvent(player.getCurrentArea());
        break;
    
      default:
      // unreachable unless error occurs
      System.out.println("Error: exploreResultError");
    }
  }

  private void chooseArea(Area area, Player player) {
    // function to choose which subsequent area  to encounter
    List<Object> subsequentAreas = area.getSubsequentAreas();
    // check to see whether there are any areas left to discover, if not, explore again
    if (subsequentAreas.size() == 0) {
      explore(area);
      return;
    }

    // roll for which area to discover
    int areaNumber = rollThresholds(area.getAreaWeightThresholds());
  
    // extract area ID    
    List<Object> areaDetails = (ArrayList<Object>) (subsequentAreas.get(areaNumber));
    String areaID = (String) areaDetails.get(0);

    // remove area from list to prevent rediscovery
    subsequentAreas.remove(areaNumber);
    area.setSubsequentAreas(subsequentAreas);

    // check to see whether area had already been discovered from another location
    boolean newArea = player.discoverArea(areaID);
    
    // if the area was not new to the player, search for a different new area instead
    if (!newArea) {
      // find a new area if any exist or explore again
      chooseArea(area, player);
      return;
    }
    // extract area text and print
    String areaEncounterText = (String) areaDetails.get(2);

    gui.discoverArea(db.getAreaName(areaID), areaEncounterText);
  }

  private Monster chooseMonster(Area area) {
    // function to choose which monster to encounter
    // roll for which monster
    int monsterNumber = rollThresholds(area.getMonsterWeightThresholds());
  
    // extract monster ID and encounter text
    List<Object> monsterDetails = (ArrayList<Object>) (area.getMonsterList()).get(monsterNumber);
    String monsterID = (String) monsterDetails.get(0);
    String monsterEncounterText = (String) monsterDetails.get(2);
  
    // print encounter text
    gui.printSpace();
    gui.print(monsterEncounterText, Color.BLACK, "italic");
  
    Monster mon = new Monster(this.db, this.gui, monsterID);
    gui.newMonster(mon);
      
    // return monster
    return mon;
  }

  private void chooseEvent(Area area) {
    // function to choose which event to encounter
    // roll for which event
    int eventNumber = rollThresholds(area.getEventWeightThresholds());

    // extract event ID
    List<Object> eventInfo = (List<Object>) area.getEventList().get(eventNumber);
    String eventID = (String) eventInfo.get(0);
    int repeatable = (int) eventInfo.get(2);

    // check to see whether non repeatable event had already been discovered by the player
    boolean eventHappens = player.discoverEvent(eventID, repeatable);
    // if the event can not happen
    if (!eventHappens) {
      explore(area);
      return;
    }
    // extract and print event text
    String eventText = (String) eventInfo.get(3);
    gui.printSpace();
    gui.print(eventText, Color.BLACK, "");
    newEvent(eventID);
    // Event event = new Event(this.db, this.gui, eventID, eventText);
  }

  private void newEvent(String eventID) {
    // each option is {text, cost, reqItemID, heal, goldPerHeal, itemGet, itemLose, equip, eventText}
    List<Object> eventOptions = db.getEventOptions(eventID);
    int numOptions = eventOptions.size();

    gui.print("\n Your choices: ", Color.BLACK, "");
    
    for (int i = 0; i<= numOptions - 1; i++) {
      List<Object> option = (List<Object>) eventOptions.get(i);
      gui.print((String) option.get(8), Color.BLACK, "");
      this.gui.newEventOption(i, option);
    }

  }

  private void activateEventOption(List<Object> option) {
    // TODO: does equip need to be an option?
    // each option is {text, cost, reqItemID, heal, goldPerHeal, itemGet, itemLose, equip}
    int cost = (int) option.get(1);
    String reqItemID = (String) option.get(2);
    String itemLose = (String) option.get(6);

    // check option is possible
    // TODO: add code for required itemID and itemLose
    if (this.player.getGold() >= cost) {
      // if option is chosen and valid, remove options from GUI
      gui.removeEvent();
      // pay option cost
      this.player.gainGold(-cost);

      // code for GP based healing
      int maxHeal = (int) option.get(3);
      if (maxHeal > 0) {
        int currentGold = this.player.getGold();
        int goldPerHeal = (int) option.get(4);
        int missingHP = this.player.getMaxHP() - this.player.getCurrentHP();
        int maxCost = missingHP*goldPerHeal;

        // if player has enough money to pay for the maximum heal
        if (currentGold >= maxCost) {
          player.heal(maxHeal);
          player.gainGold(-maxCost);
        } else {
          // heal as much as can be afforded
          int heal = currentGold / goldPerHeal;
          player.heal(heal);
          player.gainGold(-heal*goldPerHeal);
        }
        // update HP
        gui.playerCurrentHPUpdate();
      }
      

      // manage item changes
      // TODO: create code for lose item
      // this.player.loseItem(itemLose);

      // if new item is obtained
      if (option.get(5) != null){
        findItem((String) option.get(5));
      }

      // if player is forced to equip something
      if ((String) option.get(7) != null) {
        // TODO: add forceEquip code
      }
      gui.playerGoldUpdate();
      
      
    } else {
      gui.print("You do not have enough money for that", Color.BLACK, "");
    }
  }

  private void findItem(String itemID) {
    boolean keyItem = db.keyItemCheck(itemID);
    if (keyItem) {

    } else {
      this.pendingEquipment = new Equipment(db, itemID);
      pendingEquipmentType = this.pendingEquipment.getType();
      String name = this.pendingEquipment.getName();
      equipCheck(name);
    }
  }

  private void equipCheck(String name) {
    // TODO: add check for item already in backpack / equipped and GUI print different message
    gui.discoverItem(this.pendingEquipment);
    gui.print("Would you like to equip " + name + "?", Color.BLACK, "");
        gui.addBooleanChoice();
  }

  private void equip(Equipment equipment) {
    // add equipment to player
    player.equip(this.pendingEquipment, this.pendingEquipmentType);

    // add equipment to player GUI
    gui.newEquip(this.pendingEquipment, this.pendingEquipmentType);
  }
  
  
  private int rollThresholds(int[] thresholds) {
    int size = thresholds.length;
    // identify maximum roll
    int maxRoll = thresholds[size-1];
    // add 1 to make it from 1 -> max rather than 0 -> max-1
    int randomNumber = rand.nextInt(maxRoll) + 1;
    for (int i=0; i < thresholds.length - 1; i++) {
      if (thresholds[i] < randomNumber && randomNumber <= thresholds[i+1]) {
        return i;
      }
    }
    // unreachable unless error occurs
    System.out.println("rollThresholds error");
    return 0;
  }


  private void combatTurn(Player player, Monster monster) {
    // stats = {maxHP, currentHP, damage, defence, critChance, critDamage, critSuccess}
    int[] playerStats = player.getCombatStats();
    int[] monsterStats = monster.getCombatStats();

    // check for critical hits and update damage values
    playerStats = critcheck(playerStats);
    monsterStats = critcheck(monsterStats);
    
    // calculate randomised damage to be dealt after blocking
    int playerDamage = calcDamage(playerStats, monsterStats[3]);
    int monsterDamage = calcDamage(monsterStats, playerStats[3]);

    // change player and monster hp
    player.setCurrentHP(playerStats[1] - monsterDamage);
    monster.setCurrentHP(monsterStats[1] - playerDamage);

    int monsterCurrentHP = monster.getCurrentHP();
    int playerCurrentHP = player.getCurrentHP();
    
    // output result to GUI
    gui.damageUpdate("You", monster.getName(), playerDamage, playerStats[6], monsterCurrentHP);
    gui.damageUpdate(monster.getName(), "You", monsterDamage, monsterStats[6], playerCurrentHP);
 
    // check for deaths
    if (playerCurrentHP == 0) {
      death();
    }
    if (monsterCurrentHP ==0) {
      combat = false;
      monster.printDeathText();
      gui.printSpace();
      player.gainGold(monster.gold);
      player.gainXP(monster.xp);
      gui.removeMonster();

    }
  }

  private int[] critcheck(int[] stats) {
    int roll = rand.nextInt(100);
    roll += 1;
    if (stats[4] >= roll) {
      // update critSuccess to 1 
      // TODO: change critSuccess to boolean (requires changing int[])
      stats[6] = 1;
      stats[2] = stats[2]*(1+stats[5]/100);
    }
    return stats;
  }

  private int calcDamage(int[] attackerStats, int defenderDefence) {
    // create a random number from 0.8 to 1.2
    // 100.0 used to convert from percentages to decimals (.0 used to force answer to be a double)
    double attackerRandomMultiplier = (80 + rand.nextInt(41))/100.0;
    
    // multiply damage by the multiplier
    double attackerDamage = attackerStats[2]*attackerRandomMultiplier;
    
    // remove damage due to defence
    double unblockedDamage = attackerDamage - defenderDefence;
    
    // round up and ensure that excess defence does not cause negative damage
    int finalDamage = Math.max((int)(Math.ceil(unblockedDamage)), 0);

    // if crit, damage is minimum of 1
    if (finalDamage == 0 && attackerStats[6] == 1) {
      finalDamage = 1;
    }
    
    return finalDamage;
  }

  private void death() {
    gui.printSpace();
    gui.print("You have died.", Color.BLACK, "bold");
    // save stats
    // option to replay
  }
}

