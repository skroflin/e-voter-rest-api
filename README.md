# e-Voting REST API

Sigurni REST API za elektroničko glasanje s kriptografskim potpisima glasova i verifikacijom identiteta korisnika.

---

## Sadržaj

- [Korištene Tehnologije](#korištene-tehnologije)
- [Konfiguracija i Povezanost s Bazom](#konfiguracija-i-povezanost-s-bazom)
- [Migracije Baze Podataka](#migracije-baze-podataka)
- [Ed25519 Algoritam](#ed25519-algoritam)
- [BouncyCastle Biblioteka](#bouncycastle-biblioteka)
- [Autentifikacija i JWT](#autentifikacija-i-jwt)
- [Verifikacija Korisnika](#verifikacija-korisnika)
- [Pokretanje Projekta](#pokretanje-projekta)
- [Arhitektura](#arhitektura)
- [Primjer Toka Glasanja](#primjer-toka-glasanja)
- [Kritički Osvrt i Sigurnosna Ograničenja](#kritički-osvrt-i-sigurnosna-ograničenja)

---

## Korištene Tehnologije

### Backend Framework i Runtime
- **Java 17** – Jezgra aplikacije
- **Spring Boot 3.x / 4.x** – Framework za brz razvoj Spring aplikacija
- **Spring Framework 6.x** – Dependency Injection, konfiguracija i jezgrene funkcionalnosti

### Baza Podataka
- **PostgreSQL 15** – Relacijska baza podataka
- **Spring Data JPA** – ORM sloj za pristup i rad s bazom podataka
- **Flyway** – Alat za upravljanje migracijama baze podataka

### Sigurnost i Autentifikacija
- **Spring Security 6.x** – Framework za sigurnost i autorizaciju
- **JJWT (JSON Web Tokens) v0.13.0** – Izrada i validacija JWT tokena
  - `jjwt-api` – API za JWT
  - `jjwt-impl` – Implementacija JWT-a
  - `jjwt-jackson` – Integracija s Jackson JSON bibliotekom
- **BouncyCastle 1.83** – Kriptografska biblioteka (detaljnije u nastavku)
- **BCrypt** – Sažimanje i heširanje zaporki

### Ostale Biblioteke
- **MapStruct 1.6.3** – Mapiranje između DTO objekata i entiteta
- **Lombok** – Smanjenje boilerplate koda
- **Jackson (Jackson-datatype-jsr310)** – Serijalizacija/deserijalizacija JSON-a s Java Time API-jem
- **Spring Mail** – Slanje verifikacijskih e-poruka

### DevOps i Kontejnjerizacija
- **Docker** – Kontejnjerizacija aplikacije
- **Docker Compose** – Orkestracijski alat za pokretanje aplikacije i baze podataka

---

## 🔌 Konfiguracija i Povezanost s Bazom

### Konfiguracija Baze Podataka

Konfiguracija se provodi putem datoteke `application.properties`:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/e_voting_db
spring.datasource.username=e_voting_user
spring.datasource.password=tuh5phu+T=Stls_u6rap

```

### Spring Data JPA

Aplikacija koristi **Spring Data JPA** kao ORM sloj, što omogućuje:

* Automatsko generiranje SQL upita
* Repository uzorak s ugrađenim CRUD operacijama
* Pronalaženje entiteta putem prilagođenih deklarativnih metoda

Primjer repozitorija:

```java
public interface EligibleVoterRepository extends JpaRepository<EligibleVoter, UUID> {
    Optional<EligibleVoter> findByUsername(String username);
    Optional<EligibleVoter> findByEmail(String email);
}

```

### Flyway Migracije

Flyway je konfiguriran za:

* Automatsku primjenu migracija pri pokretanju aplikacije
* Praćenje verzija baze podataka
* Reproducibilnost i verzioniranje strukture baze

Konfiguracija:

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

```

---

## Migracije Baze Podataka

Migracije su organizirane po verzijama i nalaze se u direktoriju `/src/main/resources/db/migration/`.

### V1__.sql – Inicijalna Šema

Kreira sve osnovne tabele sustava:

* **admin_users** – Administratori sustava
* **eligible_voters** – Registrirani birači
* **elections** – Izbori s pripadajućim parametrima
* **candidates** – Kandidati definirani za pojedine izbore
* **votes** – Glasovi s podacima o izboru i kandidatu
* **issued_tokens** – Izdani tokeni za glasanje
* **used_tokens** – Iskorišteni tokeni (sprječavanje višestrukog glasanja)
* **user_verification** – Kodovi za verifikaciju korisničkih računa

### V2__alter_table_votes.sql – Dodavanje Digitalnog Potpisa

```sql
ALTER TABLE votes
    ADD COLUMN signature TEXT;

```

**Svrha**: Dodaje kolonu za pohranu Ed25519 digitalnog potpisa svakog glasa. Potpisani glas omogućuje:

* Verifikaciju integriteta glasa
* Neporicivost (*proof of authenticity*)
* Detekciju neovlaštene izmjene glasa nakon pohrane

### V3__create_table_election_participations.sql – Praćenje Sudjelovanja

```sql
CREATE TABLE election_participations (
    election_participation_uuid UUID PRIMARY KEY,
    voter_uuid UUID NOT NULL,
    election_uuid UUID NOT NULL,
    voted_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_voter FOREIGN KEY (voter_uuid) REFERENCES eligible_voters(voter_uuid),
    CONSTRAINT fk_election FOREIGN KEY (election_uuid) REFERENCES elections(election_uuid)
);

```

**Svrha**:

* Prati koji su birači glasali na kojim izborima
* Onemogućuje dvostruko glasanje putem jedinstvenog ograničenja na `(voter_uuid, election_uuid)`
* Bilježi vremensku oznaku kada je glas predan

### V4__remove_unique_from_issued_tokens.sql – Optimizacija Tokena

```sql
ALTER TABLE issued_tokens
DROP CONSTRAINT uc_issued_tokens_voter_uuid;

ALTER TABLE election_participations
    ADD CONSTRAINT uc_election_participation_voter_election
        UNIQUE (voter_uuid, election_uuid);

```

**Svrha**:

* Omogućuje generiranje više tokena po biraču (npr. u slučaju isteka starog tokena)
* Prebacuje kontrolu jedinstvenosti glasa na tablicu `election_participations`
* Omogućuje fleksibilnije upravljanje životnim ciklusom tokena

---

## Ed25519 Algoritam

### Što je Ed25519?

Ed25519 je asimetrični algoritam za digitalno potpisivanje temeljen na Edwards eliptičkoj krivulji (Curve25519), koji je razvio Daniel J. Bernstein. Odlikuje se:

* **Visokim performansama** – Izuzetno brza izrada i verifikacija potpisa
* **Visokom razinom sigurnosti** – Otporan na mnoge standardne napade na eliptičkim krivuljama
* **Kompaktnošću** – Kratki ključevi i potpisi visoke razine zaštite

### Implementacija u Projektu

Ed25519 se u aplikaciji koristi za digitalno potpisivanje predanih glasova. Implementacija se nalazi u `CryptoServiceImpl.java`.

#### Inicijalizacija Ključeva

Inicijalizacija pri pokretanju aplikacije (`@PostConstruct`) generira Ed25519 par ključeva:

```java
@PostConstruct
public void init() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519", "BC");
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    this.privateKey = keyPair.getPrivate();
    this.publicKey = keyPair.getPublic();
}

```

#### Potpisivanje Glasa

```java
public String generateSignature(Vote vote) {
    Signature signature = Signature.getInstance("Ed25519", "BC");
    signature.initSign(privateKey);
    
    String dataToSign = String.format(
        "%s:%s:%s",
        vote.getElection().getElectionUUID(),
        vote.getCandidate().getCandidateUUID(),
        vote.getCastAt().format(DATE_FORMATTER)
    );
    
    signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(signature.sign());
}

```

Podaci koji se potpisuju obuhvaćaju:

* Identifikator izbora (`Election UUID`)
* Identifikator kandidata (`Candidate UUID`)
* Vrijeme glasanja (`Cast Time`)

#### Verifikacija Potpisa

```java
public boolean verifySignature(Vote vote, String sigHex) {
    Signature signature = Signature.getInstance("Ed25519", "BC");
    signature.initVerify(publicKey);
    
    String dataToSign = String.format(
        "%s:%s:%s",
        vote.getElection().getElectionUUID(),
        vote.getCandidate().getCandidateUUID(),
        vote.getCastAt().format(DATE_FORMATTER)
    );
    
    signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
    byte[] signatureBytes = HexFormat.of().parseHex(sigHex);
    return signature.verify(signatureBytes);
}

```

---

## BouncyCastle Biblioteka

### Šta je BouncyCastle?

BouncyCastle je sveobuhvatna kriptografska biblioteka otvorenog koda za Javu koja proširuje *Java Cryptography Architecture* (JCA) i pruža podršku za napredne algoritam i standarde.

### Integracija

Registracija BouncyCastle pružatelja usluga u aplikaciji:

```java
Security.addProvider(new BouncyCastleProvider());

```

Uključivanje u `pom.xml`:

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
    <version>1.83</version>
</dependency>

```

---

## Autentifikacija i JWT

API koristi JSON Web Tokene (JWT) za siguran prijenos autentičnosti između klijenta i poslužitelja.

* **Tajni ključ**: BASE64 kodirani ključ iz konfiguracije
* **Trajanje**: 7200000 ms (2 sata)
* **Sadržaj (Claims)**: Korisničko ime, uloge i vrijeme izdavanja
* **Zaporke**: Enkriptirane i heširane BCrypt algoritmom

---

## ✉️ Verifikacija Korisnika

Sustav provodi verifikaciju e-pošte s vremenskim ograničenjem koda od 15 minuta:

1. Registracija korisnika
2. Slanje 6-znamenkastog verifikacijskog koda putem e-pošte
3. Potvrda koda od strane korisnika
4. Aktivacija korisničkog računa i dozvola za pristup glasanju

---

## Pokretanje Projekta

### Docker Compose (Preporučeno)

```bash
docker-compose up -d

```

### Lokalno pokretanje

```bash
mvn clean install
mvn spring-boot:run

```

---

## Arhitektura

```
┌─────────────────────────────────────────┐
│             REST Controllers            │
│   (AuthController, VoteController...)   │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│         Business Logic Services         │
│  (VoteService, AuthService, Crypto...)  │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│       Spring Data JPA Repositories      │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│         PostgreSQL Baza Podataka        │
└─────────────────────────────────────────┘

```

---

## Kritički Osvrt i Sigurnosna Ograničenja

Iako ovaj e-Voting REST API donosi osnovne funkcionalnosti za provjeru autentičnosti i integriteta podataka (putem Ed25519 digitalnih potpisa i relacijskih ograničenja protiv dvostrukog glasanja), **sustav nije u potpunosti siguran za primjenu u realnim javnim izborima visoke razine tajnosti**.

Glavni nedostatci i sigurnosna ograničenja sustava obuhvaćaju:

1. **Središnje potpisivanje na poslužitelju (*Server-side signing*)**:
* Trenutačna implementacija generira privatni Ed25519 ključ u memoriji same poslužiteljske aplikacije (`CryptoServiceImpl`). To znači da poslužitelj posjeduje ovlast i tehničku mogućnost samostalnog potpisivanja proizvoljnih glasova. U produkcijskom e-voting sustavu potpisivanje mora izvoditi isključivo klijentski uređaj birača (korištenjem klijentskih kriptografskih ključeva).


2. **Izostanak Homomorfne Enkripcije i ZKP-a**:
* Glasovi se u bazi pohranjuju u jasnom ili izravno čitljivom obliku (uz pripadajući kandidatski UUID). Zbog toga administratori baze ili poslužitelja tehnički mogu vidjeti tko je i kako glasao.
* Za potpunu anonimnost i tajnost glasanja potrebno je implementirati **homomorfnu enkripciju** (koja omogućuje prebrojavanje i agregaciju bez dešifriranja pojedinačnih glasova) ili **dokaze bez otkrivanja znanja** (*Zero-Knowledge Proofs – ZKP*), čime se omogućuje verifikacija valjanosti glasa bez otkrivanja izabrane opcije.


3. **Zaštita od prisile (*Coercion Resistance*)**:
* Sustav ne nudi kriptografsku zaštitu u slučaju da je birač prisiljen glasati pod nadzorom treće strane (npr. izostaje mehanizam ponovnog glasanja gdje važi samo posljednji predani glas).



Ovaj API služi kao funkcionalni demonstracijski model i konceptualni okvir (*Proof of Concept*), no za operativnu upotrebu zahtijeva nadogradnju klijentskim potpisivanjem i naprednim kriptografskim protokolima za očuvanje privatnosti.
