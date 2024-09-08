import java.lang.Math;
import java.util.*;


public class GameLogic {
  Random rand = new Random();
  private GUI gui;
  private DatabaseManager db;
  private Player player;
  private boolean combat;
  private Monster currentMonster;

  GameLogic(DatabaseManager db, GUI gui) {
    newGame();
         
    // while (player.getCurrentHP()>0 & monster.getCurrentHP() >0) {
    //   combatTurn(player, monster);
    // }
    
  }
  
  private void explore() {
    if (!combat) {
      Area area = player.getCurrentArea();
      explore();
    }
    

  }

  private void newGame() {
    Player player = new Player(db);
    this.player = player;
    this.gui = gui;
  }

  private void explore(Area area) {
    // function to decide whether a new area is found, a monster appears or an event occurs
    // adding 1 so that it is between 1 and 100
    int result = rollThresholds(area.getExploreChance());
    switch(result) {
      case 0:
        System.out.println("area");
      case 1: 
        combat = true;
        this.currentMonster = chooseMonster(player.getCurrentArea());
        
      case 2:
        System.out.println("event");
    }
    // unreachable unless error occurs
    System.out.println("Error: exploreResultError");
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
  
    Monster mon = new Monster(db, monsterID);
  
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
    
    // output result to GUI
    gui.damageUpdate("You", monster.getName(), playerDamage, playerStats[4], monster.getCurrentHP());
    gui.damageUpdate(monster.getName(), "You", monsterDamage, monsterStats[4], player.getCurrentHP());
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
    double playerDamage = attackerStats[2]*attackerRandomMultiplier;
    
    // remove damage due to defence
    double unblockedPlayerDamage = playerDamage - defenderDefence;
    
    // round up and ensure that excess defence does not cause negative damage
    int finalDamage = Math.max((int)(Math.ceil(unblockedPlayerDamage)), 0);
    
    return finalDamage;
  }
}

