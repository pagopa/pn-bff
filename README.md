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