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

package nl.riebie.mcclans.persistence;

import nl.riebie.mcclans.MCClans;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

    private static TaskExecutor instance;

    public final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private boolean enabled;

    private TaskExecutor() {
    }

    public static TaskExecutor getInstance() {
        if (instance == null) {
            instance = new TaskExecutor();
        }
        return instance;
    }

    public boolean initialize() {
        if (enabled || service.isShutdown() || service.isTerminated()) {
            MCClans.getPlugin().getLogger().warn(
                    "Could not initialize TaskExecutor! Enabled: " + enabled
                            + ", isShutdown: " + service.isShutdown()
                            + ", isTerminated: " + service.isTerminated()
                    , true);
            return false;
        }

        enabled = true;
        return true;
    }

    public void terminate() {
        if (!enabled) {
            return;
        }

        enabled = false;
        service.shutdown();

        try {
            service.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            MCClans.getPlugin().getLogger().error("Service shutdown interrupted", e, true);
        }
    }

    public boolean enqueue(final MCClansDatabaseTask task) {
        if (!enabled) {
            return false;
        }

        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.getQuery().execute();
                } catch (SQLException e) {
                    MCClans.getPlugin().getLogger().error("Failed to execute database task", e, true);
                }
            }
        });

        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
