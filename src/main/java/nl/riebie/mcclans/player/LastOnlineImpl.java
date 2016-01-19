package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.LastOnline;

/**
 * Created by K.Volkers on 19-1-2016.
 */

public class LastOnlineImpl implements LastOnline {

    private final long lastOnlineTime;

    public LastOnlineImpl(long lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public LastOnlineImpl() {
        lastOnlineTime = System.currentTimeMillis();
    }

    @Override
    public long getTime() {
        return lastOnlineTime;
    }

    @Override
    public String getDifferenceInText() {
        String lastOnlineText = "System time error";
        long differenceInSeconds = (System.currentTimeMillis() - lastOnlineTime) / 1000;
        if (differenceInSeconds >= 0 && differenceInSeconds < 60) {
            lastOnlineText = "Just now";
        } else if (differenceInSeconds >= 60 && differenceInSeconds < 3600) {
            long minutes = differenceInSeconds / 60;
            lastOnlineText = String.valueOf(minutes);
            if (minutes == 1) {
                lastOnlineText += " minute ago";
            } else {
                lastOnlineText += " minutes ago";
            }
        } else if (differenceInSeconds >= 3600 && differenceInSeconds < 86400) {
            long hours = differenceInSeconds / 3600;
            lastOnlineText = String.valueOf(hours);
            if (hours == 1) {
                lastOnlineText += " hour ago";
            } else {
                lastOnlineText += " hours ago";
            }
        } else if (differenceInSeconds >= 86400) {
            long days = differenceInSeconds / 86400;
            lastOnlineText = String.valueOf(days);
            if (days == 1) {
                lastOnlineText += " day ago";
            } else {
                lastOnlineText += " days ago";
            }
        }
        return lastOnlineText;
    }
}
