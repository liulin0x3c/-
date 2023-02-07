import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\facebook_combined.txt");
//        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\user_contact_v1.m");
        Path pathb = Paths.get("data\\my.txt");
        String data = Files.readString(path);
        var lines = data.split("\n");
        var arr = new ArrayList<String>(lines.length);
        System.out.println(lines.length);
        Random random = new Random(1);
        for (var line : lines) {
            if (random.nextInt(100) < 10) {
                arr.add(line);
            }
        }
        String join = String.join("\n", arr);
        System.out.println(join.split(" ").length);
        Files.writeString(pathb, join);
        System.out.println("ok");


//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        final int[] cnt = new int[1];
//        cnt[0] = 0;
//        for (int i = 0; i < Integer.MAX_VALUE; ++i) {
//            executorService.execute(() -> {
//                try {
//                    Thread.sleep(1000);
//                    int x;
//                    synchronized (Main.class) {
//                        x = cnt[0]++;
//                    }
//                    System.out.println(x);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//        executorService.shutdown();
    }
}