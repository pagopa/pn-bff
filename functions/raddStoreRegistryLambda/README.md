## radd-store-registry-lambda

## Env

| **Environment Variable**               | **Default**             | **Required** |
| -------------------------------------- | ----------------------- | :----------: |
| BFF_BUCKET_NAME                        |                         |     yes      |
| BFF_BUCKET_PREFIX                      | radd/store              |      no      |
| WEB_LANDING_BUCKET_NAME                |                         |     yes      |
| WEB_LANDING_BUCKET_PREFIX              | public/static/documents |      no      |
| FILE_NAME                              | radd-store-registry     |      no      |
| CSV_CONFIGURATION_PARAMETER            |                         |     yes      |
| GENERATE_INTERVAL                      | 30                      |      no      |
| RADD_STORE_GENERATION_CONFIG_PARAMETER |                         |      no      |
| RADD_STORE_REGISTRY_API_URL            |                         |     yes      |
| MALFORMED_ADDRESS_THRESHOLD            | 0.7                     |     yes      |

## Istruzioni per la gestione delle configurazioni per il csv dello store locator

I campi che è possibile inserire all'interno del csv prelevandoli dall'entità Pn-RaddRegistry (tabella contenente i punti di ritiro SEND presenti sul territorio)
sono i seguenti:

```
    - normalizedAddress : oggetto contenente i campi relativi all'indirizzo del punto di ritiro normalizzato da AWS Location.
    - address : Indirizzo inserito dal partner, che può essere diverso da quello normalizzato.
    - partnerId : C.F. del partner erogatore.
    - locationId : Identificativo univoco della sede del punto di ritiro.
    - description : Descrizione del punto di ritiro.
    - phoneNumbers : array di numeri di telefono del punto di ritiro.
    - email : email del punto di ritiro.
    - openingTime :  stringa o oggetto (chiave: giorno, valore: orari) contenente gli orari di apertura del punto di ritiro.
    - monday : es.09:00-13:00,14:00-18:00
    - tuesday : es.09:30-12:30
    - wednesday : es.09:00-18:00
    - thursday : es.09:00-13:00,14:00-18:00
    - friday : es.09:00-13:00,14:00-18:00
    - saturday : es.09:00-13:00,14:00-18:00
    - sunday : es.09:00-13:00,14:00-18:00
    - startValidity : data di inizio di validità del punto di ritiro in formato (yyyy-MM-dd), se non inviata viene considerata la data corrente.
    - endValidity : data di fine di validità dello sportello in formato (yyyy-MM-dd).
    - externalCodes : identificativo univoco dell’ufficio/postazione nella sede.
    - appointmentRequired : Variabile booleana per indicare se necessario prendere appuntamento.
    - website : Link al sito del punto di ritiro.
    - partnerType : Tipologia del punto di ritiro.
    - creationTimestamp : Istante di creazione del punto di ritiro.
    - updateTimestamp : Istante dell'ultima modifica del punto di ritiro.

```

I campi `normalizedAddress` e `address` sono oggetti che contengono i seguenti campi:

```
    - addressRow : Indirizzo completo
    - cap : CAP (Codice di Avviamento Postale).
    - city : Città.
    - province : Provincia.
    - country : Paese.
    - latitude : Latitudine della posizione geografica dello sportello.
    - longitude : Longitudine della posizione geografica dello sportello.
    - biasPoint : Punti di bias per la normalizzazione dell'indirizzo restituiti dal servizio di geocoding in valori compresi tra 0 e 1.
```

La configurazione da inserire sul parameter store nel parametro `/pn-radd-store-registry-lambda/csv-configuration` deve avere la seguente struttura:

```
    - header → nome della colonna del csv di output
    - field → nome del campo su radd-alt (uno dei valori indicati sopra)
```

```
{
    "configurationVersion": "1", //ogni nuova configurazione deve incrementare la version
    "configs":[
        {
            "header": "descrizione", //nome della colonna del csv
            "field": "description" //nome del campo su radd-alt (uno dei valori indicati sopra)
        },
        {
            "header": "città",
            "field": "city"
        },
        {
            "header": "cap",
            "field": "zipCode"
        },
        {
            "header": "via",
            "field": "address"
        },
        {
            "header": "provincia",
            "field": "province"
        },
        {
            "header": "URL"
        },
        {
            "header": "Email"
        },
        {
            "header": "Type"
        },
        {
            "header": "telefono",
            "field": "phoneNumber"
        }
    ]
}
```

**N.B**

1. Il campo Field può essere popolato solo con uno dei valori del punto 1.
   Qualora per un determinato header non sia presente il campo corrispondente su radd-alt, il campo "field" non dovrà essere inserito.
   Qualora fosse inserito all'interno del campo Field un valore non valido, la corrispondente colonna del csv non sarà popolata.

2. Ogni nuova configurazione deve incrementare il campo version.
   Questo parametro è fondamentale per avviare una nuova generazione quando la struttura del csv viene modificata,
   anche nel caso in cui non sia trascorso l’intervallo di tempo configurato
