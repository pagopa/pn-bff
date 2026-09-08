---
applyTo: "docs/openapi/api-external-*.yaml,docs/openapi/aws/*.yaml"
---

# Istruzioni per la review degli artefatti OpenAPI generati

Applica severità, sicurezza, privacy, gestione dei dubbi, lingua e formato dei commenti definiti in
`.github/copilot-instructions.md`.

Questi file sono generati e non devono essere corretti manualmente:

- `docs/openapi/api-external-*.yaml`;
- `docs/openapi/aws/*.yaml`.

Le fonti sono:

- `docs/openapi/api-internal-*.yaml`;
- `docs/openapi/*-schemas/*.yaml`;
- `docs/openapi/common-refs.yaml`;
- `codegen/config.json`;
- versione e configurazione di `pn-codegen`.

## Obiettivo della review

Non eseguire una seconda review completa del contratto.

Verifica soltanto:

1. riproducibilità dell'output;
2. coerenza con fonti, marker e codegen;
3. assenza di modifiche manuali isolate;
4. mantenimento della sicurezza;
5. validità YAML, OpenAPI e `$ref`.

Indica sempre la fonte o configurazione da correggere. Non suggerire modifiche
dirette all'artefatto.

## Differenze previste

Il codegen:

- rimuove le righe `# NO EXTERNAL`;
- abilita le righe `# ONLY EXTERNAL`;
- rimuove dall'external gli header internal;
- abilita `bearerAuth` nell'external.

Non segnalare:

- differenze testuali tra internal ed external;
- assenza degli header internal nell'external;
- presenza di `bearerAuth` soltanto nell'external;
- variazioni meccaniche di ordine o formattazione;
- differenze intenzionali prodotte dai marker.

## Riproducibilità

Una modifica a un artefatto deve derivare da una modifica coerente a:

- sorgente internal;
- schema o `common-refs.yaml`;
- `codegen/config.json`;
- versione o configurazione del codegen.

Segnala come `[HIGH][CONTRACT]`:

- modifiche presenti soltanto nell'output;
- modifiche non derivabili dalle fonti;
- rigenerazioni parziali;
- output correlati prodotti con versioni differenti;
- modifiche manuali destinate a essere perse.

Chiedi di correggere la fonte e rigenerare tutti gli artefatti interessati.

## Marker e sicurezza external

Verifica che:

- `# NO EXTERNAL` non lasci contenitori YAML non validi;
- `# ONLY EXTERNAL` abiliti completamente la security;
- endpoint protetti mantengano `bearerAuth`;
- parametri internal non siano esposti;
- endpoint esclusivamente internal non diventino external;
- modelli e `$ref` necessari non vengano rimossi.

Classifica come `[BLOCKING][AUTHORIZATION]` un output che:

- espone un endpoint protetto senza `bearerAuth`;
- rende external un'operazione solo internal;
- consente al consumer di fornire direttamente un'identità internal;
- espone dati protetti a causa di un marker errato.

Token, credenziali o segreti nell'output sono
`[BLOCKING][SECURITY]`.

## Artefatti AWS

Per `docs/openapi/aws/*.yaml` verifica coerenza con:

- sorgenti elencate in `codegen/config.json`;
- `intendedUsage`;
- `servicePath`;
- path e operation ID;
- security;
- throttling;
- configurazione API Gateway;
- utilizzo `WEB` o `PUBLIC`.

Segnala come `[HIGH][CONTRACT]` operazioni mancanti, output nel servizio
errato, riferimenti non validi o modifiche prive di una fonte corrispondente.

Usa una severità bloccante se l'output AWS rende concretamente pubblico un
endpoint protetto.

## Validità dell'output

Verifica:

- YAML valido;
- versione OpenAPI supportata;
- operation ID univoci;
- `$ref` risolvibili;
- assenza di blocchi vuoti prodotti dai marker;
- required, nullabilità, enum e formati coerenti con la fonte;
- request e response non perse;
- assenza di definizioni duplicate incompatibili.

Non commentare ogni variazione meccanica di un aggiornamento del codegen. In
presenza di un problema sistemico, punta alla versione o configurazione che lo
produce.

## Aggiornamento del codegen

Quando cambia `pagopa.codegen.version` verifica che:

- il cambio sia intenzionale;
- tutti gli output siano rigenerati uniformemente;
- non vengano persi security, path, response, marker o `$ref`;
- non siano mescolate modifiche manuali;
- la generazione rimanga riproducibile.

## Verifiche richieste

Quando pertinente, richiedi:

- esecuzione del codegen;
- working tree pulito dopo una seconda generazione;
- validazione YAML/OpenAPI;
- risoluzione dei `$ref`;
- `bearerAuth` sugli endpoint protetti;
- assenza nell'external dei parametri `# NO EXTERNAL`;
- rigenerazione coerente degli output AWS.

La sola assenza di automazione non è bloccante.

## Focus del commento

Tutti i commenti devono essere scritti in inglese.

Indica sempre:

- artefatto interessato;
- fonte, marker o configurazione probabile;
- motivo per cui l'output non è riproducibile o sicuro;
- correzione da applicare alla fonte;
- artefatti da rigenerare.

Non produrre commenti come:

- `Align the internal and external specifications`;
- `Fix this generated file directly`;
- `Regenerate everything`, without identifying the underlying cause;
- `Add the internal headers to the external specification`.