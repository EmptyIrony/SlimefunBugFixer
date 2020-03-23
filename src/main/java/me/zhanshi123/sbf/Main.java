package me.zhanshi123.sbf;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin
{
	private static Main instance=null;
	public static Main getInstance()
	{
		return instance;
	}
	public void onEnable()
	{
		instance=this;
		new Messages();
		Bukkit.getPluginManager().registerEvents(new Listeners(), instance);
		new ConfigManager();
		new BackpackCooldown();
		new InteractCooldown();
		Plugin sf=Bukkit.getPluginManager().getPlugin("Slimefun");
		if(sf==null){
			setEnabled(false);
			Bukkit.getConsoleSender().sendMessage("§6§lSlimefunBugFixer §7>>> §c§l未找到Slimefun，停止加载");
			return;
		}
		else{
			if(sf.getDescription().getVersion().startsWith("4.")){
				Bukkit.getPluginManager().registerEvents(new ListenersV4(), instance);
			}
		}
		Bukkit.getConsoleSender().sendMessage("§6§lSlimefunBugFixer §7>>> §a§l插件成功加载");
	}
	
}
