package nl.riebie.mcclans.messages;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.utils.FileUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;

/**
 * Created by Kippers on 27/08/2016.
 */
public class Logger {

    private static Logger instance;

    private File logsDir;
    private File file;
    private org.slf4j.Logger spongeLogger;
    private ExecutorService executor;
    private boolean useLocalLogging;
    private boolean localLoggingTrigger;

    protected Logger() {
    }

    public static Logger get() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void init(MCClans plugin, org.slf4j.Logger spongeLogger) {
        executor = Sponge.getScheduler().createAsyncExecutor(plugin);
        this.useLocalLogging = false;
        this.spongeLogger = spongeLogger;
    }

    public void enableLocalLogging(File configDir) {
        useLocalLogging = true;
        createDir(configDir);
        zipPreviousFiles();
        createFile();
    }

    public void enableLocalLoggingTrigger(boolean enable) {
        localLoggingTrigger = enable;
    }

    private void createDir(File configDir) {
        logsDir = new File(configDir, "logs");
        logsDir.mkdirs();
    }

    private void zipPreviousFiles() {
        // Zip previous file
        for (File childFile : logsDir.listFiles(pathname -> pathname.isFile() && pathname.getPath().endsWith(".log"))) {
            FileUtils.toZip(childFile);
            childFile.delete();
        }
    }

    private void createFile() {
        // Create new file
        file = new File(logsDir, Utils.getDateTimeString() + ".log");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLocalLogger(String level, String value, Throwable... throwables) {
        if (localLoggingTrigger) {
            useLocalLogging = true;
        }

        if (useLocalLogging) {
            executor.submit((Runnable) () -> {
                write(Utils.getTimeString() + " " + level + " " + value + FileUtils.getLineSeparator());
                for (Throwable throwable : throwables) {
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        write(Utils.getTimeString() + " " + level + " " + element.toString() + FileUtils.getLineSeparator());
                    }
                }
            });
        }
    }

    private synchronized void write(String value) {
        try {
            Files.write(file.toPath(), value.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String message, boolean override) {
        if (Config.getBoolean(Config.DEBUGGING) || override) {
            spongeLogger.info(message);
        }
        writeLocalLogger("[i]", message);
    }

    public void debug(String message, boolean override) {
        if (Config.getBoolean(Config.DEBUGGING) || override) {
            spongeLogger.info(message);
        }
        writeLocalLogger("[d]", message);
    }

    public void warn(String message, boolean override) {
        if (Config.getBoolean(Config.DEBUGGING) || override) {
            spongeLogger.info(message);
        }
        writeLocalLogger("[w]", message);
    }

    public void error(String message, boolean override) {
        if (Config.getBoolean(Config.DEBUGGING) || override) {
            spongeLogger.info(message);
        }
        writeLocalLogger("[e]", message);
    }

    public void error(String message, Throwable t, boolean override) {
        if (Config.getBoolean(Config.DEBUGGING) || override) {
            spongeLogger.info(message, t);
        }
        writeLocalLogger("[e]", message, t);
    }

    /**
     * This message is only logged locally.
     */
    public void local(String message) {
        writeLocalLogger("[l]", message);
    }
}
