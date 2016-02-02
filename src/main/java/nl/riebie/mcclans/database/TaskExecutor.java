package nl.riebie.mcclans.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.config.Config;

public class TaskExecutor extends Thread {

    private static TaskExecutor instance;

    private boolean running = false;
    private boolean finished = true;
    private ConcurrentLinkedQueue<MCClansDatabaseTask> queue = new ConcurrentLinkedQueue<MCClansDatabaseTask>();

    protected TaskExecutor() {
    }

    public static TaskExecutor getInstance() {
        if (instance == null) {
            instance = new TaskExecutor();
        }
        return instance;
    }

    public boolean initialize() {
        if (!running && finished) {
            running = true;
            finished = false;
            super.start();
            return true;
        } else {
            MCClans.getPlugin().getLogger().warn("Could not initialize TaskExecutor! Running: " + running + ", finished: " + finished, true);
            return false;
        }
    }

    public boolean enqueue(MCClansDatabaseTask task) {
        return running && queue.add(task);
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public void terminate() {
        if (!finished) {
            synchronized (this) {
                running = false;
                while (!finished) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (!finished) {
            MCClansDatabaseTask task = queue.poll();
            if (task == null) {
                if (!running) {
                    synchronized (this) {
                        this.notifyAll();
                        finished = true;
                    }
                }
            } else {
                PreparedStatement query = task.getQuery();
                try {
                    query.execute();
                } catch (SQLException e) {
                    if (Config.getBoolean(Config.DEBUGGING)) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
