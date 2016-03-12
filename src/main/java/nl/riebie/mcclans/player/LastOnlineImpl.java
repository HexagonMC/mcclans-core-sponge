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

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.LastOnline;

/**
 * Created by Kippers on 19-1-2016.
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
