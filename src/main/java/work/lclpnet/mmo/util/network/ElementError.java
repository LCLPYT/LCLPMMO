package work.lclpnet.mmo.util.network;

import java.util.List;
import java.util.Objects;

public class ElementError {

    private final String element;
    private final List<String> errors;

    public ElementError(String element, List<String> errors) {
        this.element = Objects.requireNonNull(element);
        this.errors = Objects.requireNonNull(errors);
    }

    public String getElement() {
        return element;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('"').append(element).append("\": [");

        boolean written = false;
        for (String s : errors) {
            builder.append("    \"");
            builder.append(s.replaceAll("\"", "\\\""));
            builder.append('"');

            if (!written) written = true;
            else builder.append(',');
        }

        builder.append(']');

        return builder.toString();
    }
}
