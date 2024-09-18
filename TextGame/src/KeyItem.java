import java.util.Objects;

public class KeyItem {
  private String id;
  private String name;

  public KeyItem(DatabaseManager db, String keyItemId) {
    this.id = keyItemId;
    this.name = db.getKeyItemName(this.id);
  }

  public String getName() {
    return this.name;
  }

  public String getId() {
    return this.id;
  }

  // override the .equals() method to be able to compare key items with
  // .contains()
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    KeyItem that = (KeyItem) o;
    return Objects.equals(this.id, that.id) && Objects.equals(name, that.name);
  }

  // Override hashCode to maintain consistency with equals
  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }

  @Override
  public String toString() {
    return "Equipment{name='" + name + "', id=" + id + "}";
  }

}
