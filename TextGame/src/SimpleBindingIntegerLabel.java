import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;

public class SimpleBindingIntegerLabel extends Label{

  SimpleBindingIntegerLabel(String string1, IntegerProperty variable, String string2) {
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

