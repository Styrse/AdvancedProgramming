package org.styrse.Graphs.Dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityGraph {
  public record Edge(String to, int weightKm) {}

  private final Map<String, List<Edge>> adjacency = new HashMap<>();

  public void addCity(String name) {
    adjacency.computeIfAbsent(name, ignored -> new ArrayList<>());
  }

  public void addDirectedRoad(String from, String to, int km) {
    if (km < 0) throw new IllegalArgumentException("Dijkstra kræver ikke-negative vægte.");
    addCity(from);
    addCity(to);
    adjacency.get(from).add(new Edge(to, km));
  }

  public Map<String, List<Edge>> adjacency() {
    return adjacency;
  }
}

