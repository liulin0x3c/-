package icModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ICModel {
    public Edge[] edges;
    public Map<Integer, Vertex> vertexMap;
    public Vertex[] initiallyVertices;
    public static final int INITIAL_INFECTED_VERTICES_NUM = 1;
    public static final int BLOCK_EDGES_NUM = 50;
    public static final double PP = 0.1;
    public static final int PRECISION = 1000;

    private <T> T[] reservoirSampling(T[] objs, int selectNum) {
        if (objs.length < selectNum) return objs;
        var random = new Random();
        T[] selectedObjs = Arrays.copyOf(objs, selectNum);
        for (int i = selectNum; i < objs.length; i++) {
            int replaceIdx = random.nextInt(i); // 0 - i-1 共i个
            //在n中选一个数，数字小于k的概率就是k/m
            if (replaceIdx < selectNum) {
                selectedObjs[replaceIdx] = objs[i];
            }
        }
        return selectedObjs;
    }

    public ICModel setInitiallyInfectedVerticesRandomly() {
        Integer[] ids = reservoirSampling(vertexMap.keySet().toArray(new Integer[0]), INITIAL_INFECTED_VERTICES_NUM);
        initiallyVertices = new Vertex[INITIAL_INFECTED_VERTICES_NUM];
        for (int i = 0; i < ids.length; i++) {
            var id = ids[i];
            initiallyVertices[i] = vertexMap.get(id);
            vertexMap.get(id).activate();
        }
        return this;
    }

    // 50个固定的数据
    public ICModel setInitiallyInfectedVerticesFixedly() {
//        var initiallyVertexIds = new int[]{225, 2626, 2777, 589, 2583, 411, 2605, 2273, 3103, 9, 656, 2439, 1507, 2349, 1380, 786, 2051, 289, 2742, 1181, 3973, 1725, 3909, 3753, 3195, 2202, 2879, 99, 3433, 1970, 3511, 1310, 2688, 2005, 916, 1768, 1045, 1824, 3854, 3710, 2001, 414, 3604, 1958, 2851, 1378, 962, 3636, 880, 1393};
        var initiallyVertexIds = new int[]{0, 1, 2, 589, 2583, 411, 2605, 2273, 3103, 9, 656, 2439, 1507, 2349, 1380, 786, 2051, 289, 2742, 1181, 3973, 1725, 3909, 3753, 3195, 2202, 2879, 99, 3433, 1970, 3511, 1310, 2688, 2005, 916, 1768, 1045, 1824, 3854, 3710, 2001, 414, 3604, 1958, 2851, 1378, 962, 3636, 880, 1393};
        initiallyVertices = new Vertex[INITIAL_INFECTED_VERTICES_NUM];
        for (int i = 0; i < initiallyVertices.length; i++) {
            initiallyVertices[i] = vertexMap.get(initiallyVertexIds[i]);
        }
        for (var v : initiallyVertices) {
            v.activate();
        }
        return this;
    }

    public ICModel initICModelFromTxt() throws IOException {
        vertexMap = new HashMap<>();
        //读取数据
//        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\facebook_combined.txt");
        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\my.txt");
        String data = Files.readString(path);
        var lines = data.split("\n");
        edges = new Edge[lines.length];
        for (int i = 0; i < lines.length; i++) {
            var line = lines[i];
            String[] blocks = line.split(" ");
            int fromId = Integer.parseInt(blocks[0]);
            int toId = Integer.parseInt(blocks[1].strip());
            Vertex fromV = vertexMap.getOrDefault(fromId, new Vertex(fromId));
            Vertex toV = vertexMap.getOrDefault(toId, new Vertex(toId));
            var edge = new Edge(fromV, toV, PP);
            edges[i] = edge;
            fromV.addOutEdge(edge);
            toV.addToEdge(edge);
            vertexMap.put(fromId, fromV);
            vertexMap.put(toId, toV);
        }
        return this;
    }

    public int spread() {
        // spread
        Queue<Vertex> disseminatorsQueue = new ArrayDeque<>(List.of(initiallyVertices));
        while (!disseminatorsQueue.isEmpty()) {
            var v = disseminatorsQueue.poll();
            var nonActivateAdjacentVertices = v.getOutEdges().stream().filter((Edge edge) -> !edge.getBlocked()).map(Edge::getTo).filter((Vertex vertex) -> !vertex.isActivate()).toList();
            for (var adjacentVertex : nonActivateAdjacentVertices) {
                var n = new Random().nextInt(PRECISION);
                if (n < PP * PRECISION) {
                    adjacentVertex.activate();
                    disseminatorsQueue.offer(adjacentVertex);
                }
            }
        }

        int activateCnt = 0;
        for (var v : vertexMap.values()) {
            activateCnt += v.isActivate() ? 1 : 0;
        }
        return activateCnt;
    }

    public double calculateExpectInfectedVerticesNum() {
        for (var v : initiallyVertices) {
            v.setActivateProbability(new BigDecimal(1));
            v.activate();
        }
        Queue<Vertex> disseminatorsQueue = new ArrayDeque<>(List.of(initiallyVertices));

        while (!disseminatorsQueue.isEmpty()) {
            //一轮
            var vId2DeactivateProbability = new HashMap<Integer, BigDecimal>();
            System.out.println("disseminatorsQueue: " + disseminatorsQueue.stream().map(Vertex::getID).toList());

            while (!disseminatorsQueue.isEmpty()) {
                var v = disseminatorsQueue.poll();
                List<Edge> noBlockedAndDirectedToDeactivateVertexEdges = v.getOutEdges().stream().filter((Edge edge) -> !edge.getBlocked() && !edge.getTo().isActivate()).toList();
                System.out.println("\tv: id:" + v.getID() + " p: " + v.getActivateProbability());
                System.out.println("\t\tto: " + noBlockedAndDirectedToDeactivateVertexEdges.stream().map(Edge::getTo).map(Vertex::getID).toList());
                for (var edge : noBlockedAndDirectedToDeactivateVertexEdges) {
                    Vertex adjacentVertex = edge.getTo();
                    BigDecimal deactivateProbability = vId2DeactivateProbability.getOrDefault(adjacentVertex.getID(), new BigDecimal(1));
                    BigDecimal v1 = v.getActivateProbability().multiply(new BigDecimal(edge.getPp().toString()));
                    deactivateProbability = deactivateProbability.multiply(new BigDecimal(1).subtract(v1));
                    vId2DeactivateProbability.put(adjacentVertex.getID(), deactivateProbability);
                }
            }
            // 所有的本轮active的节点跑完，计算出了不被激活的概率，开始计算激活的概率
            for (var entry : vId2DeactivateProbability.entrySet()) {
                var v = vertexMap.get(entry.getKey());
                var deactivateProbability = entry.getValue();
                v.setActivateProbability(new BigDecimal(1).subtract(deactivateProbability));
                v.activate();
                disseminatorsQueue.offer(v);
            }
            System.out.println("\tvId2DeactivateProbability: " + vId2DeactivateProbability);
            System.out.println();
            System.out.println();
        }
        BigDecimal activateCnt = BigDecimal.valueOf(0);
        for (var v : vertexMap.values()) {
            activateCnt = activateCnt.add(new BigDecimal((v.isActivate() ? 1 : 0)).multiply(v.getActivateProbability()));
        }
        return activateCnt.doubleValue();
    }

    public ICModel initActivateStatus() {
        for (var vertex : vertexMap.values()) {
            vertex.deactivate();
        }
        for (var vertex : initiallyVertices) {
            vertex.activate();
        }
        return this;
    }

    public ICModel blockEdge(Edge edge) {
        edge.block();
        return this;
    }

    public ICModel restoreEdge(Edge edge) {
        edge.restore();
        return this;
    }

    public static void main(String[] args) throws IOException {
        var icModel = new ICModel().initICModelFromTxt().setInitiallyInfectedVerticesFixedly();
        System.out.println("S[0]: " + Arrays.stream(icModel.initiallyVertices).map(Vertex::getID).toList());
        System.out.print(icModel.initActivateStatus().calculateExpectInfectedVerticesNum() + " ");
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            int spread = icModel.initActivateStatus().spread();
            sum += spread;
//            System.out.println(spread);
        }
        System.out.println(sum / 1000000.0);

    }
}



