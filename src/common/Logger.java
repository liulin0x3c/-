package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public final class Logger {
    private static final Path path = Paths.get("data\\log.txt");

    static {
        info("--------------start--------------");
        info("--------------start--------------");
        info("--------------start--------------");
    }

    private Logger() {
    }

    public synchronized static void info(CharSequence log) {
        String time = new Date().toString();
        log = "[INFO]\t" + time + " | " + log;
        System.out.println(log);
        try {
            Files.writeString(path, log + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
