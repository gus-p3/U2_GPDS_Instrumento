import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';

const BASE_URL = __ENV.BASE_URL || 'https://gestor-api-bgma.up.railway.app';
const PATHS = (__ENV.API_PATHS || '/actuator/health').split(',');
const PROFILE = __ENV.PROFILE || 'smoke';

const profiles = {
  smoke: { executor: 'constant-vus', vus: 1, duration: '30s' },
  load: {
    executor: 'ramping-vus', startVUs: 0, gracefulRampDown: '15s',
    stages: [
      { duration: '30s', target: 10 },
      { duration: '2m', target: 10 },
      { duration: '30s', target: 20 },
      { duration: '1m', target: 20 },
      { duration: '30s', target: 0 },
    ],
  },
  stress: {
    executor: 'ramping-vus', startVUs: 0, gracefulRampDown: '15s',
    stages: [
      { duration: '30s', target: 20 },
      { duration: '1m', target: 20 },
      { duration: '30s', target: 35 },
      { duration: '1m', target: 35 },
      { duration: '30s', target: 50 },
      { duration: '1m', target: 50 },
      { duration: '30s', target: 0 },
    ],
  },
};

export const options = {
  scenarios: { [PROFILE]: profiles[PROFILE] },
  thresholds: {
    http_req_failed: [
      { threshold: 'rate<0.01' },
      { threshold: 'rate<0.20', abortOnFail: true, delayAbortEval: '20s' },
    ],
    http_req_duration: ['p(95)<800'],
    checks: ['rate>0.99'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'max'],
};

export default function () {
  const path = PATHS[Math.floor(Math.random() * PATHS.length)];
  const res = http.get(`${BASE_URL}${path}`, { tags: { endpoint: path }, timeout: '10s' });
  check(res, { 'status 200': (r) => r.status === 200 });
  sleep(1);
}

// Funciona con el formato de resumen viejo (values) y el nuevo (plano)
function pick(metric, key) {
  if (!metric) return null;
  const v = metric.values || metric;
  return v[key] === undefined ? null : v[key];
}

export function handleSummary(data) {
  const d = data.metrics.http_req_duration;
  const f = data.metrics.http_req_failed;
  const resumen = {
    perfil: PROFILE,
    base_url: BASE_URL,
    rutas: PATHS,
    fin: new Date().toISOString(),
    duracion_ms: data.state ? data.state.testRunDurationMs : null,
    vus_max: pick(data.metrics.vus_max, 'max'),
    total_requests: pick(data.metrics.http_reqs, 'count'),
    throughput_rps: pick(data.metrics.http_reqs, 'rate'),
    avg_ms: pick(d, 'avg'),
    med_ms: pick(d, 'med'),
    p90_ms: pick(d, 'p(90)'),
    p95_ms: pick(d, 'p(95)'),
    max_ms: pick(d, 'max'),
    error_rate: pick(f, 'rate') !== null ? pick(f, 'rate') : pick(f, 'value'),
  };
  return {
    stdout: textSummary(data, { indent: ' ', enableColors: false }),
    [`perf/results/${PROFILE}-resumen.json`]: JSON.stringify(resumen, null, 2),
    [`perf/results/${PROFILE}-completo.json`]: JSON.stringify(data, null, 2),
  };
}
