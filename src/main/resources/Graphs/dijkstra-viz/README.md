# Dijkstra - JS visualisering

Åbn `index.html` i en browser.

Alternativt kan du starte den via Java-demoen:

- Sæt `USE_JS_VISUALIZATION = true` i `src/main/java/org/styrse/Graphs/DijkstraPortfolioDemo.java`
- Kør jar'en og åbn `http://localhost:8080/`

## Hvad du ser (nyttigt for at forstå Dijkstra)

- **Current**: den by algoritmen vælger (mindst kendte distance i køen)
- **Visited**: byer der er "låst" (korteste distance er fundet)
- **Frontier**: byer der ligger i priority queue (kandidater)
- **Relax-kant**: de kanter der gav en forbedring i det aktuelle trin
- **Distance inde i noderne**: bedste kendte distance lige nu (∞ hvis ukendt)
- **Korteste rute**: markeres når målet er nået

## Genveje

- `Space` næste trin
- `Left/Right` forrige/næste
- `R` reset
- `P` play/pause
