// ============================
// Initialisation du lecteur
// ============================
async function initPlayer() {
  const songs = await fetch("/api/songs").then(res => res.json());
  if (!songs || songs.length === 0) {
    console.warn("Aucune chanson trouvée !");
    return;
  }

  // Éléments HTML
  const playBtn = document.querySelector(".icon-play");
  const pauseBtn = document.querySelector(".icon-pause");
  const song = document.querySelector("#song");
  const title = document.querySelector("#title");
  const artist = document.querySelector("#artist");
  const moodsBox = document.querySelector("#moods");
  const metadataBox = document.querySelector("#metadata");
  const thumb = document.querySelector("#thumb");
  const progress = document.querySelector("#progress");
  const start = document.querySelector("#start");
  const end = document.querySelector("#end");
  const songSelector = document.getElementById("songSelector");
  const moodSelector = document.getElementById("moodSelector");
  const playButton = document.querySelector(".play");
  const prevPlayButton = document.querySelector(".prev-play");
  const nextPlayButton = document.querySelector(".next-play");
  const btnVolumeDown = document.getElementById("volumeDown");
  const btnVolumeUp = document.getElementById("volumeUp");
  const volumeLabel = document.getElementById("volumeLabel");

  // État
  let index = 0;
  let interval;
  let songProgressTrackerInterval;
  let currentVolume = 1.0; // volume initial 100%
  song.volume = currentVolume;
  volumeLabel.textContent = `Volume : ${Math.round(currentVolume * 100)}%`;

  // ============================
  // Remplissage du selecteur
  // ============================
  songs.forEach((track, i) => {
    const option = document.createElement("option");
    option.value = i;
    option.textContent = `${track.name} - ${track.artists} (${track.mood})`;
    songSelector.appendChild(option);
  });

  // ============================
  // Fonctions utilitaires
  // ============================
  function setSongDetails(songIndex) {
    const track = songs[songIndex];
    song.src = track.link;
    title.textContent = track.name;
    artist.textContent = track.artists;
    thumb.src = track.image;
    // Affichage des moods multiples
    const moods = track.moods || (track.mood ? [track.mood] : []);
    if (moodsBox) {
      moodsBox.innerHTML = moods.map(m => `<span class="mood-tag">${m}</span>`).join("");
    }
    // Affichage metadata simple
    if (metadataBox) {
      const md = track.metadata || {};
      const entries = Object.entries(md);
      metadataBox.innerHTML = entries.length ? entries.map(([k,v]) => `<span class="meta-item">${k}: ${v}</span>`).join(" | ") : "";
    }
    start.textContent = "00:00";
    end.textContent = "00:00";
    clearInterval(interval);
    song.onloadedmetadata = loadMetadata;
  }

  function loadMetadata() {
    progress.max = song.duration;
    progress.value = song.currentTime;
    updateSongTimeDisplay();
    interval = setInterval(updateSongTimeDisplay, 1000);
  }

  function updateSongTimeDisplay() {
    const min = Math.floor(song.duration / 60).toString().padStart(2, "0");
    const sec = Math.floor(song.duration % 60).toString().padStart(2, "0");
    const curMin = Math.floor(song.currentTime / 60).toString().padStart(2, "0");
    const curSec = Math.floor(song.currentTime % 60).toString().padStart(2, "0");
    end.textContent = `${min}:${sec}`;
    start.textContent = `${curMin}:${curSec}`;
  }

  function changeSong(increment) {
    if (window.currentPlaylist && window.currentPlaylist.length > 0) {
        window.currentSongIndex = (window.currentSongIndex + increment + window.currentPlaylist.length) % window.currentPlaylist.length;
        const track = window.currentPlaylist[window.currentSongIndex];

        const songElement = document.querySelector("#song");
        const title = document.querySelector("#title");
        const artist = document.querySelector("#artist");
        const thumb = document.querySelector("#thumb");

        songElement.src = track.link;
        title.textContent = track.name;
        artist.textContent = track.artists;
        thumb.src = track.image;

        songElement.play().catch(() => {});
    } else {
        // Sinon, comportement normal avec songs[]
        index = (index + increment + songs.length) % songs.length;
        setSongDetails(index);
        song.play().catch(() => {});
    }
  }


  function playPause() {
    if (!pauseBtn.classList.contains("hidden")) {
      song.pause();
      pauseBtn.classList.add("hidden");
      playBtn.classList.remove("hidden");
      thumb.classList.remove("play");
    } else {
      song.play().catch(() => {});
      startSongProgressTracker();
      playBtn.classList.add("hidden");
      pauseBtn.classList.remove("hidden");
      thumb.classList.add("play");
    }
  }

  function updateSongProgress() {
    clearInterval(songProgressTrackerInterval);
    song.currentTime = progress.value;
    startSongProgressTracker();
  }

  function startSongProgressTracker() {
    clearInterval(songProgressTrackerInterval);
    songProgressTrackerInterval = setInterval(() => {
      progress.value = song.currentTime;
      if (song.currentTime >= song.duration) {
        changeSong(1);
      }
    }, 1000);
  }

  // ============================
  // Volume control
  // ============================
  async function adjustVolume(action, amount) {
    if (action === "decrease") currentVolume = Math.max(0, currentVolume - 0.1);
    if (action === "increase") currentVolume = Math.min(1, currentVolume + 0.1);
    song.volume = currentVolume;
    volumeLabel.textContent = `Volume : ${Math.round(currentVolume * 100)}%`;

    // Optionnel : backend
    try {
      const response = await fetch(`/api/songs/volume/${action}/${amount}`);
      if (response.ok) {
        const songData = await response.json();
        console.log(`Backend volume : ${songData.volume}%`);
      }
    } catch (err) {
      console.warn("Erreur backend volume :", err);
    }
  }

  // ============================
  // Événements
  // ============================
  playButton.addEventListener("click", playPause);
  prevPlayButton.addEventListener("click", () => changeSong(-1));
  nextPlayButton.addEventListener("click", () => changeSong(1));
  progress.addEventListener("change", updateSongProgress);
  btnVolumeDown.addEventListener("click", () => adjustVolume("decrease", 10));
  btnVolumeUp.addEventListener("click", () => adjustVolume("increase", 10));

  songSelector.addEventListener("change", (e) => {
    const selectedIndex = e.target.value;
    if (selectedIndex === "") return;
    index = parseInt(selectedIndex);
    setSongDetails(index);
    song.play().catch(() => {});
    playBtn.classList.add("hidden");
    pauseBtn.classList.remove("hidden");
  });

  moodSelector.addEventListener("change", async (e) => {
    const mood = e.target.value;
    if (!mood) return;

    try {
      const response = await fetch(`/api/songs/mood/${mood}`);
      const randomSong = await response.json();
      if (!randomSong || !randomSong.link) return;

      song.src = randomSong.link;
      song.load();
      song.onloadedmetadata = loadMetadata;
      title.textContent = randomSong.name;
      artist.textContent = randomSong.artists;
      thumb.src = randomSong.image;

      index = songs.findIndex(s => s.link === randomSong.link);
      songSelector.value = index;

      playBtn.classList.add("hidden");
      pauseBtn.classList.remove("hidden");
      thumb.classList.add("play");

      song.play().catch(() => {});
    } catch (err) {
      console.error(err);
    }
  });

  // ============================
  // Initialisation du premier morceau
  // ============================
  setSongDetails(index);
  //
  const playlistToPlay = localStorage.getItem("playlistToPlay");

  if (playlistToPlay) {
      const songs = JSON.parse(playlistToPlay);

      window.currentPlaylist = songs;
      window.currentSongIndex = 0;

      // Charger la première chanson
      setSongDetails(0); // utilise la fonction existante d'initPlayer

      const songElement = document.querySelector("#song");
      songElement.play().catch(() => {});

      // Mettre à jour le selecteur si besoin
      const songSelector = document.getElementById("songSelector");
      if (songSelector) songSelector.value = 0;

      localStorage.removeItem("playlistToPlay");
  }

}

async function loadPlaylists() {
    const container = document.getElementById("playlistsContainer");
    container.innerHTML = "<p>Chargement...</p>";

    try {
        const playlists = await fetch("/api/playlists").then(res => res.json());

        if (!playlists.length) {
            container.innerHTML = "<p>Aucune playlist disponible.</p>";
            return;
        }

        container.innerHTML = "";

        playlists.forEach(pl => {
            const div = document.createElement("div");
            div.className = "playlist-item";

            div.innerHTML = `
                <h4>${pl.name}</h4>
                <p>${pl.description || "Sans description"}</p>

                <div class="playlist-actions">
                    <button class="open-playlist" data-id="${pl.id}">📂 Ouvrir</button>
                    <button class="play-playlist" data-id="${pl.id}">▶ Lire</button>
                </div>
            `;

            // Ouvrir playlist
            div.querySelector(".open-playlist").addEventListener("click", (e) => {
                e.stopPropagation(); // empêche le clic de trigger aussi le div
                window.location.href = `/playlist.html?id=${pl.id}`;
            });

            // Bouton "Lire la playlist"
            div.querySelector(".play-playlist").addEventListener("click", async (e) => {
                e.stopPropagation();

                try {
                    const playlist = await fetch(`/api/playlists/${pl.id}`).then(res => res.json());

                    if (!playlist.songs || playlist.songs.length === 0) {
                        alert("Aucune musique dans cette playlist.");
                        return;
                    }

                    // Envoyer la playlist entière au lecteur principal
                    localStorage.setItem("playlistToPlay", JSON.stringify(playlist.songs));

                    // Redirection vers lecteur
                    window.location.href = "/";
                } catch (err) {
                    console.error(err);
                }
            });

            container.appendChild(div);
        });

    } catch (err) {
        container.innerHTML = "<p>Erreur lors du chargement des playlists.</p>";
        console.error(err);
    }
}

// ----- GESTION DES TABS -----
document.querySelectorAll(".tab").forEach(tab => {
    tab.addEventListener("click", () => {
        
        // remove active class
        document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
        tab.classList.add("active");

        // hide all content
        document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("visible"));

        // show selected
        const target = document.getElementById(tab.dataset.target);
        if (target) target.classList.add("visible");

        // If Playlists tab → load
        if (tab.dataset.target === "tab-playlists") loadPlaylists();
    });
});

document.getElementById("openPlaylistsPage").addEventListener("click", () => {
    window.location.href = "/playlist.html";
});


// Lancement
initPlayer().catch(err => console.error(err));

// Chargement des playlists
loadPlaylists().catch(err => console.error(err));