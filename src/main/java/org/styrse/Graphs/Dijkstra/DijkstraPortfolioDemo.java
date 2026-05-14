package org.styrse.Graphs.Dijkstra;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DijkstraPortfolioDemo {
  // Sæt til true for at køre den mere visuelle JavaScript/SVG-visualisering.
  // Sæt til false for at køre konsol-visualisering (trin-for-trin).
  private static final boolean USE_JS_VISUALIZATION = true;

  public static void main(String[] args) {
    // Overstyr boolean med argumenter:
    // - "js"      => start JS visualisering
    // - "console" => kør konsol visualisering
    boolean useJs = USE_JS_VISUALIZATION;
    for (String arg : args) {
      if (arg.equalsIgnoreCase("js")) useJs = true;
      if (arg.equalsIgnoreCase("console")) useJs = false;
    }

    if (useJs) {
      runJsVisualization();
      return;
    }

    CityGraph graph = buildRoadNetwork();

    String start = "Skovby";
    String goal = "Kirkeby";

    boolean stepMode = true;
    for (String arg : args) {
      if (arg.equalsIgnoreCase("auto")) stepMode = false;
    }

    Dijkstra.Run run = Dijkstra.runWithSteps(graph, start, goal);

    System.out.println("Dijkstra (rettet, vægtet vejnet)");
    System.out.println("Start: " + start);
    System.out.println("Slut : " + goal);
    System.out.println();
    printLegend();

    if (stepMode) {
      runStepByStep(run.steps, graph);
    } else {
      runAuto(run.steps, graph);
    }

    System.out.println();
    List<String> path = run.result.path(start, goal);
    int dist = run.result.dist().getOrDefault(goal, Dijkstra.INF);
    if (path.isEmpty() || dist >= Dijkstra.INF) {
      System.out.println("Ingen rute fundet fra " + start + " til " + goal + ".");
    } else {
      System.out.println("Korteste rute: " + String.join(" -> ", path));
      System.out.println("Total afstand: " + dist + " km");
    }
  }

  private static void runJsVisualization() {
    int port = 8080;
    String root = "/Graphs/dijkstra-viz";
    try {
      SimpleStaticServer.start(port, root);
      System.out.println("JS-visualisering kører nu.");
      System.out.println("Åbn: http://localhost:" + port + "/");
      System.out.println("Stop: tryk ENTER i denne terminal.");
      new Scanner(System.in).nextLine();
    } catch (Exception e) {
      System.out.println("Kunne ikke starte webserveren til JS-visualiseringen: " + e.getMessage());
    }
  }

  private static void runStepByStep(List<Dijkstra.Step> steps, CityGraph graph) {
    Scanner scanner = new Scanner(System.in);
    for (Dijkstra.Step step : steps) {
      printStep(step, graph);
      System.out.print("Tryk 'y' for næste trin (eller andet for at stoppe): ");
      if (!scanner.hasNextLine()) break;
      String input = scanner.nextLine().trim();
      if (!input.equalsIgnoreCase("y")) break;
      System.out.println();
    }
  }

  private static void runAuto(List<Dijkstra.Step> steps, CityGraph graph) {
    for (Dijkstra.Step step : steps) {
      printStep(step, graph);
      System.out.println();
    }
  }

  private static void printStep(Dijkstra.Step step, CityGraph graph) {
    System.out.println("=== Trin " + step.iteration() + " ===");
    System.out.println("Valgt (mindste afstand i køen): " + step.selectedCity());
    System.out.println("Visited: " + step.visitedAfter().stream().sorted().toList());

    if (step.relaxMessages().isEmpty()) {
      System.out.println("Relax: (ingen forbedringer)");
    } else {
      System.out.println("Relax:");
      for (String msg : step.relaxMessages()) System.out.println("  - " + msg);
    }

    System.out.println("Kø (snapshot): " + step.queueSnapshot());
    System.out.println("Distance-tabel:");
    printDistanceTable(step.distAfter(), graph);
  }

  private static void printDistanceTable(Map<String, Integer> dist, CityGraph graph) {
    List<String> cities = graph.adjacency().keySet().stream().sorted(Comparator.naturalOrder()).toList();
    for (String city : cities) {
      int d = dist.getOrDefault(city, Dijkstra.INF);
      String value = (d >= Dijkstra.INF) ? "INF" : (d + " km");
      System.out.printf("  %-10s : %s%n", city, value);
    }
  }

  private static void printLegend() {
    System.out.println("Visualisering (konsol):");
    System.out.println("- Hvert trin vælger den by med mindst kendte afstand (PriorityQueue).");
    System.out.println("- 'Relax' viser hvilke afstande der blev forbedret via den valgte by.");
    System.out.println("- 'Visited' viser hvilke byer der er færdigbehandlet.");
    System.out.println("- Distance-tabel viser bedste kendte afstande (INF = ukendt).");
    System.out.println();
  }

  private static CityGraph buildRoadNetwork() {
    CityGraph g = new CityGraph();

    // Mindst 8 byer, rettede veje med km-vægte.
    g.addDirectedRoad("Skovby", "Lilleby", 7);
    g.addDirectedRoad("Skovby", "Engby", 9);
    g.addDirectedRoad("Skovby", "Havneby", 14);

    g.addDirectedRoad("Lilleby", "Bakkeby", 10);
    g.addDirectedRoad("Lilleby", "Engby", 2);

    g.addDirectedRoad("Engby", "Mølleby", 11);
    g.addDirectedRoad("Engby", "Bakkeby", 6);

    g.addDirectedRoad("Bakkeby", "Kirkeby", 9);
    g.addDirectedRoad("Bakkeby", "Søby", 3);

    g.addDirectedRoad("Søby", "Kirkeby", 4);
    g.addDirectedRoad("Mølleby", "Søby", 5);

    g.addDirectedRoad("Havneby", "Mølleby", 2);
    g.addDirectedRoad("Havneby", "Kirkeby", 20);

    return g;
  }
}
