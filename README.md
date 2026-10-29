# PN-BFF

Backend for frontend della piattaforma SEND

## Istruzioni per la compilazione

```bash
    ./mvnw clean install
```

*Aggiungendo il parametro `-DskipTests` si può evitare di eseguire i test*

## Istruzioni per il run

```bash
    ./mvnw spring-boot:run
```

## Istruzioni per i test

Per eseguire i test unitari:

```bash
    ./mvnw test
```

Per eseguire i test di integrazione:

```bash
    ./mvnw verify
```

## Tipi utente ammessi sulle API (`x-pn-allowed-cx-types`)

Ogni operazione delle API WEB (file `docs/openapi/api-internal-*.yaml` con `intendedUsage: WEB` in
`codegen/config.json`) dichiara quali tipi utente (`cx_type`) possono invocarla, con l'extension
`x-pn-allowed-cx-types` accanto all'`operationId`:

```yaml
  /bff/v1/mandate:
    post:
      operationId: createMandateV1
      x-pn-allowed-cx-types: [PF, PG]
```

I valori ammessi sono `PF`, `PA`, `PG` e `BS` (utenti di supporto). L'extension va messa solo negli
internal: external e bundle AWS (`docs/openapi/aws/api-bff-WEB-aws.yaml`) la ricevono dalla
rigenerazione con `scripts/generate-code.sh`.

Dai tag derivano le policy IAM che il `jwtAuthorizer` di `pn-auth-fleet` restituisce per ogni
`cx_type`, salvate in `docs/openapi/policies/generated/<cxType>Policy.json`. Un'operazione senza tag
non compare in nessuna policy, quindi è negata a tutti (default deny): ogni nuova API deve avere il
tag.

### Aggiornamento delle policy

Le policy non vanno modificate a mano: le genera il `policyGenerator` di
[`pn-codegen`](https://github.com/pagopa/pn-codegen), abilitato con `"generatePolicies": true` sul
blocco `WEB` di `codegen/config.json`. Rigenerando l'OpenAPI con `scripts/generate-code.sh`, il
generator legge `x-pn-allowed-cx-types` dal bundle AWS e riscrive un file per ogni `cx_type`
presente nei tag (i path parameter diventano `*` nella `Resource`). I file di `cx_type` non più
usati da nessuna operazione vengono rimossi.

Le policy di questo repo vengono poi copiate nel `jwtAuthorizer` dallo script di CI/CD di
`pn-auth-fleet`.
