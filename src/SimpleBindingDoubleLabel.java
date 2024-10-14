import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import java.text.DecimalFormat;

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
        // code to avoid scientific notation:
        DecimalFormat format = new DecimalFormat("0");
        String variableOutput = format.format(variable.get());
        return string1 + variableOutput + string2;
      }
    });
  }

}
