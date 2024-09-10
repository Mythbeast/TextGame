import java.lang.Math;
import java.util.*;


public class GameLogic {
  Random rand = new Random();
  private GUI gui;
  private DatabaseManager db;
  private Player player;
  private boolean combat;
  private Monster currentMonster;

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

  public void onAreaSelect(int index) {
    // take index and get areaID
    String areaID = player.getDiscoveredAreaIDs().get(index);
    System.out.println(areaID);

    // if area is different
    if (areaID != player.getCurrentAreaID()) {
      // change current area
      player.setCurrentArea(areaID);
      combat = false;
    }
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
        System.out.println("event");
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
    gui.discoverArea(db.getAreaName(areaID));
    System.out.println(areaEncounterText);
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
    System.out.println(monsterEncounterText);
  
    Monster mon = new Monster(this.db, monsterID);
      
    // return monster
    return mon;
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
    gui.damageUpdate("You", monster.getName(), playerDamage, playerStats[4], monsterCurrentHP);
    gui.damageUpdate(monster.getName(), "You", monsterDamage, monsterStats[4], playerCurrentHP);

    // check for deaths
    if (playerCurrentHP == 0) {
      death();
    }
    if (monsterCurrentHP ==0) {
      combat = false;
      player.gainGold(monster.gold);
      player.gainXP(monster.xp);
      monster.printDeathText();

    }
  }

  private int[] critcheck(int[] stats) {
    int roll = rand.nextInt(100);
    roll += 1;
    if (stats[4] > roll) {
      System.out.println("Critical hit!");
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
    System.out.println("You have died.");
    // save stats
    // option to replay
  }
}

