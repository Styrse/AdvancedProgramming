// Dijkstra Dashboard (Java + JS)
// - Fetches graph + precomputed step timeline from the Java server.
// - SPACE advances one step at a time.

let graph = null; // {startCity, goalCity, cities[], roads[]}
let run = null;   // {finalPath[], steps[]}

let stepIndex = 0;

const canvas = document.getElementById("graphCanvas");
const ctx = canvas.getContext("2d");

const stateTableBody = document.querySelector("#stateTable tbody");
const queueTableBody = document.querySelector("#queueTable tbody");
const explanationBox = document.getElementById("explanationBox");
const stepInfo = document.getElementById("stepInfo");

const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const resetBtn = document.getElementById("resetBtn");

const INF = 1000000000;

const COLORS = {
  unknown: "#aaaaaa",
  discovered: "#ffdc5a",
  current: "#468cff",
  visited: "#5abe78",
  final: "#dc3c3c",
  edge: "#8c8c8c",
  lastEdge: "#ff8c28"
};

function prettyDist(d) {
  if (d >= INF) return "∞";
  return String(d);
}

function cityById(id) {
  return graph.cities[id];
}

function cityIdByName(name) {
  for (const c of graph.cities) {
    if (c.name === name) return c.id;
  }
  return -1;
}

function buildFinalEdgeSet() {
  const edges = new Set();
  const path = run.finalPath || [];
  for (let i = 0; i < path.length - 1; i++) {
    const a = cityById(path[i]).name;
    const b = cityById(path[i + 1]).name;
    edges.add(a + "->" + b);
  }
  return edges;
}

function render() {
  if (!graph || !run) return;

  const step = run.steps[stepIndex];
  stepInfo.textContent = `Step: ${stepIndex} / ${run.steps.length - 1} (${step.action})`;

  renderGraph(step);
  renderTable(step);
  renderQueue(step);
  explanationBox.textContent = step.explanation || "";
}

function renderGraph(step) {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // Background
  ctx.fillStyle = "#fbfbfb";
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  const finalEdges = buildFinalEdgeSet();
  const finalPathSet = new Set(run.finalPath || []);
  const showFinal = step.action === "finish";

  // Roads first
  for (const r of graph.roads) {
    const from = graph.cities[cityIdByName(r.from)];
    const to = graph.cities[cityIdByName(r.to)];
    if (!from || !to) continue;

    let stroke = COLORS.edge;
    let width = 2;

    // Highlight last considered road
    if (step.lastRoad && step.lastRoad.from === r.from && step.lastRoad.to === r.to && step.lastRoad.weight === r.weight) {
      stroke = COLORS.lastEdge;
      width = 4;
    }

    // Highlight final path (only when finish step)
    if (showFinal && finalEdges.has(r.from + "->" + r.to)) {
      stroke = COLORS.final;
      width = 5;
    }

    drawArrow(from.x, from.y, to.x, to.y, stroke, width);
    drawWeight(from.x, from.y, to.x, to.y, r.weight);
  }

  // Cities
  for (const c of graph.cities) {
    const id = c.id;
    const fill = nodeColor(step, id, showFinal, finalPathSet);
    drawNode(c.x, c.y, fill, c.name);
  }
}

function nodeColor(step, cityId, showFinal, finalPathSet) {
  if (showFinal && finalPathSet.has(cityId)) return COLORS.final;
  if (step.currentCityIndex === cityId) return COLORS.current;
  if (step.visited[cityId]) return COLORS.visited;
  if (step.discovered[cityId]) return COLORS.discovered;
  return COLORS.unknown;
}

function drawNode(x, y, fill, label) {
  const r = 20;
  ctx.lineWidth = 2;
  ctx.strokeStyle = "#333";
  ctx.fillStyle = fill;
  ctx.beginPath();
  ctx.arc(x, y, r, 0, Math.PI * 2);
  ctx.fill();
  ctx.stroke();

  ctx.fillStyle = "#111";
  ctx.font = "12px Consolas, monospace";
  ctx.textAlign = "center";
  ctx.textBaseline = "top";
  ctx.fillText(label, x, y + r + 6);
  ctx.textAlign = "start";
  ctx.textBaseline = "alphabetic";
}

function drawArrow(x1, y1, x2, y2, color, width) {
  const r = 20; // node radius for spacing
  const dx = x2 - x1;
  const dy = y2 - y1;
  const len = Math.hypot(dx, dy);
  if (len < 1) return;

  const ux = dx / len;
  const uy = dy / len;

  const sx = x1 + ux * r;
  const sy = y1 + uy * r;
  const ex = x2 - ux * r;
  const ey = y2 - uy * r;

  ctx.strokeStyle = color;
  ctx.lineWidth = width;
  ctx.beginPath();
  ctx.moveTo(sx, sy);
  ctx.lineTo(ex, ey);
  ctx.stroke();

  // Arrow head
  const head = 10;
  const angle = Math.atan2(ey - sy, ex - sx);
  const a1 = angle + Math.PI * 5 / 6;
  const a2 = angle - Math.PI * 5 / 6;

  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.moveTo(ex, ey);
  ctx.lineTo(ex + Math.cos(a1) * head, ey + Math.sin(a1) * head);
  ctx.lineTo(ex + Math.cos(a2) * head, ey + Math.sin(a2) * head);
  ctx.closePath();
  ctx.fill();
}

function drawWeight(x1, y1, x2, y2, w) {
  const mx = (x1 + x2) / 2;
  const my = (y1 + y2) / 2;

  ctx.font = "12px Consolas, monospace";
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";

  // White label background
  ctx.fillStyle = "rgba(255,255,255,0.9)";
  ctx.strokeStyle = "rgba(200,200,200,0.8)";
  ctx.lineWidth = 1;
  roundRect(ctx, mx - 12, my - 22, 24, 16, 4);
  ctx.fill();
  ctx.stroke();

  ctx.fillStyle = "#111";
  ctx.fillText(String(w), mx, my - 14);

  ctx.textAlign = "start";
  ctx.textBaseline = "alphabetic";
}

function roundRect(ctx, x, y, w, h, r) {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.arcTo(x + w, y, x + w, y + h, r);
  ctx.arcTo(x + w, y + h, x, y + h, r);
  ctx.arcTo(x, y + h, x, y, r);
  ctx.arcTo(x, y, x + w, y, r);
  ctx.closePath();
}

function renderTable(step) {
  stateTableBody.innerHTML = "";

  for (const c of graph.cities) {
    const id = c.id;
    const tr = document.createElement("tr");

    const prevId = step.prev[id];
    const prevName = (prevId === -1) ? "-" : cityById(prevId).name;

    const status = (step.currentCityIndex === id)
      ? "current"
      : (step.visited[id] ? "visited" : (step.discovered[id] ? "opdaget" : "ukendt"));

    tr.innerHTML = `
      <td>${escapeHtml(c.name)}</td>
      <td>${escapeHtml(prettyDist(step.dist[id]))}</td>
      <td>${escapeHtml(prevName)}</td>
      <td>${step.visited[id] ? "ja" : "nej"}</td>
      <td>${escapeHtml(status)}</td>
    `;
    stateTableBody.appendChild(tr);
  }
}

function renderQueue(step) {
  queueTableBody.innerHTML = "";
  const items = step.queue || [];

  if (items.length === 0) {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td colspan="2" style="color:#666">Tom kø</td>`;
    queueTableBody.appendChild(tr);
    return;
  }

  const maxRows = 8;
  for (let i = 0; i < items.length && i < maxRows; i++) {
    const it = items[i];
    const tr = document.createElement("tr");
    tr.innerHTML = `<td>${escapeHtml(cityById(it.cityIndex).name)}</td><td>${escapeHtml(prettyDist(it.distance))}</td>`;
    queueTableBody.appendChild(tr);
  }

  if (items.length > maxRows) {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td colspan="2" style="color:#666">... (${items.length - maxRows} flere)</td>`;
    queueTableBody.appendChild(tr);
  }
}

function escapeHtml(s) {
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#039;");
}

function nextStep() {
  if (!run) return;
  if (stepIndex < run.steps.length - 1) stepIndex++;
  render();
}

function prevStep() {
  if (!run) return;
  if (stepIndex > 0) stepIndex--;
  render();
}

function reset() {
  stepIndex = 0;
  render();
}

async function init() {
  const [g, s] = await Promise.all([
    fetch("/api/graph").then(r => r.json()),
    fetch("/api/steps").then(r => r.json())
  ]);
  graph = g;
  run = s;
  stepIndex = 0;

  prevBtn.addEventListener("click", prevStep);
  nextBtn.addEventListener("click", nextStep);
  resetBtn.addEventListener("click", reset);

  window.addEventListener("keydown", (e) => {
    if (e.code === "Space") {
      e.preventDefault();
      nextStep();
    } else if (e.key === "r" || e.key === "R") {
      reset();
    } else if (e.key === "ArrowLeft") {
      prevStep();
    } else if (e.key === "ArrowRight") {
      nextStep();
    }
  });

  render();
}

init().catch(err => {
  explanationBox.textContent = "Kunne ikke hente data fra serveren. Kører Java-serveren på http://localhost:8080/?\n\n" + err;
});

