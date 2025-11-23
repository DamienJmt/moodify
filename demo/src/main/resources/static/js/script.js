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
    index = (index + increment + songs.length) % songs.length;
    setSongDetails(index);
    song.play().catch(() => {});
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
}

// Lancement
initPlayer().catch(err => console.error(err));