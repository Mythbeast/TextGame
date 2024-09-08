

// file exists to test game logic prior to UI elements

public class Main {
  static void main() {
    DatabaseManager db = new DatabaseManager();
    GUI gui = new GUI();
    GameLogic game1 = new GameLogic(db, gui);
    
    
      
  }

  public static void main(String[] args) {
    main();
    
}

}