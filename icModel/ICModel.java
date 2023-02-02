package icModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ICModel {
    public List<Edge> edges;
    public Map<Integer, Vertex> vertexMap;
    public static List<Integer> initiallyVertexIds;
    public static final int INITIAL_INFECTED_VERTICES_NUM = 50;
    public static final int BLOCK_EDGES_NUM = 50;
    public static final double PP = 0.1;
    public static final int PRECISION = 1000;
    final int THREAD_NUM = 18;
    final int TIMES_FOR_PER_THREAD = 666; //total = 9999
    final int SIMULATIONS_NUM = 1000;

    private <T> T[] reservoirSampling(T[] objs) {
        if (objs.length < ICModel.INITIAL_INFECTED_VERTICES_NUM) return objs;
        var random = new Random();
        T[] selectedObjs = Arrays.copyOf(objs, ICModel.INITIAL_INFECTED_VERTICES_NUM);
        for (int i = ICModel.INITIAL_INFECTED_VERTICES_NUM; i < objs.length; i++) {
            int replaceIdx = random.nextInt(i); // 0 - i-1 共i个
            //在n中选一个数，数字小于k的概率就是k/m
            if (replaceIdx < ICModel.INITIAL_INFECTED_VERTICES_NUM) {
                selectedObjs[replaceIdx] = objs[i];
            }
        }
        return selectedObjs;
    }

    private void initInitiallyVertexIds(Integer[] ids) {
        initiallyVertexIds = Arrays.stream(ids).toList();
        initActivateStatus();
    }

    public void setInitiallyInfectedVerticesRandomly() {
        Integer[] ids = reservoirSampling(vertexMap.keySet().toArray(new Integer[0]));
        initInitiallyVertexIds(ids);
    }


    // 50个固定的数据
    public void setInitiallyInfectedVerticesFixedly() {
        var ids = Arrays.stream(new int[]{3290, 1244, 945, 1793, 1647, 1328, 1410, 1759, 327, 682, 348, 1074, 291, 431, 279, 1623, 2704, 1411, 1450, 1014, 1086, 1031, 22, 633, 797, 527, 2153, 648, 1326, 793, 530, 1749, 1152, 1293, 963, 310, 1124, 1387, 1439, 1015, 1297, 116, 547, 1383, 1089, 473, 2635, 822, 208, 2398}).boxed().toArray(Integer[]::new);
        initInitiallyVertexIds(ids);
    }

    public synchronized ICModel initICModelFromTxt() {
        vertexMap = new HashMap<>();
        //读取数据
//        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\facebook_combined.txt");
        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\my.txt");
        String data;
        try {
            data = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var lines = data.split("\n");
        edges = new ArrayList<>(lines.length);
        for (String line : lines) {
            String[] blocks = line.split(" ");
            int fromId = Integer.parseInt(blocks[0]);
            int toId = Integer.parseInt(blocks[1].strip());
            var edge = new Edge(fromId, toId, PP);
            edges.add(edge);

            Vertex fromV = vertexMap.getOrDefault(fromId, new Vertex(fromId));
            Vertex toV = vertexMap.getOrDefault(toId, new Vertex(toId));
            fromV.addOutEdge(edge);
            toV.addInEdge(edge);
            vertexMap.put(fromId, fromV);
            vertexMap.put(toId, toV);
        }
        return this;

    }

    public int spread() {
        this.initActivateStatus();
        // spread
        Queue<Vertex> disseminatorsQueue = initiallyVertexIds.stream().map((id) -> vertexMap.get(id)).collect(Collectors.toCollection(ArrayDeque::new));
        Random random = new Random();
        while (!disseminatorsQueue.isEmpty()) {
            var vertex = disseminatorsQueue.poll();
            List<Vertex> nonActivateAdjacentVertices = vertex.getOutEdges().stream().filter(edge -> !edge.getBlocked()).map(Edge::getToId).map((Id) -> vertexMap.get(Id)).filter((v) -> !v.getActive()).toList();
            for (var adjacentVertex : nonActivateAdjacentVertices) {
                var n = random.nextInt(PRECISION);
                if (n < PP * PRECISION) {
                    adjacentVertex.setActive(true);
                    disseminatorsQueue.offer(adjacentVertex);
                }
            }
        }

        int activateCnt = 0;
        for (var v : vertexMap.values()) {
            activateCnt += v.getActive() ? 1 : 0;
        }
        return activateCnt;
    }

    public ICModel(ICModel icModel) {
        vertexMap = new HashMap<>();
        //读取数据
        edges = new ArrayList<>(icModel.edges.size());
        for (int i = 0; i < icModel.edges.size(); ++i) {
            var edge = new Edge(icModel.edges.get(i));
            edges.add(edge);

            int fromId = edge.getFromId();
            int toId = edge.getToId();
            Vertex fromV = vertexMap.getOrDefault(fromId, new Vertex(fromId));
            Vertex toV = vertexMap.getOrDefault(toId, new Vertex(toId));
            fromV.addOutEdge(edge);
            toV.addInEdge(edge);
            vertexMap.put(fromId, fromV);
            vertexMap.put(toId, toV);
        }
    }

    private ICModel() {
        this.initICModelFromTxt().setInitiallyInfectedVerticesFixedly();

    }

    private ICModel initActivateStatus() {
        for (var vertex : vertexMap.values()) {
            vertex.setActive(false);
        }
        for (var id : initiallyVertexIds) {
            vertexMap.get(id).setActive(true);
        }
        return this;
    }

    public ICModel blockEdge(Edge edge) {
        edge.setBlocked(true);
        return this;
    }

    public void restoreEdge(Edge edge) {
        edge.setBlocked(false);
    }

    public double calculateEXPConcurrent() throws InterruptedException {

        final double[] total = {0d};
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            ICModel finalIcModel = new ICModel(this);
            new Thread(() -> {
                long sum = 0;
                for (int j = 0; j < TIMES_FOR_PER_THREAD; j++) {
                    int spread = finalIcModel.initActivateStatus().spread();
                    sum += spread;
                }
                synchronized (ICModel.class) {
                    total[0] += sum / (1.0 * TIMES_FOR_PER_THREAD);
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        return total[0] / THREAD_NUM;
    }

    public double calculateEXPSingleThread() {
        return calculateEXPSingleThread(SIMULATIONS_NUM);
    }

    public double calculateEXPSingleThread(int simulationsNum) {
        long sum = 0;
        for (int j = 0; j < simulationsNum; j++) {
            int spread = spread();
            sum += spread;
        }
        return sum / (simulationsNum * 1.0);
    }

    public ICModel findBestBlockedEdge() throws InterruptedException {
        List<Edge> edges = new ArrayList<>(this.edges.stream().filter(edge -> !edge.getBlocked() && vertexMap.get(edge.getToId()).getOutEdges().size() != 0).toList());
        System.out.println("loaded edges size: " + edges.size());
        Map<Double, Edge> map = new ConcurrentHashMap<>();
        Deque<ICModel> availableModel = new ArrayDeque<>();
        for (int i = 0; i < THREAD_NUM; i++) {
            availableModel.offer(new ICModel(this));
        }
        CountDownLatch countDownLatch = new CountDownLatch(edges.size());
        try (ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM)) {
            final int[] cnt = new int[]{0};
            System.out.println("start concurrent running");
            System.out.print("0%" + "\r");

            for (Edge edge : edges) {
                executorService.execute(() -> {
                    ICModel icModel;
                    synchronized (availableModel) {
                        icModel = Objects.requireNonNull(availableModel.poll());
                    }
                    double exp = icModel.blockEdge(edge).calculateEXPSingleThread();
                    icModel.restoreEdge(edge);
                    synchronized (availableModel) {
                        availableModel.offer(icModel);
                    }
                    map.put(exp, edge);
                    if (++cnt[0] % 100 == 0) {
                        System.out.print((int) (((double) (cnt[0]) / edges.size() * 100) * 100) / 100.0 + "%" + "\r");
                    }
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await();
            System.out.println("\r100%");
            System.out.println("the best block edge:");
            for (var val : map.keySet().stream().sorted().toList()) {
                System.out.println("" + val + " " + map.get(val));
            }
            Double minEXP = map.keySet().stream().min(Comparator.comparingDouble(e -> e)).get();
            Edge edge = map.get(minEXP);
            System.out.println("block " + edge + ", exp: " + minEXP);
            return blockEdge(edge);
        }
    }

    public ICModel blockEdgesToMinimizingInfluence() throws InterruptedException {
        for (int i = 1; i <= BLOCK_EDGES_NUM; i++) {
            System.out.println("loop:" + i);
            System.out.println("blocked: " + edges.stream().filter(Edge::getBlocked).toList());
            findBestBlockedEdge();
        }

        return this;
    }


    public static void main(String[] args) throws InterruptedException {
        ICModel icModel = new ICModel();
        double base = icModel.calculateEXPConcurrent();
        System.out.println(base);
        icModel.blockEdgesToMinimizingInfluence();
    }
}



