# Istruzioni globali per la code review di pn-bff

## Contesto

Questo repository contiene il Backend for Frontend della piattaforma SEND.

Il BFF rappresenta un confine di sicurezza tra frontend e microservizi. La
review deve quindi privilegiare autorizzazione, propagazione dell'identità,
protezione dei dati e compatibilità dei contratti.

L'applicazione principale utilizza Java 17, Spring Boot, WebFlux/Reactor,
OpenAPI, MapStruct e AWS. Le directory sotto `functions/` contengono funzioni
AWS Lambda Node.js indipendenti.

Applica inoltre le istruzioni path-specific:

- `.github/instructions/openapi-review.instructions.md`;
- `.github/instructions/java-review.instructions.md`;
- `.github/instructions/lambda-review.instructions.md`.

## Ambito della review

Analizza il codice modificato dalla pull request e il comportamento direttamente
interessato.

Segnala esclusivamente problemi concreti, verificabili e correggibili.

Non segnalare:

- preferenze di stile o formattazione;
- refactoring senza beneficio funzionale concreto;
- problemi preesistenti non aggravati dalla modifica;
- rischi puramente ipotetici;
- richieste generiche di test o documentazione;
- comportamenti già garantiti dal codice generato o dagli strumenti automatici.

Non presentare supposizioni come vulnerabilità confermate. Se manca il contesto
necessario, formula una richiesta di verifica soltanto quando il rischio è
concreto e direttamente collegato alla modifica.

## Priorità

Assegna priorità, nell'ordine, a:

1. accessi non autorizzati, cross-user, cross-role o cross-tenant;
2. esposizione di segreti, credenziali o dati personali;
3. alterazione dell'identità o del contesto di autorizzazione;
4. incompatibilità dei contratti API;
5. perdita o corruzione dei dati;
6. errori di affidabilità e regressioni funzionali;
7. test mancanti sul comportamento modificato;
8. manutenibilità.

## Severità obbligatoria

Ogni commento deve iniziare con una delle seguenti classificazioni:

- `[BLOCCANTE][SICUREZZA]`
- `[BLOCCANTE][AUTORIZZAZIONE]`
- `[ALTA][PRIVACY]`
- `[ALTA][CONTRATTO]`
- `[ALTA][AFFIDABILITÀ]`
- `[ALTA][CORRETTEZZA]`
- `[MEDIA][CORRETTEZZA]`

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
- aggiramento di un controllo di sicurezza esistente.

Un rilievo bloccante rimane valido anche se:

- il codice compila;
- i test passano;
- il downstream potrebbe effettuare controlli aggiuntivi;
- è necessario conoscere un identificativo valido;
- gateway o frontend potrebbero mitigare il problema.

Non utilizzare severità bloccanti per naming, organizzazione, leggibilità,
duplicazione, manutenibilità o sola assenza di test.

### Severità alte

Usa:

- `[ALTA][PRIVACY]` per esposizione non necessaria di dati personali o sensibili;
- `[ALTA][CONTRATTO]` per incompatibilità API, `$ref` non validi o divergenze
  contrattuali;
- `[ALTA][AFFIDABILITÀ]` per indisponibilità, retry pericolosi, operazioni
  incomplete o perdita sistematica degli errori;
- `[ALTA][CORRETTEZZA]` per regressioni funzionali riproducibili o test negativi
  mancanti quando cambia un confine di autorizzazione.

Usa `[MEDIA][CORRETTEZZA]` per problemi circoscritti senza impatto su sicurezza,
contratti pubblici o funzionalità principali.

## Confini di fiducia

Considera sensibili e non liberamente sostituibili:

- identità e tipo del soggetto;
- gruppi e deleghe;
- identificativi di mittente, destinatario, ente e tenant;
- IUN, document ID, mandate ID e altri identificativi di risorsa;
- token, credenziali, chiavi e URL prefirmati;
- account, ambienti, ruoli e risorse AWS;
- dati relativi a notifiche, documenti, pagamenti e consensi.

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
- payload completi di notifiche, mandati, pagamenti o consensi.

Preferisci log strutturati con nome dell'operazione, esito, status code,
correlation ID, trace ID e identificativi minimizzati o mascherati.

Applica sempre il principio di minimizzazione dei dati.

## Test

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

La sola assenza di un test non è bloccante. Diventa bloccante soltanto quando il
codice mostra già un bypass concreto.

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

- puntare alla più piccola porzione modificata rilevante;
- spiegare il comportamento attuale e quello atteso;
- proporre una correzione attuabile;
- indicare un test solo quando utile a prevenire la regressione.

Non produrre commenti generici come “migliorare la validazione”, “aggiungere più
test” o “valutare un refactoring”.