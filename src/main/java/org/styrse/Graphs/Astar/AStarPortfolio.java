package org.styrse.Graphs.Astar;

import org.styrse.Graphs.Dijkstra.SimpleStaticServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class AStarPortfolio {
  private static final int SIZE = 8;
  private static final boolean USE_JS_VISUALIZATION = true;

  public static void main(String[] args) {
    GridMap map = buildMap();
    String start = "Østby";
    String goal = "Sydby";
    boolean stepMode = true;
    boolean useJs = USE_JS_VISUALIZATION;

    for (String arg : args) {
      if ("auto".equalsIgnoreCase(arg)) stepMode = false;
      if ("js".equalsIgnoreCase(arg)) useJs = true;
      if ("console".equalsIgnoreCase(arg)) useJs = false;
    }

    if (useJs) {
      runJsVisualization();
      return;
    }

    Run run = aStar(map, start, goal);

    System.out.println("A* portfolioopgave");
    System.out.println("Grid: 8x8");
    System.out.println("Start: " + start);
    System.out.println("Slut : " + goal);
    System.out.println();
    printLegend();
    playSteps(run.steps(), map, stepMode);

    List<Pos> path = reconstructPath(run.cameFrom(), map.city(start), map.city(goal));
    System.out.println();
    if (path.isEmpty()) {
      System.out.println("Ingen rute fundet.");
      return;
    }

    System.out.println("Korteste vej fundet.");
    System.out.println("Antal skridt: " + (path.size() - 1));
    System.out.println("Rute gennem byer/celler:");
    System.out.println(formatPath(path, map));
    printGrid(map, path, Set.of(), Set.of(), null);
  }

  private static void runJsVisualization() {
    int port = 8081;
    String root = "/Graphs/astar-viz";
    try {
      SimpleStaticServer.start(port, root);
      System.out.println("A* JS-visualisering kører nu.");
      System.out.println("Åbn: http://localhost:" + port + "/");
      System.out.println("Stop: tryk ENTER i denne terminal.");
      new Scanner(System.in).nextLine();
    } catch (Exception e) {
      System.out.println("Kunne ikke starte JS-visualiseringen: " + e.getMessage());
    }
  }

  private static Run aStar(GridMap map, String startName, String goalName) {
    Pos start = map.city(startName);
    Pos goal = map.city(goalName);

    PriorityQueue<Node> open = new PriorityQueue<>(
        Comparator.comparingInt(Node::f).thenComparingInt(Node::h)
    );
    Set<Pos> openSet = new HashSet<>();
    Set<Pos> visited = new HashSet<>();
    Map<Pos, Integer> gScore = new HashMap<>();
    Map<Pos, Pos> cameFrom = new HashMap<>();
    List<Step> steps = new ArrayList<>();

    gScore.put(start, 0);
    open.add(new Node(start, 0, heuristic(start, goal)));
    openSet.add(start);

    int iteration = 0;
    while (!open.isEmpty()) {
      Node node = open.poll();
      Pos current = node.pos();

      if (visited.contains(current)) continue;

      openSet.remove(current);
      visited.add(current);

      List<String> messages = new ArrayList<>();
      if (current.equals(goal)) {
        messages.add(map.label(current) + " er nu nået. A* stopper her.");
        steps.add(copyStep(iteration++, current, visited, openSet, gScore, messages));
        break;
      }

      for (Pos next : map.neighbors(current)) {
        if (visited.contains(next)) continue;

        int newG = gScore.get(current) + 1;
        if (newG < gScore.getOrDefault(next, Integer.MAX_VALUE / 4)) {
          cameFrom.put(next, current);
          gScore.put(next, newG);

          int h = heuristic(next, goal);
          open.add(new Node(next, newG, h));
          openSet.add(next);

          messages.add(
              "Afstanden til " + map.label(next)
                  + " er nu g=" + newG
                  + ", h=" + h
                  + ", f=" + (newG + h)
                  + " via " + map.label(current) + "."
          );
        }
      }

      steps.add(copyStep(iteration++, current, visited, openSet, gScore, messages));
    }

    return new Run(steps, cameFrom);
  }

  private static Step copyStep(
      int iteration,
      Pos current,
      Set<Pos> visited,
      Set<Pos> openSet,
      Map<Pos, Integer> gScore,
      List<String> messages
  ) {
    return new Step(
        iteration,
        current,
        new HashSet<>(visited),
        new HashSet<>(openSet),
        new HashMap<>(gScore),
        messages
    );
  }

  private static void playSteps(List<Step> steps, GridMap map, boolean stepMode) {
    Scanner scanner = stepMode ? new Scanner(System.in) : null;

    for (Step step : steps) {
      printStep(step, map);

      if (!stepMode) {
        System.out.println();
        continue;
      }

      System.out.print("Tryk y for næste trin: ");
      if (!scanner.hasNextLine() || !scanner.nextLine().trim().equalsIgnoreCase("y")) {
        break;
      }
      System.out.println();
    }
  }

  private static void printStep(Step step, GridMap map) {
    System.out.println("=== Trin " + step.iteration() + " ===");
    System.out.println("Valgt celle: " + map.label(step.current()));
    System.out.println("Visited: " + formatSet(step.visited(), map));
    System.out.println("Open set: " + formatSet(step.openSet(), map));

    if (step.messages().isEmpty()) {
      System.out.println("Opdateringer: ingen");
    } else {
      System.out.println("Opdateringer:");
      for (String message : step.messages()) {
        System.out.println("  - " + message);
      }
    }

    System.out.println("Grid:");
    printGrid(map, List.of(), step.visited(), step.openSet(), step.current());
    System.out.println("g-score tabel:");
    printScoreTable(step.gScore(), map);
  }

  private static void printLegend() {
    System.out.println("Forklaring:");
    System.out.println(".  = tom celle");
    System.out.println("#  = vej");
    System.out.println("o  = ligger i open set");
    System.out.println("v  = visited");
    System.out.println("@  = den celle A* behandler lige nu");
    System.out.println("*  = endelig korteste vej");
    System.out.println("N  = Nordby (start)");
    System.out.println("S  = Sydby (mål)");
    System.out.println("Ø  = Østby, V = Vestby, H = Havneby, M = Midtby, K = Skovby, B = Bakkeby");
    System.out.println();
  }

  private static void printScoreTable(Map<Pos, Integer> gScore, GridMap map) {
    List<String> names = new ArrayList<>(map.cities.keySet());
    Collections.sort(names);
    for (String name : names) {
      Pos pos = map.city(name);
      String value = gScore.containsKey(pos) ? String.valueOf(gScore.get(pos)) : "INF";
      System.out.printf("  %-8s : %s%n", name, value);
    }
  }

  private static String formatSet(Set<Pos> positions, GridMap map) {
    List<String> labels = new ArrayList<>();
    for (Pos pos : positions) labels.add(map.label(pos));
    Collections.sort(labels);
    return labels.toString();
  }

  private static void printGrid(
      GridMap map,
      List<Pos> path,
      Set<Pos> visited,
      Set<Pos> openSet,
      Pos current
  ) {
    Set<Pos> pathSet = new HashSet<>(path);

    for (int row = 0; row < SIZE; row++) {
      for (int col = 0; col < SIZE; col++) {
        Pos pos = new Pos(row, col);
        System.out.print(symbolFor(map, pos, pathSet, visited, openSet, current) + " ");
      }
      System.out.println();
    }
  }

  private static String symbolFor(
      GridMap map,
      Pos pos,
      Set<Pos> pathSet,
      Set<Pos> visited,
      Set<Pos> openSet,
      Pos current
  ) {
    if (current != null && current.equals(pos)) return "@";
    if (pathSet.contains(pos)) return "*";
    if (map.citySymbols.containsKey(pos)) return map.citySymbols.get(pos);
    if (visited.contains(pos)) return "v";
    if (openSet.contains(pos)) return "o";
    if (map.roads.contains(pos)) return "#";
    return ".";
  }

  private static List<Pos> reconstructPath(Map<Pos, Pos> cameFrom, Pos start, Pos goal) {
    List<Pos> path = new ArrayList<>();
    Pos current = goal;
    path.add(current);

    while (!current.equals(start)) {
      current = cameFrom.get(current);
      if (current == null) return List.of();
      path.add(current);
    }

    Collections.reverse(path);
    return path;
  }

  private static String formatPath(List<Pos> path, GridMap map) {
    List<String> parts = new ArrayList<>();
    for (Pos pos : path) parts.add(map.label(pos));
    return String.join(" -> ", parts);
  }

  private static int heuristic(Pos a, Pos b) {
    return Math.abs(a.row() - b.row()) + Math.abs(a.col() - b.col());
  }

  private static GridMap buildMap() {
    GridMap map = new GridMap();

    int[][] roads = {
        {0, 0}, {1, 0}, {1, 1}, {1, 2}, {2, 2},
        {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5},
        {4, 1}, {4, 2}, {4, 3}, {4, 4},
        {5, 1}, {5, 4},
        {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 6},
        {2, 5}, {1, 5}, {0, 5}, {0, 6}
    };

    for (int[] road : roads) {
      map.roads.add(new Pos(road[0], road[1]));
    }

    map.addCity("Nordby", "N", 0, 0);
    map.addCity("Sydby", "S", 6, 6);
    map.addCity("Østby", "Ø", 0, 6);
    map.addCity("Vestby", "V", 6, 0);
    map.addCity("Havneby", "H", 3, 1);
    map.addCity("Midtby", "M", 3, 3);
    map.addCity("Skovby", "K", 1, 2);
    map.addCity("Bakkeby", "B", 5, 4);

    return map;
  }

  private record Pos(int row, int col) {}

  private record Node(Pos pos, int g, int h) {
    int f() {
      return g + h;
    }
  }

  private record Step(
      int iteration,
      Pos current,
      Set<Pos> visited,
      Set<Pos> openSet,
      Map<Pos, Integer> gScore,
      List<String> messages
  ) {}

  private record Run(List<Step> steps, Map<Pos, Pos> cameFrom) {}

  private static class GridMap {
    private final Set<Pos> roads = new HashSet<>();
    private final Map<String, Pos> cities = new HashMap<>();
    private final Map<Pos, String> cityNames = new HashMap<>();
    private final Map<Pos, String> citySymbols = new HashMap<>();

    void addCity(String name, String symbol, int row, int col) {
      Pos pos = new Pos(row, col);
      roads.add(pos);
      cities.put(name, pos);
      cityNames.put(pos, name);
      citySymbols.put(pos, symbol);
    }

    Pos city(String name) {
      return cities.get(name);
    }

    String label(Pos pos) {
      return cityNames.getOrDefault(pos, "(" + pos.row() + "," + pos.col() + ")");
    }

    List<Pos> neighbors(Pos pos) {
      List<Pos> neighbors = new ArrayList<>();
      int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

      for (int[] direction : directions) {
        int nextRow = pos.row() + direction[0];
        int nextCol = pos.col() + direction[1];
        Pos next = new Pos(nextRow, nextCol);

        if (nextRow >= 0 && nextRow < SIZE && nextCol >= 0 && nextCol < SIZE && roads.contains(next)) {
          neighbors.add(next);
        }
      }

      return neighbors;
    }
  }
}
