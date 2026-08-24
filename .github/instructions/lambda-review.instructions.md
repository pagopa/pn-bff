---
applyTo: "functions/**/*"
---

# Istruzioni per la review delle AWS Lambda

Applica severità, fail closed, privacy e formato dei commenti definiti nelle
instructions globali.

Ogni directory direttamente sotto `functions/` rappresenta una Lambda
indipendente con runtime, dipendenze, test e packaging propri.

Considera eventi, file e risposte esterne come input non attendibili.

## Selezione delle risorse

I valori che selezionano account, ambiente, tenant, bucket, chiave, ruolo, ARN,
distribution ID, parametro SSM o endpoint devono provenire da configurazione
attendibile oppure da una allowlist restrittiva.

Segnala come `[BLOCCANTE][SICUREZZA]`:

- bucket, ruoli, ARN o endpoint arbitrari;
- accesso S3 senza vincoli su bucket e prefisso;
- assunzione di ruoli non previsti;
- lettura di parametri SSM arbitrari;
- URL downstream costruiti da input non validato;
- path traversal;
- esposizione di credenziali STS o URL prefirmati;
- segreti nel codice, nei test o negli eventi di esempio.

Segnala come `[BLOCCANTE][AUTORIZZAZIONE]` l'accesso alle risorse di un altro
tenant, ente o ambiente.

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

Una validazione mancante è bloccante soltanto se consente accesso o operazioni
privilegiate.

## Risposte ed errori

Le risposte devono:

- usare il formato previsto;
- restituire status code coerenti;
- non esporre stack trace, evento o dettagli AWS interni;
- non restituire direttamente oggetti `Error`;
- distinguere validazione, not found, access denied, throttling, timeout ed
  errori interni.

Segnala catch vuoti, errori trasformati in successo e uso non filtrato di
`error.message` quando può contenere informazioni sensibili.

## Promise e asincronia

Verifica che:

- tutte le Promise necessarie siano attese o restituite;
- la Lambda non termini prima delle operazioni;
- non esistano rejection non gestite;
- `Promise.all` non introduca concorrenza non limitata;
- non vengano mescolati callback e Promise senza necessità.

Un'operazione AWS non attesa è `[ALTA][AFFIDABILITÀ]` quando può rimanere
incompleta.

## Idempotenza

Per operazioni che modificano stato verifica:

- comportamento su eventi duplicati;
- scritture S3 ripetute;
- invalidazioni CloudFront;
- aggiornamento di file o indici;
- recupero da completamenti parziali;
- retry limitati agli errori transitori.

Non richiedere deduplicazione quando l'operazione è naturalmente idempotente.

## S3

Verifica:

- bucket configurato;
- prefisso e chiave validati;
- paginazione delle operazioni di lista;
- limiti di dimensione;
- streaming per oggetti grandi;
- content type e metadata;
- gestione di oggetti mancanti;
- assenza di esposizione pubblica;
- consistenza delle scritture.

L'accesso a bucket o prefissi arbitrari è bloccante.

## STS, SSM e CloudFront

Per STS verifica account e ruolo attesi, durata delle credenziali e assenza di
credenziali nei log.

Per SSM verifica nome del parametro, decrittazione, separazione degli ambienti e
assenza di valori sensibili nei log.

Per CloudFront verifica distribution ID, path di invalidazione, wildcard e
throttling.

La scelta arbitraria di ruolo, parametro o distribuzione è bloccante.

## Location Service e HTTP

Verifica:

- limiti delle query;
- encoding;
- endpoint attendibili;
- timeout;
- throttling;
- limiti sul numero di risultati;
- risposte vuote o malformate;
- minimizzazione di indirizzi e dati geografici nei log.

Endpoint arbitrari costruiti da input esterno sono bloccanti.

## CSV, JSON e file

Verifica:

- dimensione massima;
- encoding e struttura;
- header e campi richiesti;
- righe malformate;
- duplicati;
- caratteri di controllo;
- formule CSV;
- consumo di memoria;
- output deterministico;
- assenza di contenuti sensibili nei log.

Per file potenzialmente grandi preferisci streaming o limiti espliciti.

## Stato e concorrenza

Il runtime può essere riutilizzato tra invocazioni.

Segnala stato globale mutabile che conserva dati, credenziali o risultati tra
utenti o invocazioni.

Il riuso globale di client AWS configurati in modo immutabile è ammesso.

Verifica inoltre limiti di concorrenza, timeout, memoria e cleanup delle risorse
temporanee.

## Dipendenze e packaging

Per ogni Lambda verifica:

- coerenza tra `package.json` e lock file;
- dipendenze runtime disponibili dopo `npm prune --production`;
- compatibilità con il runtime Node.js;
- coerenza CommonJS/ESM;
- esclusione dal package di test, coverage, `.env`, scanner e artefatti locali;
- assenza di segreti;
- inclusione di tutti i file necessari a runtime.

Una dipendenza necessaria a runtime ma eliminata dal package è
`[ALTA][AFFIDABILITÀ]`.

Prima di commentare una dipendenza AWS in `devDependencies`, verifica se il
runtime o il packaging la fornisce effettivamente.

## Test Lambda

Mantieni mockate le chiamate AWS e di rete nei test unitari.

Quando pertinente verifica:

- route valida e sconosciuta;
- parametro assente, vuoto o malformato;
- evento null o parziale;
- body non valido;
- risposta AWS vuota;
- access denied;
- throttling;
- timeout ed errore inatteso;
- evento duplicato;
- paginazione;
- limite di dimensione;
- risorsa o ambiente non autorizzati;
- assenza di segreti nella risposta e nei log.

Quando cambia la selezione di bucket, chiavi, ruoli, ARN, parametri SSM,
endpoint o tenant, richiedi test negativi sul confine modificato.