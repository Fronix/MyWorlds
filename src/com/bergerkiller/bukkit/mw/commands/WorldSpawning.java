package com.bergerkiller.bukkit.mw.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.SpawnControl;
import com.bergerkiller.bukkit.mw.WorldConfig;
import com.bergerkiller.bukkit.mw.WorldManager;

public class WorldSpawning extends Command {

	public WorldSpawning(boolean allow) {
		super(Permission.COMMAND_SPAWNING, allow ? "world.allowspawn" : "world.denyspawn");
	}

	public void execute() {
		this.genWorldname(1);
		if (this.handleWorld()) {
			if (args.length != 0) {
				WorldConfig wc = WorldConfig.get(worldname);
				SpawnControl sc = wc.spawnControl;
				//Get the type to set
				String type = null;
				if (args[0].equalsIgnoreCase("animal")) {
					type = "animal";
				} else if (args[0].equalsIgnoreCase("animals")) {
					type = "animal";
				} else if (args[0].equalsIgnoreCase("monster")) {
					type = "monster";
				} else if (args[0].equalsIgnoreCase("monsters")) {
					type = "monster";
				} else if (args[0].equalsIgnoreCase("mob")) {
					type = "mob";
				} else if (args[0].equalsIgnoreCase("mobs")) {
					type = "mob";	
				} else if (args[0].equalsIgnoreCase("creature")) {
					type = "mob";
				} else if (args[0].equalsIgnoreCase("creatures")) {
					type = "mob";
				} else if (args[0].equalsIgnoreCase("all")) {
					type = "mob";
				} else if (args[0].equalsIgnoreCase("npc")) {
					type = "npc";
				} else {
					type = args[0].toUpperCase();
					EntityType ctype = ParseUtil.parseEnum(EntityType.class, type, null);
					if (ctype == null && type.endsWith("S")) {
						ctype = ParseUtil.parseEnum(EntityType.class, type.substring(0, type.length() - 2), null);
					}
					if (ctype != null) {
						type = ctype.name().toLowerCase();
					} else {
						type = null;
					}
				}
				//Set it, of course
				if (type != null) {
					if (commandNode.equals("world.allowspawn")) {
						if (type.equals("animal")) {
							sc.setAnimals(false);
						} else if (type.equals("monster")) {
							sc.setMonsters(false);
						} else if (type.equals("npc")) {
							sc.setNPC(false);
						} else if (type.equals("mob")) {
							sc.deniedCreatures.clear();
						} else {
							sc.deniedCreatures.remove(EntityType.valueOf(type.toUpperCase()));
						}
						if (WorldManager.isLoaded(worldname)) {
							message(ChatColor.GREEN + type + "s are now allowed to spawn on world: '" + worldname + "'!");
						} else {
							message(ChatColor.GREEN + type + "s are allowed to spawn on world: '" + worldname + "' once it is loaded!");
						}
					} else {
						if (type.equals("animal")) {
							sc.setAnimals(true);
						} else if (type.equals("npc")) {
							sc.setNPC(true);
						} else if (type.equals("monster")) {
							sc.setMonsters(true);
						} else if (type.equals("mob")) {
							sc.setAnimals(true);
							sc.setMonsters(true);
						} else {
							sc.deniedCreatures.add(EntityType.valueOf(type.toUpperCase()));
						}
						//Capitalize
						type = Character.toUpperCase(type.charAt(0)) + type.substring(1);
						if (WorldManager.isLoaded(worldname)) {
							message(ChatColor.YELLOW + type + "s can no longer spawn on world: '" + worldname + "'!");
						} else {
							message(ChatColor.YELLOW + type + "s can no longer spawn on world: '" + worldname + "' once it is loaded!");
						}
					}
					World w = WorldManager.getWorld(worldname);
					if (w != null) {
						for (Entity e : w.getEntities()) {
							if (sc.isDenied(e)) {
								e.remove();
							}
						}
					}
				} else {
					message(ChatColor.RED + "Invalid creature type!");
				}
			}
			// Display denied Mobs
			SpawnControl sc = WorldConfig.get(worldname).spawnControl;
			if (sc.deniedCreatures.isEmpty()) {
				message(ChatColor.WHITE + "All mobs are allowed to spawn right now.");
			} else {
				message(ChatColor.WHITE + "The following mobs are denied from spawning:");
				String message = ChatColor.YELLOW.toString();
				boolean first = true;
				for (EntityType type : sc.deniedCreatures) {
					if (first) {
						message += type.getName();
						first = false;
					} else {
						message += ", " + type.getName();
					}
				}
				message(message);
			}
		}	
	}
}
