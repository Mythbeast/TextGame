import java.util.*;
import javafx.scene.paint.Color;

public class Event {
private DatabaseManager db;
private GUI gui;
private String eventText;
private String ID;
private List<Object> options;

Event(DatabaseManager db, GUI gui, String eventID, String text) {
  this.db = db;
  this.gui = gui;
  this.ID = eventID;
  this.eventText = text;

  // each option is {text, cost, reqItemID, heal, goldPerHeal, itemGet, itemLose}
  this.options = db.getEventOptions(this.ID);
  gui.printSpace();
  gui.print(eventText, Color.BLACK, "");



}
}
