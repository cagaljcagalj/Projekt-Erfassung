# CAGALJ-FahrtenApp 2.0

Neues, eigenständiges Android-Projekt in Java, ohne Cloudkonto oder Internetzwang.

## Funktionen
- Startseite mit Monatskennzahlen, Datum, Logo und Start-/Stopp-Timer
- Tages-, Wochen-, Monats- und Jahresfilter für Einträge und Auswertung
- Mehrere Einsätze pro Tag; Einträge bearbeiten und löschen
- Kunden und zugehörige Projekte; Suche nach Kunden
- Projektadresse, Ansprechpartner, Telefonnummer, Projektnummer, Tätigkeit
- Projektbezogene Stunden- und Kilometersätze; Preise werden im Eintrag gespeichert
- Automatische Berechnung von Arbeitszeit, Fahrtkilometern und Einnahmen
- Abrechnungsstatus Offen / Abgerechnet / Bezahlt
- PDF- und echte XLSX-Exporte sämtlicher Einträge
- JSON-Sicherung und Wiederherstellung; Hell-/Dunkelmodus

## APK mit GitHub Actions bauen
1. Alle Dateien und Ordner dieses Projekts in ein **neues GitHub-Repository** hochladen. Wichtig: `.github/workflows/android.yml` muss ebenfalls hochgeladen werden.
2. Im Repository den Reiter **Actions** öffnen und den Workflow **Android APK erstellen** auswählen.
3. **Run workflow** anklicken (alternativ läuft der Build beim Push auf `main`/`master`).
4. Nach erfolgreichem Lauf das Artefakt **CAGALJ-FahrtenApp-2-APK** herunterladen, entpacken und `app-debug.apk` auf dem Android-Handy installieren.

## Alternativ in Android Studio
Projektordner öffnen und **Build > Build APK(s)** ausführen. Erfordert Android SDK 35, Java 17 und Gradle 8.9.

## Hinweise
- Die App speichert ihre Daten in einer lokalen SQLite-Datenbank.
- Arbeitszeit über Mitternacht wird unterstützt; der Timer wird auf dem Gerät gespeichert. Für exakte mehrtägige Schichten ist eine gesonderte Datums-/Zeitlogik erforderlich.
- Die Wochenansicht zeigt Einträge ab Montag bis heute.
- Fahrtzeit wird dokumentiert, aber **nicht** zusätzlich vergütet. Fahrtkosten werden als `Kilometer × Kilometersatz` berechnet; sie werden in dieser App zum Gesamtverdienst addiert.
- PDF und Excel exportieren derzeit **alle** Einträge; der gewählte Filter gilt nur für die Ansicht.
- Der PDF-Export ist eine kompakte tabellarische Übersicht; kein steuerlich geprüftes Rechnungsformular.
- Vor einer Neuinstallation, einem Handywechsel oder dem Löschen von App-Daten ein JSON-Backup erstellen.
- Der Debug-Build ist für private Tests, nicht für die Veröffentlichung im Play Store signiert.

## Entwicklungsstatus
Quellprojekt erstellt. Ein Android-Build konnte in dieser Umgebung mangels installiertem Android SDK/Gradle nicht ausgeführt werden. GitHub Actions baut und prüft die APK nach dem Upload.


## Ergänzungen (Oktober 2026)
- Kilometervergütung für neue Projekte standardmäßig 0,50 EUR/km, individuell änderbar.
- Dashboard zeigt Arbeitsverdienst und Kilometervergütung getrennt.
- Tag/Woche/Monat/Jahr: Summen für Arbeitszeit, Arbeitserlös, Fahrtstrecken, Kilometer und Gesamterlös.
- Wochenfilter umfasst nun Montag bis Sonntag, auch bei künftigen Einträgen.
- Firmen- und Projektadresse können erfasst werden; Google Maps öffnet zur manuellen Routenprüfung.
- **Nicht implementiert:** automatisches Einlesen der längsten Route, vollständiges visuelles Nachbilden der Referenz-App, Rechnungsverwaltung. Für automatische Routenberechnung wird ein Routing-Anbieter mit API und ggf. Schlüssel benötigt.
- Der GitHub-Workflow nutzt das auf dem Runner vorhandene Android SDK, um den früheren Fehler beim Nachinstallieren des veralteten `tools`-Pakets zu vermeiden.

## Version 2.1 – Kalender, Tätigkeiten, Google Routes
- Startseite → Kalender: Monatsraster mit Tagesverdienst und Stunden; Tag antippen zum Ansehen, Bearbeiten und Löschen der Einträge sowie Hinzufügen weiterer Einträge.
- Projekte: Mehrere Tätigkeiten mit individuellen Stundensätzen, pro Zeile `Tätigkeit | Preis`, z. B. `Programmierung | 95`. Neue Einträge verwenden den ausgewählten Stundensatz. Bereits gespeicherte Einträge behalten ihren ursprünglichen Stundensatz.
- Firmenlogo: unter Einstellungen hochladen, wird als transparentes Wasserzeichen im Hintergrund angezeigt.
- Google Routes API: In Firmendaten Firmenadresse und einen eigenen Google Routes API-Key hinterlegen; Projektadresse eingeben und „Längste Google-Route automatisch übernehmen“ wählen. Benötigt aktivierte Routes API und Google-Cloud-Abrechnung. Die längste der von Google gelieferten Alternativrouten wird vorgeschlagen; dies garantiert nicht die längste denkbare Straßenroute. Bei fehlendem API-Key bleibt die manuelle Google-Maps-Prüfung verfügbar. API-Schlüssel wird lokal in App-Einstellungen gespeichert; für produktive Apps empfiehlt sich eine abgesicherte Backend-Anbindung statt eines dauerhaft im Client gespeicherten Schlüssels.
- Bisherige Funktionen bleiben erhalten. Die ZIP enthält den Quellcode und GitHub Actions, keine fertig getestete APK.

**GitHub-Upload:** Gesamten Inhalt dieses Projektordners (inklusive `.github`) in die Wurzel des Repositorys hochladen. Auf GitHub kann man Ordner aus dem entpackten ZIP hochladen; das ZIP selbst allein wird nicht automatisch entpackt. Keine zweite Kopie des Projektordners innerhalb des Repositorys anlegen.
