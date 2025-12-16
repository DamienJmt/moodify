
package com.example.demo;

import com.example.demo.Controller.MusicPlayerController;
import com.example.demo.Controller.PlaylistController;
import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import com.example.demo.repo.ClasspathSongRepository;
import com.example.demo.services.PlaylistManager;
import com.example.demo.services.PlaylistService;
import com.example.demo.services.RecommendationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires + tests Web (MockMvc) dans un seul fichier.
 *
 * Objectif : 0 "méthode inventée" -> on teste UNIQUEMENT les APIs qui existent dans ton code actuel.
 */
class DemoApplicationTests {

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

	// Génère un ID déterministe comme le fait le ClasspathSongRepository pour une chanson donnée.
    private static String deterministicIdLikeRepo(String name, String artists) {
        String key = (name == null ? "" : name.trim().toLowerCase())
                + "|" + (artists == null ? "" : artists.trim().toLowerCase());
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(hash);
            return hex.substring(0, 12);
        } catch (Exception ex) {
            return key.replaceAll("\\s+", "_");
        }
    }

	// Crée une instance de Song avec les champs essentiels pour les tests.
    private static Song song(String id, String name, String artists, String mood) {
        Song s = new Song();
        s.setId(id);
        s.setName(name);
        s.setArtists(artists);
        s.setMood(mood); // ajoute aussi dans moods via setMood()
        return s;
    }
	
    // ------------------------------------------------------------
    // Unit tests (sans Spring Context)
    // ------------------------------------------------------------

    @Nested
    class SongTests {

		// Vérifie que le volume est bien limité entre 0 et 100.
        @Test
        void setVolume_should_clamp_between_0_and_100() {
            Song s = new Song();
            s.setVolume(120);
            assertThat(s.getVolume()).isEqualTo(100);

            s.setVolume(-10);
            assertThat(s.getVolume()).isEqualTo(0);

            s.setVolume(42);
            assertThat(s.getVolume()).isEqualTo(42);
        }

		// Vérifie que la méthode hasMood est insensible à la casse et fonctionne avec le champ mood seul.
        @Test
        void hasMood_should_be_case_insensitive_and_support_single_mood_field() {
            Song s = new Song();
            s.setMood("Happy");

            assertThat(s.hasMood("happy")).isTrue();
            assertThat(s.hasMood("HAPPY")).isTrue();
            assertThat(s.hasMood("sad")).isFalse();
        }
    }

    @Nested
    class PlaylistUnitTests {

		// Vérifie que la navigation dans la playlist boucle correctement du dernier au premier morceau et inversement.
        @Test
        void playlist_navigation_should_wrap_around() {
            Song a = song("a", "A", "X", "Happy");
            Song b = song("b", "B", "X", "Sad");
            Playlist pl = new Playlist(List.of(a, b));

            assertThat(pl.getCurrentSong().getId()).isEqualTo("a");
            assertThat(pl.nextSong().getId()).isEqualTo("b");
            assertThat(pl.nextSong().getId()).isEqualTo("a"); // wrap
            assertThat(pl.prevSong().getId()).isEqualTo("b"); // wrap backwards
        }

		// Vérifie qu’une même chanson ne peut pas être ajoutée deux fois dans une playlist.
        @Test
        void addSong_should_avoid_duplicates_by_id() {
            Playlist pl = new Playlist(new ArrayList<>());

            Song s1 = song("id1", "Song1", "Artist", "Happy");
            Song s2 = song("id1", "Song1bis", "Artist", "Happy"); // same id

            pl.addSong(s1);
            pl.addSong(s2);

			// seul le premier doit être ajouté
            assertThat(pl.getSongs()).hasSize(1);
			// et c'est bien celui avec l'ID "id1"
            assertThat(pl.getSongs().get(0).getId()).isEqualTo("id1");
        }

		// Vérifie qu’une chanson peut être supprimée de la playlist en utilisant son identifiant.
        @Test
        void removeSongById_should_remove_matching_song() {
            Song s1 = song("id1", "Song1", "Artist", "Happy");
            Song s2 = song("id2", "Song2", "Artist", "Sad");
            Playlist pl = new Playlist(new ArrayList<>(List.of(s1, s2)));

            boolean removed = pl.removeSongById("id1");

            assertThat(removed).isTrue();
            assertThat(pl.getSongs()).extracting(Song::getId).containsExactly("id2");
        }

		// Vérifie que les chansons sont correctement filtrées selon leur mood.
        @Test
        void getByMood_should_filter_songs() {
            Song happy = song("h", "H", "A", "Happy");
            Song sad = song("s", "S", "A", "Sad");
            Playlist pl = new Playlist(List.of(happy, sad));

            assertThat(pl.getByMood("happy")).extracting(Song::getId).containsExactly("h");
            assertThat(pl.getByMood("SAD")).extracting(Song::getId).containsExactly("s");
        }
    }

    @Nested
    class ClasspathSongRepositoryTests {

		// Vérifie que les chansons sont bien chargées depuis le fichier JSON et que leurs identifiants sont générés de manière cohérente.
        @Test
        void repository_should_load_songs_from_classpath_and_generate_deterministic_ids() {
            ClasspathSongRepository repo = new ClasspathSongRepository();
            List<Song> songs = repo.findAll();

            assertThat(songs).isNotEmpty();

            // Le fichier songs.json commence par {name:"Happier", artists:"Marshmello", mood:"Happy"}
            Song first = songs.get(0);
            assertThat(first.getName()).isEqualTo("Happier");
            assertThat(first.getArtists()).isEqualTo("Marshmello");

            // ID déterministe (comme dans le repo)
            String expectedId = deterministicIdLikeRepo("Happier", "Marshmello");
            assertThat(first.getId()).isEqualTo(expectedId);

            // normalizeSingleMood() doit assurer la cohérence mood/moods
            assertThat(first.getMood()).isNotBlank();
            assertThat(first.getMoods()).isNotEmpty();
            assertThat(first.hasMood("happy")).isTrue();
        }
    }

    @Nested
    class RecommendationServiceTests {

		// Vérifie que le service évite de sélectionner des chansons récemment jouées lorsque c'est possible.
        @Test
        void pickNext_should_avoid_recent_when_possible() {
            RecommendationService service = new RecommendationService();

            Song a = song("a", "A", "X", "Happy");
            Song b = song("b", "B", "X", "Happy");

            Song first = service.pickNext(List.of(a, b)).orElseThrow();
            Song second = service.pickNext(List.of(a, b)).orElseThrow();

            // le 2e pick ne doit pas reprendre l'ID tout juste mémorisé si l'autre existe
            assertThat(second.getId()).isNotEqualTo(first.getId());
        }

		// Vérifie que le service retombe sur n'importe quel candidat si tous ont été récemment joués.
		// Cela garantit qu'une chanson peut toujours être sélectionnée même si toutes sont récentes.
        @Test
        void pickNext_should_fallback_to_any_candidate_if_all_are_recent() {
            RecommendationService service = new RecommendationService();
            Song only = song("only", "Only", "X", "Happy");

            // un seul candidat => il est forcément récent après le 1er appel => fallback
            for (int i = 0; i < 5; i++) {
                Optional<Song> pick = service.pickNext(List.of(only));
                assertThat(pick).isPresent();
                assertThat(pick.get().getId()).isEqualTo("only");
            }
        }
    }

    @Nested
    class PlaylistServiceUnitTests {

		// Vérifie que les chansons candidates pour une playlist excluent celles déjà ajoutées et respectent les moods de la playlist.
        @Test
        void getCandidateSongs_should_exclude_already_added_and_respect_moods() {
            PlaylistManager pm = org.mockito.Mockito.mock(PlaylistManager.class);

            Song happy1 = song("h1", "H1", "A", "Happy");
            Song happy2 = song("h2", "H2", "A", "Happy");
            Song sad1 = song("s1", "S1", "A", "Sad");

            when(pm.getAllSongs()).thenReturn(List.of(happy1, happy2, sad1));

            PlaylistService service = new PlaylistService(pm);

            Playlist pl = service.createPlaylist("pl", "desc", Set.of("Happy"));
            service.addSongToPlaylist(pl.getId(), "h1");

            List<Song> candidates = service.getCandidateSongs(pl.getId());

            assertThat(candidates).extracting(Song::getId).containsExactly("h2");
        }

		// Vérifie qu’une chanson ne peut pas être ajoutée à une playlist si elle ne correspond pas aux moods de la playlist.
        @Test
        void addSongToPlaylist_should_ignore_song_if_not_matching_playlist_moods() {
            PlaylistManager pm = org.mockito.Mockito.mock(PlaylistManager.class);

            Song happy = song("h", "H", "A", "Happy");
            Song sad = song("s", "S", "A", "Sad");
            when(pm.getAllSongs()).thenReturn(List.of(happy, sad));

            PlaylistService service = new PlaylistService(pm);
            Playlist pl = service.createPlaylist("pl", "desc", Set.of("Happy"));

            Playlist updated = service.addSongToPlaylist(pl.getId(), "s");

            assertThat(updated.getSongs()).isEmpty();
        }

		// Vérifie qu’une playlist peut être supprimée correctement.
		// Après suppression, la playlist ne doit plus être accessible.
		// Le test mocke le PlaylistManager pour isoler le test au service uniquement.
		// mocke = utiliser Mockito pour simuler le comportement du PlaylistManager.
		// MOCKITO = bibliothèque Java pour créer des objets simulés (mocks) dans les tests unitaires.
        @Test
        void deletePlaylist_should_return_true_when_exists() {
            PlaylistManager pm = org.mockito.Mockito.mock(PlaylistManager.class);
            when(pm.getAllSongs()).thenReturn(List.of());

            PlaylistService service = new PlaylistService(pm);
            Playlist pl = service.createPlaylist("pl", "desc", Set.of());

            assertThat(service.deletePlaylist(pl.getId())).isTrue();
            assertThat(service.getPlaylist(pl.getId())).isNull();
        }
    }
}

/**
 * Tests Web/API via MockMvc, toujours dans le même fichier.
 *
 * Ici on mock les services pour tester uniquement le mapping HTTP + codes retour.
 */
@WebMvcTest(controllers = {MusicPlayerController.class, PlaylistController.class})
class ApiControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PlaylistManager playlistManager;

    @MockBean
    PlaylistService playlistService;

	// Vérifie que l'endpoint API pour récupérer toutes les chansons fonctionne correctement et retourne un tableau JSON.
	// GET /api/songs doit retourner 200 et un tableau JSON avec les chansons.
    @Test
    void GET_api_songs_should_return_200_and_json_array() throws Exception {
        Song s = new Song();
        s.setId("id1");
        s.setName("Happier");
        s.setArtists("Marshmello");
        s.setMood("Happy");

        when(playlistManager.getAllSongs()).thenReturn(List.of(s));

        mockMvc.perform(get("/api/songs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("id1"))
                .andExpect(jsonPath("$[0].name").value("Happier"));
    }

	// Vérifie que l'endpoint API pour récupérer la chanson en cours fonctionne correctement et retourne un objet JSON.
	// GET /api/songs/current doit retourner 200 et un objet JSON avec la chanson
    @Test
    void GET_api_songs_current_should_return_200() throws Exception {
        Song s = new Song();
        s.setId("id1");
        s.setName("Now");
        when(playlistManager.getCurrentSong()).thenReturn(s);

        mockMvc.perform(get("/api/songs/current").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.name").value("Now"));
    }

	// Vérifie que l'endpoint API pour augmenter le volume fonctionne correctement avec un montant positif.
	// GET /api/songs/volume/increase/{amount} avec un montant positif doit
    @Test
    void GET_api_songs_volume_increase_should_return_200_for_positive_amount() throws Exception {
        Song s = new Song();
        s.setId("id1");
        s.setName("Now");

        when(playlistManager.getCurrentSong()).thenReturn(s);

        mockMvc.perform(get("/api/songs/volume/increase/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id1"));
    }

	// Vérifie que l'endpoint API pour récupérer toutes les playlists fonctionne correctement et retourne un tableau JSON.
	// GET /api/playlists doit retourner 200 et un tableau JSON avec les playlists
    @Test
    void GET_api_playlists_should_return_200() throws Exception {
        Playlist pl = new Playlist();
        pl.setId("p1");
        pl.setName("My playlist");

        when(playlistService.listPlaylists()).thenReturn(List.of(pl));

        mockMvc.perform(get("/api/playlists").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[0].name").value("My playlist"));
    }

	// Vérifie que l'endpoint API pour récupérer une playlist par ID retourne 404 si la playlist n'existe pas.
	// GET /api/playlists/{id} avec un ID inexistant doit retourner 404
    @Test
    void GET_api_playlists_id_should_return_404_when_not_found() throws Exception {
        when(playlistService.getPlaylist("missing")).thenReturn(null);

        mockMvc.perform(get("/api/playlists/missing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

	// Vérifie que l'endpoint API pour créer une nouvelle playlist fonctionne correctement.
	// POST /api/playlists avec un corps JSON valide doit créer et retourner la playlist
    @Test
    void POST_api_playlists_should_create_and_return_playlist() throws Exception {
        Playlist created = new Playlist();
        created.setId("p1");
        created.setName("Chill");
        created.setDescription("desc");
        created.setMoods(Set.of("Happy"));

        when(playlistService.createPlaylist(eq("Chill"), eq("desc"), anySet())).thenReturn(created);

        String body = """
                {
                  "name": "Chill",
                  "description": "desc",
                  "moods": ["Happy"]
                }
                """;

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Chill"));
    }

	// Vérifie que l'endpoint API pour récupérer les chansons candidates pour une playlist retourne 404 si la playlist n'existe pas.
	// GET /api/playlists/{id}/candidates avec un ID inexistant doit retourner 404
    @Test
    void GET_api_playlists_candidates_should_return_404_when_playlist_missing() throws Exception {
        when(playlistService.getPlaylist("missing")).thenReturn(null);

        mockMvc.perform(get("/api/playlists/missing/candidates").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
