public class Main {
  
  public static void main(String[] args) {
    
    DatabaseManager db = new DatabaseManager();
    GameLogic game1 = new GameLogic(db);

    GUI.setGameLogic(game1);
    GUI.launch(GUI.class, args);
    
    
 
    
}

}