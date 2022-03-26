package work.lclpnet.mmo.client.gui.login;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordTextField extends TextFieldWidget {

    public PasswordTextField(TextRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, TranslatableText msg) {
        super(fontIn, xIn, yIn, widthIn, heightIn, msg);
        this.setRenderTextProvider((string, integer) -> {
            // redact password
            string = IntStream.range(0, string.length())
                    .mapToObj(i -> "*")
                    .collect(Collectors.joining());

            return OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
        });
    }
}
