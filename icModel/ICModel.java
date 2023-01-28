package icModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ICModel {
    public Edge[] edges;
    public Vertex[] vertices;
    public Vertex[] initiallyVertices;
    public final int initialNum = 50;
    public final double pp = 0.1;
    public final int precision = 1000;

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
        initiallyVertices = reservoirSampling(vertices, initialNum);
        for (var v : initiallyVertices) {
            v.activate();
        }
        return this;
    }

    public ICModel initICModelFromTxt() throws IOException {
        vertices = new Vertex[4039];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vertex();
        }
        edges = new Edge[88234];
        //读取数据
        Path path = Paths.get("C:\\Users\\liuli\\Desktop\\facebook_combined.txt");
        String data = Files.readString(path);
        var lines = data.split("\n");
        for (int i = 0; i < lines.length; i++) {
            var line = lines[i];
            String[] blocks = line.split(" ");
            int fromId = Integer.parseInt(blocks[0]);
            int toId = Integer.parseInt(blocks[1]);
            var edge = new Edge(vertices[fromId], vertices[toId], pp);
            edges[i] = edge;
            vertices[fromId].addAdjacentVertex(vertices[toId]);
        }
        return this;
    }

    public int spread() {
        // spread
        Queue<Vertex> disseminatorsQueue = new ArrayDeque<>(List.of(initiallyVertices));
        while (!disseminatorsQueue.isEmpty()) {
            var v = disseminatorsQueue.poll();
            var nonActivateAdjacentVertices = Objects.requireNonNull(v).getAdjacentVertices().stream().filter((Vertex vertex) -> !vertex.isActivate()).toList();
            for (var adjacentVertex : nonActivateAdjacentVertices) {
                var n = new Random().nextInt(precision);
                if (n < pp * precision) {
                    adjacentVertex.activate();
                    disseminatorsQueue.offer(adjacentVertex);
                }
            }
        }

        int activateCnt = 0;
        for (var v : vertices) {
            activateCnt += v.isActivate() ? 1 : 0;
        }
        return activateCnt;
    }

    public ICModel clean() {
        for (var vertex : vertices) {
            vertex.deactivate();
        }
        for (var vertex : initiallyVertices) {
            vertex.activate();
        }
        return this;
    }

    public static void main(String[] args) throws IOException {
        var m = new ICModel().initICModelFromTxt().setInitiallyInfectedVerticesRandomly();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(m.clean().spread());
//        }
    }
}



