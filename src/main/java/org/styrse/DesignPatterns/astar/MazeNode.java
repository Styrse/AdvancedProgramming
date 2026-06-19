package org.styrse.DesignPatterns.astar;

import java.util.ArrayList;
import java.util.List;

public class MazeNode {
    private int row;
    private int col;
    private List<MazeNode> neighbors;

    public MazeNode(int row, int col) {
        this.row = row;
        this.col = col;
        this.neighbors = new ArrayList<>();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public List<MazeNode> getNeighbors() { return neighbors; }

    public void addNeighbor(MazeNode neighbor) {
        neighbors.add(neighbor);
    }
}
