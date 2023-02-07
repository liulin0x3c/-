package icModel;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import common.*;

import static common.Logger.*;
import static common.CONST.*;

public final class ICModel {
    public static final Set<Edge> edgeSet = new HashSet<>();
    public static final Map<Integer, Vertex> vertexMap = new HashMap<>();
    public static final Set<Vertex> initActiveVertexSet = new HashSet<>();
    //    public static final List<Double> expHistory
    public final Set<Vertex> activeVertexSet = new HashSet<>(vertexMap.size());
    public final Set<Edge> blockedEdgeSet = new HashSet<>(edgeSet.size());

    static {
        initICModelFromTxt();
        setInitiallyInfectedVerticesRandomly();
        info("V: " + vertexMap.size() + ", E: " + edgeSet.size() + ", S: " + initActiveVertexSet.stream().map(Vertex::getId).toList() + ", BLOCK_EDGES_NUM: " + BLOCK_EDGES_NUM + ", SIMULATIONS_NUM: " + SIMULATIONS_NUM + ", THREAD_NUM: " + THREAD_NUM + "");
    }

    private static void setInitiallyInfectedVerticesRandomly() {
        var random = new Random(1);
        List<Vertex> selectedVertexList = new ArrayList<>(INITIAL_INFECTED_VERTICES_NUM);
        List<Vertex> values = new ArrayList<>(vertexMap.values());
        for (int i = 0; i < values.size(); i++) {
            if (i < INITIAL_INFECTED_VERTICES_NUM) {
                selectedVertexList.add(values.get(i));
            } else {
                int replaceIdx = random.nextInt(i);
                if (replaceIdx < INITIAL_INFECTED_VERTICES_NUM) {
                    selectedVertexList.set(replaceIdx, values.get(i));
                }
            }
        }
        initActiveVertexSet.addAll(selectedVertexList);
    }

    public static void initICModelFromTxt() {
        //读取数据
        Path path = Paths.get("data\\my.txt");
        String data;
        try {
            data = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Arrays.stream(data.split("\n")).forEach(line -> {
            String[] blocks = line.split(" ");
            int fromId = Integer.parseInt(blocks[0]);
            int toId = Integer.parseInt(blocks[1].strip());

            Vertex fromV = vertexMap.getOrDefault(fromId, new Vertex(fromId));
            Vertex toV = vertexMap.getOrDefault(toId, new Vertex(toId));
            var edge = new Edge(fromV, toV, PP);
            edgeSet.add(edge);
            fromV.addOutEdge(edge);
            toV.addInEdge(edge);
            vertexMap.put(fromId, fromV);
            vertexMap.put(toId, toV);
        });
    }


    public ICModel(ICModel icModel) {
        activeVertexSet.addAll(ICModel.initActiveVertexSet);
        blockedEdgeSet.addAll(icModel.blockedEdgeSet);

    }

    public ICModel() {
    }

    public ICModel blockEdge(Edge edge) {
        blockedEdgeSet.add(edge);
        return this;
    }

    public ICModel restoreEdge(Edge edge) {
        blockedEdgeSet.remove(edge);
        return this;
    }

    private final Random random = new Random();

    private boolean randomlyActivate(double pp) {
        return random.nextInt(PRECISION) < pp * PRECISION;
    }

    private boolean validEdge(Edge e) {
        return !activeVertexSet.contains(e.to()) && !blockedEdgeSet.contains(e);
    }

    public int spread(Queue<Edge> disseminatorsQueue) {
        while (!disseminatorsQueue.isEmpty()) {
            var edge = disseminatorsQueue.poll();
            if (activeVertexSet.contains(edge.to())) {
                continue;
            }
            var toVertex = edge.to();
            if (randomlyActivate(edge.pp())) {
                activeVertexSet.add(toVertex);
                toVertex.getOutEdges().stream().filter(this::validEdge).forEach(disseminatorsQueue::offer);
            }
        }
        return activeVertexSet.size();
    }

    public double calculateEXPSingleThread() {
        Queue<Edge> disseminatorsQueue = new ArrayDeque<>(edgeSet.size());
        long sum = 0;
        Collection<Edge> collect = initActiveVertexSet.stream().<Edge>mapMulti((vertex, consumer) -> vertex.getOutEdges().forEach(consumer)).filter(e -> !activeVertexSet.contains(e.to()) && !blockedEdgeSet.contains(e)).toList();
        for (int i = 0; i < SIMULATIONS_NUM; i++) {
            disseminatorsQueue.addAll(collect);
            activeVertexSet.clear();
            activeVertexSet.addAll(initActiveVertexSet);
            int spread = spread(disseminatorsQueue);
            sum += spread;
        }
        return sum / (SIMULATIONS_NUM * 1.0);
    }


    public Entry<Edge, Double> findBestBlockedEdge() throws InterruptedException {
        try (ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM)) {
            BlockingQueue<ICModel> availableModel = new ArrayBlockingQueue<>(THREAD_NUM);
            for (int i = 0; i < THREAD_NUM; i++) {
                availableModel.add(new ICModel(this));
            }

            Set<Edge> invalidedEdgeSet = initActiveVertexSet.stream().<Edge>mapMulti((vertex, consumer) -> vertex.getInEdges().forEach(consumer)).collect(Collectors.toSet());
            List<Edge> noBlockedEdgeList = edgeSet.stream().filter(edge -> !blockedEdgeSet.contains(edge) && !invalidedEdgeSet.contains(edge)).toList();
            CountDownLatch countDownLatch = new CountDownLatch(noBlockedEdgeList.size());

            System.out.println("start concurrent running");
            System.out.print("0%" + "\r");

            Entry<Edge, Double> minEntry = new Entry<>(null, Double.MAX_VALUE);
            noBlockedEdgeList.forEach(edge -> executorService.execute(() -> {
                ICModel icModel = Objects.requireNonNull(availableModel.poll());
                double exp = icModel.blockEdge(edge).calculateEXPSingleThread();
                availableModel.add(icModel.restoreEdge(edge));
                if (minEntry.exp > exp) {
                    minEntry.exp = exp;
                    minEntry.edge = edge;
                }
                countDownLatch.countDown();
                long count = countDownLatch.getCount();
                if ((noBlockedEdgeList.size() - count) % 5 == 0) {
                    System.out.print((int) (((double) ((noBlockedEdgeList.size() - count)) / noBlockedEdgeList.size() * 100) * 100) / 100.0 + "%" + "\r");
                }
            }));
            countDownLatch.await();
            System.out.print("\r100%\n");
            System.out.println("the best block edge:");
            Edge edge = minEntry.edge;
            this.blockEdge(edge);
            return minEntry;
        }
    }

    public void blockEdgesToMinimizingInfluence() throws InterruptedException {
        System.out.println("calculating base");
        final double[] sum = {0};
        var num = THREAD_NUM;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            new Thread(() -> {
                var value = new double[]{0d};
                value[0] = new ICModel(this).calculateEXPSingleThread();
                synchronized (ICModel.class) {
                    sum[0] = sum[0] + value[0];
                }
                countDownLatch.countDown();
            }).start();

        }
        countDownLatch.await();
        var base = sum[0] / num;
        info("base: " + base);
        for (int i = 1; i <= BLOCK_EDGES_NUM; i++) {
            info("loop:" + i);
            Entry<Edge, Double> minEntry = findBestBlockedEdge();
            info("blocked: " + minEntry.edge + ", exp: " + minEntry.exp + ", all blocked edge: " + blockedEdgeSet.stream().toList());
        }
        info("finished");
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        ICModel icModel = new ICModel();

        icModel.blockEdgesToMinimizingInfluence();
    }
}



