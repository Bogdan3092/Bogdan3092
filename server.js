// AI Orchestrator — server fără dependențe (Node 18+).
// Rulează un flux: răspuns → verificare → sinteză, folosind modele gratuite OpenRouter.

import http from 'node:http';
import { readFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const PORT = Number(process.env.PORT || 3000);
const SERVER_KEY = (process.env.OPENROUTER_API_KEY || '').trim();
const OR_BASE = process.env.OPENROUTER_BASE || 'https://openrouter.ai/api/v1';
const SITE_URL = process.env.SITE_URL || 'http://localhost';
// Limita zilnică a contului gratuit OpenRouter este 50 cereri pe modele :free.
// Păstrăm o marjă mică, ca să nu blocăm complet contul.
const DAILY_CALL_CAP = Number(process.env.DAILY_CALL_CAP || 45);
const QUESTIONS_PER_IP = Number(process.env.QUESTIONS_PER_IP_PER_DAY || 5);

const usage = { day: '', calls: 0 };
const ipCount = new Map();

const today = () => new Date().toISOString().slice(0, 10);
function rollDay() {
  const d = today();
  if (usage.day !== d) {
    usage.day = d;
    usage.calls = 0;
    ipCount.clear();
  }
}

class HttpError extends Error {
  constructor(status, message, code) {
    super(message);
    this.status = status;
    this.code = code;
  }
}
class FatalError extends Error {}

/* ---------- Modele gratuite ---------- */

let modelCache = { at: 0, list: [] };

function sizeOf(id) {
  const m = /(?:^|[-_/ ])(\d+(?:\.\d+)?)b(?:[-_:]|$)/i.exec(id.replace(/:free$/, ''));
  return m ? Number(m[1]) : null;
}

async function getFreeModels() {
  if (Date.now() - modelCache.at < 10 * 60e3 && modelCache.list.length) return modelCache.list;
  const r = await fetch(OR_BASE + '/models', { signal: AbortSignal.timeout(15000) });
  if (!r.ok) throw new Error('Lista de modele indisponibilă (' + r.status + ')');
  const { data } = await r.json();
  const list = (data || [])
    .filter((m) => {
      const p = m.pricing || {};
      const free = Number(p.prompt) === 0 && Number(p.completion) === 0;
      const out = m.architecture?.output_modalities || ['text'];
      const inn = m.architecture?.input_modalities || ['text'];
      return (
        free &&
        out.length === 1 &&
        out[0] === 'text' &&
        inn.includes('text') &&
        (m.context_length || 0) >= 8000 &&
        !/guard|embed|moderation/i.test(m.id)
      );
    })
    .map((m) => ({
      id: m.id,
      name: m.name || m.id,
      ctx: m.context_length || 0,
      created: m.created || 0,
      provider: m.id.split('/')[0],
      router: m.id.startsWith('openrouter/'),
      score: m.id.startsWith('openrouter/') ? -1 : (sizeOf(m.id) ?? 25),
    }))
    .sort((a, b) => b.score - a.score || b.created - a.created);
  modelCache = { at: Date.now(), list };
  return list;
}

function pickDefaults(list) {
  const real = list.filter((m) => !m.router);
  const picked = [];
  const seen = new Set();
  for (const m of real) {
    if (!seen.has(m.provider)) {
      seen.add(m.provider);
      picked.push(m.id);
    }
    if (picked.length === 3) break;
  }
  for (const m of real) {
    if (picked.length >= 3) break;
    if (!picked.includes(m.id)) picked.push(m.id);
  }
  const fallback = 'openrouter/free';
  const [best, second, third] = [picked[0] || fallback, picked[1] || picked[0] || fallback, picked[2] || picked[0] || fallback];
  // cel mai puternic model face sinteza finală
  return { answer: second, verify: third, synth: best };
}

/* ---------- Apel OpenRouter cu fallback ---------- */

async function callModel({ model, messages, key, fallbacks, signal, maxTokens = 1500 }) {
  const candidates = [model, ...fallbacks.filter((m) => m !== model)].slice(0, 4);
  let lastErr = 'niciun model disponibil';

  for (const id of candidates) {
    if (signal.aborted) throw new Error('Anulat');

    if (key.isServer) {
      rollDay();
      if (usage.calls >= DAILY_CALL_CAP) {
        throw new FatalError('Limita zilnică a serverului a fost atinsă. Introdu propria cheie OpenRouter (gratuită) din Setări sau revino mâine.');
      }
      usage.calls++;
    }

    try {
      const r = await fetch(OR_BASE + '/chat/completions', {
        method: 'POST',
        headers: {
          Authorization: 'Bearer ' + key.value,
          'Content-Type': 'application/json',
          'HTTP-Referer': SITE_URL,
          'X-Title': 'AI Orchestrator',
        },
        body: JSON.stringify({ model: id, messages, temperature: 0.4, max_tokens: maxTokens }),
        signal: AbortSignal.any([signal, AbortSignal.timeout(90000)]),
      });
      const raw = await r.text();
      let j = null;
      try {
        j = JSON.parse(raw);
      } catch {}

      if (r.status === 401) throw new FatalError('Cheia API este invalidă sau a fost revocată.');
      if (r.status === 429 && /free-models-per-day|per-day/i.test(raw)) {
        throw new FatalError('Limita zilnică de cereri gratuite OpenRouter a fost atinsă (50/zi fără credit). Reîncearcă după miezul nopții UTC sau folosește altă cheie.');
      }
      if (!r.ok || j?.error) {
        lastErr = (j?.error?.message || 'HTTP ' + r.status).toString().slice(0, 200);
        continue;
      }
      let text = j?.choices?.[0]?.message?.content;
      if (typeof text !== 'string') {
        lastErr = 'răspuns gol de la ' + id;
        continue;
      }
      text = text.replace(/<think>[\s\S]*?<\/think>/gi, '').trim();
      if (!text) {
        lastErr = 'răspuns gol de la ' + id;
        continue;
      }
      return { text, model: id };
    } catch (e) {
      if (e instanceof FatalError) throw e;
      if (signal.aborted) throw new Error('Anulat');
      lastErr = e.name === 'TimeoutError' ? 'timeout la ' + id : e.message;
    }
  }
  throw new Error('Toate modelele încercate au eșuat: ' + lastErr);
}

/* ---------- Prompturi ---------- */

const SYS_BASE =
  'Ești un asistent AI util, precis și onest. Răspunde în limba în care scrie utilizatorul. ' +
  'Nu ai acces la internet și nu poți verifica surse live: dacă nu ești sigur de ceva, spune-o clar și nu inventa date, citate sau linkuri.';

const SYS_VERIFY =
  'Ești un verificator critic. Primești întrebarea unui utilizator și un răspuns propus. ' +
  'Caută erori factuale, calcule greșite, omisiuni importante și raționamente slabe. ' +
  'Fii concis (max 8 puncte). Răspunde în limba întrebării. ' +
  'Încheie OBLIGATORIU cu o ultimă linie exactă: "VERDICT: OK" dacă răspunsul este corect și complet, ' +
  'sau "VERDICT: CORECTURI" dacă are probleme care trebuie reparate.';

const SYS_SYNTH =
  'Ești editorul final. Primești întrebarea, un răspuns-draft și o verificare critică. ' +
  'Scrie răspunsul final complet și corect: aplică corecturile valide, ignoră obiecțiile nefondate, păstrează ce era bun. ' +
  'Scrie direct răspunsul către utilizator, în limba lui, fără să menționezi draftul, verificatorul sau procesul.';

/* ---------- Chat (SSE) ---------- */

function getKey(req) {
  const user = String(req.headers['x-user-key'] || '').trim();
  if (user) {
    if (!/^[\w.-]{10,200}$/.test(user)) throw new HttpError(400, 'Format de cheie invalid.');
    return { value: user, isServer: false };
  }
  if (SERVER_KEY) return { value: SERVER_KEY, isServer: true };
  throw new HttpError(401, 'Serverul nu are o cheie configurată. Adaugă cheia ta OpenRouter gratuită în Setări.', 'need_key');
}

function clientIp(req) {
  return (String(req.headers['x-forwarded-for'] || '').split(',')[0] || '').trim() || req.socket.remoteAddress || 'unknown';
}

function readJson(req, limit) {
  return new Promise((resolve, reject) => {
    let n = 0;
    const chunks = [];
    req.on('data', (c) => {
      n += c.length;
      if (n > limit) {
        reject(new HttpError(413, 'Mesaj prea mare.'));
        req.destroy();
        return;
      }
      chunks.push(c);
    });
    req.on('end', () => {
      try {
        resolve(JSON.parse(Buffer.concat(chunks).toString() || '{}'));
      } catch {
        reject(new HttpError(400, 'JSON invalid.'));
      }
    });
    req.on('error', reject);
  });
}

function sendJson(res, status, obj) {
  res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
  res.end(JSON.stringify(obj));
}

async function handleChat(req, res) {
  const body = await readJson(req, 64 * 1024);
  const message = typeof body.message === 'string' ? body.message.trim() : '';
  if (!message || message.length > 4000) throw new HttpError(400, 'Mesajul trebuie să aibă între 1 și 4000 de caractere.');
  const mode = body.mode === 'quick' ? 'quick' : 'verified';
  const key = getKey(req);

  const history = (Array.isArray(body.history) ? body.history : [])
    .filter((m) => m && (m.role === 'user' || m.role === 'assistant') && typeof m.content === 'string')
    .slice(-8)
    .map((m) => ({ role: m.role, content: m.content.slice(0, 4000) }));

  if (key.isServer) {
    rollDay();
    const needed = mode === 'quick' ? 1 : 3;
    if (usage.calls + needed > DAILY_CALL_CAP) {
      throw new HttpError(429, 'Limita zilnică a serverului gratuit a fost atinsă. Adaugă propria cheie OpenRouter (gratuită) în Setări sau revino mâine.', 'server_cap');
    }
    const ip = clientIp(req);
    const n = ipCount.get(ip) || 0;
    if (n >= QUESTIONS_PER_IP) {
      throw new HttpError(429, `Ai atins limita de ${QUESTIONS_PER_IP} întrebări/zi pe cheia serverului. Adaugă propria cheie gratuită în Setări pentru a continua.`, 'ip_cap');
    }
    ipCount.set(ip, n + 1);
  }

  let list = [];
  try {
    list = await getFreeModels();
  } catch {}
  const defaults = pickDefaults(list);
  const known = new Set(list.map((m) => m.id));
  const pick = (v, d) => (typeof v === 'string' && /^[\w.\-/:]{3,100}$/.test(v) ? v : d);
  const models = {
    answer: pick(body.models?.answer, defaults.answer),
    verify: pick(body.models?.verify, defaults.verify),
    synth: pick(body.models?.synth, defaults.synth),
  };
  const fallbacks = list.map((m) => m.id).filter((id) => known.has(id));
  if (!fallbacks.includes('openrouter/free')) fallbacks.push('openrouter/free');

  res.writeHead(200, {
    'Content-Type': 'text/event-stream; charset=utf-8',
    'Cache-Control': 'no-cache, no-transform',
    Connection: 'keep-alive',
    'X-Accel-Buffering': 'no',
  });
  const send = (o) => {
    if (!res.writableEnded) res.write('data: ' + JSON.stringify(o) + '\n\n');
  };
  const ping = setInterval(() => {
    if (!res.writableEnded) res.write(': ping\n\n');
  }, 15000);
  const ac = new AbortController();
  res.on('close', () => {
    if (!res.writableEnded) ac.abort();
  });

  const ctx = { key, fallbacks, signal: ac.signal };
  const userMsg = { role: 'user', content: message };

  try {
    if (mode === 'quick') {
      send({ type: 'stage', id: 'answer', label: 'Răspuns', status: 'start', model: models.synth });
      const r = await callModel({ ...ctx, model: models.synth, messages: [{ role: 'system', content: SYS_BASE }, ...history, userMsg] });
      send({ type: 'stage', id: 'answer', label: 'Răspuns', status: 'done', model: r.model });
      send({ type: 'final', text: r.text, model: r.model });
    } else {
      send({ type: 'stage', id: 'answer', label: '1 · Răspuns inițial', status: 'start', model: models.answer });
      const draft = await callModel({ ...ctx, model: models.answer, messages: [{ role: 'system', content: SYS_BASE }, ...history, userMsg] });
      send({ type: 'stage', id: 'answer', label: '1 · Răspuns inițial', status: 'done', model: draft.model, text: draft.text });

      send({ type: 'stage', id: 'verify', label: '2 · Verificare', status: 'start', model: models.verify });
      let review;
      try {
        review = await callModel({
          ...ctx,
          model: models.verify,
          maxTokens: 900,
          messages: [
            { role: 'system', content: SYS_VERIFY },
            { role: 'user', content: `ÎNTREBARE:\n${message}\n\nRĂSPUNS PROPUS:\n${draft.text}` },
          ],
        });
      } catch (e) {
        if (e instanceof FatalError || ac.signal.aborted) throw e;
        send({ type: 'stage', id: 'verify', label: '2 · Verificare', status: 'skipped', note: 'Verificarea a eșuat; se returnează răspunsul inițial.' });
        send({ type: 'final', text: draft.text, model: draft.model });
        send({ type: 'done' });
        return;
      }
      send({ type: 'stage', id: 'verify', label: '2 · Verificare', status: 'done', model: review.model, text: review.text });

      const ok = /VERDICT:\s*OK/i.test(review.text) && !/VERDICT:\s*CORECTURI/i.test(review.text);
      if (ok) {
        send({ type: 'stage', id: 'synth', label: '3 · Sinteză', status: 'skipped', note: 'Verificatorul a confirmat răspunsul, sinteza nu a fost necesară.' });
        send({ type: 'final', text: draft.text, model: draft.model });
      } else {
        send({ type: 'stage', id: 'synth', label: '3 · Sinteză', status: 'start', model: models.synth });
        const final = await callModel({
          ...ctx,
          model: models.synth,
          messages: [
            { role: 'system', content: SYS_SYNTH },
            { role: 'user', content: `ÎNTREBARE:\n${message}\n\nDRAFT:\n${draft.text}\n\nVERIFICARE CRITICĂ:\n${review.text}` },
          ],
        });
        send({ type: 'stage', id: 'synth', label: '3 · Sinteză', status: 'done', model: final.model });
        send({ type: 'final', text: final.text, model: final.model });
      }
    }
    send({ type: 'done' });
  } catch (e) {
    send({ type: 'error', message: e.message || 'Eroare necunoscută.' });
  } finally {
    clearInterval(ping);
    res.end();
  }
}

/* ---------- Config + static ---------- */

async function handleConfig(req, res) {
  rollDay();
  let list = [];
  try {
    list = await getFreeModels();
  } catch {}
  sendJson(res, 200, {
    hasServerKey: !!SERVER_KEY,
    dailyCap: DAILY_CALL_CAP,
    remainingServerCalls: Math.max(0, DAILY_CALL_CAP - usage.calls),
    questionsPerIp: QUESTIONS_PER_IP,
    models: list.map((m) => ({ id: m.id, name: m.name, ctx: m.ctx })),
    defaults: pickDefaults(list),
  });
}

let indexHtml = null;
async function serveIndex(res) {
  if (!indexHtml || process.env.NODE_ENV !== 'production') {
    indexHtml = await readFile(path.join(__dirname, 'public', 'index.html'));
  }
  res.writeHead(200, {
    'Content-Type': 'text/html; charset=utf-8',
    'X-Content-Type-Options': 'nosniff',
    'Referrer-Policy': 'no-referrer',
    'X-Frame-Options': 'DENY',
  });
  res.end(indexHtml);
}

const server = http.createServer(async (req, res) => {
  try {
    const url = new URL(req.url, 'http://x');
    if (req.method === 'GET' && (url.pathname === '/' || url.pathname === '/index.html')) return await serveIndex(res);
    if (req.method === 'GET' && url.pathname === '/api/config') return await handleConfig(req, res);
    if (req.method === 'POST' && url.pathname === '/api/chat') return await handleChat(req, res);
    if (url.pathname === '/favicon.ico') {
      res.writeHead(204);
      return res.end();
    }
    if (url.pathname === '/healthz') return sendJson(res, 200, { ok: true });
    sendJson(res, 404, { message: 'Negăsit.' });
  } catch (e) {
    if (res.headersSent) return res.end();
    if (e instanceof HttpError) return sendJson(res, e.status, { message: e.message, code: e.code });
    console.error(e);
    sendJson(res, 500, { message: 'Eroare internă.' });
  }
});

server.listen(PORT, () => {
  console.log(`AI Orchestrator pornit pe http://localhost:${PORT}`);
  console.log(SERVER_KEY ? 'Cheie server: configurată' : 'Cheie server: LIPSĂ (vizitatorii vor folosi propria cheie)');
});
