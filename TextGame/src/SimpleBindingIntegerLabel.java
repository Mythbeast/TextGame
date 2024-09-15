import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;

public class SimpleBindingIntegerLabel extends Label{

  SimpleBindingIntegerLabel(String string, IntegerProperty variable) {
    this.textProperty().bind(new StringBinding() {
        {
          super.bind(variable);
        }
        @Override
        protected String computeValue() {
          return string + variable.get();
        }
      });
  }
}

