
public class GUI {

  GUI() {

  }

  public void damageUpdate(String attacker, String defender, int damage, int critSuccess, int newHP) {
    
    if (critSuccess == 1) {
      System.out.println(attacker + " scored a critical hit!");
    }
    System.out.println(attacker + " dealt " + damage + " to the " + defender);
    System.out.println(defender + " now has " + newHP + " health remaining.");
    }
    
  }


