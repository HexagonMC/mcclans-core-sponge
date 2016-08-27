/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.config.Config;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Kippers on 12-1-2016.
 */
public final class Utils {

    private static List<String> worldsBlockedFromKdr;
    private static List<String> worldsBlockedFromFf;
    private static List<String> blockedTagsAndNames;

    private Utils() {
        // Private constructor
    }

    public static TextColor getTextColorByName(String textColorName, TextColor fallbackColor) {
        if (TextColors.AQUA.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.AQUA;
        } else if (TextColors.BLACK.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.BLACK;
        } else if (TextColors.BLUE.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.BLUE;
        } else if (TextColors.DARK_AQUA.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_AQUA;
        } else if (TextColors.DARK_BLUE.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_BLUE;
        } else if (TextColors.DARK_GRAY.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_GRAY;
        } else if (TextColors.DARK_GREEN.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_GREEN;
        } else if (TextColors.DARK_PURPLE.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_PURPLE;
        } else if (TextColors.DARK_RED.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.DARK_RED;
        } else if (TextColors.GOLD.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.GOLD;
        } else if (TextColors.GRAY.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.GRAY;
        } else if (TextColors.GREEN.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.GREEN;
        } else if (TextColors.LIGHT_PURPLE.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.LIGHT_PURPLE;
        } else if (TextColors.RED.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.RED;
        } else if (TextColors.WHITE.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.WHITE;
        } else if (TextColors.YELLOW.getName().equalsIgnoreCase(textColorName)) {
            return TextColors.YELLOW;
        } else {
            return fallbackColor;
        }
    }

    public static TextColor getTextColorById(String textColorId, TextColor fallbackColor) {
        if (TextColors.AQUA.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.AQUA;
        } else if (TextColors.BLACK.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.BLACK;
        } else if (TextColors.BLUE.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.BLUE;
        } else if (TextColors.DARK_AQUA.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_AQUA;
        } else if (TextColors.DARK_BLUE.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_BLUE;
        } else if (TextColors.DARK_GRAY.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_GRAY;
        } else if (TextColors.DARK_GREEN.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_GREEN;
        } else if (TextColors.DARK_PURPLE.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_PURPLE;
        } else if (TextColors.DARK_RED.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.DARK_RED;
        } else if (TextColors.GOLD.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.GOLD;
        } else if (TextColors.GRAY.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.GRAY;
        } else if (TextColors.GREEN.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.GREEN;
        } else if (TextColors.LIGHT_PURPLE.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.LIGHT_PURPLE;
        } else if (TextColors.RED.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.RED;
        } else if (TextColors.WHITE.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.WHITE;
        } else if (TextColors.YELLOW.getId().equalsIgnoreCase(textColorId)) {
            return TextColors.YELLOW;
        } else {
            return fallbackColor;
        }
    }

    public static Text formatKdr(int total, int high, int medium, int low) {
        return Text.join(
                Text.of(String.valueOf(total)),
                Text.builder(" [").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(high)),
                Text.builder(" : ").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(medium)),
                Text.builder(" : ").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(low)),
                Text.builder("]").color(TextColors.GRAY).build()
        );
    }

    public static Text formatLocation(@Nullable Location<World> location) {
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
                    Text.builder(" " + location.getExtent().getName()).color(TextColors.GRAY).build()
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

    public static boolean isWorldBlockedFromAllowingFriendlyFireProtection(String worldName) {
        if (worldsBlockedFromFf == null) {
            worldsBlockedFromFf = Config.getList(Config.BLOCKED_WORLDS_FF_PROTECTION, String.class);
        }
        for (String world : worldsBlockedFromFf) {
            if (world.equalsIgnoreCase(worldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWorldBlockedFromLoggingPlayerKDR(String worldName) {
        if (worldsBlockedFromKdr == null) {
            worldsBlockedFromKdr = Config.getList(Config.BLOCKED_WORLDS_PLAYER_KDR, String.class);
        }
        for (String world : worldsBlockedFromKdr) {
            if (world.equalsIgnoreCase(worldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClanTagOrNameBlocked(String clanTagOrName) {
        if (blockedTagsAndNames == null) {
            blockedTagsAndNames = Config.getList(Config.BLOCKED_CLAN_TAGS_AND_NAMES, String.class);
        }
        for (String name : blockedTagsAndNames) {
            if (name.equalsIgnoreCase(clanTagOrName)) {
                return true;
            }
        }
        return false;
    }

    public static String getDateTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis());
    }

    public static String getTimeString() {
        return new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());
    }
}
