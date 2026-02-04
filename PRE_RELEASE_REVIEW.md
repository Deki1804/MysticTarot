# MysticTarot – Review prije prvog slanja u Closed Testing

Datum pregleda: prije prvog uploada u Google Play Closed Testing.

**Napomena:** Testni AdMob ID-ovi i RevenueCat placeholder su namjerno ostavljeni dok testiraš; prije productiona ih zamijeni pravim vrijednostima (vidi 1.2 i 2.1).

---

## 1. MUST FIX – bez ovoga app neće raditi / ne smije u production

### 1.1 Firebase Web Client ID (Google Sign-In)
- **Lokacija:** `app/src/main/res/values/strings.xml`
- **Problem:** `default_web_client_id` je `YOUR_WEB_CLIENT_ID_HERE`
- **Što napraviti:** U Firebase Console → Project Settings → Your apps → Web app → kopiraj **Web client ID**. Zalijepi u `strings.xml` kao vrijednost za `default_web_client_id`. Bez ovoga Google Sign-In pada (Resources$NotFoundException ili auth ne radi).

### 1.2 AdMob – pravi ID-ovi (obavezno za production; za test ostavi test ID-ove)
- **AndroidManifest:** `com.google.android.gms.ads.APPLICATION_ID` – trenutno **test** ID (namjerno za testiranje).
- **AdManager.kt:** `AD_UNIT_ID` – trenutno **test** ID (namjerno za testiranje).
- **Prije productiona:** U AdMob kreiraj app i rewarded ad unit, u manifest stavi pravi App ID, u AdManager.kt pravi Rewarded ad unit ID.

### 1.3 Release signing – keystore
- **Lokacija:** `app/build.gradle.kts` (signingConfigs) + root projekta: `keystore.properties`
- **Problem:** Ako `keystore.properties` ne postoji, **release build će srušiti** Gradle (null keyAlias). `.gitignore` već isključuje `keystore.properties` (ispravno).
- **Što napraviti:**
  1. Generiraj release keystore:  
     `keytool -genkey -v -keystore mystic-release.keystore -alias mystic -keyalg RSA -keysize 2048 -validity 10000`
  2. Kreiraj u rootu projekta `keystore.properties` (samo lokalno, ne commitaj):
     ```properties
     storeFile=mystic-release.keystore
     storePassword=***
     keyAlias=mystic
     keyPassword=***
     ```
  3. **Napravljeno:** U `build.gradle.kts` je signingConfig postavljen **samo ako** `keystore.properties` postoji – `assembleDebug` sada radi i bez keystore-a; `bundleRelease` zahtijeva keystore.

---

## 2. PREPORUČENO – za closed testing / prije productiona

### 2.1 RevenueCat
- **BillingManager.kt:** `apiKey = "goog_PLACEHOLDER_KEY"` – kupnje su zakomentirane.
- Za closed testing može ostati tako ako ne testiraš IAP. Kad budeš uključivao kupnje: stavi pravi RevenueCat Google API key i odkomentiraj `fetchOfferings` / `purchase`. Inače `Purchases.configure()` s placeholderom samo logira greške, ne ruši app.

### 2.2 RevenueCat log level
- **Napravljeno:** U release buildu `Purchases.logLevel = WARN`, u debug buildu `DEBUG` (BillingManager.kt).

### 2.3 Logovi u release buildu
- **Napravljeno:** Uveden je `LogUtil` (core.util) – svi logovi idu samo kada je `BuildConfig.DEBUG`. U release buildu se ništa ne šalje u log.

### 2.4 Backup / Data extraction
- **backup_rules.xml** i **data_extraction_rules.xml** uključuju `sharedpref` i `database` za cloud backup i device transfer.
- DataStore (kovanice, postavke) i eventualno lokalna baza (dnevnik) idu u backup. Ako u preferenceima imaš nešto osjetljivo, razmisli o isključivanju putanja (npr. exclude za auth token). Za closed testing obično nije kritično.

---

## 3. STORE / PLAY CONSOLE – što pripremiti

### 3.1 App Bundle
- Google Play traži **AAB** (Android App Bundle), ne APK. Build:  
  `./gradlew bundleRelease`  
  Izlaz: `app/build/outputs/bundle/release/app-release.aab`

### 3.2 Verzija
- Trenutno: `versionCode = 1`, `versionName = "1.0"` – u redu za prvi upload. Svaki sljedeći upload mora imati **veći** `versionCode`.

### 3.3 Store listing (za closed test obično minimalno)
- **App name:** npr. MysticTarot (već u `strings.xml`).
- **Short description / Full description** – unesi u Play Console.
- **Screenshots** – barem jedan (npr. 16:9 ili prema Play zahtjevima).
- **App icon:** 512×512 PNG za Play (možeš eksportirati iz `ic_mystic_icon` ili napraviti poseban asset).

### 3.4 Privacy policy i Data safety
- Koristiš Firebase Auth (email/Google), Analytics, možda Firestore, lokalne podatke (kovanice, dnevnik). Google traži **Privacy policy URL** i ispunjen **Data safety** obrazac.
- Što treba: javno dostupan URL s politikom privatnosti (što skupljaš, kako koristiš, kako brišeš). U Play Console → App content → Privacy policy unesi URL. Zatim Data safety – označi što skupljaš (npr. email, ID, usage data) i svrhu.

### 3.5 Dozvole
- U manifestu: `INTERNET`, `POST_NOTIFICATIONS`. Obje su u redu; notifikacije se traže u runtime na Android 13+. Nema prekomjernih dozvola.

---

## 4. TEHNIČKI / KOD – stanje

### 4.1 Manifest
- Jedina aktivnost: `MainActivity`, `exported="true"` s MAIN/LAUNCHER – ispravno.
- AD_SERVICES_CONFIG override za AdMob/measurement – postavljeno, merger ne pada.
- `allowBackup="true"`, dataExtractionRules i fullBackupContent – postavljeno.

### 4.2 Build
- **compileSdk 35**, **targetSdk 35**, **minSdk 26** – u redu.
- **Release:** minify off, ProGuard datoteke prisutne, signing ovisan o `keystore.properties` (vidi 1.3).
- **google-services** plugin – očekuje `app/google-services.json`. Datoteka je u `.gitignore`; mora biti u projektu lokalno (preuzeta iz Firebase Console).

### 4.3 Sigurnost
- Nema hardcodiranih lozinki ili API keyeva u repo osim placeholdera (RevenueCat, AdMob test ID-ovi – vidi 1.2). Firebase konfiguracija iz `google-services.json` – ispravno ne commitati.

### 4.4 Pristupačnost
- Većina važnih ikona ima `contentDescription`. Nekoliko mjesta ima `contentDescription = null` (dekorativne ikone) – prihvatljivo. Za buduće: sve interaktivne stvari imati opis.

### 4.5 Navigacija i flow
- Bottom bar: Home, Reading, Journal, Shop. Settings dostupan preko rute "settings" (npr. iz Home). Struktura je jasna.

---

## 5. CHECKLIST – prije prvog slanja u Closed Testing

- [ ] **strings.xml:** `default_web_client_id` zamijenjen pravim Firebase Web client ID-om
- [ ] **google-services.json** u `app/` (preuzet iz Firebase)
- [ ] **keystore** kreiran, **keystore.properties** napunjen u rootu (ne commitati)
- [ ] **AdMob:** u manifestu pravi App ID; u AdManager.kt pravi Rewarded ad unit ID (ili ostaviti test za closed test ako eksplicitno testiraš samo s test ID-ovima, ali za production mora pravi)
- [ ] **Build AAB:** `./gradlew bundleRelease` uspješan
- [ ] **Privacy policy** URL pripremljen i unesen u Play Console
- [ ] **Data safety** obrazac ispunjen u Play Console
- [ ] **Store listing:** naziv, opisi, barem jedan screenshot i ikona 512×512
- [ ] Jednom prođeš cijeli flow u appu: welcome → guest ili Google → home → čitanje → dnevnik → shop (oglas) → postavke

---

## 6. Sažetak

- **Blokeri za “normalan” prvi release:** Web Client ID, pravi AdMob ID-ovi, keystore + keystore.properties, google-services.json, Privacy policy + Data safety.
- **Kod i arhitektura:** uredni, pripremljeni za closed test. RevenueCat i logovi mogu se dotjerati prije productiona.
- Nakon ovih koraka možeš uploadati AAB u Closed testing track i dodati testere.
