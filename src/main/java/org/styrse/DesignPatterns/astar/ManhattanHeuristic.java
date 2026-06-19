package org.styrse.DesignPatterns.astar;

public class ManhattanHeuristic implements Heuristic {
    @Override
    public int estimate(MazeNode node, MazeNode destination) {
        return Math.abs(destination.getRow() - node.getRow())
                + Math.abs(destination.getCol() - node.getCol());
    }
}
