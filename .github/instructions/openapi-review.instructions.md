---
applyTo: "docs/openapi/**/*.yaml"
---

# Istruzioni per la review OpenAPI

Applica severità, criteri di sicurezza e formato dei commenti definiti nelle
instructions globali.

## Operazioni e compatibilità

Per ogni operazione modificata verifica:

- stabilità e unicità dell'`operationId`;
- compatibilità di path e metodo HTTP;
- header di identità e autorizzazione;
- tipi, formati, limiti, default e obbligatorietà dei parametri;
- schema e nullabilità di request e response;
- status code di successo ed errore;
- enum e compatibilità con i consumer;
- paginazione e filtri;
- minimizzazione dei dati restituiti.

Segnala come `[ALTA][CONTRATTO]` rimozioni o rinomine incompatibili non
esplicitamente versionate di operazioni, parametri, campi, enum, header o
response code.

## Autorizzazione nel contratto

Segnala come `[BLOCCANTE][AUTORIZZAZIONE]` una modifica che:

- rimuove o rende facoltativo un header necessario all'autorizzazione;
- rimuove gruppi o `mandateId` da un'operazione che li richiede;
- sostituisce l'identità autenticata con un'identità nel body;
- rende esterno un endpoint interno;
- consente l'accesso a una risorsa tramite il solo identificativo;
- elimina il contesto necessario a verificare ownership o delega;
- documenta come successo un accesso negato contenente dati protetti.

Presta particolare attenzione a notifiche, documenti, pagamenti, mandati,
consensi, API key, public key e virtual key.

## Contratti internal ed external

Confronta i corrispondenti file `api-internal-*` e `api-external-*`.

Segnala divergenze non intenzionali relative a:

- operazioni esposte;
- header e parametri;
- obbligatorietà e nullabilità;
- status code;
- modelli ed enum;
- dati interni o sensibili;
- vincoli di autorizzazione.

Internal ed external non devono necessariamente essere identici. Commenta solo
le differenze che producono una regressione, un'incompatibilità o una riduzione
dei controlli.

## `$ref` e schemi

Verifica che:

- file e schemi referenziati esistano;
- i percorsi relativi siano corretti;
- i riferimenti siano risolvibili dalla build;
- non rimangano riferimenti obsoleti dopo una rinomina;
- non vengano create duplicazioni divergenti;
- le specifiche remote siano fissate a versioni o commit immutabili.

Segnala come `[ALTA][CONTRATTO]` riferimenti non risolvibili o modifiche che
impediscono la generazione del codice.

## Naming dei modelli BFF

I modelli definiti specificamente dal BFF devono iniziare con il prefisso `Bff`.

Esempi:

- `BffNotificationDetail`;
- `BffDocumentDownloadMetadataResponse`;
- `BffPaymentInfo`.

Segnala come `[ALTA][CONTRATTO]`:

- nuovi modelli BFF senza prefisso;
- rimozione del prefisso da un modello modificato;
- naming incoerente tra modelli equivalenti;
- `$ref` aggiornati solo parzialmente dopo una rinomina.

La regola non si applica ai modelli dei microservizi referenziati direttamente,
che devono mantenere il nome definito dal servizio proprietario.

Non richiedere la rinomina retroattiva di modelli non interessati dalla pull
request.

## Posizione dei modelli

Nei file `api-internal-*.yaml` non devono normalmente essere definite strutture
applicative non banali.

I modelli riutilizzabili o complessi devono stare in file di schema separati ed
essere inclusi tramite `$ref`.

Segnala:

- modelli complessi sotto `components/schemas` del file internal;
- request o response complesse definite inline;
- modelli duplicati in più file API;
- concetti condivisi non estratti nella directory di schema appropriata.

È ammessa un'eccezione per modelli molto semplici, locali a una sola operazione e
con scarsa probabilità di riuso.

Un modello può essere considerato molto semplice quando:

- contiene pochi campi scalari;
- non contiene oggetti annidati;
- non usa `allOf`, `oneOf` o `anyOf`;
- non contiene collezioni di oggetti complessi;
- non rappresenta un concetto condiviso;
- non è usato da più operazioni.

Classifica normalmente la posizione errata come `[MEDIA][CORRETTEZZA]`.
Utilizza `[ALTA][CONTRATTO]` se causa divergenze, riferimenti non validi o
generazione errata.

## Riuso dei modelli dei microservizi

Dove possibile, referenzia i modelli canonici dei microservizi invece di
definire nuovi modelli BFF equivalenti.

Prima di suggerire il riuso verifica che:

- il modello downstream abbia la stessa semantica;
- campi, enum, required e nullabilità siano compatibili;
- il riferimento sia stabile e risolvibile;
- non vengano esposti campi interni o sensibili;
- il BFF non debba evolvere indipendentemente.

Segnala:

- duplicazioni integrali di modelli downstream;
- copie locali già divergenti;
- enum ridefinite senza trasformazione;
- riferimenti a specifiche mobili;
- duplicazioni manuali evitabili con `$ref`.

Un modello BFF dedicato è appropriato quando il BFF:

- aggrega più servizi;
- filtra, rinomina, arricchisce o trasforma campi;
- deve nascondere dati interni;
- espone un sottoinsieme intenzionale;
- ha semantica o nullabilità differenti;
- deve mantenere un contratto indipendente e stabile.

Non suggerire il riuso se amplia la superficie dati esposta.

## Required, nullabilità ed enum

Verifica separatamente:

- presenza;
- obbligatorietà;
- possibilità di `null`;
- lista vuota;
- default.

Un default non deve ampliare ruolo, gruppo o accesso.

Per le enum verifica compatibilità downstream, nuovi valori, rimozioni e
gestione dei valori sconosciuti.

## Dati sensibili

Segnala modelli che espongono dati non necessari, in particolare:

- identificativi utente interni;
- dati completi di destinatari o delegati;
- dettagli di pagamento o consenso;
- contenuti o metadata sensibili dei documenti;
- dettagli completi degli errori downstream.

Token, segreti, credenziali AWS o chiavi private nel contratto sono
`[BLOCCANTE][SICUREZZA]`.

## Impatto applicativo

Quando cambia un contratto verifica, se pertinente, l'aggiornamento di:

- controller;
- service;
- `pnclient`;
- mapper;
- test;
- configurazione OpenAPI nel `pom.xml`;
- corrispondente contratto internal o external.

Non suggerire modifiche manuali al codice generato.