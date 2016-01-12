package nl.riebie.mcclans.utils;

import com.sun.istack.internal.Nullable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

/**
 * Created by K.Volkers on 12-1-2016.
 */
public final class Utils {

    private Utils() {
        // Private constructor
    }

    public static TextColor getTextColorByName(String colorName) {
//        for (TextColor chatColor : TextColors.values()) {
//            if (chatColor.name().equalsIgnoreCase(colorName)) {
//                return chatColor;
//            }
//        }
        // todo FIX MEH TIS NOT A FOCKIN ENUM
        return null;
    }

    public static Text formatLocation(@Nullable Location location) {
        if (location == null) {
            return Text.builder("NOT SET").color(TextColors.DARK_RED).build();
        } else {
            return Text.join(
                    Text.builder("X:").color(TextColors.GRAY).build(),
                    Text.of(location.getBlockX()),
                    Text.builder(" Y:").color(TextColors.GRAY).build(),
                    Text.of(location.getBlockY()),
                    Text.builder(" Z:").color(TextColors.GRAY).build(),
                    Text.of(location.getBlockZ()),
                    Text.builder(" WORLD").color(TextColors.GRAY).build() // todo FIX MEH THERS NO FOCKIN WORWLD. OK getExtent IS ZE WORLD BUT NO getName WOT
            );
        }
    }

    public static Text formatTime(long seconds, TextColor textColor, TextColor highlightColor) {
        int differenceInSeconds = (int) seconds;

        if (differenceInSeconds < 3600) {
            int minutes = differenceInSeconds / 60;
            return formatMinutes(minutes, textColor, highlightColor);
        } else {
            int hours = differenceInSeconds / 3600;
            int minutes = (differenceInSeconds % 3600) / 60;

            Text timeText = formatMinutes(minutes, textColor, highlightColor);
            if (hours == 0) {
                return timeText;
            } else {
                return Text.join(
                        formatHours(hours, textColor, highlightColor),
                        Text.of(" "),
                        timeText
                );
            }
        }
    }

    private static Text formatMinutes(int minutes, TextColor textColor, TextColor highlightColor) {
        Text timeText = Text.builder(String.valueOf(minutes)).color(highlightColor).build();
        return Text.join(
                timeText, Text.of(" "),
                Text.builder((minutes == 1) ? "minute" : "minutes").color(textColor).build()
        );
    }

    private static Text formatHours(int hours, TextColor textColor, TextColor highlightColor) {
        Text timeText = Text.builder(String.valueOf(hours)).color(highlightColor).build();
        return Text.join(
                timeText, Text.of(" "),
                Text.builder((hours == 1) ? "hour" : "hours").color(textColor).build()
        );
    }
}