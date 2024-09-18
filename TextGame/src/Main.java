public class Main {

  public static void main(String[] args) {

    DatabaseManager db = new DatabaseManager();
    GameLogic game1 = new GameLogic(db);

    GUI.setGameLogic(game1);
    GUI.launch(GUI.class, args);

    // TODO: create classes for db returns to avoid casting
    // TODO: change main class to application
    // TODO: mathutil class for things like clamp
    // TODO: Write code that simplifies keyItems and Equipment and allows functions
    // to work for both

  }

}