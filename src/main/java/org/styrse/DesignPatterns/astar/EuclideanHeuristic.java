package org.styrse.DesignPatterns.astar;

public class EuclideanHeuristic implements Heuristic {
    @Override
    public int estimate(MazeNode node, MazeNode destination) {
        double dx = destination.getRow() - node.getRow();
        double dy = destination.getCol() - node.getCol();
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }
}
