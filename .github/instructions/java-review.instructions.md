---
applyTo: "src/**/*.java"
---

# Istruzioni per la review Java

Applica severità, confini di fiducia, fail closed, privacy, requisiti dei test,
lingua e formato dei commenti definiti in `.github/copilot-instructions.md`.

Queste istruzioni contengono soltanto controlli specifici per Java, Spring
WebFlux, MapStruct e per l'architettura di `pn-bff`.

Il flusso applicativo atteso è:

`controller -> service -> pnclient -> client downstream generato`

## Priorità specifiche

Dopo le priorità globali, presta particolare attenzione a:

1. propagazione di identità, ruolo, gruppi, deleghe e ownership;
2. corrispondenza dei parametri tra i diversi layer;
3. correttezza delle pipeline Reactor;
4. traduzione degli errori downstream;
5. mapping e nullabilità;
6. performance di mapper e utility;
7. chiamate downstream N+1;
8. leggibilità e concisione del codice introdotto;
9. test del comportamento modificato.

Non produrre micro-ottimizzazioni speculative o suggerimenti stilistici.

## Separazione dei livelli

Mantieni queste responsabilità:

- i controller implementano le interfacce OpenAPI generate e delegano;
- i service orchestrano chiamate, mapping e comportamento di business;
- i `pnclient` adattano i client downstream generati;
- i mapper MapStruct convertono DTO BFF e downstream;
- le utility contengono trasformazioni trasversali realmente condivise;
- i fallback di business risiedono nei service.

Segnala:

- logica di business significativa nei controller;
- controller che invocano direttamente i client generati;
- gestione HTTP nei mapper;
- mapping complesso o fallback di business nei `pnclient`;
- controlli di autorizzazione applicati solo dopo aver recuperato dati protetti;
- richieste costruite manualmente quando il client generato espone già
  l'operazione.

Usa `[BASSA][MANUTENIBILITÀ]` solo quando la collocazione errata introduce una
duplicazione o un rischio concreto di divergenza, non per preferenze
architetturali.

Non suggerire modifiche manuali al codice generato.

## Propagazione di identità e autorizzazione

Per ogni endpoint modificato segui, dove applicabile, la catena:

`OpenAPI -> controller -> service -> mapper -> pnclient -> client generato`

Verifica la propagazione di:

- `xPagopaPnUid`;
- `xPagopaPnCxId`;
- `xPagopaPnCxType`;
- `xPagopaPnCxGroups`;
- `xPagopaPnSrcCh`;
- `xPagopaPnSrcChDetails`;
- `mandateId`;
- sender, recipient, institution e group ID;
- IUN, document ID, document index e attachment index;
- retrieval ID;
- identificativi di API key, public key e virtual key;
- filtri che restringono la ricerca.

Controlla che:

- i valori autenticati non siano sostituiti da body o query parameter;
- la conversione di `CxType` non ampli il ruolo;
- gruppi e mandato non vengano rimossi o ampliati;
- sender e recipient non vengano scambiati;
- source channel e relativi dettagli non vengano persi;
- l'ownership della risorsa rimanga associata allo stesso soggetto;
- venga invocata la variante downstream corretta;
- `401` e `403` non vengano convertiti in fallback con dati protetti.

Classifica come `[BLOCCANTE][AUTORIZZAZIONE]` soltanto uno scenario concreto
che consenta accesso non autorizzato, escalation o perdita di ownership.

Non assumere che il possesso di un identificativo autorizzi l'accesso o che il
downstream compensi una propagazione errata del BFF.

## Parametri posizionali omogenei

Considera ad alto rischio i metodi con molti parametri dello stesso tipo, come
`String`, `UUID`, `Integer` e `List<String>`.

Quando una firma cambia, confronta:

1. interfaccia OpenAPI e controller;
2. controller e service;
3. service e `pnclient`;
4. `pnclient` e client generato;
5. test e argomenti attesi.

La compilazione non rileva lo scambio di parametri dello stesso tipo.

Classifica lo scambio come:

- `[BLOCCANTE][AUTORIZZAZIONE]` se altera identità, ruolo, delega o ownership;
- `[ALTA][CORRETTEZZA]` se causa una regressione funzionale rilevante;
- `[MEDIA][CORRETTEZZA]` se l'impatto è circoscritto.

## Controller

Verifica:

- coerenza con l'interfaccia OpenAPI generata;
- inoltro completo e ordinato di header, parametri e body;
- status code corretti;
- gestione reattiva dei request body;
- validazione dei parametri nullable;
- assenza di chiamate bloccanti o `subscribe()` manuali;
- delega dell'orchestrazione al service.

Segnala controller che:

- ignorano parametri obbligatori;
- sostituiscono valori autenticati;
- restituiscono successo dopo un errore;
- espongono dettagli downstream;
- manipolano direttamente DTO downstream;
- duplicano trasformazioni già presenti nei mapper.

## Service

Verifica:

- applicazione dei vincoli prima dell'accesso ai dati;
- corretta composizione delle chiamate;
- fallback limitati a dati accessori;
- assenza di stato mutabile condiviso;
- gestione esplicita di valori null e publisher vuoti;
- assenza di effetti collaterali duplicabili accidentalmente;
- assenza di chiamate downstream N+1;
- utilizzo coerente dei mapper.

Un arricchimento best-effort non deve:

- nascondere errori di autorizzazione;
- restituire dati protetti dopo `401` o `403`;
- modificare l'ownership della risposta;
- rendere indisponibile la risorsa principale senza necessità.

## pnclient e client generati

Verifica che ogni `pnclient`:

- utilizzi l'API generata corretta;
- invochi l'operazione corretta;
- passi argomenti completi e nel giusto ordine;
- propaghi gli header richiesti;
- utilizzi il logging del servizio esterno in modo coerente;
- non costruisca manualmente URL o query già supportati;
- non contenga mapping complesso o fallback di business;
- restituisca il tipo previsto dal contratto.

La correzione di un client generato deve essere applicata al contratto, alla
configurazione del generatore o al `pnclient`, non al sorgente generato.

## Spring WebFlux e Reactor

Segnala come `[ALTA][AFFIDABILITÀ]`, quando introdotti nel percorso reattivo:

- `block()`, `blockOptional()` o attese sincrone equivalenti;
- `Thread.sleep(...)`;
- I/O bloccante;
- chiamate sincrone di rete, AWS o filesystem;
- `subscribe()` manuali;
- publisher creati ma non restituiti;
- trasformazioni il cui risultato viene ignorato;
- retry non limitati o su operazioni non idempotenti;
- concorrenza non limitata;
- `collectList()` su flussi potenzialmente non limitati.

Verifica inoltre:

- `map` per trasformazioni sincrone;
- `flatMap` per funzioni che restituiscono publisher;
- assenza di `null` nelle callback;
- corretta semantica di `Mono.empty()`;
- propagazione di errori e cancellazione;
- operatori di errore applicati alle eccezioni previste;
- assenza di stato mutabile condiviso tra sottoscrizioni.

Non suggerire:

- `block()` per semplificare codice o test;
- `parallelStream()` nel percorso WebFlux;
- `boundedElastic()` per mascherare una trasformazione CPU inefficiente;
- `cache()` su dati utente senza limiti, isolamento e scadenza.

Per operazioni bloccanti inevitabili, richiedi un isolamento esplicito e
limitato. Per lavoro CPU non bloccante, preferisci ridurre complessità e
allocazioni.

## Errori downstream

Distingui almeno:

- `400 Bad Request`;
- `401 Unauthorized`;
- `403 Forbidden`;
- `404 Not Found`;
- altri `4xx`;
- `5xx`;
- timeout e throttling.

Quando viene utilizzata `PnBffExceptionUtility`, verifica che:

- lo status downstream venga preservato quando appropriato;
- il modello `Problem` sia decodificato in modo sicuro;
- body vuoti o malformati non provochino una seconda eccezione;
- la causa originale venga mantenuta;
- i messaggi esposti non contengano dati sensibili;
- campi assenti nel `Problem` non causino errori inattesi.

Segnala:

- `onErrorResume` troppo generici;
- `401` o `403` trasformati in successo, lista vuota o `404`;
- errori di validazione trasformati in `500`;
- perdita del codice strutturato;
- body downstream completi nei log;
- `Objects.requireNonNull` su dati non garantiti dal contratto.

Un retry deve essere limitato, applicato solo a errori transitori e a operazioni
idempotenti.

## Mapping e nullabilità

Per ogni modifica ai mapper verifica:

- propagazione dei campi aggiunti o rinominati;
- conversione semantica delle enum e gestione di valori sconosciuti;
- oggetti annidati null;
- liste null o vuote;
- date, fusi orari, importi e identificativi;
- pagination key e indici;
- esclusione di campi interni o sensibili;
- assenza di perdita involontaria dei dati;
- assenza di mutazioni condivise;
- coerenza con required e nullabilità OpenAPI.

I DTO generati possono contenere `null` se il contratto non garantisce il
campo.

Controlla in particolare:

- autounboxing di tipi boxed;
- accessi a liste per indice;
- confronti tra enum nullable;
- proprietà annidate;
- conversione di parametri facoltativi verso primitivi;
- differenza tra `null`, lista vuota e publisher vuoto.

Un default non deve ampliare accesso, gruppi o risultati.

## Performance di mapper e utility

Dedica particolare attenzione a:

- `src/main/java/**/mappers/**`;
- `src/main/java/**/utils/**`.

Questi componenti trasformano collezioni di notifiche, timeline, destinatari,
documenti, pagamenti, mandati e gruppi nel percorso delle richieste.

Non assumere che tali collezioni siano piccole, salvo un limite contrattuale
esplicito.

### Quando segnalare

Un rilievo prestazionale deve indicare:

1. il percorso frequente o la collezione interessata;
2. il costo introdotto in CPU, memoria, allocazioni, I/O o latenza;
3. come il costo cresce con l'input;
4. una correzione concreta;
5. la semantica da preservare.

Non segnalare differenze teoriche tra Stream e cicli, allocazioni trascurabili o
micro-ottimizzazioni prive di un impatto plausibile.

### Problemi prioritari

Controlla soprattutto:

- cicli annidati o ricerche lineari ripetute, con complessità `O(n²)` o peggiore;
- `List.contains`, `filter` o `findFirst` dentro un altro ciclo;
- più scansioni, ordinamenti o parsing degli stessi dati;
- liste, mappe, DTO o copie intermedie non necessarie;
- serializzazione JSON o reflection usate per copiare o mappare DTO;
- regex o formatter costosi ricreati per ogni elemento;
- mapping ripetuto dello stesso oggetto;
- `@BeforeMapping`, `@AfterMapping`, `expression` o metodi `default` costosi;
- chiamate downstream per ogni elemento;
- trasformazioni CPU-intensive sul thread reattivo;
- crescita non limitata della memoria o della concorrenza.

Classifica come:

- `[ALTA][AFFIDABILITÀ]` complessità non limitata, N+1, blocco dei thread o
  rischio concreto di esaurimento delle risorse;
- `[ALTA][CORRETTEZZA]` regressioni prestazionali rilevanti e riproducibili su
  percorsi frequenti;
- `[MEDIA][CORRETTEZZA]` inefficienze concrete ma circoscritte;
- `[BASSA][MANUTENIBILITÀ]` solo complessità non necessaria che aumenta il
  rischio di future regressioni, senza impatto prestazionale attuale rilevante.

### Ottimizzazioni ammesse

Quando esistono lookup ripetuti, valuta un indice `Map` o `Set` costruito una
sola volta, ma soltanto se:

- la collezione può essere significativa;
- la chiave è semanticamente corretta;
- il costo di costruzione è giustificato;
- ordine, duplicati e valori null restano invariati.

Per chiamate downstream N+1 preferisci, nell'ordine:

1. un'API batch esistente;
2. deduplicazione locale delle chiavi;
3. riuso locale del risultato nella singola richiesta;
4. concorrenza esplicitamente limitata.

Non introdurre cache globali per dati dipendenti da utente, ruolo, richiesta o
tenant.

MapStruct è il meccanismo di mapping preferito. Non suggerire di sostituirlo con
reflection, `ObjectMapper` o mapping dinamico.

Ogni ottimizzazione deve preservare:

- ordine e duplicati;
- nullabilità;
- precisione di date e importi;
- semantica delle enum;
- errori e fallback;
- isolamento tra richieste;
- minimizzazione dei dati;
- autorizzazione.

## Mutabilità e thread safety

Segnala stato mutabile quando può:

- essere condiviso tra richieste;
- trasferire dati tra destinatari;
- conservare dati di elaborazioni precedenti;
- produrre race condition;
- rendere il risultato dipendente dall'ordine di sottoscrizione.

La mutazione locale di un DTO creato e usato nella singola pipeline è ammessa se
non viene condiviso.

Non suggerire il riuso globale di mapper stateful, builder, formatter mutabili o
cache per ridurre le allocazioni.

## Configurazione e WebFilter

Per modifiche sotto `config/` verifica:

- CORS e header esposti;
- HSTS e altri header di sicurezza;
- ordine dei filtri;
- propagazione del trace ID;
- endpoint e configurazione dei client;
- separazione tra ambienti;
- default sicuri;
- assenza di credenziali hardcoded.

CORS ampliato, endpoint arbitrari o disattivazione di controlli esistenti sono
bloccanti soltanto con uno scenario concreto e raggiungibile nell'ambiente
interessato.

## Leggibilità e concisione

Oltre a sicurezza, correttezza, affidabilità e performance, valuta la
leggibilità e la lunghezza del codice introdotto dalla pull request.

Considera solo il codice aggiunto o modificato dalla pull request.

### Leggibilità

Segnala:

- catene reattive con annidamenti profondi di `flatMap`, `map` o `zip` che
  rendono difficile seguire il flusso principale;
- condizioni booleane composte non estratte in una variabile o in un metodo con
  nome esplicativo;
- metodi che concentrano validazione, mapping, chiamata downstream e gestione
  degli errori nello stesso percorso;
- blocchi `if/else` annidati sostituibili con early return o guard clause;
- variabili temporanee prive di significato o commenti che spiegano codice che
  può essere reso auto-esplicativo;
- conversioni scritte a mano che replicano logica già esprimibile in modo
  dichiarativo con MapStruct.

### Lunghezza e concisione

A parità di comportamento osservabile, preferisci la soluzione che utilizza
meno righe di codice.

Segnala il codice più verboso del necessario quando esiste un costrutto
equivalente più breve e altrettanto chiaro, ad esempio:

- `Optional.map` e `orElse` al posto di catene di `if` sui valori nulli;
- `Stream` e `Collectors` al posto di cicli di accumulo manuale;
- `List.of` o `Map.of` per collezioni immutabili;
- `var` soltanto dove il tipo resta evidente dal contesto;
- text block per stringhe multilinea;
- `switch` espressivo al posto di catene `if/else` sullo stesso valore;
- un metodo condiviso quando la pull request introduce blocchi identici
  duplicati;
- rimozione di boilerplate evitabile, come null check già garantiti dalla
  validazione a monte o `try/catch` che si limitano a rilanciare.

La riduzione delle righe non deve mai peggiorare:

- la leggibilità del percorso principale;
- la gestione e il contesto degli errori;
- la tracciabilità nei log;
- il comportamento reattivo, che non deve introdurre operazioni bloccanti;
- la semantica di ordinamento, duplicati e nullabilità.

### Cosa non segnalare

- pura preferenza stilistica, formattazione, ordine di import o di metodi;
- one-liner criptici, ternari annidati o catene di stream illeggibili proposti
  solo per ridurre le righe;
- codice preesistente non toccato dalla pull request;
- rinomine di variabili senza un beneficio concreto sulla comprensione.

### Severità e forma del rilievo

Classifica i rilievi di leggibilità e concisione come `[BASSA][MANUTENIBILITÀ]`
e non elevarli a una severità superiore.

Ogni rilievo deve indicare:

1. classe o metodo interessato;
2. il punto specifico poco leggibile o verboso;
3. il motivo concreto per cui rende più difficile la comprensione o la
   manutenzione;
4. una correzione minima e attuabile, preferibilmente con un esempio breve del
   costrutto equivalente più conciso.

## Test Java

Scegli il livello più vicino al comportamento:

- controller test per parametri, header e risposta HTTP;
- service test per orchestrazione, fallback ed errori;
- mapper test per campi, enum e nullabilità;
- test dei `pnclient` per operazione e argomenti;
- `*TestIT` per WebClient, serializzazione ed errori downstream;
- LocalStack o Testcontainers per integrazioni AWS.

Per Reactor preferisci `StepVerifier` o gli helper esistenti.

Quando cambia un confine di autorizzazione, applica i test negativi richiesti
nelle instructions globali.

Per una correzione prestazionale:

- richiedi test funzionali che preservino risultato, ordine, duplicati e null;
- usa input abbastanza grandi da esercitare il percorso problematico;
- richiedi JMH o misurazioni riproducibili solo se il beneficio non è evidente
  dalla complessità algoritmica;
- non richiedere benchmark per ogni mapper.

Non richiedere nuovi test per un rilievo di sola leggibilità o concisione,
salvo che la correzione proposta modifichi il comportamento osservabile.

## Focus del commento

Tutti i commenti devono essere scritti in inglese.

Nel commento Java indica sempre:

- classe o metodo;
- percorso che attiva il problema;
- impatto concreto;
- correzione minima;
- test utile, se necessario.

Per le performance indica anche dimensione dell'input e variazione della
complessità o del numero di chiamate.

Per leggibilità e concisione indica il punto specifico e il costrutto
equivalente proposto, non un principio generale.

Non produrre commenti generici come:

- `Optimize this method`;
- `Use a Stream`;
- `Consider adding a cache`;
- `Extract this into a method`;
- `Add more tests`;
- `Improve readability`;
- `Make this shorter`.