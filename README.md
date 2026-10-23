# Orchestrator AI

Aplicație web în care mai multe modele AI **gratuite** (prin OpenRouter) colaborează:

1. **Răspuns**: un model scrie răspunsul inițial.
2. **Verificare**: alt model îl critică și dă un verdict (`OK` / `CORECTURI`).
3. **Sinteză**: dacă verdictul cere corecturi, un al treilea model scrie răspunsul final. Dacă verdictul e `OK`, pasul se omite (economisești o cerere).

Există și modul **Rapid** (un singur model). Modelele sunt alese automat dintre cele mai mari modele gratuite disponibile în acel moment (de la furnizori diferiți), iar dacă unul e supraîncărcat, aplicația trece singură la următorul.

## Ce trebuie să știi despre „gratuit”

- Orice program care folosește modele AI are nevoie de o **cheie API**. Pe OpenRouter cheia se creează gratuit, fără card, în 1 minut: https://openrouter.ai/keys
- Modelele cu sufixul `:free` costă 0, dar au limite **per cont**: 20 cereri/minut și **50 cereri/zi** (1000/zi dacă ai cumpărat vreodată 10 USD credit). Sursa: https://openrouter.ai/docs/api-reference/limits
- Un flux „Verificat” folosește 2–3 cereri, deci cheia partajată a serverului acoperă ~15–20 de întrebări pe zi pentru toți vizitatorii la un loc.
- De aceea fiecare vizitator își poate introduce **propria cheie gratuită** în Setări (stocată doar în browserul lui). Atunci are limita lui, separat de a ta.
- Modelele disponibile gratuit sunt în special open-source (Llama, Gemma, Qwen, DeepSeek, GPT-OSS etc.). Claude, GPT și Perplexity propriu-zise sunt plătite; le poți scrie manual în Setări ca ID OpenRouter dacă ai credit.

## Rulare locală

```bash
cp .env.example .env        # pune cheia ta în OPENROUTER_API_KEY
export $(grep -v '^#' .env | xargs)
npm start                   # http://localhost:3000
```

Necesită Node.js 20+. Nu are dependențe de instalat.

## Publicare gratuită pe Render (acces pentru oricine)

1. Creează un repository pe GitHub și urcă aici toate fișierele (fără `.env`).
2. Pe https://render.com: **New → Web Service** → alege repository-ul. Render citește `render.yaml` (plan Free).
3. La **Environment** adaugă `OPENROUTER_API_KEY` cu cheia ta. Opțional `SITE_URL` cu adresa aplicației.
4. Deploy. Primești o adresă `https://ceva.onrender.com` pe care o poate deschide oricine.

Notă: pe planul gratuit Render serviciul „adoarme” după ~15 minute fără trafic, iar prima deschidere durează ~30–60 secunde.

## Protecții incluse

- Cheia serverului nu ajunge niciodată în browser.
- Plafon zilnic global (`DAILY_CALL_CAP`, implicit 45) ca să nu consumi tot cota contului.
- Limită de întrebări pe IP pe cheia partajată (`QUESTIONS_PER_IP_PER_DAY`, implicit 5), cu efect de corectitudine, nu de securitate absolută.
- Cu cheia proprie a vizitatorului, plafoanele serverului nu se aplică.
- Contoarele sunt în memorie și se resetează la repornire și la miezul nopții UTC.

## Structură

- `server.js`: serverul (API, orchestrare, SSE, limite)
- `public/index.html`: interfața (HTML + CSS + JS într-un singur fișier)
- `render.yaml`: configurare deploy Render
- `.env.example`: variabile de mediu

## Idei de continuare

Căutare web pentru răspunsuri actuale (Perplexity Sonar necesită credit), încărcare de fișiere, conturi de utilizator cu cote individuale, istoric salvat pe server.
