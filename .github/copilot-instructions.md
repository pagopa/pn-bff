# Istruzioni globali per la code review di pn-bff

## Contesto

Questo repository contiene il Backend for Frontend della piattaforma SEND.

Il BFF rappresenta un confine di sicurezza tra frontend e microservizi. La
review deve privilegiare autorizzazione, propagazione dell'identità, protezione
dei dati e compatibilità dei contratti.

L'applicazione principale utilizza Java 17, Spring Boot, WebFlux/Reactor,
OpenAPI, MapStruct, Maven e AWS.

Le directory sotto `functions/` contengono funzioni AWS Lambda Node.js
indipendenti.

Applica inoltre le istruzioni path-specific disponibili:

- `.github/instructions/openapi-review.instructions.md`;
- `.github/instructions/openapi-generated-review.instructions.md`;
- `.github/instructions/java-review.instructions.md`;
- `.github/instructions/lambda-review.instructions.md`.

Le regole globali si applicano anche ai file non coperti da instructions
path-specific, in particolare:

- `pom.xml`;
- `codegen/**`;
- `scripts/**`;
- `.github/workflows/**`;
- file di configurazione, build e automazione.

## Ambito della review

Analizza il codice modificato dalla pull request e il comportamento direttamente
interessato.

Segnala esclusivamente problemi concreti, verificabili e correggibili.

Non segnalare:

- preferenze di stile o formattazione;
- refactoring senza beneficio concreto;
- problemi preesistenti non aggravati dalla modifica;
- rischi puramente ipotetici;
- richieste generiche di test o documentazione;
- comportamenti già garantiti dal codice generato o dagli strumenti automatici;
- opportunità di riorganizzazione prive di impatto concreto;
- differenze intenzionali prodotte dal codegen.

Non presentare supposizioni come vulnerabilità confermate.

Se manca il contesto necessario, formula una richiesta di verifica soltanto
quando il rischio è concreto e direttamente collegato alla modifica.

## Priorità

Assegna priorità, nell'ordine, a:

1. accessi non autorizzati, cross-user, cross-role o cross-tenant;
2. esposizione di segreti, credenziali o dati personali;
3. alterazione dell'identità o del contesto di autorizzazione;
4. compromissione della supply chain, della build o della CI/CD;
5. incompatibilità dei contratti API;
6. perdita o corruzione dei dati;
7. errori di affidabilità e regressioni funzionali;
8. test mancanti sul comportamento modificato;
9. problemi concreti di manutenibilità introdotti dalla modifica.

I problemi di manutenibilità hanno priorità inferiore rispetto a sicurezza,
autorizzazione, privacy, supply chain, contratto, affidabilità e correttezza.

Non produrre rilievi di manutenibilità quando la modifica rappresenta soltanto
una preferenza stilistica o quando il beneficio suggerito non è concreto.

## Severità obbligatoria

Ogni commento deve iniziare con una delle seguenti classificazioni:

- `[BLOCCANTE][SICUREZZA]`
- `[BLOCCANTE][AUTORIZZAZIONE]`
- `[ALTA][PRIVACY]`
- `[ALTA][CONTRATTO]`
- `[ALTA][AFFIDABILITÀ]`
- `[ALTA][CORRETTEZZA]`
- `[MEDIA][CORRETTEZZA]`
- `[BASSA][MANUTENIBILITÀ]`

Non utilizzare classificazioni differenti da quelle elencate.

### `[BLOCCANTE][AUTORIZZAZIONE]`

Utilizza questa severità quando la modifica consente concretamente:

- accesso a dati o risorse di un altro soggetto;
- escalation di ruolo o privilegi;
- bypass di ownership, gruppo o delega;
- utilizzo di identità controllate dal chiamante al posto di quelle autenticate;
- perdita o alterazione del contesto di autorizzazione;
- accesso basato sul solo possesso di un identificativo;
- conversione di un accesso negato in una risposta contenente dati protetti.

### `[BLOCCANTE][SICUREZZA]`

Utilizza questa severità quando la modifica consente concretamente:

- esposizione di token, password, segreti o credenziali;
- SSRF, injection o path traversal;
- accesso a risorse AWS non autorizzate;
- accesso cross-account o cross-environment;
- esecuzione di operazioni privilegiate tramite input non attendibile;
- esposizione pubblica involontaria di funzionalità interne;
- aggiramento di un controllo di sicurezza esistente;
- esecuzione di codice non attendibile con credenziali o permessi privilegiati;
- compromissione concreta della supply chain o della pipeline di rilascio.

Un rilievo bloccante rimane valido anche se:

- il codice compila;
- i test passano;
- il downstream potrebbe effettuare controlli aggiuntivi;
- è necessario conoscere un identificativo valido;
- gateway o frontend potrebbero mitigare il problema.

Non utilizzare severità bloccanti per:

- naming;
- organizzazione;
- leggibilità;
- duplicazione;
- manutenibilità;
- sola assenza di test;
- preferenze implementative.

### `[ALTA][PRIVACY]`

Utilizza questa severità per:

- esposizione non necessaria di dati personali o sensibili;
- logging di body, eventi o payload contenenti dati protetti;
- esposizione di dettagli downstream sensibili;
- violazioni concrete del principio di minimizzazione;
- URL prefirmati con durata o ambito eccessivi.

### `[ALTA][CONTRATTO]`

Utilizza questa severità per:

- incompatibilità API non gestite;
- rimozione o modifica incompatibile di operazioni, campi, enum o parametri;
- `$ref` non validi;
- artefatti generati non riproducibili;
- divergenze contrattuali concrete;
- modifiche che impediscono o alterano involontariamente la generazione;
- modifiche manuali destinate a essere perse alla rigenerazione.

### `[ALTA][AFFIDABILITÀ]`

Utilizza questa severità per:

- indisponibilità plausibile del servizio;
- build o deployment non riproducibili;
- chiamate bloccanti nel percorso reattivo;
- retry non limitati o applicati a operazioni non idempotenti;
- operazioni asincrone che possono rimanere incomplete;
- perdita sistematica degli errori;
- crescita non limitata di memoria, thread, connessioni o chiamate downstream;
- regressioni prestazionali rilevanti su percorsi frequenti;
- concorrenza non limitata;
- pipeline CI/CD che può pubblicare artefatti incompleti o non verificati.

### `[ALTA][CORRETTEZZA]`

Utilizza questa severità per:

- regressioni funzionali riproducibili sulle funzionalità principali;
- perdita, alterazione o mapping errato di dati obbligatori;
- scambi di parametri che producono risultati errati;
- test negativi mancanti quando cambia un confine di autorizzazione;
- errori che compromettono stabilmente il comportamento previsto senza
  costituire un bypass di sicurezza.

### `[MEDIA][CORRETTEZZA]`

Utilizza questa severità per problemi:

- circoscritti;
- riproducibili;
- con impatto funzionale limitato;
- che non compromettono sicurezza, contratto pubblico o funzionalità
  principali;
- che causano inefficienze concrete ma limitate.

### `[BASSA][MANUTENIBILITÀ]`

Utilizza questa severità soltanto quando la modifica introduce un problema
concreto che aumenta significativamente il rischio di errori futuri o rende
ingiustificatamente difficile modificare, verificare o diagnosticare il codice.

Esempi appropriati:

- duplicazione della stessa regola di business in punti destinati a divergere;
- responsabilità collocata nel layer errato e destinata a essere duplicata;
- codice manuale che replica il comportamento del codegen;
- costanti o configurazioni duplicate in modo incoerente;
- logica complessa priva della separazione minima necessaria;
- dipendenze inutilizzate o configurazioni obsolete introdotte dalla PR;
- codice irraggiungibile o strutture non utilizzate introdotte dalla modifica.

Non utilizzare `[BASSA][MANUTENIBILITÀ]` per naming, formattazione, ordine dei
metodi, costrutti equivalenti o richieste generiche di refactoring.

Un rilievo di manutenibilità deve spiegare il rischio concreto di divergenza o
errore e proporre una correzione minima.

## Confini di fiducia

Considera sensibili e non liberamente sostituibili:

- identità e tipo del soggetto;
- gruppi e deleghe;
- identificativi di mittente, destinatario, ente e tenant;
- IUN, document ID, mandate ID e altri identificativi di risorsa;
- token, credenziali, chiavi e URL prefirmati;
- account, ambienti, ruoli e risorse AWS;
- dati relativi a notifiche, documenti, pagamenti e consensi;
- input usati da script e workflow privilegiati;
- riferimenti a dipendenze, action, container e specifiche remote.

Il possesso di un identificativo non costituisce autorizzazione.

Non assumere che frontend, gateway o servizi downstream correggano un contesto
errato prodotto dal BFF.

## Fail closed

Identità, ruolo, gruppo, delega, tenant, ambiente o risorsa mancanti o non validi
devono produrre un errore quando sono necessari all'autorizzazione.

Segnala come bloccante un comportamento che:

- rimuove silenziosamente un vincolo;
- utilizza un valore predefinito più permissivo;
- prosegue senza il contesto richiesto;
- tratta l'assenza del contesto come accesso generale;
- restituisce dati protetti dopo un errore di autorizzazione;
- seleziona un account, ambiente, ruolo o risorsa più privilegiati.

I fallback best-effort sono ammessi soltanto per dati accessori e non devono
ampliare l'accesso alla risorsa principale.

## Privacy e logging

Non registrare o esporre senza necessità:

- token, password, API key o credenziali;
- header di autorizzazione;
- parametri SSM sensibili;
- credenziali temporanee STS;
- body completi di richieste, risposte, eventi o errori;
- documenti o URL prefirmati;
- codici fiscali e identificativi utente;
- payload completi di notifiche, mandati, pagamenti o consensi;
- contenuto dei secret o delle variabili protette della CI/CD.

Preferisci log strutturati con:

- nome dell'operazione;
- esito;
- status code;
- correlation ID;
- trace ID;
- identificativi minimizzati o mascherati.

Applica sempre il principio di minimizzazione dei dati.

## OpenAPI e artefatti generati

Le fonti OpenAPI mantenute manualmente sono:

- `docs/openapi/api-internal-*.yaml`;
- i file sotto `docs/openapi/*-schemas/`;
- `docs/openapi/common-refs.yaml`.

I file seguenti sono artefatti generati:

- `docs/openapi/api-external-*.yaml`;
- `docs/openapi/aws/*.yaml`.

Il codegen genera gli artefatti external e AWS dalle fonti internal elencate in
`codegen/config.json`.

I marker:

- `# NO EXTERNAL` escludono elementi dall'output external;
- `# ONLY EXTERNAL` abilitano elementi soltanto nell'output external.

È previsto che gli header interni di identità siano assenti dai file external e
che la security `bearerAuth` sia abilitata soltanto nell'output external.

Non trattare internal ed external come contratti peer mantenuti manualmente.

Non suggerire modifiche dirette agli artefatti generati. La correzione deve
essere applicata alla sorgente, agli schemi, ai marker o alla configurazione del
codegen, seguita dalla rigenerazione.

Applica le verifiche dettagliate definite nei file path-specific OpenAPI.

## Build Maven e `pom.xml`

Quando una pull request modifica `pom.xml`, verifica:

- compatibilità con Java 17 e con il parent Maven;
- scope corretto delle dipendenze;
- assenza di dipendenze runtime configurate soltanto come test;
- assenza di dipendenze di test incluse nell'artefatto di produzione;
- versioni riproducibili e coerenti;
- compatibilità tra librerie, annotation processor e plugin;
- configurazione di MapStruct e Lombok;
- esclusione del codice generato dalla coverage;
- fasi Maven nelle quali vengono eseguiti generatori e plugin;
- riproducibilità di `clean install`, `test` e `verify`;
- assenza di repository o endpoint non attendibili;
- assenza di credenziali nella configurazione.

Per `openapi-generator-maven-plugin`, verifica inoltre:

- `inputSpec` corretto;
- distinzione tra generazione server e client;
- package API e model coerenti;
- configurazione reattiva;
- compatibilità della versione del generatore;
- sorgenti remote fissate a commit immutabili;
- assenza di branch mobili come `main`, `master` o `develop`;
- aggiornamento coerente di mapper, service, `pnclient` e test;
- assenza di modifiche manuali al codice generato;
- compatibilità degli output con i contratti BFF.

Segnala come `[ALTA][CONTRATTO]` un aggiornamento della specifica downstream che
modifica il client generato senza i corrispondenti adeguamenti applicativi.

Segnala come `[ALTA][AFFIDABILITÀ]` una configurazione Maven che rende la build
non riproducibile o omette dalla build gli artefatti runtime necessari.

Non proporre aggiornamenti di dipendenze non collegati alla pull request.

## Configurazione del codegen

Quando una pull request modifica `codegen/**`, verifica:

- che ogni sorgente elencata esista;
- che `intendedUsage` sia corretto;
- che `servicePath` sia coerente con l'API generata;
- che l'aggiunta o rimozione di una sorgente sia intenzionale;
- che tutti gli artefatti correlati siano rigenerati;
- che il processo distingua correttamente output `WEB` e `PUBLIC`;
- che il risultato sia riproducibile con la versione configurata;
- che non vengano esposti endpoint internal o dati sensibili;
- che gli endpoint protetti mantengano la security prevista;
- che la configurazione non utilizzi input remoti non immutabili.

Se cambia la versione di `pn-codegen`, verifica che:

- l'aggiornamento sia intenzionale;
- gli output siano rigenerati in modo uniforme;
- non vengano persi marker, security, path, response o `$ref`;
- le differenze meccaniche siano coerenti con la nuova versione;
- non siano mescolate modifiche manuali agli artefatti generati.

Una modifica a `codegen/config.json` che espone concretamente un'API protetta
nel contesto errato può essere `[BLOCCANTE][SICUREZZA]` o
`[BLOCCANTE][AUTORIZZAZIONE]`.

## Script di build e generazione

Quando una pull request modifica `scripts/**`, verifica:

- corretta propagazione degli errori;
- terminazione con exit code non zero in caso di fallimento;
- quoting delle variabili e dei path;
- gestione sicura di spazi, glob e valori vuoti;
- assenza di `eval` o costruzione insicura di comandi;
- assenza di command injection tramite argomenti o variabili;
- assenza di credenziali nei comandi o nei log;
- versioni immutabili di immagini, tool e dipendenze;
- uso sicuro di Docker e dei volumi montati;
- riproducibilità locale e in CI;
- assenza di modifiche involontarie a file estranei;
- comportamento coerente sui sistemi supportati.

Per `scripts/generate-code.sh`, verifica in particolare:

- risoluzione corretta di `pagopa.codegen.version`;
- validazione di eventuali override da riga di comando;
- utilizzo intenzionale della versione scelta;
- propagazione del fallimento di Maven o Docker;
- mount limitato alla directory necessaria;
- assenza di privilegi o accessi al Docker daemon non necessari;
- rigenerazione coerente di external e AWS.

Classifica come `[BLOCCANTE][SICUREZZA]` una command injection concretamente
attivabile o l'esecuzione di un'immagine non attendibile con accesso
privilegiato.

Classifica come `[ALTA][AFFIDABILITÀ]` uno script che ignora un fallimento e
produce artefatti parziali considerati validi.

## GitHub Actions e automazione

Quando una pull request modifica `.github/workflows/**`, verifica:

- `permissions` impostati al minimo necessario;
- assenza di permessi `write` non giustificati;
- separazione tra job di verifica e job privilegiati;
- uso sicuro di `pull_request` e `pull_request_target`;
- impossibilità per codice non attendibile di accedere ai secret;
- assenza di checkout ed esecuzione del codice della PR in un contesto
  privilegiato;
- protezione da command injection attraverso branch, tag, title, body, label,
  output e altri valori controllabili dall'utente;
- utilizzo sicuro di `GITHUB_ENV`, `GITHUB_OUTPUT` e shell script;
- action di terze parti fissate a commit SHA immutabili;
- immagini container fissate a versioni o digest affidabili;
- secret passati soltanto agli step necessari;
- assenza di secret in log, output, artifact o cache;
- condizioni corrette per publish, deploy, merge e aggiornamenti automatici;
- concorrenza e cancellazione coerenti;
- timeout definiti per job potenzialmente non limitati;
- caching che non permetta avvelenamento o condivisione tra trust boundary;
- artifact prodotti soltanto dopo build e test riusciti;
- protezione degli ambienti e dei deployment.

Classifica come `[BLOCCANTE][SICUREZZA]`:

- esecuzione del codice di una PR non attendibile con secret o token in scrittura;
- uso vulnerabile di `pull_request_target`;
- interpolazione diretta di input controllabile in comandi shell;
- action non attendibili eseguite con permessi privilegiati;
- esposizione di secret;
- pubblicazione o deployment attivabili da un soggetto non autorizzato.

Classifica come `[ALTA][AFFIDABILITÀ]`:

- workflow che può pubblicare senza completare le verifiche richieste;
- errori ignorati;
- dipendenze non riproducibili;
- artefatti generati con configurazioni differenti;
- workflow che modifica soltanto parte degli output correlati.

## Custom action JavaScript

Per il codice sotto `.github/workflows/actions/**`, verifica inoltre:

- validazione degli input;
- permessi del token GitHub;
- repository, owner, branch e file target corretti;
- assenza di injection in branch, commit message, PR title o body;
- gestione della paginazione delle API GitHub;
- gestione esplicita degli errori e delle risposte parziali;
- idempotenza degli aggiornamenti;
- impossibilità di modificare repository o branch arbitrari;
- coerenza tra `action.yml`, sorgenti JavaScript e dipendenze;
- inclusione nel package dei file runtime necessari;
- aggiornamento coerente di `package.json` e lock file;
- assenza di token e dati sensibili nei log.

Un'automazione che crea branch, commit o pull request deve fallire in modo
chiuso se repository, branch base o autorizzazione non sono quelli previsti.

## Dipendenze e supply chain

Per modifiche a dipendenze, plugin, action, container o tool verifica:

- provenienza attendibile;
- versione immutabile quando supportata;
- compatibilità con runtime e build;
- lock file aggiornato;
- scope minimo necessario;
- assenza di nuove credenziali richieste senza motivazione;
- assenza di script di installazione inattesi;
- impatto sugli artefatti di produzione;
- aggiornamento coerente delle configurazioni correlate.

Non segnalare genericamente che una versione non è la più recente.

Segnala soltanto rischi concreti introdotti dalla versione o dal metodo di
risoluzione scelto.

## Test e verifiche

Richiedi test sul comportamento modificato, scegliendo il livello più vicino
alla modifica.

Quando cambia un confine di autorizzazione, richiedi i casi negativi pertinenti:

- identità mancante o incoerente;
- ruolo non autorizzato;
- gruppo o tenant differente;
- delega mancante, scaduta o appartenente a un altro soggetto;
- accesso a una risorsa appartenente a un altro soggetto;
- identificativo valido ma non autorizzato;
- accesso negato dal downstream;
- assenza di dati sensibili nella risposta di errore.

Per build, codegen, script e workflow richiedi, quando pertinente:

- build Maven riproducibile;
- esecuzione di `test` o `verify`;
- rigenerazione completa degli artefatti OpenAPI;
- working tree pulito dopo una seconda generazione;
- validazione YAML e risoluzione dei `$ref`;
- test degli script sui casi di errore;
- verifica dei permessi del workflow;
- test della custom action con input invalidi e API GitHub in errore;
- conferma che codice non attendibile non riceva secret o token privilegiati.

La sola assenza di un test non è bloccante.

Diventa bloccante soltanto quando la modifica mostra già un bypass concreto.

Non richiedere test per una segnalazione di sola manutenibilità, salvo che la
correzione proposta modifichi anche il comportamento osservabile.

## Requisiti dei commenti

Prima di segnalare un problema identifica:

1. il soggetto o input che può attivarlo;
2. il controllo o comportamento mancante;
3. la risorsa o il dato interessato;
4. l'effetto concreto;
5. la correzione minima.

Per sicurezza e autorizzazione usa uno scenario simile a:

`Un chiamante con ruolo X può utilizzare Y per accedere o modificare Z perché il controllo W è assente, alterato o non propagato.`

Ogni commento deve:

- iniziare con una classificazione ammessa;
- puntare alla più piccola porzione modificata rilevante;
- spiegare il comportamento attuale e quello atteso;
- descrivere un impatto concreto;
- proporre una correzione attuabile;
- indicare un test solo quando utile a prevenire la regressione.

Per un rilievo di manutenibilità, descrivi il rischio di divergenza, errore o
difficoltà di modifica introdotto dalla pull request.

Non produrre commenti generici come:

- “migliorare la validazione”;
- “aggiungere più test”;
- “valutare un refactoring”;
- “semplificare questo codice”;
- “aggiornare le dipendenze”;
- “verificare il workflow”.

Indica sempre comportamento o struttura interessata, scenario, impatto e
correzione.

## Esempi di commenti

Esempio di autorizzazione:

> **[BLOCCANTE][AUTORIZZAZIONE]** Il `mandateId` ricevuto dal controller non
> viene propagato al client downstream. Un destinatario che opera come delegato
> può richiedere il documento tramite un IUN valido senza applicare il vincolo
> del mandato. Propaga `mandateId` attraverso service e `pnclient` e aggiungi un
> test negativo con un mandato appartenente a un altro soggetto.

Esempio di workflow:

> **[BLOCCANTE][SICUREZZA]** Questo job usa `pull_request_target`, esegue il
> codice della PR e dispone di un token con permesso `contents: write`. Un autore
> esterno può modificare lo script eseguito e utilizzare il token del repository.
> Non eseguire il checkout della PR nel contesto privilegiato oppure separa la
> validazione non attendibile dal job che utilizza il token.

Esempio di codegen:

> **[ALTA][CONTRATTO]** La nuova specifica downstream è referenziata tramite il
> branch `develop`, quindi la build può generare client differenti senza
> modifiche al repository. Fissa `inputSpec` a un commit immutabile e rigenera
> gli artefatti correlati.

Esempio di script:

> **[ALTA][AFFIDABILITÀ]** Il comando di generazione ignora il fallimento di
> Docker e prosegue con gli artefatti precedenti, che possono essere pubblicati
> come aggiornati. Propaga l'exit code e interrompi lo script prima degli step
> successivi.

Esempio di manutenibilità:

> **[BASSA][MANUTENIBILITÀ]** Questa modifica duplica nel workflow la versione
> del codegen già dichiarata nel `pom.xml`. I due valori possono divergere nei
> prossimi aggiornamenti. Ricava la versione dalla fonte esistente oppure
> mantieni una sola configurazione canonica.