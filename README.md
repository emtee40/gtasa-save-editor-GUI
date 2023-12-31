# GTA:SA Savegame Editor 
![Version Badge](https://img.shields.io/github/v/release/gtasa-savegame-editor/gtasa-savegame-editor?include_prereleases)
[![Windows](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/windows.yml/badge.svg)](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/windows.yml)
[![macOS](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/macos.yml/badge.svg)](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/macos.yml)
[![Linux](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/linux.yml/badge.svg)](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/linux.yml)
[![Join the chat at Gitter](https://badges.gitter.im/gtasa-savegame-editor/Lobby.svg)](https://gitter.im/gtasa-savegame-editor/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 
[![GitHub Pages Badge](https://img.shields.io/badge/docs-gh--pages-70dcf4.svg)](https://gtasa-savegame-editor.github.io/docs/#/)
<details>
<summary>Even more badges...</summary>

![License Badge](https://img.shields.io/github/license/gtasa-savegame-editor/gtasa-savegame-editor)
![Languages Badge](https://img.shields.io/github/languages/count/gtasa-savegame-editor/gtasa-savegame-editor)
![Repo Size Badge](https://img.shields.io/github/repo-size/gtasa-savegame-editor/gtasa-savegame-editor)
![Top Language Badge](https://img.shields.io/github/languages/top/gtasa-savegame-editor/gtasa-savegame-editor)
[![CodeQL](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/codeql.yml/badge.svg)](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/actions/workflows/codeql.yml)

</details>

This is a modified/updated version of the software that can be found on [paulinternet.nl](https://paulinternet.nl/?page=sa).

This version contains the following features (additionally to the ones found in the original version):
- requires Java >= 11
- macOS specific fixes
- support for German/european steam versions of the game
- support for macOS Steam version of the game
- support for Linux Steam version of the game (SteamPlay/Proton)
- support for editing garages and cars in them
- support for transferring savegames from Android
    - support for *reading* Android savegames without crashing is still in development
    - if you think you can help with this, please take a look at [#4](https://github.com/gtasa-savegame-editor/gtasa-savegame-editor/issues/4)
- notifications about new versions
- different download options (additionally to `.jar`):
    - a `.exe` for windows
    - a `.app` (and `.dmg`) for macOS
    - a `.deb` for Debian/Ubuntu
    - a `.rpm` for Fedora/CentOS
- support for theming

## Downloading

Just go to the [Releases page](https://github.com/lfuelling/gtasa-savegame-editor/releases) and download the file you need.

## Building

To build the application:

```bash
$ mvn clean install -DskipTests=true
```

You should now have the following files inside the newly created `savegame-editor/target` folder:

- `gtasaveedit-[version]-jar-with-dependencies.jar`
    - This is the main executable. You can run it with `java -jar [jarfile]`.

### Profiles

You can also build specific application formats. For example: `mvn clean package -Pwindows` will create a `.exe` file.

Available profiles:
- `macOS`
    - builds a `.dmg` and `.app` file
- `deb`
    - builds a `.deb` file
- `rpm`
    - builds a `.rpm` file
- `windows`
    - builds a `.exe`
