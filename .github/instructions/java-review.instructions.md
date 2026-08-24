---
applyTo: "src/**/*.java"
---

# Istruzioni per la review Java

Applica severità, confini di fiducia, criteri di privacy, fail closed e formato
dei commenti definiti in `.github/copilot-instructions.md`.

Il progetto utilizza Java 17, Spring Boot, Spring WebFlux, Project Reactor,
OpenAPI e MapStruct.

Il flusso applicativo atteso è:

`controller -> service -> pnclient -> client downstream generato`

## Priorità specifiche

Oltre alle priorità globali, dedica particolare attenzione a:

1. propagazione di identità, ruolo, gruppi, deleghe e ownership;
2. correttezza dei parametri tra controller, service, `pnclient` e client
   generati;
3. complessità algoritmica nei mapper e nelle utility;
4. numero di scansioni e trasformazioni applicate alle collezioni;
5. allocazioni, copie e strutture intermedie nel mapping;
6. chiamate downstream N+1 durante gli arricchimenti;
7. operazioni bloccanti, CPU-intensive o non limitate nella pipeline WebFlux;
8. gestione degli errori e dei fallback;
9. preservazione di ordine, duplicati, nullabilità e semantica durante le
   ottimizzazioni;
10. test del comportamento modificato.

Non proporre micro-ottimizzazioni speculative. Un rilievo prestazionale deve
descrivere il percorso interessato, la causa del costo e il miglioramento
concreto suggerito.

## Separazione dei livelli

Preserva le responsabilità adottate dal repository:

- i controller implementano le interfacce OpenAPI generate e delegano ai
  service;
- i service orchestrano le chiamate e applicano il comportamento di business;
- i `pnclient` adattano i client downstream generati;
- i mapper MapStruct convertono DTO BFF e DTO downstream;
- le utility contengono logica di trasformazione trasversale realmente
  condivisa;
- i fallback di business rimangono nei service;
- la configurazione costruisce bean, filtri e client.

Segnala:

- logica di business significativa nei controller;
- controller che invocano direttamente client downstream generati;
- gestione HTTP introdotta nei mapper;
- mapping complesso implementato nei `pnclient`;
- fallback di business nei controller o nei client;
- chiamate dirette ai client generati da layer impropri;
- costruzione manuale di richieste già supportate dai client generati;
- duplicazione della traduzione degli errori;
- controlli di autorizzazione applicati soltanto dopo il recupero di dati
  protetti.

Non richiedere nuove interfacce, adapter o livelli se non risolvono un problema
funzionale concreto.

## Propagazione di identità e autorizzazione

Per ogni endpoint modificato segui l'intera catena:

`OpenAPI -> controller -> service -> mapper -> pnclient -> client generato`

Verifica la propagazione di:

- `xPagopaPnUid`;
- `xPagopaPnCxId`;
- `xPagopaPnCxType`;
- `xPagopaPnCxGroups`;
- `xPagopaPnSrcCh`;
- `xPagopaPnSrcChDetails`;
- `mandateId`;
- sender ID;
- recipient ID;
- institution ID;
- group ID;
- IUN;
- document ID e document index;
- attachment name e attachment index;
- retrieval ID;
- API key, public key e virtual key;
- date, page size e pagination key quando restringono la ricerca.

Controlla che:

- l'identità rimanga invariata lungo la catena;
- i valori autenticati non siano sostituiti da campi del body o della query;
- il ruolo non venga ampliato;
- la conversione di `CxType` preservi la semantica;
- i gruppi non vengano rimossi, sostituiti o ampliati;
- il mandato sia propagato quando l'operazione viene svolta per delega;
- sender e recipient non vengano scambiati;
- source channel e relativi dettagli non vengano persi;
- l'identificativo della risorsa rimanga associato allo stesso soggetto;
- venga invocata la variante downstream corretta;
- gli errori `401` e `403` mantengano la propria semantica;
- i fallback non rimuovano vincoli di autorizzazione.

Non assumere che la conoscenza di uno IUN, `documentId`, `retrievalId`,
`mandateId`, indice o chiave sia sufficiente per autorizzare l'accesso.

Non assumere che gateway o microservizi downstream compensino una propagazione
errata effettuata dal BFF.

## Controlli bloccanti di autorizzazione

Segnala come `[BLOCCANTE][AUTORIZZAZIONE]`:

- omissione di un parametro necessario all'autorizzazione;
- scambio tra UID, CxId, sender e recipient;
- mancata propagazione del mandato;
- rimozione o ampliamento dei gruppi;
- conversione di `CxType` verso un ruolo più privilegiato;
- utilizzo di un valore del body al posto del valore autenticato;
- invocazione di una variante downstream meno vincolata;
- accesso a una risorsa basato soltanto sul relativo identificativo;
- fallback applicato dopo un errore `401` o `403`;
- restituzione di metadata o URL di download senza il contesto richiesto;
- esposizione di dati appartenenti a un altro destinatario, ente o tenant;
- valore predefinito che amplia l'accesso;
- controllo di ownership rimosso o applicato soltanto dopo il recupero dei dati.

Il problema rimane bloccante anche quando il downstream potrebbe applicare
controlli aggiuntivi.

## Parametri dello stesso tipo

Considera ad alto rischio i metodi con molti parametri dello stesso tipo:

- `String`;
- `UUID`;
- `Integer`;
- `List<String>`;
- enum semanticamente simili.

Quando cambia una firma confronta:

1. interfaccia OpenAPI e controller;
2. controller e service;
3. service e `pnclient`;
4. `pnclient` e client generato;
5. test e argomenti attesi.

La compilazione non rileva scambi tra parametri dello stesso tipo.

Uno scambio che altera autorizzazione o ownership è
`[BLOCCANTE][AUTORIZZAZIONE]`.

Uno scambio che produce soltanto un risultato funzionalmente errato è
`[ALTA][CORRETTEZZA]`.

## Fail closed nel codice Java

Quando un valore necessario all'autorizzazione è assente, invalido o incoerente,
il flusso deve terminare con errore.

Segnala come bloccante un comportamento che:

- rimuove silenziosamente un vincolo;
- utilizza un ruolo più permissivo;
- utilizza tutti i gruppi o elimina il filtro;
- ignora il mandato;
- prosegue senza il contesto richiesto;
- recupera la risorsa prima di verificarne l'ownership;
- restituisce una risposta parziale contenente dati protetti;
- applica un valore predefinito che amplia l'accesso.

Un fallback best-effort è ammesso esclusivamente per dati accessori e dopo che
l'accesso alla risorsa principale è stato autorizzato.

## Controller

I controller devono:

- implementare le interfacce OpenAPI generate;
- inoltrare correttamente parametri e body;
- delegare ai service;
- trasformare la risposta del service nella risposta HTTP prevista;
- applicare soltanto validazioni di confine semplici e coerenti con il
  contratto.

Durante la review verifica:

- completezza e ordine dei parametri inoltrati;
- corretta propagazione degli header;
- status code restituiti;
- gestione dei request body reattivi;
- assenza di logica di business duplicata;
- assenza di chiamate bloccanti;
- assenza di `subscribe()` manuali;
- coerenza con l'interfaccia generata;
- gestione sicura dei parametri nullable.

Segnala controller che:

- ignorano un parametro obbligatorio;
- sostituiscono un valore autenticato con un valore della richiesta;
- restituiscono sempre `200` anche quando il service segnala un errore;
- espongono eccezioni o dettagli downstream;
- manipolano direttamente DTO downstream;
- eseguono più chiamate downstream senza delegare l'orchestrazione al service.

## Service

I service devono:

- orchestrare le chiamate;
- applicare il comportamento di business;
- utilizzare mapper dedicati;
- distinguere errori bloccanti e fallback best-effort;
- restituire publisher reattivi senza bloccare;
- non ampliare il contesto di autorizzazione.

Durante la review verifica:

- ordine delle operazioni;
- applicazione dei vincoli prima dell'accesso ai dati;
- fallback limitati a dati accessori;
- assenza di stato mutabile condiviso;
- corretta composizione con `map`, `flatMap`, `zip` e operatori di errore;
- gestione esplicita dei valori null;
- assenza di effetti collaterali non idempotenti ripetibili accidentalmente;
- assenza di chiamate downstream N+1;
- utilizzo dei mapper coerente con il contratto.

Segnala:

- errori di autorizzazione trasformati in fallback;
- chiamate concorrenti che condividono e mutano lo stesso DTO;
- errori ignorati senza uno stato esplicito nella risposta;
- arricchimenti che rendono indisponibile la risorsa principale senza
  necessità;
- arricchimenti che restituiscono dati protetti dopo un fallimento di
  autorizzazione.

## pnclient e client generati

Le classi `pnclient` devono adattare le API generate senza duplicare logica di
business.

Verifica che:

- venga utilizzata l'API generata corretta;
- venga invocata l'operazione corretta;
- gli argomenti siano completi e nel giusto ordine;
- il logging identifichi il servizio e l'operazione reali;
- non vengano costruiti manualmente URL o query string già supportati;
- non vengano eliminati header di correlazione o autorizzazione;
- non vengano applicati fallback di business;
- il tipo restituito corrisponda al contratto.

Non suggerire modifiche manuali ai client o ai DTO generati. La correzione deve
essere applicata al contratto OpenAPI, alla configurazione del generatore o
all'adapter scritto manualmente.

## WebFlux e Reactor

Segnala come `[ALTA][AFFIDABILITÀ]`:

- `block()` o `blockOptional()`;
- `toFuture().get()` o attese sincrone equivalenti;
- `Thread.sleep(...)`;
- I/O bloccante nel percorso WebFlux;
- chiamate sincrone di rete, AWS o file;
- `subscribe()` manuali nei controller, service o client;
- publisher creati ma non restituiti;
- risultati di trasformazioni reattive ignorati;
- conversione non motivata di un errore in successo;
- retry senza limite;
- retry applicati a operazioni non idempotenti.

Verifica inoltre:

- utilizzo di `map` per trasformazioni sincrone;
- utilizzo di `flatMap` quando la funzione restituisce un publisher;
- gestione corretta di `Mono.empty()`;
- assenza di `null` restituiti dalle callback;
- propagazione della cancellazione;
- assenza di stato mutabile condiviso;
- corretta gestione delle eccezioni negli operatori;
- corretta collocazione di `onErrorMap` e `onErrorResume`.

Non suggerire `block()` per semplificare l'implementazione o i test.

## Gestione degli errori downstream

Distingui almeno:

- `400 Bad Request`;
- `401 Unauthorized`;
- `403 Forbidden`;
- `404 Not Found`;
- altri errori `4xx`;
- errori `5xx`;
- timeout;
- throttling;
- indisponibilità temporanea.

Quando viene utilizzata `PnBffExceptionUtility`, verifica che:

- lo status downstream sia preservato quando appropriato;
- il modello `Problem` venga decodificato in modo sicuro;
- body vuoti o malformati non causino una seconda eccezione;
- la causa originale venga preservata;
- il messaggio esposto non contenga dati sensibili;
- l'assenza di campi nel `Problem` non provochi errori inattesi.

Segnala:

- `onErrorResume` applicati a tutte le eccezioni;
- `401` o `403` trasformati in `404`, lista vuota o successo;
- errori di validazione trasformati in `500`;
- body downstream completi registrati nei log;
- fallback che intercettano errori di programmazione;
- perdita del codice di errore strutturato;
- utilizzo non sicuro di `Objects.requireNonNull` su dati downstream non
  garantiti dal contratto.

Un `404` non deve nascondere automaticamente un `403`.

## Retry, timeout e idempotenza

Quando viene introdotto un retry, verifica che:

- sia applicato soltanto a errori transitori;
- abbia un numero massimo di tentativi;
- utilizzi backoff quando opportuno;
- non includa errori `4xx` permanenti;
- non includa `401` o `403`;
- l'operazione sia idempotente;
- non provochi duplicazioni di notifiche, chiavi, mandati o pagamenti;
- il timeout complessivo rimanga compatibile con la richiesta BFF.

Segnala retry generici su operazioni di creazione, modifica o cancellazione se
non esiste una garanzia esplicita di idempotenza.

## Performance di mapper e utility

Dedica particolare attenzione alle prestazioni del codice sotto:

- `src/main/java/**/mappers/**`;
- `src/main/java/**/utils/**`.

Questi componenti eseguono gran parte della trasformazione dei dati del BFF e
possono essere invocati per ogni richiesta o per ogni elemento di collezioni
potenzialmente grandi.

Considera una regressione prestazionale in mapper e utility più rilevante della
stessa inefficienza in un percorso eseguito raramente.

Non assumere che collezioni di notifiche, timeline, destinatari, documenti,
pagamenti, mandati o gruppi siano sempre piccole, salvo la presenza di un limite
contrattuale esplicito e affidabile.

### Requisiti per i rilievi di performance

Segnala un problema prestazionale soltanto quando puoi indicare:

1. il percorso nel quale il codice viene eseguito;
2. la collezione, il payload o l'operazione interessata;
3. la causa dell'aumento di CPU, memoria, allocazioni o latenza;
4. come il costo cresce rispetto alla dimensione dell'input;
5. una correzione concreta che preservi la semantica.

Non proporre:

- micro-ottimizzazioni speculative;
- cache globali senza requisiti di isolamento e scadenza;
- strutture più complesse senza un beneficio plausibile;
- modifiche motivate soltanto da preferenze tra cicli e Stream;
- benchmark per trasformazioni semplici e non critiche.

### Severità delle regressioni prestazionali

Classifica come `[ALTA][AFFIDABILITÀ]` una modifica che può:

- introdurre complessità quadratica o peggiore su collezioni non limitate;
- effettuare I/O, parsing o serializzazione ripetuti per ogni elemento;
- caricare integralmente in memoria payload potenzialmente grandi;
- bloccare thread del runtime WebFlux;
- provocare crescita non limitata della memoria;
- creare chiamate downstream N+1;
- ripetere una trasformazione costosa nella stessa richiesta;
- introdurre concorrenza non limitata;
- rendere plausibile l'esaurimento di thread, memoria o connessioni.

Classifica come `[ALTA][CORRETTEZZA]` una regressione riproducibile che aumenta
significativamente latenza, CPU o allocazioni su un percorso frequente senza
compromettere direttamente la disponibilità.

Usa `[MEDIA][CORRETTEZZA]` per inefficienze circoscritte, con input limitato e
impatto plausibile ma non critico.

Non classificare come bloccante una questione esclusivamente prestazionale,
salvo che consenta anche un attacco concreto di esaurimento delle risorse.

### Complessità algoritmica

Nei mapper e nelle utility verifica:

- cicli annidati sulle stesse collezioni;
- ricerche lineari all'interno di altri cicli;
- chiamate ripetute a `stream().filter(...)`, `findFirst()` o `contains()`;
- uso ripetuto di `List.contains(...)`;
- costruzione ripetuta della stessa `Map` o dello stesso indice;
- ordinamenti multipli sugli stessi dati;
- scansioni complete ripetute;
- concatenazioni o copie ripetute di liste;
- ricorsione non limitata su strutture controllabili dall'input;
- conversioni multiple dello stesso oggetto tra modelli equivalenti.

Quando sono presenti più lookup sulla stessa collezione, valuta la costruzione
di una `Map` o di un `Set` una sola volta.

Suggerisci questa ottimizzazione soltanto quando:

- i lookup sono realmente ripetuti;
- la collezione può contenere un numero significativo di elementi;
- è possibile definire correttamente la chiave;
- duplicati, ordine e valori null hanno una semantica chiara;
- il costo dell'indice è giustificato.

Verifica che il passaggio da `List` a `Set` o `Map` non alteri:

- ordine degli elementi;
- gestione dei duplicati;
- selezione del primo elemento;
- comportamento con chiavi duplicate;
- semantica dei valori null.

### Stream e collezioni

Non considerare automaticamente gli Stream più efficienti dei cicli né i cicli
più efficienti degli Stream. Valuta il costo concreto.

Segnala:

- pipeline Stream ripetute sulla stessa collezione;
- liste intermedie usate una sola volta;
- `collect(...)` seguito immediatamente da una nuova scansione;
- `sorted(...)` quando l'ordine non è richiesto;
- `distinct()` con `equals` o `hashCode` costosi o non coerenti;
- `parallelStream()` nel percorso WebFlux;
- `Collectors.groupingBy(...)` che crea strutture più grandi del necessario;
- `flatMap` di collezioni con espansione potenzialmente non limitata;
- copie difensive ripetute della stessa collezione;
- conversioni ripetute tra array, liste, set e stream.

Non suggerire `parallelStream()` come ottimizzazione generica: utilizza il
common pool e può rendere imprevedibile il consumo di risorse.

### Allocazioni e copie

Verifica la creazione ripetuta, dentro cicli o pipeline frequenti, di:

- DTO temporanei;
- builder;
- wrapper;
- liste e mappe intermedie;
- formatter;
- parser;
- espressioni regolari;
- copie complete di oggetti;
- stringhe costruite tramite concatenazioni ripetute.

Segnala allocazioni evitabili quando si moltiplicano per il numero di elementi o
copiano payload rilevanti.

Non suggerire il riuso globale di oggetti mutabili. Mapper e utility devono
rimanere thread-safe e isolati tra richieste.

Preferisci oggetti locali e immutabili. Valuta il riuso soltanto per componenti
immutabili o thread-safe e costosi da costruire.

### MapStruct e mapping generato

MapStruct genera normalmente mapping statico e sincrono efficiente.

Non suggerire di sostituirlo con:

- reflection;
- serializzazione JSON;
- mapping dinamico;
- copia tramite `ObjectMapper`.

Per i mapper MapStruct verifica:

- metodi `@AfterMapping`, `@BeforeMapping`, `expression` o `default` con
  scansioni ripetute;
- mapper richiamati più volte per lo stesso oggetto;
- creazione di DTO completi quando serve soltanto un sottoinsieme;
- mapping di collezioni seguito da ulteriori copie equivalenti;
- accesso ripetuto a proprietà calcolate o annidate costose;
- utility chiamate dal mapping con complessità non evidente;
- conversioni JSON usate per copiare DTO;
- reflection introdotta nel percorso di mapping;
- calcoli invarianti ripetuti per ogni elemento.

Valuta l'uso di `@Context` per condividere nella singola operazione un indice o
un risultato precalcolato soltanto quando:

- riduce elaborazioni realmente ripetute;
- il contesto rimane locale alla singola richiesta;
- non introduce stato globale;
- non altera ordine, duplicati o nullabilità;
- mantiene leggibile il mapping.

Non proporre mapping manuale al posto di MapStruct senza una regressione
dimostrabile o una trasformazione non rappresentabile in modo chiaro.

### Utility di trasformazione

Per le utility che elaborano timeline, notifiche, destinatari, documenti,
pagamenti o gruppi verifica:

- quante volte viene attraversata ogni collezione;
- se la stessa ricerca viene ripetuta;
- se risultati invarianti vengono ricalcolati;
- se vengono costruite strutture intermedie non necessarie;
- se ordinamenti, parsing o normalizzazioni vengono ripetuti;
- se il metodo modifica l'input e forza copie difensive nei chiamanti;
- se la complessità è coerente con la dimensione massima prevista;
- se l'uso della memoria rimane limitato.

Quando un valore non cambia durante l'elaborazione, calcolalo una sola volta
fuori dal ciclo o dalla pipeline.

Evita cache globali per dati dipendenti da utente, richiesta, ruolo o tenant:
possono causare crescita della memoria ed esposizione cross-user.

### Stringhe, regex, date e serializzazione

Verifica:

- concatenazioni di stringhe in cicli;
- regex compilate ripetutamente;
- regex vulnerabili a backtracking eccessivo;
- formatter di data ricreati per ogni elemento;
- parsing ripetuto dello stesso valore;
- serializzazione e deserializzazione JSON usate come copia;
- conversioni ripetute tra `OffsetDateTime`, `Instant`, stringhe e fusi orari;
- body downstream deserializzati più volte.

Formatter e pattern possono essere riutilizzati soltanto se immutabili e
thread-safe.

Una regex con costo non limitato su input controllabile dal chiamante può essere
`[ALTA][AFFIDABILITÀ]` o `[BLOCCANTE][SICUREZZA]` quando consente un attacco
concreto di denial of service.

### Interazione tra trasformazioni e Reactor

Le trasformazioni CPU-intensive dentro `map` o `flatMap` vengono eseguite sul
thread della pipeline finché non viene selezionato esplicitamente uno scheduler
differente.

Segnala:

- trasformazioni CPU-intensive sul thread reattivo;
- scansioni molto costose nella pipeline;
- elaborazioni ripetute per ogni sottoscrizione;
- `collectList()` su flussi potenzialmente non limitati;
- `flatMap` con concorrenza non limitata;
- chiamate downstream N+1;
- uso di `cache()` che conserva dati utente o payload senza limite;
- `publishOn` o `subscribeOn` aggiunti senza un confine operativo chiaro.

Non suggerire automaticamente `boundedElastic()` per accelerare una
trasformazione CPU-intensive. Per operazioni non bloccanti preferisci ridurre
complessità e lavoro eseguito.

Usa scheduler alternativi soltanto quando la natura dell'operazione lo richiede
e il consumo delle risorse rimane limitato.

### Chiamate downstream N+1

Presta particolare attenzione a service e utility che arricchiscono collezioni
tramite chiamate downstream.

Segnala:

- una chiamata remota per ogni elemento quando esiste un'API batch;
- chiamate duplicate per la stessa chiave;
- arricchimenti indipendenti eseguiti sequenzialmente;
- concorrenza non limitata introdotta per velocizzare un N+1;
- assenza di limiti sul numero di elementi arricchiti;
- retry moltiplicati per ogni elemento.

Preferisci, nell'ordine:

1. un'API batch già disponibile;
2. deduplicazione locale delle chiavi nella singola richiesta;
3. riuso locale del risultato per chiavi ripetute;
4. concorrenza esplicitamente limitata;
5. fallback per dati accessori, quando previsto.

Non introdurre cache condivise tra richieste senza requisiti espliciti di
scadenza, isolamento, invalidazione e protezione dei dati.

### Preservazione della correttezza

Ogni ottimizzazione deve preservare:

- ordine degli elementi quando significativo;
- duplicati;
- gestione dei valori null;
- semantica delle enum;
- selezione del primo elemento;
- precisione di date e importi;
- comportamento in caso di errore;
- isolamento tra richieste;
- minimizzazione dei dati;
- controlli di autorizzazione.

Non suggerire ottimizzazioni che cambiano il contratto o riducono i controlli di
sicurezza.

## Mapping e nullabilità

Per ogni modifica ai mapper verifica:

- campi aggiunti, rimossi o non propagati;
- enum nuove o sconosciute;
- oggetti annidati null;
- liste null o vuote;
- date, fusi orari, importi e identificativi;
- pagination key e indici;
- esclusione dei campi interni;
- assenza di perdita involontaria dei dati;
- assenza di mutazioni condivise;
- allineamento con obbligatorietà e nullabilità OpenAPI.

I DTO generati possono contenere `null` se il contratto non garantisce il campo.

Controlla:

- autounboxing di `Integer`, `Boolean` e altri tipi boxed;
- accessi a liste per indice;
- confronti tra enum;
- accessi a proprietà annidate;
- conversione di parametri facoltativi in primitivi;
- differenza tra valore null, publisher vuoto e lista vuota.

Un parametro OpenAPI facoltativo non deve essere passato a un primitivo, come
`int`, senza validazione o default esplicito.

Un default non deve ampliare privilegi, gruppi, risultati o accesso.

## Mutazione dei DTO

Segnala mutazioni quando possono:

- trasferire dati tra destinatari differenti;
- conservare dati di una richiesta precedente;
- esporre campi riservati a un altro ruolo;
- produrre race condition;
- riutilizzare una risposta downstream condivisa;
- rendere il risultato dipendente dall'ordine di sottoscrizione.

Non classificare come problematica la mutazione locale di un DTO creato e
utilizzato esclusivamente nella singola pipeline, salvo uno scenario concreto
di condivisione o contaminazione.

## Privacy e logging Java

Non registrare:

- codici fiscali;
- UID completi;
- token o header di autorizzazione;
- API key, public key o virtual key sensibili;
- credenziali;
- body completi;
- contenuti dei documenti;
- URL prefirmati;
- payload di notifiche, mandati, pagamenti o consensi;
- body completi degli errori downstream.

Valuta con attenzione anche:

- IUN;
- mandate ID;
- document ID;
- retrieval ID;
- gruppi;
- sender ID;
- recipient ID.

Preferisci:

- trace ID;
- correlation ID;
- nome dell'operazione;
- servizio downstream;
- status code;
- identificativi minimizzati o mascherati.

Non suggerire di aggiungere dati sensibili ai log per facilitare il debug.

## Configurazione e WebFilter

Per modifiche sotto `config/` verifica:

- ordine e comportamento dei filtri;
- header aggiunti o esposti;
- configurazione CORS;
- HSTS e altri header di sicurezza;
- propagazione del trace ID;
- configurazione dei client downstream;
- valori di configurazione e default;
- separazione degli ambienti;
- assenza di credenziali hardcoded;
- assenza di endpoint arbitrari controllabili dall'esterno.

Classifica come `[BLOCCANTE][SICUREZZA]` una modifica raggiungibile in produzione
che:

- espone header sensibili al browser;
- amplia CORS in modo non intenzionale;
- consente endpoint downstream arbitrari;
- introduce segreti nel codice;
- disattiva un controllo di sicurezza.

Non classificare una configurazione locale permissiva come vulnerabilità di
produzione senza verificare il profilo o il percorso di attivazione.

## Test Java

Richiedi il test più vicino al comportamento modificato:

- controller test per validazione, parametri, header e risposta HTTP;
- service test per orchestrazione, fallback ed errori;
- mapper test per campi, enum e nullabilità;
- test unitari dei `pnclient` per operazione e argomenti;
- test `*TestIT` per serializzazione, WebClient ed errori downstream;
- LocalStack o Testcontainers per integrazioni AWS;
- test di regressione per bug corretti.

Per il codice reattivo utilizza `StepVerifier` o gli helper esistenti.

Verifica, quando rilevante:

- emissione corretta del valore;
- completamento senza elementi;
- propagazione dell'errore;
- cancellazione;
- fallback previsto;
- mancata applicazione del fallback;
- assenza di chiamate downstream non necessarie.

### Test delle ottimizzazioni

Quando una modifica corregge una regressione prestazionale, richiedi:

- un test funzionale che preservi il risultato;
- input sufficientemente grandi da esercitare il percorso problematico;
- verifica di ordine, duplicati e nullabilità quando rilevanti;
- verifica che non vengano ripetute chiamate downstream equivalenti;
- un benchmark JMH o una misurazione riproducibile soltanto quando il
  miglioramento non è deducibile chiaramente dalla complessità algoritmica.

Non richiedere benchmark per ogni mapper.

Per miglioramenti algoritmici evidenti, come il passaggio da scansioni annidate
a un indice costruito una sola volta, è sufficiente:

- descrivere la variazione della complessità;
- aggiungere test funzionali;
- verificare la preservazione della semantica.

### Test negativi di autorizzazione

Quando cambiano identità, ruolo, gruppi, mandati, documenti, pagamenti,
consensi o chiavi, richiedi i casi pertinenti:

- identità mancante;
- identità incoerente;
- ruolo non autorizzato;
- gruppo non appartenente al soggetto;
- mandato mancante, scaduto o di un altro soggetto;
- accesso a una risorsa di un altro utente o ente;
- IUN o document ID valido ma non autorizzato;
- attachment index valido ma non autorizzato;
- risposta downstream `401`;
- risposta downstream `403`;
- fallback non applicato a errori di autorizzazione;
- risposta di errore priva di dati sensibili.

La sola assenza del test non è bloccante, salvo che il codice mostri già un
bypass concreto.

## Formato dei commenti Java

Ogni commento deve:

1. iniziare con la severità;
2. indicare layer, classe o metodo interessato;
3. descrivere lo scenario concreto;
4. spiegare l'impatto;
5. proporre la correzione minima;
6. indicare un test quando utile.

Esempio di autorizzazione:

> **[BLOCCANTE][AUTORIZZAZIONE]** Il controller riceve `mandateId`, ma il
> service non lo propaga al `pnclient`. Un destinatario che opera come delegato
> può quindi richiedere il documento tramite un IUN valido senza applicare il
> vincolo del mandato. Propaga `mandateId` fino al client generato e aggiungi un
> test negativo con un mandato appartenente a un altro soggetto.

Esempio di complessità:

> **[ALTA][AFFIDABILITÀ]** Questa utility esegue `findFirst()` sull'intera lista
> dei destinatari per ogni elemento della timeline, portando il mapping a
> complessità `O(n × m)` nel percorso di dettaglio della notifica. Costruisci una
> sola volta una mappa indicizzata per identificativo e riutilizzala durante il
> mapping, preservando il comportamento in presenza di duplicati.

Esempio di allocazioni:

> **[MEDIA][CORRETTEZZA]** Il mapper crea e materializza tre liste intermedie
> consecutive, ma soltanto il risultato finale viene utilizzato. Combina le
> trasformazioni in una singola pipeline per evitare copie proporzionali al
> numero di notifiche, mantenendo ordine e duplicati.

Esempio N+1:

> **[ALTA][AFFIDABILITÀ]** L'arricchimento invoca il servizio downstream una
> volta per ogni pagamento. Su una risposta con molti elementi questo genera un
> pattern N+1 e una quantità non limitata di chiamate. Utilizza l'API batch, se
> disponibile, oppure deduplica le chiavi e limita esplicitamente la concorrenza.

Non produrre commenti generici come:

- “valutare un refactoring”;
- “ottimizzare questo metodo”;
- “usare uno Stream”;
- “aggiungere più test”;
- “considerare una cache”.

Indica sempre percorso, input, costo, impatto, correzione e vincoli semantici da
preservare.