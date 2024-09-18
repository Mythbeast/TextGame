import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;

public class SimpleBindingDoubleLabel extends Label {

  public SimpleBindingDoubleLabel(String string1, DoubleProperty variable, String string2) {
    this.textProperty().bind(new StringBinding() {
      {
        super.bind(variable);
      }

      @Override
      protected String computeValue() {
        // special case for ItemStatLabels where no stat is added
        if (variable.get() == 0 && string2.equals(")")) {
          return "-";
        }
        return string1 + variable.get() + string2;
      }
    });
  }
}
