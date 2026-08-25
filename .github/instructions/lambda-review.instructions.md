---
applyTo: "functions/**/*"
---

# Istruzioni per la review delle AWS Lambda

Applica severità, confini di fiducia, fail closed, privacy, test, lingua e
formato dei commenti definiti in `.github/copilot-instructions.md`.

Ogni directory direttamente sotto `functions/` rappresenta una Lambda
indipendente con runtime, dipendenze, test e packaging propri.

Considera eventi, file e risposte esterne come input non attendibili.

## Priorità specifiche

Presta particolare attenzione a:

1. accesso cross-account, cross-environment o cross-tenant;
2. selezione di risorse AWS tramite input esterno;
3. segreti e credenziali;
4. validazione dell'evento;
5. idempotenza e retry;
6. Promise, timeout e concorrenza;
7. dipendenze e packaging.

## Selezione delle risorse

Account, ambiente, tenant, bucket, chiave, ruolo, ARN, distribution ID,
parametro SSM ed endpoint devono provenire da configurazione attendibile o da
una allowlist restrittiva.

Segnala come `[BLOCCANTE][SICUREZZA]`:

- bucket, ruoli, ARN o endpoint arbitrari;
- accesso S3 senza vincoli su bucket e prefisso;
- assunzione di ruoli non previsti;
- lettura di parametri SSM arbitrari;
- URL downstream costruiti da input non validato;
- SSRF o path traversal;
- esposizione di credenziali STS o URL prefirmati;
- segreti nel codice, test o eventi di esempio.

L'accesso alle risorse di un altro tenant, ente o ambiente è
`[BLOCCANTE][AUTORIZZAZIONE]`.

## Validazione dell'evento

Verifica:

- tipo e provenienza dell'evento;
- route o resource riconosciuta;
- parametri obbligatori;
- distinzione tra assente, vuoto e non valido;
- parsing sicuro del body;
- limiti di lunghezza e dimensione;
- validazione di ID, date, coordinate e nomi;
- gestione di query e path parameter null;
- rifiuto degli eventi sconosciuti.

Una validazione mancante è bloccante soltanto se abilita accesso o operazioni
privilegiate.

## Risposte ed errori

Verifica che:

- il formato della risposta sia quello previsto;
- gli status code distinguano validazione, not found, access denied, throttling,
  timeout ed errore interno;
- errori AWS, stack trace ed eventi non siano restituiti;
- `error.message` non esponga informazioni sensibili;
- i catch non ignorino errori o restituiscano successo;
- retry non vengano applicati a validazione o access denied.

## Promise, concorrenza e risorse

Verifica che:

- tutte le Promise necessarie siano attese o restituite;
- la Lambda non termini prima delle operazioni;
- non esistano rejection non gestite;
- `Promise.all` non introduca concorrenza non limitata;
- memoria, timeout e filesystem temporaneo siano limitati;
- lo stato globale mutabile non contamini invocazioni warm.

Il riuso globale di client AWS configurati in modo immutabile è ammesso.

Un'operazione AWS non attesa o concorrenza non limitata è
`[ALTA][AFFIDABILITÀ]` quando può lasciare operazioni incomplete o esaurire
risorse.

## Idempotenza

AWS può ripetere lo stesso evento.

Per operazioni che modificano stato verifica:

- comportamento su duplicati;
- scritture S3 ripetute;
- invalidazioni CloudFront;
- aggiornamenti di file o indici;
- recupero da completamenti parziali;
- retry limitati agli errori transitori.

Non richiedere deduplicazione quando l'operazione è naturalmente idempotente.

## S3, STS, SSM e CloudFront

Per S3 verifica:

- bucket configurato;
- prefisso e chiave validati;
- paginazione delle liste;
- limiti di dimensione e streaming per oggetti grandi;
- assenza di esposizione pubblica;
- gestione di oggetti mancanti e scritture parziali.

Per STS verifica account e ruolo attesi, durata minima e assenza di credenziali
nei log.

Per SSM verifica nome del parametro, separazione degli ambienti e assenza di
valori sensibili nei log.

Per CloudFront verifica distribution ID, path di invalidazione, wildcard,
throttling e ambiente corretto.

La scelta arbitraria di bucket, ruolo, parametro o distribuzione è bloccante.

## Location Service e HTTP

Verifica:

- limiti e validazione delle query;
- encoding;
- endpoint attendibili;
- timeout e throttling;
- limite del numero di risultati;
- gestione di risposte vuote o malformate;
- minimizzazione di indirizzi e dati geografici nei log.

Endpoint arbitrari costruiti da input esterno sono bloccanti.

## CSV, JSON e file

Verifica:

- dimensione massima;
- encoding, header e struttura;
- righe malformate o duplicate;
- caratteri di controllo e formule CSV;
- consumo di memoria;
- output deterministico;
- assenza di contenuti sensibili nei log.

Per file potenzialmente grandi preferisci streaming o limiti espliciti.

## Dipendenze e packaging

Per ogni Lambda verifica:

- coerenza tra `package.json` e lock file;
- dipendenze runtime disponibili dopo `npm prune --production`;
- compatibilità con il runtime Node.js;
- coerenza CommonJS/ESM;
- inclusione dei file necessari a runtime;
- esclusione di test, coverage, `.env`, scanner e artefatti locali;
- assenza di segreti nel package.

Una dipendenza runtime eliminata dal package è
`[ALTA][AFFIDABILITÀ]`.

Prima di segnalare un client AWS nelle `devDependencies`, verifica se runtime o
packaging lo forniscano effettivamente.

## Test Lambda

Mantieni mockate le chiamate AWS e di rete nei test unitari.

Quando pertinente, verifica:

- route valida e sconosciuta;
- parametro assente, vuoto o malformato;
- evento null o parziale;
- body non valido;
- access denied, throttling e timeout;
- risposta AWS vuota;
- evento duplicato;
- paginazione e limiti di dimensione;
- risorsa, tenant o ambiente non autorizzati;
- assenza di segreti nella risposta e nei log.

Quando cambia la selezione di bucket, ruolo, ARN, parametro SSM, endpoint o
tenant, richiedi un test negativo sul confine modificato.

## Focus del commento

Tutti i commenti devono essere scritti in inglese.

Indica sempre:

- handler e operazione;
- input che attiva il problema;
- risorsa o dato interessato;
- impatto concreto;
- correzione minima;
- test negativo utile.

Non produrre commenti generici come:

- `Improve input validation`;
- `Improve error handling`;
- `Add a retry`;
- `Consider adding a cache`.