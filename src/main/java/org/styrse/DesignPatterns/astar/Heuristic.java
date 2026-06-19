package org.styrse.DesignPatterns.astar;

public interface Heuristic {
    int estimate(MazeNode node, MazeNode destination);
}
