package com.questworld.util;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.questworld.QuestWorldPlugin;

/**
 * A shared home for tools related to entities and entity manipulation.
 * 
 * @author Erik Zeiger
 */
public class EntityTools {
	private static final EntityType[] alive;
	static {
		// Alive entities
		ArrayList<EntityType> entities = new ArrayList<>();
		entities.add(EntityType.COMPLEX_PART);

		for (EntityType ent : EntityType.values())
			if (ent.isAlive())
				entities.add(ent);

		alive = entities.toArray(new EntityType[entities.size()]);
	}

	/**
	 * Accessor for living entity types.
	 * 
	 * @return An array of all living entities
	 */
	public static EntityType[] aliveEntityTypes() {
		return alive.clone();
	}

	/**
	 * Creates an ItemBuidler that represents a desired entity type, primarily for
	 * use in menus.
	 * 
	 * @param type The desired entity type
	 * @return an {@link ItemBuilder} with display properties specific to
	 *         <tt>type</tt>
	 */
	public static ItemBuilder getEntityDisplay(EntityType type) {
		switch (type) {
			case PLAYER:
				return new ItemBuilder(Material.PLAYER_HEAD);
				
			case GIANT:
				return new ItemBuilder(Material.ZOMBIE_HEAD);
				
			case ENDER_DRAGON:
				return new ItemBuilder(Material.DRAGON_HEAD);

			case WITHER:
				return new ItemBuilder(Material.WITHER_SKELETON_SKULL);
			
			case ARMOR_STAND:
				return new ItemBuilder(Material.ARMOR_STAND);

			case SNOWMAN:
				return new ItemBuilder(Material.SNOWBALL);
				
			case IRON_GOLEM:
				return new ItemBuilder(Material.IRON_INGOT);
				
			case COMPLEX_PART:
				return new ItemBuilder(Material.NETHER_STAR);

			default:
				
				try {
					ItemBuilder ib = new ItemBuilder(Material.STONE);
					Reflect.getAdapter().makeSpawnEgg(ib.get(), type);
					
					return ib;
				}
				catch (IllegalArgumentException e) {
					return new ItemBuilder(Material.BARRIER);
				}
		}
	}

	/**
	 * Returns a "nice" string representing the entities name. May have special
	 * names for entities that are not traditional mobs and are used internally for
	 * other purposes.
	 * <p>
	 * Specifically, <tt>COMPLEX_ENTITY</tt> currently represents a wild card, and
	 * will return "Any Entity" as its name.
	 * 
	 * @param entity The desired entity type
	 * @return A clean name that describes the entity
	 */
	public static String nameOf(EntityType entity) {
		if (entity == EntityType.COMPLEX_PART)
			return "Any Entity";

		return Text.niceName(entity.toString());
	}

	/**
	 * Queries metadata to determine if QuestWorld thinks this mob came from a
	 * spawner.
	 * 
	 * @param entity The target entity
	 * @return Whether or not this entity was spawned by a mob spawner block
	 */
	public static boolean isFromSpawner(LivingEntity entity) {
		return QuestWorldPlugin.get().getSpawnListener().isFromSpawner(entity);
	}
}
