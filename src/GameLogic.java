import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;

import javafx.scene.chart.PieChart.Data;
import javafx.scene.paint.Color;

public class GameLogic {
  Random rand = new Random();
  private Gui gui;
  private DatabaseManager db;
  private int saveNumber;
  private Player player;
  private RunStatList runStats;
  private boolean combat;
  private Monster currentMonster;
  private boolean newDiscoveryMonster = false;
  private boolean event = false;
  private int eventWarning = 0;

  public GameLogic(DatabaseManager db, Gui gui, int saveNumber) {
    this.saveNumber = saveNumber;
    this.db = db;
    this.gui = gui;
    Gui.setGameLogic(this);
    makeGame();

    // while (player.getCurrentHP()>0 & monster.getCurrentHP() >0) {
    // combatTurn(player, monster);
    // }

  }

  // event handling methods
  public void onExploreButton() {
    if (event && eventWarning < 2) {
      gui.print("There is an event active, are you sure?", Color.BLACK, null);
      eventWarning += 1;
    } else {
      if (!combat) {
        runStats.addStatTimesExplored();
      }
      gui.removeEvent();
      eventWarning = 0;
      player.saveGame();
      if (!combat) {
        Area area = player.getCurrentArea();
        explore(area);
      } else {
        combatTurn(player, currentMonster);
      }
    }
  }

  public void onOptionButton(EventOption option) {
    activateEventOption(option);
  }

  public void onAreaSelect(int index) {
    // take index and get areaID
    String areaID = player.getDiscoveredAreaIds().get(index);

    // if area is different
    if (areaID != player.getCurrentAreaId()) {
      // change current area
      player.setCurrentArea(areaID);
      // reset event and combat in new area
      event = false;
      eventWarning = 0;
      combat = false;
      gui.removeMonster();
      gui.removeEvent();
    }
  }

  public void restartGame() {
    this.saveNumber = 0;
    combat = false;
    event = false;
    eventWarning = 0;
    makeGame();
  }

  public Player getPlayer() {
    return this.player;
  }

  public DatabaseManager getDatabaseManager() {
    return this.db;
  }

  private void makeGame() {
    this.player = db.loadSave(saveNumber, gui);
    // ensure that the new save does not overwrite any old save game if new game
    if (this.saveNumber == 0) {
      this.saveNumber = db.howManySaves() + 1;
      player.setSaveNumber(this.saveNumber);

    }
    this.runStats = player.getRunStats();
    gui.setPlayer(player);
    player.setEquipment();
    player.guiSetUp();
    gui.createScrollableRunStats();
    gui.addAllToWindow();
  }

  public void setEvent(boolean bool) {
    this.event = bool;
    if (!bool) {
      eventWarning = 0;
    }
  }

  public void saveRecord(String text, int value) {
    db.saveRecord(text, value);
  }

  private void explore(Area area) {
    // function to decide whether a new area is found, a monster appears or an event
    // occurs
    // adding 1 so that it is between 1 and 100
    int result = rollThresholds(area.getExploreChance());
    switch (result) {
      case 0:
        chooseArea(area, player);
        break;

      case 1:
        chooseMonster(player.getCurrentArea());
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
    // function to choose which subsequent area to encounter
    List<Object> subsequentAreas = area.getSubsequentAreas();
    // check to see whether there are any areas left to discover, if not, explore
    // again
    if (subsequentAreas.size() == 0) {
      explore(area);
      return;
    }

    // roll for which area to discover
    int areaNumber = rollThresholds(area.getAreaWeightThresholds());

    // extract area ID
    List<Object> areaDetails = (ArrayList<Object>) (subsequentAreas.get(areaNumber));
    String newAreaId = (String) areaDetails.get(0);
    Area newArea = db.getArea(newAreaId, player.getDiscoveredAreaIds());

    // remove area from list to prevent rediscovery
    subsequentAreas.remove(areaNumber);
    area.setSubsequentAreas(subsequentAreas);

    // check to see whether area had already been discovered from another location
    boolean newAreaCheck = player.discoverArea(newAreaId, newArea);

    // if the area was not new to the player, search for a different new area
    // instead
    if (!newAreaCheck) {
      // find a new area if any exist or explore again
      chooseArea(area, player);
      return;
    }
    // extract area text and print
    String areaEncounterText = (String) areaDetails.get(2);
    // list of all areas that connect to new area
    ArrayList<String> allConnections = db.getPreviousAreaList(newAreaId);
    for (String areaName : allConnections) {
      gui.updateArea(areaName);
    }

    gui.discoverArea(newArea, areaEncounterText);

  }

  private void chooseMonster(Area area) {
    // function to choose which monster to encounter
    // roll for which monster
    int monsterNumber = rollThresholds(area.getMonsterWeightThresholds());

    // extract monster ID and encounter text
    List<Object> monsterDetails = (ArrayList<Object>) (area.getMonsterList()).get(monsterNumber);
    String monsterId = (String) monsterDetails.get(0);
    String monsterEncounterText = (String) monsterDetails.get(2);
    newMonster(monsterId, monsterEncounterText);
    // if the monster is new to the player - update Gui for each location
    if (player.discoverMonster(monsterId)) {
      this.newDiscoveryMonster = true;
    }
  }

  private void newMonster(String monsterId, String encounterText) {
    gui.removeMonster();
    combat = true;
    this.newDiscoveryMonster = false;

    // print encounter text
    gui.printSpace();
    gui.print(encounterText, Color.BLACK, "italic");

    Monster mon = new Monster(this.db, this.gui, monsterId);
    gui.newMonster(mon);
    runStats.addStatMonstersFound();

    // return monster
    this.currentMonster = mon;
  }

  private void chooseEvent(Area area) {
    // function to choose which event to encounter
    // roll for which event
    int eventNumber = rollThresholds(area.getEventWeightThresholds());

    // extract event ID
    List<Object> eventInfo = (List<Object>) area.getEventList().get(eventNumber);
    String eventID = (String) eventInfo.get(0);
    int repeatable = (int) eventInfo.get(2);

    // check to see whether non repeatable event had already been discovered by the
    // player
    boolean eventHappens = player.discoverEvent(eventID, repeatable);
    // if the event can not happen
    if (!eventHappens) {
      explore(area);
      return;
    }
    // extract and print event text
    String eventText = (String) eventInfo.get(3);
    gui.printSpace();
    gui.print(eventText, Color.BLACK, null);
    newEvent(eventID);
    // Event event = new Event(this.db, this.gui, eventID, eventText);
  }

  private void newEvent(String eventID) {
    // equip, eventText}
    List<EventOption> eventOptions = db.getEventOptions(eventID);
    int numOptions = eventOptions.size();

    gui.print("\n Your choices:", Color.BLACK, "bold");

    for (int i = 0; i <= numOptions - 1; i++) {
      // select each option of list
      EventOption option = eventOptions.get(i);
      // check whether option requires an item and whether the player has it
      String reqItem = option.getReqItemId();
      if (reqItem != null) {
        boolean owned = player.checkIfOwned(reqItem);
        if (owned) {
          gui.print(option.getChoiceText(), Color.BLACK, "bold");
          this.gui.newEventOption(i, option);
        }

        // option has no required item and therefore is an option
      } else {
        gui.print((String) option.getChoiceText(), Color.BLACK, "bold");
        this.gui.newEventOption(i, option);
      }
    }
    // add event flag so that explore button has a warning before discarding the
    // event
    this.event = true;
  }

  private void activateEventOption(EventOption option) {

    gui.printSpace();
    // extract option variables
    int cost = option.getGoldCost();
    int maxHeal = option.getHeal();
    int goldPerHeal = option.getGoldPerHeal();
    String itemGet = option.getItemGet();
    String itemLose = option.getItemLose();
    String equip = option.getEquip();
    String fight = option.getFight();
    String resultText = option.getResultText();

    // check option is possible and if not, output to player that he doesn't have
    // enough money (at end of method)
    if (this.player.getGold() >= cost) {
      // if option is chosen and valid, remove options from Gui
      gui.removeEvent();
      // pay option cost
      this.player.gainGold(-cost);

      // code for GP based healing
      if (maxHeal > 0) {
        int currentGold = this.player.getGold();
        int missingHp = this.player.getMaxHp() - this.player.getCurrentHp();
        int maxCost = missingHp * goldPerHeal;

        // if player has enough money to pay for the maximum heal
        if (currentGold >= maxCost) {
          player.heal(maxHeal);
          player.gainGold(-maxCost);
        } else {
          // heal as much as can be afforded
          int heal = currentGold / goldPerHeal;
          player.heal(heal);
          player.gainGold(-heal * goldPerHeal);
        }
        // update player Hp on GUI
        gui.playerCurrentHpUpdate();
      }

      // if there is a resultText
      if (resultText != null) {
<<<<<<< HEAD
        gui.print(resultText, Color.BLACK, resultText);
=======
        gui.print(resultText, Color.BLACK, null);
>>>>>>> db0f18f063f749eb5d1efb6ef8b84df5414d43e7
      }

      // manage item changes
      // if new item is obtained
      if (itemGet != null) {
        findItem(itemGet);
      }

      if (itemLose != null) {
        player.removeItem(itemLose);
      }

      // if player is forced to equip something
      if (equip != null) {
        // TODO: add forceEquip code
      }

      // if player must fight
      if (fight != null) {
        newMonster(fight, null);
      }

      // update Gui gold
      gui.playerGoldUpdate();
      // clear event flags as event is over
      this.event = false;
      eventWarning = 0;
      // if not enough money, print to textbox and do not clear event
    } else {
      gui.print("You do not have enough money for that", Color.BLACK, null);
    }
  }

  private void findItem(String itemId) {
    player.addItem(itemId);

  }

  private int rollThresholds(int[] thresholds) {
    int size = thresholds.length;
    // identify maximum roll
    int maxRoll = thresholds[size - 1];
    // add 1 to make it from 1 -> max rather than 0 -> max-1
    int randomNumber = rand.nextInt(maxRoll) + 1;
    for (int i = 0; i < thresholds.length - 1; i++) {
      if (thresholds[i] < randomNumber && randomNumber <= thresholds[i + 1]) {
        return i;
      }
    }
    // unreachable unless error occurs
    System.out.println("rollThresholds error");
    return 0;
  }

  private void combatTurn(Player player, Monster monster) {
    // stats = {maxHp, currentHp, damage, defence, critChance, critDamage,
    // critSuccess}
    int[] playerStats = player.getCombatStats();
    int[] monsterStats = monster.getCombatStats();

    // check for critical hits and update damage values
    playerStats = critcheck(playerStats);
    monsterStats = critcheck(monsterStats);

    // calculate randomised damage to be dealt after blocking
    int playerDamage = calcDamage(playerStats, monsterStats[3]);
    int monsterDamage = calcDamage(monsterStats, playerStats[3]);

    // change player and monster hp
    player.setCurrentHp(playerStats[1] - monsterDamage);
    monster.setCurrentHp(monsterStats[1] - playerDamage);

    int monsterCurrentHp = monster.getCurrentHp();
    int playerCurrentHp = player.getCurrentHp();

    // output result to Gui
    gui.damageUpdate("You", monster.getName(), playerDamage, playerStats[6]);
    gui.damageUpdate(monster.getName(), "You", monsterDamage, monsterStats[6]);

    // check for deaths
    if (playerCurrentHp == 0) {
      death();
    }
    if (monsterCurrentHp == 0) {
      combat = false;
      monster.printDeathText();
      player.gainGold(monster.gold);
      player.gainXp(monster.xp);
      // check for monster drops by creating a random number from 1-100 and seeing if
      // the drop rate is above it
      HashMap<String, Integer> drops = monster.getDrops();
      if (drops.size() > 0) {
        for (Map.Entry<String, Integer> entry : drops.entrySet()) {
          int dropCheck = rand.nextInt(100) + 1;
          if (entry.getValue() >= dropCheck) {
            findItem(entry.getKey());
          }

        }
      }
      runStats.addStatMonstersKilled();
      if (newDiscoveryMonster) {
        ArrayList<String> allLocations = db.getMonsterLocationList(monster.getId());
        this.runStats.addStatDifferentMonsters();
        for (String areaName : allLocations) {
          gui.updateAreaMonsters(areaName);

        }
      }
      gui.printSpace();
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
      stats[2] = stats[2] * (1 + stats[5] / 100);
    }
    return stats;
  }

  private int calcDamage(int[] attackerStats, int defenderDefence) {
    // create a random number from 0.8 to 1.2
    // 100.0 used to convert from percentages to decimals (.0 used to force answer
    // to be a double)
    double attackerRandomMultiplier = (80 + rand.nextInt(41)) / 100.0;

    // multiply damage by the multiplier
    double attackerDamage = attackerStats[2] * attackerRandomMultiplier;

    // remove damage due to defence
    double unblockedDamage = attackerDamage - defenderDefence;

    // round up and ensure that excess defence does not cause negative damage
    int finalDamage = Math.max((int) (Math.ceil(unblockedDamage)), 0);

    // if crit, damage is minimum of 1
    if (finalDamage == 0 && attackerStats[6] == 1) {
      finalDamage = 1;
    }

    return finalDamage;
  }

  private void death() {
    db.deleteSave(this.saveNumber);
    gui.death(db.getRecords());

  }
}
