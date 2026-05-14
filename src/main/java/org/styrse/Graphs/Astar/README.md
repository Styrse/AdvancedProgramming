# A* portfolioopgave

Denne mappe indeholder en helt enkel Java-implementering af A* i et `8x8` grid.

Byer i griddet:

- `Nordby` er start
- `Sydby` er mål
- `Østby`
- `Vestby`
- `Havneby`
- `Midtby`
- `Skovby`
- `Bakkeby`

Visualiseringen er i konsollen og viser:

- hvilken celle A* vælger i hvert trin
- hvilke celler der er i `open set`
- hvilke celler der er `visited`
- g-scores for byerne
- den endelige korteste vej

Der er også en enkel JavaScript-visualisering i:

- `src/main/resources/Graphs/astar-viz`

Kør sådan fra projektroden:

```powershell
javac src\main\java\org\styrse\Graphs\Astar\AStarPortfolio.java
java -cp src\main\java org.styrse.Graphs.Astar.AStarPortfolio
```

Hvis du vil køre uden at trykke `y` mellem hvert trin:

```powershell
java -cp src\main\java org.styrse.Graphs.Astar.AStarPortfolio auto
```

Hvis du vil starte JavaScript-visualiseringen:

```powershell
javac src\main\java\org\styrse\Graphs\Dijkstra\SimpleStaticServer.java src\main\java\org\styrse\Graphs\Astar\AStarPortfolio.java
java -cp src\main\java;src\main\resources org.styrse.Graphs.Astar.AStarPortfolio js
```
