# Plan de Test - Projet Musappli

Ce document décrit la stratégie et le plan de test détaillé pour l'application **Projet Musappli**, ciblant spécifiquement les packages suivants :
1. `com.example.projetmuseappli.elements`
2. `com.example.projetmuseappli.androidClasses.utilitaries`
3. `com.example.projetmuseappli.androidClasses.composant`

---

## 1. Stratégie Générale de Test

L'application repose sur deux niveaux de tests automatisés :

### A. Tests Unitaires JVM (Locaux - `app/src/test`)
- **Objectif** : Valider la logique métier pure, la cohérence des données (modèles), les validations d'entrées et les comparateurs sans dépendre d'un émulateur ou appareil Android.
- **Exécution** : Très rapide, directement sur la JVM.
- **Outils** : JUnit 4, Mockito.

### B. Tests d'Intégration et Composants UI (Instrumentés / Robolectric - `app/src/androidTest` ou `app/src/test` avec Robolectric)
- **Objectif** : Tester les mécanismes nécessitant le contexte Android (sauvegarde et désérialisation sur stockage interne, manipulation des URIs/ContentResolver, comportements des adaptateurs `RecyclerView`, filtres et barres de recherche).
- **Outils** : JUnit 4, AndroidX Test Runner, Espresso, Robolectric.

---

## 2. Plan Détaillé par Package

### A. Package `com.example.projetmuseappli.elements`

#### 1. `Song` (`src/test`)
- **`testSongCreationValid`** :
  - **Description** : Vérifier qu'un objet `Song` est correctement instancié avec un UUID, un titre et une extension valide (`mp3`, `flac`, `ogg`).
  - **Assertions** : `getUUID()`, `getTitre()`, `getExtension()` et `getDate()` (doit correspondre à `LocalDate.now()`).
- **`testSongCreationInvalidExtension`** :
  - **Description** : Tester l'instanciation avec une extension non prise en charge (ex. `txt`, `wav`, `pdf`).
  - **Résultat attendu** : Une exception `IllegalArgumentException` est levée.
- **`testSongCreationEmptyTitle`** :
  - **Description** : Tester la création avec un titre vide (`""`).
  - **Résultat attendu** : Une exception `IllegalArgumentException` est levée.
- **`testIsExtentionValide`** :
  - **Description** : Tester la méthode statique avec différentes valeurs (`mp3`, `MP3`, `flac`, `invalid`, `null`).
  - **Assertions** : Doit être insensible à la casse et retourner `true` pour les extensions gérées, `false` sinon.
- **`testGetDateString`** :
  - **Description** : Tester le formatage de la date d'importation.
  - **Assertions** : Format par défaut (`dd/MM/yyyy`) et formats personnalisés (ex. `yyyy-MM-dd`).
- **Comparateurs** :
  - **`SongNameComparator`** : Vérifier le tri alphabétique par titre d'une liste de `Song`.
  - **`SongDateComparator`** : Vérifier le tri chronologique par date d'importation.

#### 2. `Playlist` (`src/test`)
- **`testPlaylistCreationWithSingleSong`** :
  - **Description** : Création d'une playlist avec un nom et un morceau initial.
  - **Assertions** : Nom correct, taille = 1, présence du morceau.
- **`testPlaylistCreationWithList`** :
  - **Description** : Création d'une playlist à partir d'une liste de morceaux.
  - **Assertions** : Taille égale à la liste, contenu identique.
- **`testPlaylistCreationEmptyName`** :
  - **Description** : Tenter d'instancier une playlist avec un nom vide (`""`) ou `null`.
  - **Résultat attendu** : `IllegalArgumentException`.
- **`testAddSongSuccess`** :
  - **Description** : Ajouter un morceau non encore présent.
  - **Assertions** : Taille augmentée de 1, présence vérifiée.
- **`testAddSongDuplicate`** :
  - **Description** : Tenter d'ajouter un morceau déjà présent dans la playlist.
  - **Résultat attendu** : `IllegalArgumentException` ("La playlist contient déjà ce son").
- **`testRemoveSongSuccess`** :
  - **Description** : Supprimer un morceau quand la playlist contient au moins 2 morceaux.
  - **Assertions** : Le morceau est retiré, la taille diminue.
- **`testRemoveSongNotFound`** :
  - **Description** : Tenter de supprimer un morceau absente de la playlist.
  - **Résultat attendu** : `IllegalArgumentException`.
- **`testRemoveSongLastElement`** :
  - **Description** : Tenter de supprimer le dernier morceau d'une playlist (taille = 1).
  - **Résultat attendu** : `IllegalArgumentException` ("La playlist ne peut pas être vide").
- **`testGetDateString`** :
  - **Description** : Valider le formatage de la date de création de la playlist.

#### 3. `Masterlist` (`src/androidTest` / Robolectric)
- **`testSingletonUnicity`** :
  - **Description** : Vérifier que `getInstance()` retourne systématiquement la même instance.
- **`testInitAndSerialization`** :
  - **Description** : Tester `init(context)` avec et sans fichier existant (`masterlist.ser`).
  - **Assertions** : La sauvegarde `save()` écrit correctement sur le stockage interne et la désérialisation restitue l'état.
- **`testAddAndGetSong`** :
  - **Description** : Ajouter un `Song` et le récupérer via son UUID.
- **`testRemoveSong`** :
  - **Description** : Supprimer un `Song` et vérifier qu'il n'est plus accessible ni dans la map ni dans `getMasterlistSongList()`.
- **`testAddAndGetPlaylist`** :
  - **Description** : Ajouter, récupérer et supprimer des playlists.
- **`testAddToPlaylistAndRemoveFromPlaylist`** :
  - **Description** : Ajouter/retirer un morceau à une playlist via les méthodes d'encapsulation de `Masterlist`.
- **`testGetRawObject`** :
  - **Description** : Vérifier que `getRawObject(uuid)` retourne l'objet approprié (`Song` ou `Playlist`).
- **`testMasterlistListeners`** :
  - **Description** : Enregistrer un listener, déclencher une modification (ajout/suppression) et vérifier l'appel à `onMasterlistChanged()`.

#### 4. `MasterlistListener`
- **Contrat d'interface** : Tester les notifications via des objets Mocks (Mockito).

---

### B. Package `com.example.projetmuseappli.androidClasses.utilitaries`

#### 1. `Utilitaries` (`src/androidTest` / Robolectric)
- **`testGetFileNameFromContentUri`** :
  - **Description** : Extraire le nom d'affichage d'un fichier à partir d'un schéma `content://` (simulation via `MockContentResolver` ou `Cursor`).
  - **Assertions** : Nom exact restitué (ex. `"chanson.mp3"`).
- **`testGetFileNameFromFileUri`** :
  - **Description** : Extraire le nom d'un fichier à partir d'un schéma `file:///path/to/song.flac`.
  - **Assertions** : `"song.flac"`.
- **`testGetFileNameErrorHandling`** :
  - **Description** : Vérifier le comportement face à une URI invalide ou un curseur nul.

#### 2. `LocalStorageImportManager` (`src/androidTest` / Robolectric)
- **`testImportAudioFileSuccess`** :
  - **Description** : Importer un fichier audio valide à partir d'une URI temporaire.
  - **Assertions** : Le fichier est copié dans `context.getFilesDir()`, et un objet `Song` correspondant est créé et ajouté à la `Masterlist`.
- **`testImportAudioFileInvalidExtension`** :
  - **Description** : Tenter d'importer un fichier avec une extension non reconnue.
  - **Résultat attendu** : `IllegalArgumentException`.
- **`testImportAudioFileIOException`** :
  - **Description** : Simuler un problème d'accès/lecture I/O au flux source.
  - **Résultat attendu** : `RuntimeException` capturant l'erreur.

#### 3. `ImportHelper` (`src/androidTest`)
- **`testLauncherRegistration`** :
  - **Description** : S'assurer que le sélecteur de documents est initialisé avec les formats autorisés dans `Masterlist.ACCEPTED_FORMATS`.
- **`testImportSongCall`** :
  - **Description** : Appeler `importSong()` avec une liste d'URIs fictives et vérifier le déclenchement de la gestion d'importation et l'affichage du Toast de succès.

---

### C. Package `com.example.projetmuseappli.androidClasses.composant`

#### 1. Adaptateurs (`adapters/`)
Classes concernées : `SongAdapter`, `PlaylistAdapter`, `PlaylistPreviewAdapter`, `PlaylistAdapterAbstract`.

- **`testGetItemCount`** :
  - **Description** : Vérifier que l'adaptateur renvoie exactement le nombre d'éléments présents dans la liste de données.
- **`testOnBindViewHolder`** :
  - **Description** : Vérifier la mise à jour correcte des composants UI (`TextView`, icônes, état de sélection) pour un élément donné.
- **`testSelectionMode` (Multi-sélection)** :
  - **Description** :
    - Activation/Désactivation du mode sélection (`setSelectionMode`).
    - BASCULEMENT d'un élément (`toggleSelection`).
    - Tout sélectionner / Tout désélectionner (`selectAll`, `clearSelection`).
  - **Assertions** : Vérification du compteur `getSelectedItemsCount()` et de l'état visuel des éléments sélectionnés.
- **Événements de clic** :
  - **`testOnItemClick`** : Vérifier le déclenchement du listener principal lors d'un clic normal.
  - **`testOnBurgerClick`** : Vérifier le déclenchement du listener de menu contextuel (bouton burger).
  - **`testOnBackPressed`** : Vérifier que l'appui sur le bouton retour désactive le mode sélection multiple si celui-ci est actif.

#### 2. Composants En-tête et Pied de page (`headersAndFooters/`)
Classes concernées : `PlaylistHeader`, `MainHeader`, `MainFooter`.

- **`PlaylistHeader`** :
  - **`testToggleSearchMode`** : Vérifier le passage entre le titre de la playlist et la barre de recherche (`searchInput`).
  - **`testFilteringWithSearchInput`** : Saisir du texte dans `searchInput` et vérifier que `TextWatcher` filtre dynamiquement la liste affichée par le `SongAdapter` associé.
  - **`testClearSearch`** : Vérifier l'effacement de la recherche et la restauration de la liste complète.
- **`MainHeader` / `MainFooter`** :
  - **`testComponentBinding`** : Vérifier la liaison des vues aux identifiants XML (`R.layout.component_main_header`, `R.layout.component_main_footer`).
  - **`testButtonActions`** : Vérifier le déclenchement des actions associées (importation, création de playlist, navigation).

---

## 3. Cas Particuliers & Analyse des Fonctionnalités Supplémentaires

### A. Gestion des Noms Identiques (Doublons de titres/noms pour `Song` et `Playlist`)

> [!NOTE]
> **Statut du code actuel** : **NON GÉRÉ** dans le code métier actuel.
> - `Song` et `Playlist` sont stockés dans la `Masterlist` au sein de `HashMap<UUID, Song>` et `HashMap<UUID, Playlist>`.
> - L'unicité est basée uniquement sur le `UUID`. Le système autorise actuellement deux morceaux ou deux playlists d'avoir exactement le même nom à la création ou lors du changement de nom (`setName`).
> - `Playlist.setName()` ne vérifie pas non plus si le nom fourni est vide `""` (alors que le constructeur le fait).

#### Cas de test prévus / à ajouter :
1. **`testSongDuplicateNameAllowedCurrentBehavior`** :
   - **Description** : Création de deux morceaux `Song` ayant le même titre (ex. `"SongA"`).
   - **Résultat actuel** : Les deux morceaux sont créés et stockés séparément car leurs UUIDs diffèrent.
2. **`testPlaylistDuplicateNameAllowedCurrentBehavior`** :
   - **Description** : Création de deux playlists ayant le même nom (ex. `"Ma Playlist"`).
   - **Résultat actuel** : Les deux playlists sont acceptées dans `Masterlist`.
3. **`testPlaylistRenameDuplicateName`** :
   - **Description** : Renommage d'une playlist avec un nom déjà utilisé par une autre playlist via `setName()`.
   - **Résultat actuel** : Le renommage réussit sans exception.
4. **`testPlaylistRenameEmptyName`** (*Anomalie potentielle*) :
   - **Description** : Renommage d'une playlist avec une chaîne vide `""` via `setName()`.
   - **Résultat actuel** : Accepté par `setName()` (contrairement au constructeur de `Playlist` qui lève une `IllegalArgumentException`).

---

### B. Tri d'une Playlist (par Date d'ajout ou par Nom)

> [!NOTE]
> **Statut du code actuel** : **MÉTHODE DE TRI NON ENCORE IMPLÉMENTÉE** sur `Playlist`.
> - `Playlist` conserve les morceaux dans une `LinkedList<Song>` selon l'ordre d'insertion (ordre par défaut).
> - Les comparateurs `SongNameComparator` et `SongDateComparator` sont **déjà implémentés** dans `Song.java`.
> - Aucune méthode de tri (ex. `sortByName()` ou `sortByDate()`) n'existe encore sur la classe `Playlist`.

#### Cas de test pour la future méthode de tri :
1. **`testPlaylistDefaultOrderIsInsertionOrder`** :
   - **Description** : Vérifier que l'ordre par défaut de `getSongList()` correspond à l'ordre chronologique d'ajout des morceaux dans la playlist.
2. **`testPlaylistSortByName`** (*Future méthode de tri*) :
   - **Description** : Appeler la future méthode de tri par nom sur une playlist contenant des morceaux mélangés (ex. `"Zebra"`, `"Alpha"`, `"Beta"`).
   - **Résultat attendu** : La liste renvoyée doit être triée alphabétiquement (`"Alpha"`, `"Beta"`, `"Zebra"`).
3. **`testPlaylistSortByDate`** (*Future méthode de tri*) :
   - **Description** : Appeler la future méthode de tri par date sur une playlist.
   - **Résultat attendu** : La liste renvoyée doit être ordonnée selon les dates d'importation des morceaux.

---

## 4. Configuration et Dépendances Injectées dans Gradle

Les dépendances de test unitaires et instrumentés ont été ajoutées dans `app/build.gradle.kts` :

```kotlin
dependencies {
    // Tests unitaires JVM
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.robolectric:robolectric:4.12.2")

    // Tests instrumentés Android
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation("org.mockito:mockito-android:5.11.0")
}
```
