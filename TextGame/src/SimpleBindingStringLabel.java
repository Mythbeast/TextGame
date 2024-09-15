import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;

public class SimpleBindingStringLabel extends Label{

  SimpleBindingStringLabel(String string, StringProperty variable) {
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
