package com.naghtrion.logger;

import java.util.logging.Level;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Naghtrion
 */
public class Logger extends JavaPlugin implements Listener {

    long timelog = new Date().getTime() + 60 * 60 * 1000;

    SimpleDateFormat format = new SimpleDateFormat("dd_MM_yy_HH_mm");
    private String archive = format.format(timelog);

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            try {
                getDataFolder().createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "Plugin iniciado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin desligado com sucesso!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("logger.logger")) {
            writeLog("[" + e.getPlayer().getName() + "] Entrou no Servidor!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer().hasPermission("logger.logger")) {
            writeLog("[" + e.getPlayer().getName() + "] Saiu do Servidor!");
        }
    }

    @EventHandler
    public void comando(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("logger.logger")) {
            writeLog("(" + e.getPlayer().getName() + ") Usou o comando: " + e.getMessage());
        }
    }

    public void writeLog(String message) {
        Thread t = new Thread(() -> {
            try {
                File saveTo = new File(getDataFolder(), getArchiveName());
                if (!saveTo.exists()) {
                    saveTo.createNewFile();
                }
                FileWriter fw = new FileWriter(saveTo, true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(formMessage(message));
                pw.flush();
                pw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public String getArchiveName() {
        if (new Date().getTime() > timelog) {
            timelog = new Date().getTime() + 60 * 60 * 1000;
            archive = format.format(timelog);
            File saveTo = new File(getDataFolder(), archive);
            if (!saveTo.exists()) {
                try {
                    saveTo.createNewFile();
                } catch (IOException ex) {
                    getLogger().log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        return archive + ".log";
    }

    public static String formMessage(String message) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd:MM:yy HH:mm:ss");
        return "[" + format.format(now) + "] " + message;
    }
}
