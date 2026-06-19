package org.styrse.DesignPatterns.astar;

public class ZeroHeuristic implements Heuristic {
    @Override
    public int estimate(MazeNode node, MazeNode destination) {
        return 0;
    }
}
