# EtatStockService - Role des fonctions

## Vue d'ensemble
`EtatStockService` calcule les donnees d'affichage de l'etat de stock:
- stock global (une ligne par article a une date)
- stock detail (reste reel par entree pour un article)

Il lit les donnees via les DAO:
- `mouvementDao`
- `mouvementSourceDao`
- `articleDao`
- `methodeDao`

## Constructeur
### `EtatStockService()`
Initialise les DAO utilises par le service.

## Fonctions publiques
### `getStockGlobalByDate(LocalDate dateLimite)`
Retourne, pour chaque article, le dernier mouvement connu avant ou a `dateLimite`.

Role:
- filtre les mouvements apres la date limite
- garde uniquement le mouvement le plus recent par article (selon `id`)

Sortie:
- `List<Mouvement>` (1 mouvement max par article)

### `getStockDetailLignesByDate(Long articleId, LocalDate dateLimite)`
Construit le detail de stock reel d'un article: les lots d'entree encore disponibles a `dateLimite`.

Role:
- recupere les mouvements de l'article
- separe entrees et sorties
- initialise le restant de chaque entree avec sa quantite
- applique les consommations de sortie:
  - priorite aux donnees de `mouvement_source` si presentes
  - sinon, recalcul de consommation FIFO/LIFO via `consommerSansSources(...)`
- retourne seulement les entrees avec `qteRestante > 0`

Sortie:
- `List<LigneStockResteDto>`

### `getStockGlobalLignesByDate(LocalDate dateLimite)`
Prepare les lignes d'affichage du stock global.

Role:
- s'appuie sur `getStockGlobalByDate(...)`
- convertit les ids en libelles (`article`, `methode`)
- mappe chaque mouvement vers `LigneStockGlobalDto`

Sortie:
- `List<LigneStockGlobalDto>`

## Fonctions privees
### `findByArticleId(List<Mouvement> mouvements, Long articleId)`
Cherche dans une liste le mouvement associe a un article donne.

Usage:
- aide `getStockGlobalByDate(...)` a savoir si un article a deja une ligne candidate.

### `getStockDetailByDate(Long articleId, LocalDate dateLimite)`
Filtre les mouvements d'un seul article jusqu'a la date limite.

Usage:
- source de base pour `getStockDetailLignesByDate(...)`.

### `consommerSansSources(BigDecimal qteSortie, List<Mouvement> entrees, Map<Long, BigDecimal> restantParEntree, String libelleMethode)`
Applique une sortie quand il n'y a pas de trace detaillee dans `mouvement_source`.

Role:
- choisit l'ordre de consommation:
  - `LIFO` -> entrees les plus recentes d'abord
  - sinon (`FIFO`/defaut) -> entrees les plus anciennes d'abord
- decremente `restantParEntree` jusqu'a consommer toute la quantite de sortie

Remarque:
- cette methode met a jour la map de restant en place.
