package org.styrse.Graphs.Dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {
  public static final int INF = Integer.MAX_VALUE / 4;

  public record Result(Map<String, Integer> dist, Map<String, String> prev) {
    public List<String> path(String start, String goal) {
      if (!dist.containsKey(goal) || dist.get(goal) >= INF) return List.of();
      List<String> path = new ArrayList<>();
      String cur = goal;
      while (cur != null) {
        path.add(cur);
        if (cur.equals(start)) break;
        cur = prev.get(cur);
      }
      Collections.reverse(path);
      if (path.isEmpty() || !path.get(0).equals(start)) return List.of();
      return path;
    }
  }

  public record Step(
      int iteration,
      String selectedCity,
      Set<String> visitedAfter,
      Map<String, Integer> distAfter,
      List<String> relaxMessages,
      List<String> queueSnapshot
  ) {}

  private record NodeDist(String city, int dist) {}

  public static class Run {
    public final Result result;
    public final List<Step> steps;

    public Run(Result result, List<Step> steps) {
      this.result = result;
      this.steps = steps;
    }
  }

  public static Run runWithSteps(CityGraph graph, String start, String goal) {
    Map<String, Integer> dist = new HashMap<>();
    Map<String, String> prev = new HashMap<>();
    Set<String> visited = new HashSet<>();

    for (String city : graph.adjacency().keySet()) dist.put(city, INF);
    dist.put(start, 0);

    PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingInt(NodeDist::dist));
    pq.add(new NodeDist(start, 0));

    List<Step> steps = new ArrayList<>();
    int iteration = 0;

    while (!pq.isEmpty()) {
      NodeDist current = pq.poll();
      if (visited.contains(current.city)) continue;

      List<String> relaxMessages = new ArrayList<>();
      visited.add(current.city);

      for (CityGraph.Edge edge : graph.adjacency().getOrDefault(current.city, List.of())) {
        if (visited.contains(edge.to())) continue;

        int candidate = dist.get(current.city) + edge.weightKm();
        if (candidate < dist.get(edge.to())) {
          dist.put(edge.to(), candidate);
          prev.put(edge.to(), current.city);
          pq.add(new NodeDist(edge.to(), candidate));
          relaxMessages.add(
              "Afstanden fra " + start + " til " + edge.to() + " er nu " + candidate + " km (via " + current.city + ")."
          );
        }
      }

      steps.add(new Step(
          iteration++,
          current.city,
          new HashSet<>(visited),
          new HashMap<>(dist),
          relaxMessages,
          snapshotQueue(pq)
      ));

      if (current.city.equals(goal)) break;
    }

    return new Run(new Result(dist, prev), steps);
  }

  private static List<String> snapshotQueue(PriorityQueue<NodeDist> pq) {
    List<NodeDist> list = new ArrayList<>(pq);
    list.sort(Comparator.comparingInt(NodeDist::dist).thenComparing(NodeDist::city));
    List<String> out = new ArrayList<>();
    for (NodeDist nd : list) out.add(nd.city + ":" + nd.dist + "km");
    return out;
  }
}

