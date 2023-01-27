package icModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ICModel {

//    private static void creatGraphFromEdges(List<int[]> edges) {
//        for (int[] edge :
//                edges) {
//            int from = edge[0];
//            int to = edge[1];
//
//        }
//    }

    public static void main(String[] args) throws IOException {
        final Double pp = 0.1;
        Vertex[] vertices = new Vertex[4039];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vertex();
        }
        Edge[] edges = new Edge[88234];
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

        // initially infected set S,随机选取50个点
        final int initialNum = 50;
        Vertex[] initiallyVertices = new Vertex[initialNum];
        for (int i = 0; i < initialNum; i++) {
            initiallyVertices[i] = vertices[i];
        }
        var random = new Random();
        for (int i = initialNum; i < vertices.length; i++) {
            int replaceIdx = random.nextInt(i); // 0 - i-1 共i个
            //在n中选一个数，数字小于k的概率就是k/m
            if (replaceIdx < initialNum) {
                initiallyVertices[replaceIdx] = vertices[i];
            }
        }

        // spread
        Queue<Vertex> infectedVertex = new ArrayDeque<>(List.of(initiallyVertices));
        System.out.println(infectedVertex.size());

        while (!infectedVertex.isEmpty()) {
            // 一个时间伦次
            for (int i = 0; i < infectedVertex.size(); i++) {
                var v = infectedVertex.poll();
                for (var adjacentVertex : v.getAdjacentVertices()) {
                    if (!adjacentVertex.isActivate()) {
                        var n = random.nextInt(1000);
                        if (n < pp * 1000) {
                            adjacentVertex.activate();
                            infectedVertex.add(adjacentVertex);
                        }
                    }
                }
            }
            System.out.println(infectedVertex.size());

//            int activateCnt = 0;
//            for (var v : vertices) {
//                activateCnt += v.isActivate() ? 1 : 0;
//            }
//            System.out.println(activateCnt);
        }
        int activateCnt = 0;
        for (var v : vertices) {
            activateCnt += v.isActivate() ? 1 : 0;
        }
        System.out.println(activateCnt);

    }
}



