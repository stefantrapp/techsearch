
# Anwendung Technologiesuche - TechSearch

Die Anwendung besteht aus einem Backend mittels Spring Boot und einer Frontend mittels Angular. Hinter dem Sptring-Boot-Backend wird noch eine Backend mittels Python eingesetzt.

## Vorausstzungen

### Umgebung

1. Ein JDK. Getestet wurde mit einem AdoptOpenJDK 11.0.11.9 [Link](https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.11%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.11_9.msi)
2. Python 3. Getestet wurde mit WinPYthon [Link](https://github.com/winpython/winpython/releases/download/4.3.20210620/Winpython64-3.9.5.0.exe)
3. Apache Maven. Getestet wurde mit Apache Maven 3.5.4. [Link](https://dlcdn.apache.org/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip)
4. PostgreSQL. Getestet wurde mit PostgreSQLPortable 13.1 [Link]([https://link](https://get.enterprisedb.com/postgresql/postgresql-13.1-1-windows-x64-binaries.zip))
5. Eine leere Datenbank namens "techsearch". Als Zugangsdaten wird der Benutzer "postgres" mit dem Passwort "admin" verwendet. Die Zugangsdaten können in der Datei ```techsearch/backend/config/application.yaml``` geändert werden.

### Datenbank
Die Datenbank kann mit folgendem Skript angelegt werden.

``` SQL
CREATE DATABASE techsearch
    WITH 
    OWNER = postgres
    TEMPLATE = template0
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    CONNECTION LIMIT = -1;
```

## Die Anwendung bauen
### Spring-Boot und Angular
im Verzeichnis ```techsearch``` folgendes ausführen:
```
mvn clean install -DskipTests
```

Es wird das Frontend und das Sptring-Boot-Backend erstellt.

Die Spring-Boot-Anwendung befindet sich dann unter ```techsearch/backend/target/backend-1.0.0-SNAPSHOT.jar```

Falls nur das Backend neu erstellt werden soll, folgendes Ausführen:
```
mvn clean install -DskipTests -Dskip.npm
```

### Python-Backend
Die Abhängigkeiten können mittels ```pip``` installiert werden. Sie liegen in der Datei ```techsearch/backend-python/requirements.txt```
Wenn das aktuelle Verzeichnis ```techsearch/backend-python``` ist, dann geschieht die z. B. mit folgendem Befehl:
```
pip install -r requirements.txt
```
Es kann das 'global' installierte Python oder ein "Virtual Environment" verwendet werden. Empfohlen wird ein "Virtual Environments". Dann muss ggf. auch die Datei ```techsearch/backend-python/run.cmd``` angepasst werden, mit dem das Python-Backend erstellt wird.

## Die Anwendung starten
Es wird vorausgesetzt, dass die Datenbank läuft.

Für den Start der Anwendung muss das aktuelle Verzeichnis ```techsearch\backend``` sein. Dann kann das Backend wie folgt gestartet werden:
```
java.exe -Dfile.encoding=UTF-8 -jar target\backend-1.0.0-SNAPSHOT.jar
```
Das Python-Backend wird über die Datei ```techsearch/backend-python/run.cmd``` gestartet. Dort könngen, falls notwendig, noch Anpassungen vorgenommen werden.

Beim ersten Start können Fehler auftreten, die daher rühren, dass zunächst automatisch Tabellen in der Datenbank angelegt werden. Die Anwendung in solche einem Fall beenden und nochmals starten.

Das Frontend ist dann über folgende URL erreichbar:
http://localhost:8080/

