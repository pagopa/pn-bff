---
applyTo: "docs/openapi/api-internal-*.yaml,docs/openapi/*-schemas/*.yaml,docs/openapi/common-refs.yaml"
---

# Istruzioni per la review delle sorgenti OpenAPI

Applica severità, confini di fiducia, fail closed, privacy, test, gestione dei dubbi, lingua e
formato dei commenti definiti in `.github/copilot-instructions.md`.

Queste istruzioni riguardano esclusivamente le sorgenti OpenAPI mantenute
manualmente:

- `docs/openapi/api-internal-*.yaml`;
- `docs/openapi/*-schemas/*.yaml`;
- `docs/openapi/common-refs.yaml`.

Gli artefatti `api-external-*.yaml` e `docs/openapi/aws/*.yaml` sono generati e
non devono essere trattati come contratti peer mantenuti manualmente.

## Processo di generazione

Il codegen usa i file internal elencati in `codegen/config.json` per generare
gli artefatti external e AWS.

I marker hanno questa semantica:

- `# NO EXTERNAL`: esclude la riga dall'output external;
- `# ONLY EXTERNAL`: abilita la riga soltanto nell'output external.

Gli header internal di identità e autorizzazione sono normalmente marcati
`# NO EXTERNAL`. Nell'external vengono sostituiti da `bearerAuth`, marcato
`# ONLY EXTERNAL`.

Non segnalare:

- l'assenza nell'external degli header internal;
- la presenza di `bearerAuth` soltanto nell'external;
- differenze intenzionali prodotte dai marker;
- la mancata corrispondenza testuale tra internal ed external.

## Operazioni e compatibilità

Per ogni operazione modificata verifica:

- stabilità e unicità dell'`operationId`;
- compatibilità di path e metodo HTTP;
- header internal necessari alla propagazione dell'identità;
- parametri, request body e response;
- tipi, formati, limiti, default, required e nullabilità;
- status code ed enum;
- paginazione e filtri;
- minimizzazione dei dati;
- coerenza dei marker.

Segnala come `[ALTA][CONTRATTO]` rimozioni o rinomine incompatibili non
versionate di operazioni, parametri, campi, enum, header o response code.

Non segnalare una modifica incompatibile esplicitamente dichiarata se la pull
request gestisce versionamento, migrazione e aggiornamento dei consumer.

## Autorizzazione e marker

Valuta l'autorizzazione sulla sorgente internal e sulla trasformazione prodotta
dal codegen.

Segnala come `[BLOCCANTE][AUTORIZZAZIONE]` una modifica che:

- rimuove un header necessario a identità, ruolo, gruppo o delega;
- elimina gruppi o `mandateId` da un'operazione che li richiede;
- accetta dal body un'identità che sostituisce quella autenticata;
- consente accesso tramite il solo identificativo della risorsa;
- rende esterna un'operazione che deve restare internal;
- rimuove `# ONLY EXTERNAL` e genera un endpoint protetto senza `bearerAuth`;
- rimuove `# NO EXTERNAL` ed espone un parametro internal sensibile;
- altera concretamente ownership o vincoli di autorizzazione.

Per ogni blocco con marker verifica che:

- tutte le righe del blocco siano marcate coerentemente;
- l'output rimanga YAML valido;
- non rimangano contenitori vuoti o elementi senza contenitore;
- `$ref`, request e response necessari non vengano esclusi;
- parametri internal non vengano esposti;
- la security external rimanga presente.

Un marker che produce YAML o riferimenti non validi è
`[ALTA][CONTRATTO]`. Usa una severità bloccante solo con un bypass concreto.

## `$ref` e specifiche remote

Verifica che:

- file, schema o parametro referenziato esistano;
- i percorsi relativi siano corretti;
- il riferimento sia risolvibile dalla build;
- rinomine e spostamenti aggiornino tutti i riferimenti;
- non vengano introdotte definizioni duplicate e divergenti;
- riferimenti remoti usino commit immutabili;
- il modello referenziato sia quello corretto;
- gli schemi necessari all'external non siano esclusi dai marker.

Segnala come `[ALTA][CONTRATTO]` `$ref` non risolvibili, riferimenti a branch
mobili o modifiche che impediscono la generazione.

## Naming dei modelli BFF

I modelli definiti dal BFF devono iniziare con `Bff`.

Esempi corretti:

- `BffNotificationDetail`;
- `BffDocumentDownloadMetadataResponse`;
- `BffPaymentInfo`.

Segnala come `[ALTA][CONTRATTO]`:

- nuovi modelli BFF privi del prefisso;
- rimozione del prefisso da modelli modificati;
- naming incoerente tra modelli equivalenti;
- `$ref` aggiornati solo parzialmente dopo una rinomina.

La regola non si applica ai modelli dei microservizi referenziati direttamente,
che mantengono il nome definito dal servizio proprietario.

Non richiedere rinomine retroattive di modelli non interessati dalla PR.

## Posizione dei modelli

Nei file `api-internal-*.yaml` non devono normalmente essere definite strutture
applicative non banali.

Colloca modelli complessi o riutilizzabili nei file `*-schemas/*.yaml` e
referenziali tramite `$ref`.

Segnala:

- request o response complesse inline;
- modelli complessi sotto `components/schemas` del file internal;
- modelli duplicati in più file;
- concetti condivisi non estratti nello schema del dominio.

È ammessa un'eccezione per un modello locale a una sola operazione, composto da
pochi campi scalari e privo di oggetti annidati, composizioni o collezioni
complesse.

Classifica normalmente la posizione errata come
`[MEDIA][CORRETTEZZA]`; usa `[ALTA][CONTRATTO]` se causa divergenze,
generazione errata o `$ref` non validi.

## Riuso dei modelli dei microservizi

Dove possibile, referenzia il modello canonico del microservizio invece di
definire un modello BFF equivalente.

Suggerisci il riuso soltanto se:

- la semantica coincide;
- campi, required, nullabilità, enum e formati sono compatibili;
- il riferimento è stabile e risolvibile;
- non vengono esposti dati internal o sensibili;
- il BFF non necessita di un contratto indipendente.

Un modello BFF dedicato è appropriato quando il BFF aggrega, filtra, rinomina,
arricchisce o trasforma i dati, espone un sottoinsieme intenzionale oppure deve
evolvere indipendentemente dal downstream.

Non suggerire il riuso se amplia la superficie dati esposta.

Classifica una duplicazione evitabile come `[MEDIA][CORRETTEZZA]`; usa
`[ALTA][CONTRATTO]` quando esiste già una divergenza funzionale o incompatibile.

## Required, nullabilità, composizioni ed enum

Verifica separatamente:

- presenza;
- required;
- possibilità di `null`;
- lista vuota;
- default.

Un default non deve ampliare ruolo, gruppo o accesso.

Per `allOf`, `oneOf` e `anyOf` verifica proprietà richieste, discriminator,
ambiguità e compatibilità con il generatore.

Per le enum verifica aggiunte, rimozioni, compatibilità downstream e gestione
dei valori sconosciuti.

## Dati sensibili e risposte di errore

Verifica che gli schemi espongano soltanto i dati necessari al frontend.

Presta particolare attenzione a:

- identificativi utente;
- dati di destinatari e delegati;
- documenti, pagamenti e consensi;
- URL prefirmati;
- dettagli degli errori downstream;
- token, credenziali o chiavi.

Per gli endpoint protetti verifica che l'output external mantenga
`bearerAuth` e status coerenti per autenticazione e autorizzazione.

Non richiedere gli header internal nell'external.

## Paginazione e accesso alle risorse

Per endpoint di ricerca verifica:

- limiti della page size;
- pagination key;
- date e fusi orari;
- filtri di sender, recipient, gruppo e mandato;
- impossibilità di ampliare involontariamente i risultati.

Per documenti e pagamenti verifica:

- associazione tra identità, IUN, destinatario e mandato;
- validazione di `documentId`, `documentIdx` e `attachmentIdx`;
- impossibilità di accedere a risorse di un altro soggetto;
- metadata di download minimizzati;
- security external generata correttamente.

## Impatto applicativo

Quando cambia un contratto verifica, se pertinente:

- controller;
- service;
- `pnclient`;
- mapper;
- test;
- configurazione OpenAPI nel `pom.xml`;
- `codegen/config.json`;
- marker;
- artefatti external e AWS rigenerati.

Non suggerire modifiche manuali al codice Java generato o agli artefatti
external e AWS.

La correzione deve essere applicata alla sorgente, agli schemi, ai marker o alla
configurazione del codegen.

## Focus del commento

Tutti i commenti devono essere scritti in inglese.

Indica sempre:

- operazione, modello, `$ref` o marker interessato;
- trasformazione attesa;
- impatto concreto;
- sorgente da correggere;
- eventuale rigenerazione necessaria.

Non produrre commenti generici come:

- `Align the internal and external specifications`;
- `Add the authorization headers to the external specification`;
- `Modify the generated file`;
- `Improve this model`.