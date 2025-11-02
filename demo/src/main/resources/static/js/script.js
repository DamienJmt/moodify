const songs = await fetch("/data/songs.json").then(res => res.json());

const playBtn = document.querySelector(".icon-play");
const pauseBtn = document.querySelector(".icon-pause");
const song = document.querySelector("#song");
const title = document.querySelector("#title");
const artist = document.querySelector("#artist");
const thumb = document.querySelector("#thumb");
const progress = document.querySelector("#progress");
const start = document.querySelector("#start");
const end = document.querySelector("#end");

// === Sélecteur de chanson ===
const selector = document.getElementById("songSelector");

const playButton = document.querySelector(".play");
const prevPlayButton = document.querySelector(".prev-play");
const nextPlayButton = document.querySelector(".next-play");

playButton.addEventListener("click", playPause);
prevPlayButton.addEventListener("click", () => changeSong(-1));
nextPlayButton.addEventListener("click", () => changeSong(1));
progress.addEventListener("change", updateSongProgress);

let index = 0;
let interval;
let songProgressTrackerInterval;

// Remplir la liste une fois les chansons chargées
songs.forEach((track, i) => {
  const option = document.createElement("option");
  option.value = i;
  option.textContent = `${track.name} - ${track.artists}`;
  selector.appendChild(option);
});

// Quand l'utilisateur choisit une chanson
selector.addEventListener("change", (e) => {
  const selectedIndex = e.target.value;
  if (selectedIndex === "") return;

  index = parseInt(selectedIndex);
  setSongDetails(index);
  song.play();
  playBtn.classList.add("hidden");
  pauseBtn.classList.remove("hidden");
});

setSongDetails(index);

function setSongDetails(songIndex) {
  song.src = songs[songIndex].link;
  title.textContent = songs[songIndex].name;
  artist.textContent = songs[songIndex].artists;
  thumb.src = songs[songIndex].image;
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
  let min = Math.floor(song.duration / 60);
  let sec = Math.floor(song.duration % 60);

  let curMin = Math.floor(song.currentTime / 60);
  let curSec = Math.floor(song.currentTime % 60);

  if (sec < 10) {
    sec = "0" + sec;
  }
  if (curSec < 10) {
    curSec = "0" + curSec;
  }
  if (min < 10) {
    min = "0" + min;
  }
  if (curMin < 10) {
    curMin = "0" + curMin;
  }
  end.innerHTML = min + ":" + sec;
  start.innerHTML = curMin + ":" + curSec;
}

function changeSong(increment) {
  index = (index + increment + songs.length) % songs.length;
  setSongDetails(index);
  song.play();
}

function playPause() {
  if (!pauseBtn.classList.contains("hidden")) {
    song.pause();
    pauseBtn.classList.add("hidden");
    playBtn.classList.remove("hidden");
    thumb.classList.remove("play");
    return;
  }
  song.play();
  startSongProgressTracker();
  playBtn.classList.add("hidden");
  pauseBtn.classList.remove("hidden");
  thumb.classList.add("play");
}

function updateSongProgress() {
  clearInterval(songProgressTrackerInterval);
  song.currentTime = progress.value;
  startSongProgressTracker();
};

function startSongProgressTracker() {
  clearInterval(songProgressTrackerInterval);
  songProgressTrackerInterval = setInterval(() => {
    progress.value = song.currentTime;
    if (song.currentTime == song.duration) {
      changeSong(1);
    }
  }, 1000);
}