## radd-store-registry-lambda

## Env
| **Environment Variable**               | **Default**             | **Required** |
|----------------------------------------|-------------------------|:------------:|
| BFF_BUCKET_NAME                        |                         |     yes      |
| BFF_BUCKET_PREFIX                      | radd/store              |     no       |
| WEB_LANDING_BUCKET_NAME                |                         |     yes      |
| WEB_LANDING_BUCKET_PREFIX              | public/static/documents |     no       |
| FILE_NAME                              | radd-store-registry     |     no       |
| CSV_CONFIGURATION_PARAMETER            |                         |     yes      |
| GENERATE_INTERVAL                      | 7                       |     no       |
| RADD_STORE_GENERATION_CONFIG_PARAMETER |                         |     no       |
| RADD_STORE_REGISTRY_API_URL            |                         |     yes      |
| AWS_LOCATION_REGION                    | eu-central-1            |     yes      |
| AWS_LOCATION_REQUESTS_PER_SECOND       | 95                      |     no       |

## Istruzioni per la gestione delle configurazioni per il csv dello store locator

I campi che è possibile inserire all'interno del csv prelevandoli dall'entità Pn-RaddRegistry (tabella contenente i punti di ritiro SEND presenti sul territorio)
sono i seguenti:
```
    - description
    - city
    - address
    - province
    - zipCode
    - phoneNumber
    - openingTime :  orari di apertura con struttura Mon=09:00-12:00_16:00-19:00#Tue=09:00-19:00.
    - monday : es.09:00-12:00_16:00-19:00
    - tuesday : es.09:00-12:00_16:00-19:00
    - wednesday : es.09:00-12:00_16:00-19:00 
    - thursday : es.09:00-12:00_16:00-19:00
    - friday : es.09:00-12:00_16:00-19:00
    - saturday : es.09:00-12:00_16:00-19:00
    - sunday : es.09:00-12:00_16:00-19:00
    - latitude
    - longitude
```

La configurazione da inserire sul parameter store nel parametro `/pn-radd-store-registry-lambda/csv-configuration` deve avere la seguente struttura:

```
    - header → nome della colonna del csv di output 
    - field → nome del campo su radd-alt (uno dei valori al punto 1)
 ```   

```
{
    "configurationVersion":"1", //ogni nuova configurazione deve incrementare la version
    "configs":[
        {
            "header": "descrizione", //nome della colonna del csv
            "field": "description" //nome del campo su radd-alt (uno dei valori al punto 1)
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

4. Ogni nuova configurazione deve incrementare il campo version.
   Questo parametro è fondamentale per avviare una nuova generazione quando la struttura del csv viene modificata,
   anche nel caso in cui non sia trascorso l’intervallo di tempo configurato

