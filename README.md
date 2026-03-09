<p align="center">
  <img src="https://cdn.lolyay.dev/img/lavmusicbot.png" alt="LavMusicBot Logo" width="150">
</p>
# LavMusicBot

Currently Pending Rewrite to DST

<p align="center">
  <img src="https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white" alt="Discord">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven">
</p>

---

## 📋 Table of Contents

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
## ✨ Features

- **High-Quality Audio:** Delivers smooth, high-quality music streaming.
~~- **Dual Operating Modes:** Choose between using external **Lavalink nodes** for scalability or a simple, **integrated
  Lavaplayer** for ease of use.~~
- **Expanded Source Support:** Play music from YouTube, SoundCloud, Spotify, Apple Music, Deezer, Tidal, and more.
- **Hybrid Commands:** Supports both modern slash commands (`/`) and traditional prefix-based commands for flexibility.
- **Full Queue Control:** A comprehensive queue system with options to repeat tracks or the entire queue.
~~- **Easy Configuration:** A single, well-documented `settings.yml` file makes configuration a breeze.~~
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
~~- **A running Lavalink Server (Optional):** Only required if you choose the `nodes` operating mode.~~

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
~ ~
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
