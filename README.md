<p align="center">
  <img src="https://cdn.lolyay.dev/img/lavmusicbot.png" alt="LavMusicBot Logo" width="150">
</p>

# LavMusicBot

A simple yet powerful music bot for Discord, built with JDA and powered by Lavalink. It's designed to be lightweight, easy to use, and simple to self-host, providing high-quality audio playback for your server.

<p align="center">
  <img src="https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white" alt="Discord">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven">
</p>

---

## 📋 Table of Contents

- [Use The Public Instance](#-use-the-public-instance)
- [✨ Features](#-features)
- [🎵 Commands](#-commands)
- [🚀 Self-Hosting Guide](#-self-hosting-guide)
    - [Prerequisites](#prerequisites)
    - [Setup Instructions](#setup-instructions)
    - [Configuration](#-configuration-settingsyml)
- [🗺️ Roadmap](#️-roadmap)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)
- [Faq](#-faq)

---
## 🤖 Use The Public Instance
# The Public Instance is Deprecated and wont be Updated. Please Host the Bot Yourself!

## ✨ Features

- **High-Quality Audio:** Delivers smooth, high-quality music streaming.
- **Dual Operating Modes:** Choose between using external **Lavalink nodes** for scalability or a simple, **integrated
  Lavaplayer** for ease of use.
- **Expanded Source Support:** Play music from YouTube, SoundCloud, Spotify, Apple Music, Deezer, Tidal, and more.
- **Hybrid Commands:** Supports both modern slash commands (`/`) and traditional prefix-based commands for flexibility.
- **Full Queue Control:** A comprehensive queue system with options to repeat tracks or the entire queue.
- **Easy Configuration:** A single, well-documented `settings.yml` file makes configuration a breeze.
- **Lyrics Support:** Fetch lyrics for songs, with a "live lyrics" feature that highlights the current line.

---

## 🎵 Commands

Here are all the available music commands:

| Command               | Description                                                                                                             |
|:----------------------|:------------------------------------------------------------------------------------------------------------------------|
| **/play `<song>`**    | Plays a song or adds it to the queue. Can be a song name, YouTube URL, SoundCloud URL, etc.                             |
| **/pause**            | Pauses the currently playing track.                                                                                     |
| **/resume**           | Resumes playback if paused.                                                                                             |
| **/stop**             | Stops playback and clears the queue.                                                                                    |
| **/skip**             | Skips the current track and plays the next one in the queue.                                                            |
| **/repeat `<mode>`**  | Sets the repeat mode. Options: `false/off` (no repeat), `true/all` (repeat queue), `one/single` (repeat current track). |
| **/volume `<1-150>`** | Sets the volume (1-150).                                                                                                |
| **/status**           | Shows the current playback status and queue information.                                                                |
| **/version**          | Displays the current version of the bot.                                                                                |
| **/changenode**       | Changes to a different Lavalink node if using multiple nodes.                                                           |
| **/lyrics**           | Shows you the lyrics of the currently playing song and starts live lyrics if avalible.                                  |
| **/stoplive**         | Stops the Live Lyrics (if playing).                                                                                     |

***Note**: If you receive the error **"Unknown Interaction"**, try restarting your Discord client with Ctrl+R*

***

### 🚀 Self-Hosting Guide

This guide is for running the bot using the pre-compiled releases. No development tools are required.

#### Prerequisites

- **Java 23 or higher:** You need Java installed to run the `.jar` file.
- **A Discord Bot Token:** Create a bot application in the [Discord Developer Portal](https://discord.com/developers/applications).
- **A running Lavalink Server (Optional):** Only required if you choose the `nodes` operating mode.

### Setup Instructions

1.  **Download the Bot**
    Go to the [**Latest Release Page**](https://github.com/LOLYAY-INC/LavMusicBot/releases/latest) on GitHub. Download
    the `.jar` file (e.g., `LavMusicBot-1.0.0.jar`) and save it to a new, empty folder.

2.  **Generate the Configuration File**
    Open your terminal or command prompt, navigate to the folder where you saved the bot, and run it for the first time:
    ```sh
    java -jar LavMusicBot-1.0.0.jar
    ```
    *(Replace `LavMusicBot-1.0.0.jar` with the actual name of the file you downloaded).*

    The bot will state that a new configuration file was created and then shut down. You will now see a `settings.yml`
    file in the folder.

3.  **Edit the Configuration**
    Open the newly created `settings.yml` file with any text editor. Follow the guide below to configure the bot. You
    must at least provide your `discord-bot-token`.

4. **Remote Cipher Server**
   If you want the best experience it is very recommended to host a `Remote cipher server` yourself.
   Instructions on How to do that are at https://github.com/kikkia/yt-cipher.
   After Hosting a Remote Cipher Server you need to fill out the `remote-ciphering-server-url` and
   `remote-ciphering-server-password` in the `settings.yml` file.

5. **Run the Bot**
    Once you have saved your changes to `settings.yml`, run the same command again:
    ```sh
    java -jar LavMusicBot.jar
    ```
    This time, the bot will read your configuration and connect to Discord. Congratulations!

---

## ⚙️ Configuration (`settings.yml`)

The `settings.yml` file is your control panel for the bot. The most important setting is `operating-mode`, which
determines how the bot handles audio.

### **1. Operating Mode**

You must choose one of two modes:

- **`lavaplayer`**: (Recommended for beginners) Uses a built-in audio player. It's easy to set up and supports many
  sources like Spotify, Apple Music, etc., out-of-the-box. **No separate Lavalink server is needed.**
- **`nodes`**: Uses one or more external Lavalink servers. This is for more advanced users who want to distribute the
  bot's load. You will need to provide connection details for your Lavalink server(s).

```yaml
# =======================
# The Bot can Operate in 2 modes:
# 1. Using one or more Lavalink servers (nodes) that need to be run separately.
# 2. Using an Integrated Lavaplayer, which is simpler to set up.
# =======================
operating-mode: lavaplayer # Either "lavaplayer" or "nodes"
```

---

### **2. General Configuration**

These settings apply regardless of the operating mode.

```yaml
# Your discord bot token, get it from https://discord.com/developers/applications
discord-bot-token: "YOUR_TOKEN_HERE"

# The default volume of the bot when it starts playing in a new server.
default-volume: 10

# Should the bot stop playing music when the voice-channel is empty
stop-on-empty-channel: true

# If a playlist gets loaded, should the whole playlist be added to the queue?
add-full-playlists-to-queue: false

# The bot primarily uses modern slash commands. You can also define an optional prefix for legacy text-based commands.
command-prefix: "."
```

---

### **3. Lavalink Node Mode (`operating-mode: nodes`)**

These settings are **only** used if `operating-mode` is set to `nodes`.

You can connect to Lavalink servers in two ways:

**A) Using a `nodes.json` file (for multiple servers):**

```yaml
using-nodes-json-file: true
nodes-json-file: "nodes.json" # The path to your nodes.json file
```

**B) Connecting to a single server directly:**

```yaml
using-nodes-json-file: false

# The hostname or IP address of the Lavalink server.
lavalink-host: "lavalink.example.com"
# The port the Lavalink server is running on.
lavalink-port: 2000
# Set to 'true' if your Lavalink server uses a secure (SSL/TLS) connection.
lavalink-secure: false
# The password for your Lavalink server.
lavalink-password: "pleaseletmein"
```

---

### **4. Integrated Lavaplayer Mode (`operating-mode: lavaplayer`)**

These settings are **only** used if `operating-mode` is set to `lavaplayer`.

#### **YouTube OAuth**

To prevent issues with YouTube, the bot can use an authenticated account. After starting the bot for the first time, you
will be asked to open a browser window and log into your YouTube account. The bot will then output a refresh token in
the console. Paste it here to avoid doing this process again.

```yaml
youtube-oauth-refresh-token: ""
```

#### **Additional Music Sources**

Enable and configure extra music sources. The `country-code` (e.g., "US", "DE") is used to get region-specific search
results.

```yaml
# The country code for searches, default is "us"
country-code: "us"
```

- **Spotify**
  ```yaml
  enable-spotify: false
  # To get a Spotify clientId & clientSecret, go to https://developer.spotify.com/dashboard
  spotify-client-id: "YOUR_SPOTIFY_CLIENT_ID"
  spotify-client-secret: "YOUR_SPOTIFY_CLIENT_SECRET"
  ```

- **Apple Music**
  ```yaml
  enable-apple-music: false
  # How to get a token is described in the config file.
  apple-music-token: "YOUR_APPLE_MUSIC_TOKEN"
  ```

- **Deezer**
  ```yaml
  enable-deezer: false
  # Instructions for getting the key and cookie are in the config file.
  deezer-decryption-key: "YOUR_DEEZER_DECRYPTION_KEY"
  deezer-arl-cookie: "YOUR_DEEZER_ARL_COOKIE"
  ```

- **Tidal**
  ```yaml
  enable-tidal: false
  # Instructions for getting the token are in the config file.
  tidal-token: "YOUR_TIDAL_TOKEN"
  ```

---

### **5. Permissions & Lyrics**

These sections are configured towards the bottom of the `settings.yml` file. Please refer to the detailed comments in
the generated file for instructions on setting up role-based permissions and the lyrics feature.

## 🗺️ Roadmap

Here are some of the features planned for future updates:

- [x] **Auto-Leave:** The bot will automatically leave the voice channel after a configurable amount of time when it's left alone.
- [x] **/lyrics Command:** A new command to fetch and display the lyrics for the currently playing song. (Using
  MusixMatch)
- [x] **Live Lyrics:** Highlight the current line of the song being played
- [ ] **Single-Guild Mode:** An option in the config to restrict the bot to operate in only one server, simplifying permissions and setup for private use.
- [ ] **DJ Role:** A configurable "DJ role" that can exclusively use music commands.

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

*Note: I made this [Lavalink JSON to Java Converter](https://lolyay.dev/tools/lavalinkconverter/) to use with the JSON
you get from: [The lavalink Server List](https://lavalink-list.appujet.site/non-ssl)

## 📜 License

LavMusicBot is released under the [Apache License 2.0](https://github.com/LOLYAY-INC/LavMusicBot/blob/main/LICENSE).

## Faq

### The bot doesn't Play music and it prints `Suspicious exception for playback of xxxxxxxx, This video requires login.`

This is because of YouTube's recent changes to their API. To fix this, you need to host a remote cipher server yourself.
Instructions on How to do that are at https://github.com/kikkia/yt-cipher.

### If you have any other Errors, Bugs or more create a Issue at https://github.com/LOLYAY-INC/LavMusicBot/issues
