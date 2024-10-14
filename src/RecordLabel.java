import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;

public class RecordLabel extends Label {

    public RecordLabel(IntegerProperty variable) {
        this.textProperty().bind(new StringBinding() {
            {
                super.bind(variable);
            }

            @Override
            protected String computeValue() {
                // special case for ItemStatLabels where no stat is added
                if (variable.get() == 0) {
                    return "-";
                }
                return String.valueOf(variable.get());
            }
        });
    }
}
