
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Add player database for saved games.

public class DatabaseManager {
  String location = "jdbc:sqlite:C:\\Temp\\TextGame\\game.db";
  private Connection conn;

  public DatabaseManager() {
    this.conn = connect();
  }

  public ArrayList<Object> getPlayerStats(int level) {
    String sql = "SELECT xpNeeded, maxHP, attack, defence, critChance, critDamage FROM PlayerLevelStats WHERE Level = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in level to SQL query
      pstmt.setInt(1, level);
      try (ResultSet rs = pstmt.executeQuery()) {
        ArrayList<Object> result = new ArrayList<Object>();

        if (rs.next()) {
          result.add(rs.getDouble("xpNeeded"));
          result.add(rs.getInt("maxHP"));
          result.add(rs.getInt("attack"));
          result.add(rs.getInt("defence"));
          result.add(rs.getInt("critChance"));
          result.add(rs.getInt("critDamage"));
        }

        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getPlayerStats error");
      ArrayList<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public Area getArea(String areaId, ArrayList<String> discoveredAreaIds) {
    // return List of column information of area
    String sql = "SELECT areaName, areaChance, monsterChance, eventChance FROM Area WHERE areaId = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in areaID to SQL query
      pstmt.setString(1, areaId);

      try (ResultSet rs = pstmt.executeQuery()) {

        String name = rs.getString("areaName");
        int areaChance = rs.getInt("areaChance");
        int monsterChance = rs.getInt("monsterChance");
        int eventChance = rs.getInt("eventChance");

        return (new Area(this, areaId, name, areaChance, monsterChance, eventChance, discoveredAreaIds));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getAreaInfo error");
      Area errorArea = new Area(null, "", "", 0, 0, 0, new ArrayList<>());
      return errorArea;
    }
  }

  public List<Object> getAreaList(String areaID) {
    String sql = "SELECT connectedAreaID, areaWeight, discoverText FROM areaConnections where areaID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in monsterID to SQL query
      pstmt.setString(1, areaID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<Object>();

        while (rs.next()) {
          List<Object> area = new ArrayList<>();
          area.add(rs.getString("connectedAreaID"));
          area.add(rs.getInt("areaWeight"));
          area.add(rs.getString("discoverText"));
          result.add(area);
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getAreaList error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public ArrayList<String> getPreviousAreaList(String nextAreaId) {
    // returns list of all area names that connect to nextAreaId
    String sql = "SELECT areaID FROM areaConnections where connectedAreaId = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in monsterID to SQL query
      pstmt.setString(1, nextAreaId);
      try (ResultSet rs = pstmt.executeQuery()) {
        ArrayList<String> result = new ArrayList<String>();

        while (rs.next()) {
          String areaId = rs.getString("areaId");
          result.add(getAreaName(areaId));
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getAreaList error");
      ArrayList<String> errorList = new ArrayList<String>();
      return errorList;
    }
  }

  public String getAreaName(String areaID) {
    String sql = "SELECT areaName FROM Area WHERE areaID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, areaID);
      try (ResultSet rs = pstmt.executeQuery()) {
        String name = "";

        while (rs.next()) {
          name = rs.getString("areaName");
        }
        return name;
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getAreaName error");
      return "";
    }
  }

  public List<Object> getMonsterInfo(String monsterID) {
    String sql = "SELECT monsterName, level, xp, maxHP, attack, defence, critChance, critDamage, gold, deathText FROM MonsterStats WHERE monsterID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in monsterID to SQL query
      pstmt.setString(1, monsterID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<Object>();

        if (rs.next()) {
          result.add(rs.getString("monsterName"));
          result.add(rs.getInt("level"));
          result.add(rs.getDouble("xp"));
          result.add(rs.getInt("maxHP"));
          result.add(rs.getInt("attack"));
          result.add(rs.getInt("defence"));
          result.add(rs.getInt("critChance"));
          result.add(rs.getInt("critDamage"));
          result.add(rs.getInt("gold"));
          result.add(rs.getString("deathText"));
        }

        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getMonsterInfo error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public List<Object> getMonsterList(String areaID) {
    String sql = "SELECT monsterID, monsterWeight, discoverText FROM MonsterLocations where areaID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in monsterID to SQL query
      pstmt.setString(1, areaID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<Object>();

        while (rs.next()) {
          List<Object> monster = new ArrayList<>();
          monster.add(rs.getString("monsterID"));
          monster.add(rs.getInt("monsterWeight"));
          monster.add(rs.getString("discoverText"));
          result.add(monster);
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getMonsterList error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public HashMap<String, Integer> getMonsterDrops(String monsterId) {
    String sql = "SELECT itemID, dropRate FROM MonsterDrops where monsterId = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in monsterID to SQL query
      pstmt.setString(1, monsterId);
      try (ResultSet rs = pstmt.executeQuery()) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        while (rs.next()) {
          result.put(rs.getString("itemID"), rs.getInt("dropRate"));
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getMonsterDrops error");
      HashMap<String, Integer> errorList = new HashMap<String, Integer>();
      return errorList;
    }
  }

  public List<Object> getEventList(String areaID) {
    String sql = "SELECT eventID, eventWeight, repeatable, eventText FROM Event where areaID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in areaID to SQL query
      pstmt.setString(1, areaID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<Object>();

        while (rs.next()) {
          List<Object> event = new ArrayList<>();
          event.add(rs.getString("eventID"));
          event.add(rs.getInt("eventWeight"));
          event.add(rs.getInt("repeatable"));
          event.add(rs.getString("eventText"));
          result.add(event);
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEventList error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public List<EventOption> getEventOptions(String eventID) {
    String sql = "SELECT optionID FROM EventOptionIndex where eventID = ?";
    List<Object> optionIDs = new ArrayList<Object>();

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in eventID to SQL query to obtain list of optionIDs
      pstmt.setString(1, eventID);
      try (ResultSet rs = pstmt.executeQuery()) {
        // add optionID to list
        while (rs.next()) {
          optionIDs.add(rs.getString("optionID"));
        }
      }
      // create list of option details and cycle through optionIDs adding details
      List<EventOption> eventOptions = new ArrayList<>();
      for (int i = 0; i <= optionIDs.size() - 1; i++) {
        eventOptions.add(getOptionInfo((String) optionIDs.get(i)));
      }
      return eventOptions;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEventOptions error");
      List<EventOption> errorList = new ArrayList<EventOption>();
      return errorList;
    }
  }

  public EventOption getOptionInfo(String optionID) {

    String sql = "SELECT buttonText, choiceText, resultText, goldCost, heal, goldPerHeal, reqItemId, itemGet, itemLose, equip, fight FROM EventOptions where optionID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in optionID to SQL query
      pstmt.setString(1, optionID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<>();
        // while (rs.next()) {
        String buttonText = rs.getString("buttonText");
        String choiceText = rs.getString("choiceText");
        String resultText = rs.getString("resultText");
        int goldCost = rs.getInt("goldCost");
        int heal = rs.getInt("heal");
        int goldPerHeal = rs.getInt("goldPerHeal");
        String reqItemId = rs.getString("reqItemId");
        String itemGet = rs.getString("itemGet");
        String itemLose = rs.getString("itemLose");
        String equip = rs.getString("equip");
        String fight = rs.getString("fight");
        // }
        EventOption option = new EventOption(buttonText, choiceText, resultText, goldCost, heal, goldPerHeal, reqItemId,
            itemGet, itemLose, equip, fight);
        return option;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getOptionInfo error");
      EventOption errorList = new EventOption("", "", "", 0, 0, 0, "", "", "", "", "");
      return errorList;
    }
  }

  public List<Object> getEquipmentInfo(String equipmentID) {

    String sql = "SELECT name, type, HP, attack, defence, critChance, critDamage FROM Equipment where equipmentID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in equipmentID to SQL query
      pstmt.setString(1, equipmentID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<>();
        while (rs.next()) {
          result.add(rs.getString("name"));
          result.add(rs.getString("type"));
          result.add(rs.getInt("HP"));
          result.add(rs.getInt("attack"));
          result.add(rs.getInt("defence"));
          result.add(rs.getInt("critChance"));
          result.add(rs.getInt("critDamage"));
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEquipmentInfo error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public boolean keyItemCheck(String itemID) {

    String sql = "SELECT keyIndicator FROM ItemIndex where itemID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in itemID to SQL query
      pstmt.setString(1, itemID);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          int result = rs.getInt(1);
          switch (result) {
            case 1:
              return true;
            default:
              return false;
          }
        } else {
          System.out.println("Error: keyItemCheck error with itemID " + itemID);
          return false;
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEquipmentInfo error");
      return false;
    }
  }

  public String getKeyItemName(String keyItemId) {

    String sql = "SELECT keyItemName FROM KeyItems where keyItemID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in itemID to SQL query
      pstmt.setString(1, keyItemId);
      String result = "";
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          result = rs.getString("keyItemName");
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEquipmentInfo error");
      return "";
    }
  }

  public ArrayList<String> getEventTriggerItems(String eventId) {

    String sql = "SELECT reqItem, stopRepeat FROM Event where eventID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in eventId to SQL query
      pstmt.setString(1, eventId);
      ArrayList<String> result = new ArrayList<>();
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          result.add(rs.getString("reqItem"));
          result.add(rs.getString("stopRepeat"));
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEquipmentInfo error");
      ArrayList<String> errorList = new ArrayList<String>();
      return errorList;
    }
  }

  public void saveGame(int saveNumber, int level, double xp, int gold, ArrayList<String> discoveredAreasIds,
      ArrayList<Equipment> currentEquipment, ArrayList<Equipment> backpack, ArrayList<KeyItem> keyItems,
      ArrayList<String> discoveredEventIds) {

    deleteSave(saveNumber);

    saveStat(saveNumber, "keyValue", "level", level);
    saveStat(saveNumber, "keyValue", "gold", gold);
    saveStatDouble(saveNumber, "keyValue", "xp", xp);
    for (String areaId : discoveredAreasIds) {
      saveStat(saveNumber, "area", areaId, 1);
      System.out.println(areaId);
    }
    for (String eventId : discoveredEventIds) {
      saveStat(saveNumber, "event", eventId, 1);
    }
    for (Equipment equipment : currentEquipment) {
      String equipId = equipment.getId();
      saveStat(saveNumber, "currentEquipment", equipId, 1);
    }
    for (Equipment equipment : backpack) {
      String equipId = equipment.getId();
      saveStat(saveNumber, "backpack", equipId, 1);
    }
    for (KeyItem key : keyItems) {
      String keyId = key.getId();
      saveStat(saveNumber, "keyItem", keyId, 1);
    }
  }

  public saveInfo getSaveInfo(int saveNumber) {
    String sql = "SELECT ArrayListName, String, Value FROM SavedGame WHERE saveNumber = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in saveNumber to SQL query
      pstmt.setInt(1, saveNumber);
      // create variables to hold save info
      ArrayList<String> areaIds = new ArrayList<>();
      int level = 0;
      // populate variables
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String type = rs.getString("ArrayListName");
          switch (type) {
            case "keyValue":
              if (rs.getString("String").equals("level")) {
                level = rs.getInt("value");
              }
              break;
            case "area":
              areaIds.add(rs.getString("String"));
              break;
          }
        }
      }
      int areasDiscovered = areaIds.size();
      return new saveInfo(areasDiscovered, level, "explore");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: loadSave error");
      return new saveInfo(0, saveNumber, "explore");
    }
  }

  public Player loadSave(int saveNumber, Gui gui) {
    String sql = "SELECT ArrayListName, String, Value FROM SavedGame WHERE saveNumber = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in saveNumber to SQL query
      pstmt.setInt(1, saveNumber);
      // create variables to hold save info
      ArrayList<String> areaIds = new ArrayList<>();
      ArrayList<String> eventIds = new ArrayList<>();
      ArrayList<String> currentEquipment = new ArrayList<>();
      ArrayList<String> backpack = new ArrayList<>();
      ArrayList<String> keyItems = new ArrayList<>();
      int gold = 0;
      double xp = 0;
      int level = 1;
      // populate variables
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String type = rs.getString("ArrayListName");
          switch (type) {
            case "keyValue":
              if (rs.getString("String").equals("gold")) {
                gold = rs.getInt("value");
              }
              if (rs.getString("String").equals("xp")) {
                xp = rs.getDouble("value");
              }
              if (rs.getString("String").equals("level")) {
                level = rs.getInt("value");
              }
              break;
            case "area":
              areaIds.add(rs.getString("String"));
              break;
            case "event":
              eventIds.add(rs.getString("String"));
              break;
            case "currentEquipment":
              currentEquipment.add(rs.getString("String"));
              break;
            case "backpack":
              backpack.add(rs.getString("String"));
              break;
            case "keyItem":
              keyItems.add(rs.getString("String"));
              break;
          }

        }
        return new Player(this, gui, saveNumber, gold, level, xp, areaIds, eventIds, currentEquipment, backpack,
            keyItems);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: loadSave error");
      return new Player(null, gui, saveNumber, 0, 1, 0, null, null, null, null, null);
    }
  }

  public void deleteSave(int saveNumber) {
    String sql = "DELETE FROM SavedGame WHERE saveNumber = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in saveNumber to SQL query
      pstmt.setInt(1, saveNumber);

      pstmt.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: removeSave error");
    }
  }

  public void showSaveDetails(int saveNumber) {

  }

  public int howManySaves() {
    String sql = "SELECT MAX(SaveNumber) FROM SavedGame";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }

      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: howManySaves error");
      return -1;
    }
    System.out.println("Error: howManySaves error");
    return -2;
  }

  private Connection connect() {
    // connect to database
    Connection conn = null;

    try {
      conn = DriverManager.getConnection(location);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    return conn;
  }

  private void saveStat(int saveNumber, String ArrayListName, String text, int value) {
    String sql = "INSERT INTO SavedGame (SaveNumber, ArrayListName, String, Value) VALUES(?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in values to SQL query
      pstmt.setInt(1, saveNumber);
      pstmt.setString(2, ArrayListName);
      pstmt.setString(3, text);
      pstmt.setInt(4, value);

      pstmt.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: saveGame error");
    }

  }

  private void saveStatDouble(int saveNumber, String ArrayListName, String text, double value) {
    String sql = "INSERT INTO SavedGame (SaveNumber, ArrayListName, String, Value) VALUES(?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in values to SQL query
      pstmt.setInt(1, saveNumber);
      pstmt.setString(2, ArrayListName);
      pstmt.setString(3, text);
      pstmt.setDouble(4, value);

      pstmt.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println("Error: saveGame error");
    }

  }

}
