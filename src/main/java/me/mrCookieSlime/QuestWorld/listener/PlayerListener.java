package me.mrCookieSlime.QuestWorld.listener;

import me.mrCookieSlime.QuestWorld.GuideBook;
import me.mrCookieSlime.QuestWorld.QuestingImpl;
import me.mrCookieSlime.QuestWorld.api.Decaying;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IPlayerStatus;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.manager.Party;
import me.mrCookieSlime.QuestWorld.manager.ProgressTracker;
import me.mrCookieSlime.QuestWorld.manager.Party.LeaveReason;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onQuestBook(PlayerInteractEvent event) {
		Action a = event.getAction();
		if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
			if (GuideBook.isGuide(event.getItem()))
				QuestBook.openLastMenu(event.getPlayer());
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		Player p = event.getEntity();
		IPlayerStatus playerStatus = QuestWorld.getPlayerStatus(p);
		
		for(IMission mission : QuestWorld.getViewer().getDecayingMissions())
			if(playerStatus.hasDeathEvent(mission))
				((Decaying) mission).onDeath(event, QuestWorld.getMissionEntry(mission, p));
	}
	
	HashMap<UUID, Integer> partyKick = new HashMap<>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		int task_id = partyKick.getOrDefault(p.getUniqueId(), -1);
		if(task_id > -1) {
			Bukkit.getScheduler().cancelTask(task_id);
			partyKick.remove(task_id);
		}
		
		if (QuestWorld.getPlugin().getConfig().getBoolean("book.on-first-join") &&
				!ProgressTracker.exists(p.getUniqueId()))
			p.getInventory().addItem(GuideBook.get());
	}
	
	@EventHandler
	public void onleave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		
		int autokick = QuestWorld.getPlugin().getConfig().getInt("party.auto-kick", -1);
		if(autokick == 0) {
			Party party = QuestWorld.getPlayerStatus(player).getParty();
			if(party.isLeader(player))
				party.disband();
			else
				party.playerLeave(player, LeaveReason.DISCONNECT);
		}
		else if(autokick > 0) {
			Party party = QuestWorld.getPlayerStatus(player).getParty();
			int task_id = new BukkitRunnable(){
				@Override
				public void run() {
					if(party.isLeader(player))
						party.disband();
					else
						party.playerLeave(player, LeaveReason.DISCONNECT);
					partyKick.remove(getTaskId());
				}
			}.runTaskLater(QuestWorld.getPlugin(), autokick).getTaskId();
			
			partyKick.put(player.getUniqueId(), task_id);
		}
		
		((QuestingImpl)QuestWorld.getAPI()).playerLeave(player);
	}
	
	// Since we can't randomly update recipes at runtime, replace result with latest lore
	@EventHandler
	public void preCraft(PrepareItemCraftEvent e) {
		boolean hasTable = false;
		for(ItemStack is : e.getInventory().getMatrix())
			if(is != null) {
				if(is.getType() == Material.WORKBENCH && !hasTable)
					hasTable = true;
				else
					return;
			}
		
		if(hasTable)
			e.getInventory().setResult(GuideBook.get());
	}
}
