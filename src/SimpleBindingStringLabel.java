import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class SimpleBindingStringLabel extends Label {

  public SimpleBindingStringLabel(String string1, StringProperty variable, String string2) {
    this.textProperty().bind(new StringBinding() {
      {
        super.bind(variable);
      }

      @Override
      protected String computeValue() {
        return string1 + variable.get() + string2;
      }
    });
  }
}
