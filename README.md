# GestionDeStock

## Lancer PostgreSQL

```bash
sudo systemctl start postgresql
sudo -u postgres psql
psql -U postgres -h localhost -d gestion_stock
# mot de passe : postgres
```

## Compiler et exécuter (recommandé)

Toujours se placer à la racine du projet :

```bash
cd ~/Projet/2026/s4/nouveau/INF210_Projets/GestionDeStock
```

Compiler (tous les `.class` dans `classes/`) :

```bash
javac -d classes -cp "/usr/share/java/postgresql.jar" $(find src/main/java -name "*.java")
```

Exécuter :

```bash
java -cp "classes:/usr/share/java/postgresql.jar" com.gestionStock.Main
```

## Si vous êtes déjà dans `src/main/java`

Compiler :

```bash
javac -d ../../../classes -cp "/usr/share/java/postgresql.jar" $(find . -name "*.java")
```

Exécuter sans changer de dossier :

```bash
java -cp "../../../classes:/usr/share/java/postgresql.jar" com.gestionStock.Main
```
