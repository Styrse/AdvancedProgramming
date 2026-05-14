const SIZE = 8;
const DEFAULT_START = "Nordby";
const DEFAULT_GOAL = "Sydby";

const roads = new Set([
  "0,0","1,0","1,1","1,2","2,2",
  "3,1","3,2","3,3","3,4","3,5",
  "4,1","4,2","4,3","4,4",
  "5,1","5,4",
  "6,0","6,1","6,2","6,3","6,4","6,5","6,6",
  "2,5","1,5","0,5","0,6"
]);

const cities = new Map([
  ["Nordby", { row: 0, col: 0, symbol: "N" }],
  ["Sydby", { row: 6, col: 6, symbol: "S" }],
  ["Østby", { row: 0, col: 6, symbol: "Ø" }],
  ["Vestby", { row: 6, col: 0, symbol: "V" }],
  ["Havneby", { row: 3, col: 1, symbol: "H" }],
  ["Midtby", { row: 3, col: 3, symbol: "M" }],
  ["Skovby", { row: 1, col: 2, symbol: "K" }],
  ["Bakkeby", { row: 5, col: 4, symbol: "B" }]
]);

const cityByPos = new Map([...cities.entries()].map(([name, city]) => [`${city.row},${city.col}`, { name, symbol: city.symbol }]));

const gridEl = document.getElementById("grid");
const stepInfoEl = document.getElementById("stepInfo");
const messageBoxEl = document.getElementById("messageBox");
const startSelectEl = document.getElementById("startSelect");
const goalSelectEl = document.getElementById("goalSelect");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const playBtn = document.getElementById("playBtn");
const resetBtn = document.getElementById("resetBtn");

let run;
let stepIndex = 0;
let timer = null;

buildGrid();
buildSelects();
wireControls();
rerun();
render();

function aStar(startName, goalName) {
  const start = cities.get(startName);
  const goal = cities.get(goalName);
  const open = [{ pos: start, g: 0, h: heuristic(start, goal) }];
  const openSet = new Set([key(start)]);
  const visited = new Set();
  const gScore = new Map([[key(start), 0]]);
  const cameFrom = new Map();
  const steps = [];

  let iteration = 0;
  while (open.length > 0) {
    open.sort((a, b) => (a.g + a.h) - (b.g + b.h) || a.h - b.h);
    const currentNode = open.shift();
    const currentKey = key(currentNode.pos);
    if (visited.has(currentKey)) continue;

    openSet.delete(currentKey);
    visited.add(currentKey);

    const messages = [];
    if (samePos(currentNode.pos, goal)) {
      messages.push(goalName + " er nu nået. A* stopper her.");
      steps.push(copyStep(iteration++, currentNode.pos, visited, openSet, messages));
      break;
    }

    for (const next of neighbors(currentNode.pos)) {
      const nextKey = key(next);
      if (visited.has(nextKey)) continue;

      const newG = gScore.get(currentKey) + 1;
      if (newG < (gScore.get(nextKey) ?? Number.MAX_SAFE_INTEGER)) {
        gScore.set(nextKey, newG);
        cameFrom.set(nextKey, currentKey);

        const h = heuristic(next, goal);
        open.push({ pos: next, g: newG, h });
        openSet.add(nextKey);

        messages.push(`Ny bedste vej til ${label(next)}: g=${newG}, h=${h}, f=${newG + h}.`);
      }
    }

    steps.push(copyStep(iteration++, currentNode.pos, visited, openSet, messages));
  }

  const path = reconstructPath(cameFrom, key(start), key(goal));
  return { steps, path };
}

function buildSelects() {
  const names = [...cities.keys()];

  for (const name of names) {
    const startOption = document.createElement("option");
    startOption.value = name;
    startOption.textContent = name;
    if (name === DEFAULT_START) startOption.selected = true;
    startSelectEl.appendChild(startOption);

    const goalOption = document.createElement("option");
    goalOption.value = name;
    goalOption.textContent = name;
    if (name === DEFAULT_GOAL) goalOption.selected = true;
    goalSelectEl.appendChild(goalOption);
  }
}

function rerun() {
  run = aStar(startSelectEl.value, goalSelectEl.value);
  stepIndex = 0;
}

function copyStep(iteration, current, visited, openSet, messages) {
  return {
    iteration,
    current: key(current),
    visited: new Set(visited),
    openSet: new Set(openSet),
    messages: [...messages]
  };
}

function reconstructPath(cameFrom, startKey, goalKey) {
  const path = [goalKey];
  let current = goalKey;

  while (current !== startKey) {
    current = cameFrom.get(current);
    if (!current) return [];
    path.push(current);
  }

  path.reverse();
  return path;
}

function buildGrid() {
  gridEl.innerHTML = "";
  for (let row = 0; row < SIZE; row++) {
    for (let col = 0; col < SIZE; col++) {
      const cell = document.createElement("div");
      cell.className = "cell";
      cell.dataset.pos = `${row},${col}`;
      gridEl.appendChild(cell);
    }
  }
}

function render() {
  const step = run.steps[stepIndex];
  const showPath = stepIndex === run.steps.length - 1;

  stepInfoEl.textContent = `Trin ${step.iteration} ud af ${run.steps.length - 1}`;
  const currentName = label(fromKey(step.current));
  const lines = [
    `Aktuel celle: ${currentName}`,
    `Start: ${startSelectEl.value}`,
    `Mål: ${goalSelectEl.value}`,
    `Visited: ${formatNames(step.visited)}`,
    `Open set: ${formatNames(step.openSet)}`
  ];
  if (step.messages.length === 0) lines.push("Ingen opdateringer i dette trin.");
  else lines.push(...step.messages);
  if (showPath) lines.push(`Korteste vej: ${run.path.map(labelFromKey).join(" -> ")}`);
  messageBoxEl.textContent = lines.join("\n");

  for (const cell of gridEl.children) {
    const pos = cell.dataset.pos;
    cell.className = "cell";

    if (roads.has(pos)) cell.classList.add("road");
    if (step.openSet.has(pos)) cell.classList.add("open");
    if (step.visited.has(pos)) cell.classList.add("visited");
    if (showPath && run.path.includes(pos)) cell.classList.add("path");
    if (pos === step.current) cell.classList.add("current");

    const city = cityByPos.get(pos);
    cell.textContent = city ? city.symbol : "";
    if (city && !showPath && pos !== step.current) cell.classList.add("city");
    if (city && showPath && run.path.includes(pos)) cell.classList.add("path");
  }
}

function wireControls() {
  startSelectEl.addEventListener("change", () => {
    stopPlay();
    rerun();
    render();
  });

  goalSelectEl.addEventListener("change", () => {
    stopPlay();
    rerun();
    render();
  });

  prevBtn.addEventListener("click", () => {
    stopPlay();
    if (stepIndex > 0) stepIndex--;
    render();
  });

  nextBtn.addEventListener("click", () => {
    stopPlay();
    if (stepIndex < run.steps.length - 1) stepIndex++;
    render();
  });

  resetBtn.addEventListener("click", () => {
    stopPlay();
    stepIndex = 0;
    render();
  });

  playBtn.addEventListener("click", () => {
    if (timer) {
      stopPlay();
      return;
    }

    playBtn.textContent = "Pause";
    timer = setInterval(() => {
      if (stepIndex >= run.steps.length - 1) {
        stopPlay();
        return;
      }
      stepIndex++;
      render();
    }, 800);
  });
}

function stopPlay() {
  if (timer) clearInterval(timer);
  timer = null;
  playBtn.textContent = "Play";
}

function neighbors(pos) {
  const out = [];
  const dirs = [[-1,0],[1,0],[0,-1],[0,1]];

  for (const [dr, dc] of dirs) {
    const next = { row: pos.row + dr, col: pos.col + dc };
    if (next.row >= 0 && next.row < SIZE && next.col >= 0 && next.col < SIZE && roads.has(key(next))) {
      out.push(next);
    }
  }

  return out;
}

function heuristic(a, b) {
  return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
}

function key(pos) {
  return `${pos.row},${pos.col}`;
}

function fromKey(posKey) {
  const [row, col] = posKey.split(",").map(Number);
  return { row, col };
}

function samePos(a, b) {
  return a.row === b.row && a.col === b.col;
}

function label(pos) {
  return labelFromKey(key(pos));
}

function labelFromKey(posKey) {
  return cityByPos.get(posKey)?.name ?? `(${posKey})`;
}

function formatNames(set) {
  return [...set].map(labelFromKey).sort((a, b) => a.localeCompare(b)).join(", ");
}
