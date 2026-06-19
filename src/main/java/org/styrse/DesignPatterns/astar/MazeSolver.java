package org.styrse.DesignPatterns.astar;

import java.util.*;

public class MazeSolver {

    // 0 = åben, 1 = mur
    static int[][] grid = {
            {0,0,0,1,0,0,0,1,0,0,0,0},
            {1,1,0,1,0,1,0,1,0,1,1,0},
            {0,0,0,0,0,1,0,0,0,0,1,0},
            {0,1,1,1,1,1,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,1,0,0,0},
            {1,1,1,0,1,1,1,0,1,1,0,1},
            {0,0,0,0,0,0,1,0,0,0,0,0},
            {0,1,1,1,1,0,1,1,1,1,1,0},
            {0,0,0,0,1,0,0,0,0,0,1,0},
            {1,1,0,1,1,0,1,1,0,1,1,0},
            {0,0,0,0,0,0,1,0,0,0,1,0},
            {0,1,1,1,0,1,1,0,1,1,0,0},
    };

    static final int ROWS = 12, COLS = 12;
    private final Heuristic heuristic;

    public MazeSolver(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public static void main(String[] args) {
        // Byg alle noder
        MazeNode[][] nodes = new MazeNode[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] == 0) {
                    nodes[r][c] = new MazeNode(r, c);
                }
            }
        }

        // Forbind naboer — urettede kanter i alle 4 retninger
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (nodes[r][c] == null) continue;
                for (int[] d : directions) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && nodes[nr][nc] != null) {
                        nodes[r][c].addNeighbor(nodes[nr][nc]);
                    }
                }
            }
        }

        MazeNode source = nodes[0][0];
        MazeNode destination = nodes[11][11];

        MazeSolver manhattanSolver = new MazeSolver(new ManhattanHeuristic());
        System.out.println("=== Manhattan Heuristic ===");
        manhattanSolver.findShortestPath(source, destination);

        MazeSolver euclideanSolver = new MazeSolver(new EuclideanHeuristic());
        System.out.println("\n=== Euclidean Heuristic ===");
        euclideanSolver.findShortestPath(source, destination);

        MazeSolver zeroSolver = new MazeSolver(new ZeroHeuristic());
        System.out.println("\n=== Zero Heuristic (Dijkstra) ===");
        zeroSolver.findShortestPath(source, destination);
    }



    public void findShortestPath(MazeNode source, MazeNode destination) {
        Map<MazeNode, MazeNode> prev = new HashMap<>();
        Map<MazeNode, Integer> dist = new HashMap<>();
        Set<MazeNode> visited = new HashSet<>();

        PriorityQueue<NodeWithDist> queue = new PriorityQueue<>();
        queue.add(new NodeWithDist(source, 0, heuristic.estimate(source, destination)));
        dist.put(source, 0);

        while (!queue.isEmpty()) {
            NodeWithDist current = queue.poll();

            if (current.node.equals(destination)) break;
            if (visited.contains(current.node)) continue;
            visited.add(current.node);

            for (MazeNode next : current.node.getNeighbors()) {
                if (visited.contains(next)) continue;

                // Alle skridt koster 1 i en labyrint
                int newDist = current.gCost + 1;

                if (newDist < dist.getOrDefault(next, Integer.MAX_VALUE)) {
                    dist.put(next, newDist);
                    prev.put(next, current.node);
                    queue.add(new NodeWithDist(next, newDist, heuristic.estimate(next, destination)));
                }
            }
        }

        // Rekonstruer stien via prev
        List<String> path = new ArrayList<>();
        MazeNode step = destination;
        while (step != null) {
            path.add(0, "(" + step.getRow() + "," + step.getCol() + ")");
            step = prev.get(step);
        }

        System.out.println("Korteste vej: " + path);
        System.out.println("Antal skridt: " + (path.size() - 1));
    }

    private static class NodeWithDist implements Comparable<NodeWithDist> {
        MazeNode node;
        int gCost;
        int fCost;

        public NodeWithDist(MazeNode node, int gCost, int hCost) {
            this.node = node;
            this.gCost = gCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(NodeWithDist other) {
            return Integer.compare(this.fCost, other.fCost);
        }
    }
}
