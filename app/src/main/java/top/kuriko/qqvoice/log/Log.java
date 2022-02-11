package top.kuriko.qqvoice.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Log {
    private static final HashMap<String, ArrayList<LogItem>> logs = new HashMap<>();
    static final Map<Integer, LogLevel> levels;

    static {
        HashMap<Integer, LogLevel> lst = new HashMap<>();
        for (LogLevel level: LogLevel.values())
            lst.put(level.getValue(), level);
        levels = Collections.unmodifiableMap(lst);
    }

    private static LogLevel logLevel = LogLevel.Info;
    public static LogLevel getLogLevel() {
        return logLevel;
    }
    public static void setLogLevel(LogLevel level) {
        Objects.requireNonNull(level);
        logLevel = level;
    }

    public static void pushLog(String tag, LogItem log) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(log);
        if (!logs.containsKey(tag))
            logs.put(tag, new ArrayList<>());
        //noinspection ConstantConditions
        logs.get(tag).add(log);
    }

    public static List<LogItem> get(String tag) {
        Objects.requireNonNull(tag);
        List<LogItem> lst = logs.get(tag);
        return Collections.unmodifiableList(lst != null ? lst : new ArrayList<>());
    }
}
