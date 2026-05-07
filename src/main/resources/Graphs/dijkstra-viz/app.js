const INF = 1_000_000_000;

const COLORS = {
  unknown: getCss("--unknown"),
  frontier: getCss("--frontier"),
  current: getCss("--current"),
  visited: getCss("--visited"),
  final: getCss("--final"),
  edge: getCss("--edge"),
  edgeStrong: getCss("--edgeStrong"),
  relax: getCss("--relax")
};

const svg = document.getElementById("graphSvg");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const playBtn = document.getElementById("playBtn");
const resetBtn = document.getElementById("resetBtn");
const stepInfo = document.getElementById("stepInfo");
const explanationBox = document.getElementById("explanationBox");
const queueBody = document.querySelector("#queueTable tbody");
const distBody = document.querySelector("#distTable tbody");
const startCityEl = document.getElementById("startCity");
const goalCityEl = document.getElementById("goalCity");

const graph = buildGraph();
const start = "Skovby";
const goal = "Kirkeby";
startCityEl.textContent = start;
goalCityEl.textContent = goal;

const run = dijkstraWithSteps(graph, start, goal);

let stepIndex = 0;
let playing = false;
let playTimer = null;

initSvg();
wireControls();
render();

function buildGraph() {
  const cities = [
    { name: "Skovby", x: 120, y: 120 },
    { name: "Lilleby", x: 290, y: 90 },
    { name: "Engby", x: 300, y: 210 },
    { name: "Havneby", x: 110, y: 310 },
    { name: "Bakkeby", x: 520, y: 180 },
    { name: "Mølleby", x: 360, y: 420 },
    { name: "Søby", x: 610, y: 330 },
    { name: "Kirkeby", x: 780, y: 250 }
  ];

  const edges = [
    { from: "Skovby", to: "Lilleby", w: 7 },
    { from: "Skovby", to: "Engby", w: 9 },
    { from: "Skovby", to: "Havneby", w: 14 },

    { from: "Lilleby", to: "Bakkeby", w: 10 },
    { from: "Lilleby", to: "Engby", w: 2 },

    { from: "Engby", to: "Mølleby", w: 11 },
    { from: "Engby", to: "Bakkeby", w: 6 },

    { from: "Bakkeby", to: "Kirkeby", w: 9 },
    { from: "Bakkeby", to: "Søby", w: 3 },

    { from: "Søby", to: "Kirkeby", w: 4 },
    { from: "Mølleby", to: "Søby", w: 5 },

    { from: "Havneby", to: "Mølleby", w: 2 },
    { from: "Havneby", to: "Kirkeby", w: 20 }
  ];

  const byName = new Map(cities.map(c => [c.name, c]));
  const adj = new Map(cities.map(c => [c.name, []]));
  for (const e of edges) adj.get(e.from).push(e);

  return { cities, edges, adj, byName };
}

function dijkstraWithSteps(graph, start, goal) {
  const dist = new Map(graph.cities.map(c => [c.name, INF]));
  const prev = new Map(graph.cities.map(c => [c.name, null]));
  const visited = new Set();

  dist.set(start, 0);

  const pq = []; // {city, d}
  pqPush(pq, { city: start, d: 0 });

  const steps = [];
  let iteration = 0;

  while (pq.length > 0) {
    const current = pqPopMin(pq);
    if (visited.has(current.city)) continue;

    visited.add(current.city);

    const relaxEdges = [];
    const relaxMessages = [];

    for (const e of graph.adj.get(current.city) || []) {
      if (visited.has(e.to)) continue;
      const candidate = dist.get(current.city) + e.w;
      if (candidate < dist.get(e.to)) {
        dist.set(e.to, candidate);
        prev.set(e.to, current.city);
        pqPush(pq, { city: e.to, d: candidate });
        relaxEdges.push({ from: e.from, to: e.to });
        relaxMessages.push(`Relax: ${e.from} -> ${e.to} gav ny distance ${candidate} km.`);
      }
    }

    steps.push({
      iteration: iteration++,
      current: current.city,
      visited: new Set(visited),
      dist: new Map(dist),
      prev: new Map(prev),
      queue: snapshotPq(pq),
      relaxEdges,
      relaxMessages
    });

    if (current.city === goal) break;
  }

  const finalPath = reconstructPath(prev, start, goal);
  return { steps, finalPath };
}

function reconstructPath(prev, start, goal) {
  const path = [];
  let cur = goal;
  while (cur != null) {
    path.push(cur);
    if (cur === start) break;
    cur = prev.get(cur);
  }
  path.reverse();
  if (path.length === 0 || path[0] !== start) return [];
  return path;
}

function pqPush(pq, item) {
  pq.push(item);
}

function pqPopMin(pq) {
  let bestIdx = 0;
  for (let i = 1; i < pq.length; i++) {
    if (pq[i].d < pq[bestIdx].d) bestIdx = i;
  }
  const [item] = pq.splice(bestIdx, 1);
  return item;
}

function snapshotPq(pq) {
  return pq
    .slice()
    .sort((a, b) => a.d - b.d || a.city.localeCompare(b.city))
    .slice(0, 10);
}

function render() {
  const step = run.steps[stepIndex];
  if (!step) return;

  stepInfo.textContent = `Trin: ${stepIndex} / ${run.steps.length - 1}`;

  renderSvg(step);
  renderQueue(step);
  renderDistTable(step);
  renderExplanation(step);
}

function renderExplanation(step) {
  const lines = [];
  lines.push(`Valgt (mindst kendte distance): ${step.current}`);
  lines.push(`Visited: ${sorted([...step.visited]).join(", ")}`);
  if (step.relaxMessages.length === 0) lines.push("Relax: ingen forbedringer i dette trin.");
  else lines.push(...step.relaxMessages);

  if (step.current === goal) {
    if (run.finalPath.length === 0) lines.push("Mål nået, men ingen rute fundet.");
    else lines.push(`Mål nået. Korteste rute: ${run.finalPath.join(" -> ")}`);
  }

  explanationBox.textContent = lines.join("\n");
}

function renderQueue(step) {
  queueBody.innerHTML = "";
  if (step.queue.length === 0) {
    queueBody.innerHTML = `<tr><td colspan="2" class="muted">Tom kø</td></tr>`;
    return;
  }
  for (const it of step.queue) {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${escapeHtml(it.city)}</td><td>${escapeHtml(prettyDist(it.d))}</td>`;
    queueBody.appendChild(tr);
  }
}

function renderDistTable(step) {
  distBody.innerHTML = "";
  const frontier = new Set(step.queue.map(it => it.city));

  for (const city of sorted(graph.cities.map(c => c.name))) {
    const tr = document.createElement("tr");
    const d = step.dist.get(city);
    const p = step.prev.get(city);
    const visited = step.visited.has(city);

    tr.innerHTML = `
      <td>${escapeHtml(city)}</td>
      <td>${escapeHtml(prettyDist(d))}</td>
      <td>${escapeHtml(p ?? "-")}</td>
      <td>${visited ? "ja" : "nej"}</td>
    `;

    if (city === step.current) tr.style.color = COLORS.current;
    else if (run.finalPath.includes(city) && step.current === goal) tr.style.color = COLORS.final;
    else if (visited) tr.style.color = COLORS.visited;
    else if (frontier.has(city)) tr.style.color = COLORS.frontier;

    distBody.appendChild(tr);
  }
}

function initSvg() {
  svg.innerHTML = "";

  const defs = el("defs");
  defs.appendChild(makeMarker("arrowEdge", COLORS.edge));
  defs.appendChild(makeMarker("arrowStrong", COLORS.edgeStrong));
  defs.appendChild(makeMarker("arrowRelax", COLORS.relax));
  defs.appendChild(makeMarker("arrowFinal", COLORS.final));
  svg.appendChild(defs);

  svg.appendChild(el("g", { id: "edgesLayer" }));
  svg.appendChild(el("g", { id: "nodesLayer" }));
}

function renderSvg(step) {
  const edgesLayer = svg.querySelector("#edgesLayer");
  const nodesLayer = svg.querySelector("#nodesLayer");
  edgesLayer.innerHTML = "";
  nodesLayer.innerHTML = "";

  const frontier = new Set(step.queue.map(it => it.city));
  const relaxSet = new Set(step.relaxEdges.map(e => `${e.from}->${e.to}`));
  const finalSet = new Set();
  if (step.current === goal) {
    for (let i = 0; i < run.finalPath.length - 1; i++) {
      finalSet.add(`${run.finalPath[i]}->${run.finalPath[i + 1]}`);
    }
  }

  // Edges
  for (const e of graph.edges) {
    const a = graph.byName.get(e.from);
    const b = graph.byName.get(e.to);
    const key = `${e.from}->${e.to}`;

    let stroke = COLORS.edge;
    let width = 2.2;
    let marker = "url(#arrowEdge)";
    let opacity = 0.9;

    if (finalSet.has(key)) {
      stroke = COLORS.final;
      width = 4.8;
      marker = "url(#arrowFinal)";
      opacity = 1;
    } else if (relaxSet.has(key)) {
      stroke = COLORS.relax;
      width = 4.0;
      marker = "url(#arrowRelax)";
      opacity = 1;
    } else if (step.visited.has(e.from)) {
      stroke = COLORS.edgeStrong;
      width = 2.6;
      marker = "url(#arrowStrong)";
      opacity = 0.95;
    }

    const { x1, y1, x2, y2 } = shortenLine(a.x, a.y, b.x, b.y, 22);
    const path = el("path", {
      d: `M ${x1} ${y1} L ${x2} ${y2}`,
      stroke,
      "stroke-width": width,
      "marker-end": marker,
      fill: "none",
      opacity
    });
    edgesLayer.appendChild(path);

    const labelPos = edgeLabelPos(a.x, a.y, b.x, b.y, 12);
    const badge = weightBadge(labelPos.x, labelPos.y, e.w);
    edgesLayer.appendChild(badge);
  }

  // Nodes
  for (const c of graph.cities) {
    const d = step.dist.get(c.name);
    const fill = nodeFill(step, frontier, c.name);

    const group = el("g", { transform: `translate(${c.x}, ${c.y})` });
    group.appendChild(el("circle", {
      r: 22,
      fill,
      stroke: "rgba(0,0,0,0.35)",
      "stroke-width": 2
    }));

    const distText = prettyDistShort(d);
    group.appendChild(el("text", {
      x: 0, y: 4,
      "text-anchor": "middle",
      "font-size": 13,
      "font-family": "ui-monospace, Menlo, Consolas, monospace",
      fill: "rgba(0,0,0,0.75)"
    }, distText));

    const nameText = el("text", {
      x: 0, y: 44,
      "text-anchor": "middle",
      "font-size": 13,
      fill: "rgba(255,255,255,0.92)"
    }, c.name);
    group.appendChild(nameText);

    // small outline when in final path
    if (step.current === goal && run.finalPath.includes(c.name)) {
      group.appendChild(el("circle", {
        r: 26,
        fill: "none",
        stroke: COLORS.final,
        "stroke-width": 3.5,
        opacity: 0.9
      }));
    }

    nodesLayer.appendChild(group);
  }
}

function nodeFill(step, frontier, city) {
  if (step.current === goal && run.finalPath.includes(city)) return COLORS.final;
  if (city === step.current) return COLORS.current;
  if (step.visited.has(city)) return COLORS.visited;
  if (frontier.has(city)) return COLORS.frontier;
  return COLORS.unknown;
}

function weightBadge(x, y, w) {
  const g = el("g", { transform: `translate(${x}, ${y})` });
  g.appendChild(el("rect", {
    x: -14, y: -10, width: 28, height: 18, rx: 6, ry: 6,
    fill: "rgba(0,0,0,0.35)",
    stroke: "rgba(255,255,255,0.14)"
  }));
  g.appendChild(el("text", {
    x: 0, y: 4,
    "text-anchor": "middle",
    "font-size": 12,
    "font-family": "ui-monospace, Menlo, Consolas, monospace",
    fill: "rgba(255,255,255,0.90)"
  }, String(w)));
  return g;
}

function edgeLabelPos(ax, ay, bx, by, offset) {
  const mx = (ax + bx) / 2;
  const my = (ay + by) / 2;
  const dx = bx - ax;
  const dy = by - ay;
  const len = Math.hypot(dx, dy) || 1;
  const nx = -dy / len;
  const ny = dx / len;
  return { x: mx + nx * offset, y: my + ny * offset };
}

function shortenLine(ax, ay, bx, by, r) {
  const dx = bx - ax;
  const dy = by - ay;
  const len = Math.hypot(dx, dy) || 1;
  const ux = dx / len;
  const uy = dy / len;
  return {
    x1: ax + ux * r,
    y1: ay + uy * r,
    x2: bx - ux * r,
    y2: by - uy * r
  };
}

function makeMarker(id, color) {
  const marker = el("marker", {
    id,
    markerWidth: 10,
    markerHeight: 10,
    refX: 9,
    refY: 3,
    orient: "auto",
    markerUnits: "strokeWidth"
  });
  marker.appendChild(el("path", { d: "M0,0 L10,3 L0,6 Z", fill: color }));
  return marker;
}

function wireControls() {
  prevBtn.addEventListener("click", prevStep);
  nextBtn.addEventListener("click", nextStep);
  resetBtn.addEventListener("click", reset);
  playBtn.addEventListener("click", togglePlay);

  window.addEventListener("keydown", (e) => {
    if (e.code === "Space") { e.preventDefault(); nextStep(); }
    else if (e.key === "ArrowLeft") prevStep();
    else if (e.key === "ArrowRight") nextStep();
    else if (e.key === "r" || e.key === "R") reset();
    else if (e.key === "p" || e.key === "P") togglePlay();
  });
}

function nextStep() {
  stopPlay();
  if (stepIndex < run.steps.length - 1) stepIndex++;
  render();
}

function prevStep() {
  stopPlay();
  if (stepIndex > 0) stepIndex--;
  render();
}

function reset() {
  stopPlay();
  stepIndex = 0;
  render();
}

function togglePlay() {
  if (playing) stopPlay();
  else startPlay();
}

function startPlay() {
  if (playing) return;
  playing = true;
  playBtn.textContent = "Pause";
  playTimer = setInterval(() => {
    if (stepIndex >= run.steps.length - 1) {
      stopPlay();
      return;
    }
    stepIndex++;
    render();
  }, 900);
}

function stopPlay() {
  playing = false;
  playBtn.textContent = "Play";
  if (playTimer) clearInterval(playTimer);
  playTimer = null;
}

function prettyDist(d) {
  if (d == null || d >= INF) return "INF";
  return `${d} km`;
}

function prettyDistShort(d) {
  if (d == null || d >= INF) return "∞";
  return String(d);
}

function sorted(arr) {
  return arr.slice().sort((a, b) => a.localeCompare(b));
}

function escapeHtml(s) {
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#039;");
}

function el(name, attrs = {}, text = null) {
  const node = document.createElementNS("http://www.w3.org/2000/svg", name);
  for (const [k, v] of Object.entries(attrs)) node.setAttribute(k, String(v));
  if (text != null) node.textContent = text;
  return node;
}

function getCss(name) {
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}

