package KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag;

import KartoffelKanaalPlugin.plugin.Main;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Logger;

/**
 * Created by Vincent on 17-Jan-15.
 */
public class AutoAntilagLoop implements Runnable {
    private Thread t;
    private AutoAntilag parent;
    private int timeout = 0;
    private long endtime = 0;
    private String name;
    private String[] commands;

    public AutoAntilagLoop(AutoAntilag parent, String name, String[] commands, int timeout){
        this.parent = parent;
        this.name = name;
        if(this.name == null || this.name.length() == 0)this.name = "AutoAntilagLoop op Thread " + Thread.currentThread().getName();
        this.commands = commands;
        this.timeout = timeout;
    }

    public boolean containsCommand(String cmd){
        if(cmd == null || cmd.length() == 0 || this.commands == null)return false;
        for(int i = 0; i < this.commands.length; i++){
            if(this.commands[i] != null && cmd.equals(this.commands[i]))return true;
        }
        return false;
    }

    public String getName(){return this.name;}
    public long getEndtime(){ return this.endtime;}

    public int getTimeout(){return this.timeout;}
    public void setTimeout(int newTimeout){
        this.timeout = newTimeout;
        if(this.timeout < 30000)this.timeout = 0;
        if(Main.sm != null)Main.sm.notifyChange();
    }

    public void setTimeout(int minutesTimeout, CommandSender cs){
        if(minutesTimeout < 1){
            this._stop();
            this.timeout = 0;
            cs.sendMessage("§e\"" + this.name + "\" is nu uitgeschakeld");
            return;
        }
        this.timeout = minutesTimeout * 60000;
        if(Main.sm != null)Main.sm.notifyChange();
        Logger.getLogger("Minecraft").info("[KKP] De timeout van \"" + this.name + "\" is veranderd naar " + minutesTimeout + " minuten (" + this.timeout + " milliseconden)");
        if(!(cs instanceof ConsoleCommandSender)){
            cs.sendMessage("§eDe timeout van \"" + this.name + "\" is veranderend naar " + minutesTimeout + " minuten (" + this.timeout + " milliseconden)");
        }
        if(this.t != null && this.t.isAlive()) {
            Logger.getLogger("Minecraft").info("[KKP] \"" + this.name + "\" wordt herstart vanwege veranderingen aan de timeout...");
            this.t.interrupt();
        }
        this.t = new Thread(this);
        t.start();
    }

    public void _start(){
        if(this.t == null || !this.t.isAlive()){
            this.t = new Thread(this);
            this.t.start();
        }
    }

    public void _stop(){
        if(this.t != null && this.t.isAlive()){
            this.t.interrupt();
            this.t = null;
        }
    }

    public boolean isRunning(){
        return this.t != null && this.t.isAlive();
    }

    public void run(){
        if(this.timeout < 30000){
            Logger.getLogger("Minecraft").info("[KKP] \"" + this.name + "\" wordt niet gestart omdat die uit is");
            return;
        }
        this.t = Thread.currentThread();
        int failure = 0;
        Server s = Main.plugin.getServer();
        try{
            Logger.getLogger("Minecraft").info("[KKP] \"" + this.name + "\" is gestart op Thread \"" + Thread.currentThread().getName() + "\"");
            while(this.parent.running){
                endtime = System.currentTimeMillis() + timeout;
                Thread.sleep(timeout);
                if(this.parent.preventAction())return;
                try{
                    for(int i = 0; i < this.commands.length; i++){
                        s.dispatchCommand(s.getConsoleSender(), this.commands[i]);
                    }
                }catch(Throwable e){
                    //Logger.getLogger("Minecraft").warning("[KKP] Kon AutoAntilag niet uitvoeren (ThreadName = " + Thread.currentThread().getName() + ")");
                    Logger.getLogger("Minecraft").warning("[KKP] Kon een commando bij \"" + this.name + "\" niet uitvoeren: " + e.toString());
                    if(++failure >= 3){
                        Logger.getLogger("Minecraft").warning("[KKP] Het is 3 keren niet gelukt de commando's uit te voeren bij \"" + this.name + "\". De loop zal daarom beëindigd worden. ThreadName = \"" + Thread.currentThread().getName() + "\"");
                        return;
                    }
                }
            }
        }catch(Throwable e){
            Logger.getLogger("Minecraft").info("[KKP] \"" + this.name + "\" is gestopt");
            return;
        }
    }
}
