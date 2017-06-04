package me.mrCookieSlime.QuestWorld.hooks;

import org.bukkit.plugin.Plugin;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestExtension;
import me.mrCookieSlime.QuestWorld.quests.pluginmissions.ASkyBlockLevelMission;

public class ASkyBlockHook extends QuestExtension {
	@Override
	public String[] getDepends() {
		return new String[] { "ChatReaction" };
	}

	MissionType mission;
	@Override
	public void initialize(Plugin parent) {
		mission = new ASkyBlockLevelMission();
	}

	@Override
	public MissionType[] getMissions() {
		return new MissionType[] { mission };
	}
}