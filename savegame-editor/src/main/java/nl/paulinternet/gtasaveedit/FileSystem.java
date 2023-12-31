package nl.paulinternet.gtasaveedit;

import nl.paulinternet.libsavegame.steam.SteamConfigNode;
import nl.paulinternet.libsavegame.steam.SteamConfigReader;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static nl.paulinternet.gtasaveedit.view.Main.LINUX;
import static nl.paulinternet.gtasaveedit.view.Main.MAC;

public class FileSystem {
    public static final File activeDir = new File(System.getProperty("user.dir"));

    private static boolean dllLoaded;

    private static File savegameDir;

    static {
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                try {
                    System.loadLibrary("editor32");
                } catch (Throwable t) {
                    System.loadLibrary("editor64");
                }
                dllLoaded = true;
            }
        } catch (Throwable ignored) {
        }
    }

    public static final File detectedSaDir = detectSanAndreasDirectory();

    public static File getSavegameDirectory() {
        switch (Settings.getSavegameDirectoryType()) {
            case Settings.DIR_DEFAULT:
                return getDefaultSavegameDirectory();
            case Settings.DIR_ACTIVE:
                return activeDir;
            case Settings.DIR_CUSTOM:
                return new File(Settings.getCustomSavegameDirectory());
            default:
                throw new IllegalStateException();
        }
    }

    public static File getSanAndreasDirectory() {
        switch (Settings.getSanAndreasDirectoryType()) {
            case Settings.DIR_DEFAULT:
                return detectedSaDir;
            case Settings.DIR_CUSTOM:
                return new File(Settings.getCustomSanAndreasDirectory());
            default:
                throw new IllegalStateException();
        }
    }

    public static File getSanAndreasExecutable() {
        File sanAndreasDirectory = getSanAndreasDirectory();
        String saDir;
        if (sanAndreasDirectory != null) {
            saDir = sanAndreasDirectory.getAbsolutePath();
        } else {
            saDir = "";
        }

        if (MAC && !"".equals(saDir)) {
            return new File(saDir.substring(0, saDir.indexOf(".app") + 4));
            // new File(saDir.getAbsolutePath().split("[.]app")[0] + ".app"); slower one
        }

        File file1 = new File(saDir, "gta_sa.exe");
        if (file1.exists()) return file1;

        File file2 = new File(saDir, "gta-sa.exe");
        if (file2.exists()) return file2;

        return null;
    }

    public static boolean isDllLoaded() {
        return dllLoaded;
    }

    private static File detectSanAndreasDirectory() {
        // Windows
        if (dllLoaded) {
            return getWindowsGameDirOrNull();
        } else if (MAC) {
            return getMacOSGameDirOrNull();
        } else if (LINUX) {
            File steamDir = new File(System.getProperty("user.home"), ".steam/steam/steamapps/common/Grand Theft Auto San Andreas");
            if (steamDir.exists()) return steamDir;
        }
        return null;
    }

    private static File getMacOSGameDirOrNull() {
        // Try steam
        File f1 = getMacOSSteamGameDirOrNull();
        if (f1 != null) return f1;

        // Try non-steam
        File f = new File("/Applications/Grand Theft Auto - San Andreas.app");
        if (f.exists()) return f;

        return null;
    }

    private static File getMacOSSteamGameDirOrNull() {
        File steamDir = new File(System.getProperty("user.home"), "Library/Application Support/Steam");
        SteamConfigNode node = SteamConfigReader.readSteamConfig(steamDir);
        if (node != null) {
            String installDir = node.getString("InstallConfigStore", "Software", "Valve", "Steam", "apps", "12250", "installdir");
            if (installDir != null) {
                File f = new File(installDir, "Grand Theft Auto - San Andreas.app");
                if (f.exists()) return f;
            }
        }
        return null;
    }

    private static File getWindowsGameDirOrNull() {
        // Try steam
        File f = getWindowsSteamGameDirOrNull();
        if (f != null) return f;

        // Try not-steam
        byte[] data = getSaDir();
        if (data != null) {
            return getFile(data).getParentFile();
        }

        return null;
    }

    private static File getWindowsSteamGameDirOrNull() {
        byte[] data = getSteamDir();
        if (data != null) {
            File steamDir = getFile(data);
            SteamConfigNode node = SteamConfigReader.readSteamConfig(steamDir);
            if (node != null) {
                String installDir = node.getString("InstallConfigStore", "Software", "Valve", "Steam", "apps", "12120", "installdir");
                if (installDir != null) {
                    File f = new File(installDir);
                    if (f.exists()) return f;
                }

            }
        }
        return null;
    }

    public static File getPlayerImageFile() {
        return getPlayerImageFile(getSanAndreasDirectory());
    }

    private static File getPlayerImageFile(File saDir) {
        if (saDir == null) return null;

        File f = new File(saDir, "models/player.img");
        if (f.exists()) return f;

        f = new File(saDir, "Contents/Resources/transgaming/c_drive/Program Files/Rockstar Games/GTA San Andreas/models/player.img");
        if (f.exists()) return f;

        return null;
    }

    public static File getDefaultSavegameDirectory() {
        if (savegameDir == null) {
            String userHome = System.getProperty("user.home");
            if (MAC) {
                savegameDir = new File(userHome, "/Documents/Rockstar Games/GTA San Andreas User Files");
            } else if (LINUX) {
                savegameDir = new File(userHome, "/.steam/steam/steamapps/compatdata/12120/pfx/drive_c/users/steamuser/My Documents/GTA San Andreas User Files");
            } else {
                savegameDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "GTA San Andreas User Files");
            }
        }
        return savegameDir;
    }

    private static File getFile(byte[] data) {
        int start = 0, end = data.length;

        if (end > 0 && data[end - 1] == 0) end--;
        if (end > 0 && data[end - 1] == 34) end--;
        if (end > 0 && data[0] == 34) start++;

        return new File(new String(data, start, end - start, StandardCharsets.UTF_8));
    }

    private static native byte[] getSaDir();

    private static native byte[] getSteamDir();
}
