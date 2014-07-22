/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun.arena.structure;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tntrun.arena.Arena;

public class StructureManager {

	private Arena arena;
	public StructureManager(Arena arena) {
		this.arena = arena;
	}

	private String world;

	public String getWorldName() {
		return world;
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	private Vector p1 = null;

	public Vector getP1() {
		return p1;
	}

	private Vector p2 = null;

	public Vector getP2() {
		return p2;
	}

	private GameZone gamezone = new GameZone();

	public GameZone getGameZone() {
		return gamezone;
	}

	private int gameleveldestroydelay = 8;

	public int getGameLevelDestroyDelay() {
		return gameleveldestroydelay;
	}

	private LoseLevel loselevel = new LoseLevel();

	public LoseLevel getLoseLevel() {
		return loselevel;
	}

	private Vector spawnpoint = null;

	public Vector getSpawnPointVector() {
		return spawnpoint;
	}

	public Location getSpawnPoint() {
		Location spawn = new Location(getWorld(), spawnpoint.getX(), spawnpoint.getY(), spawnpoint.getZ());
		return spawn;
	}

	private int maxPlayers = 6;

	public int getMaxPlayers() {
		return maxPlayers;
	}

	private int minPlayers = 2;

	public int getMinPlayers() {
		return minPlayers;
	}

	private double votesPercent = 0.75;

	public double getVotePercent() {
		return votesPercent;
	}

	private int timelimit = 180;

	public int getTimeLimit() {
		return timelimit;
	}

	private int countdown = 10;

	public int getCountdown() {
		return countdown;
	}

	private Kits kits = new Kits();

	public Kits getKits() {
		return kits;
	}

	private Rewards rewards = new Rewards();

	public Rewards getRewards() {
		return rewards;
	}


	private TeleportDestination teleportDest = TeleportDestination.PREVIOUS;

	public TeleportDestination getTeleportDestination() {
		return teleportDest;
	}

	public static enum TeleportDestination {
		PREVIOUS, LOBBY;
	}

	public boolean isInArenaBounds(Location loc) {
		if (loc.toVector().isInAABB(getP1(), getP2())) {
			return true;
		}
		return false;
	}

	public boolean isArenaConfigured() {
		return isArenaConfiguredString().equals("yes");
	}

	public String isArenaConfiguredString() {
		if (getP1() == null || getP2() == null || world == null) {
			return "Arena bounds not set";
		}
		if (!loselevel.isConfigured()) {
			return "Arena looselevel not set";
		}
		if (spawnpoint == null) {
			return "Arena spawnpoint not set";
		}
		return "yes";
	}

	public void setArenaPoints(Location loc1, Location loc2) {
		this.world = loc1.getWorld().getName();
		this.p1 = loc1.toVector();
		this.p2 = loc2.toVector();
	}

	public void setGameLevelDestroyDelay(int delay) {
		gameleveldestroydelay = delay;
	}

	public boolean setLooseLevel(Location loc1, Location loc2) {
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2)) {
			loselevel.setLooseLocation(loc1, loc2);
			return true;
		}
		return false;
	}

	public boolean setSpawnPoint(Location loc) {
		if (isInArenaBounds(loc)) {
			spawnpoint = loc.toVector();
			return true;
		}
		return false;
	}

	public void setMaxPlayers(int maxplayers) {
		this.maxPlayers = maxplayers;
	}

	public void setMinPlayers(int minplayers) {
		this.minPlayers = minplayers;
	}

	public void setVotePercent(double votepercent) {
		this.votesPercent = votepercent;
	}

	public void setTimeLimit(int timelimit) {
		this.timelimit = timelimit;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public void addKit(String name, Player player) {
		kits.registerKit(name, player);
	}

	public void removeKit(String name) {
		kits.unregisterKit(name);
	}

	public void setRewards(ItemStack[] rewards) {
		this.rewards.setRewards(rewards);
	}

	public void setRewards(int money) {
		this.rewards.setRewards(money);
	}

	public void setTeleportDestination(TeleportDestination teleportDest) {
		this.teleportDest = teleportDest;
	}

	public void saveToConfig() {
		FileConfiguration config = new YamlConfiguration();
		// save arena bounds
		try {
			config.set("world", world);
			config.set("p1", p1);
			config.set("p2", p2);
		} catch (Exception e) {
		}
		// save gamelevel destroy delay
		config.set("gameleveldestroydelay", gameleveldestroydelay);
		// save looselevel
		try {
			loselevel.saveToConfig(config);
		} catch (Exception e) {
		}
		// save spawnpoint
		try {
			config.set("spawnpoint", spawnpoint);
		} catch (Exception e) {
		}
		// save maxplayers
		config.set("maxPlayers", maxPlayers);
		// save minplayers
		config.set("minPlayers", minPlayers);
		// save vote percent
		config.set("votePercent", votesPercent);
		// save timelimit
		config.set("timelimit", timelimit);
		// save countdown
		config.set("countdown", countdown);
		// save teleport destination
		config.set("teleportto", teleportDest.toString());
		// save kits
		kits.saveToConfig(config);
		// save rewards
		rewards.saveToConfig(config);
		try {
			config.save(arena.getArenaFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
		// load arena world location
		world = config.getString("world", null);
		// load arena bounds
		p1 = config.getVector("p1", null);
		p2 = config.getVector("p2", null);
		// load gamelevel destroy delay
		gameleveldestroydelay = config.getInt("gameleveldestroydelay", gameleveldestroydelay);
		// load looselevel
		loselevel.loadFromConfig(config);
		// load spawnpoint
		spawnpoint = config.getVector("spawnpoint", null);
		// load maxplayers
		maxPlayers = config.getInt("maxPlayers", maxPlayers);
		// load minplayers
		minPlayers = config.getInt("minPlayers", minPlayers);
		// load vote percent
		votesPercent = config.getDouble("votePercent", votesPercent);
		// load timelimit
		timelimit = config.getInt("timelimit", timelimit);
		// load countdown
		countdown = config.getInt("countdown", countdown);
		// load teleport destination
		teleportDest = TeleportDestination.valueOf(config.getString("teleportto", "PREVIOUS"));
		// load kits
		kits.loadFromConfig(config);
		// load rewards
		rewards.loadFromConfig(config);
	}

}
