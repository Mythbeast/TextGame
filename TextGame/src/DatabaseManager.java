
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
  String location = "jdbc:sqlite:C:\\Coding Projects\\TextGame\\game.db";
  private Connection conn;

  public DatabaseManager() {
    this.conn = connect();
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

  public List<Object> getAreaInfo(String areaID) {
    // return List of column information of area
    String sql = "SELECT areaName, areaChance, monsterChance, eventChance FROM Area WHERE areaID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in areaID to SQL query
      pstmt.setString(1, areaID);

      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<Object>();

        // add information to result
        if (rs.next()) {
          result.add(rs.getString("areaName"));
          result.add(rs.getInt("areaChance"));
          result.add(rs.getInt("monsterChance"));
          result.add(rs.getInt("eventChance"));
        }

        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getAreaInfo error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
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
    String sql = "SELECT monsterName, level, xp, maxHP, attack, defence, critChance, critDamage, gold, deathText FROM Monster WHERE monsterID = ?";

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

  public List<Object> getEventOptions(String eventID) {
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
      List<Object> eventOptions = new ArrayList<>();
      for (int i = 0; i <= optionIDs.size() - 1; i++) {
        eventOptions.add(getOptionInfo((String) optionIDs.get(i)));
      }
      return eventOptions;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getEventOptions error");
      List<Object> errorList = new ArrayList<Object>();
      return errorList;
    }
  }

  public List<Object> getOptionInfo(String optionID) {

    String sql = "SELECT optionText, goldCost, reqItemID, heal, goldPerHeal, itemGet, itemLose, equip, fight, eventText, resultText FROM EventOptions where optionID = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      // add in optionID to SQL query
      pstmt.setString(1, optionID);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Object> result = new ArrayList<>();
        while (rs.next()) {
          result.add(rs.getString("optionText"));
          result.add(rs.getInt("goldCost"));
          result.add(rs.getString("reqItemID"));
          result.add(rs.getInt("heal"));
          result.add(rs.getInt("goldPerHeal"));
          result.add(rs.getString("itemGet"));
          result.add(rs.getString("itemLose"));
          result.add(rs.getString("equip"));
          result.add(rs.getString("fight"));
          result.add(rs.getString("eventText"));
          result.add(rs.getString("resultText"));
        }
        return result;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // unreachable unless error occurs
      System.out.println("Error: getOptionInfo error");
      List<Object> errorList = new ArrayList<Object>();
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
      // add in itemID to SQL query
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

}
