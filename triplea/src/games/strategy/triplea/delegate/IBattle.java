/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
/*
 * Battle.java
 * 
 * Created on November 15, 2001, 12:39 PM
 */
package games.strategy.triplea.delegate;

import games.strategy.engine.data.Change;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Route;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.delegate.IDelegateBridge;
import games.strategy.net.GUID;
import games.strategy.triplea.delegate.dataObjects.BattleRecord.BattleResultDescription;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 
 * @author Sean Bridges
 * @version 1.0
 * 
 *          Represents a battle.
 */
public interface IBattle extends java.io.Serializable
{
	public static enum WhoWon
	{
		NOTFINISHED, DRAW, ATTACKER, DEFENDER
	}
	
	
	public static enum BattleType
	{
		NORMAL("Battle"), AIR_BATTLE("Air Battle"), AIR_RAID("Air Raid"), BOMBING_RAID("Bombing Raid");
		
		private final String m_type;
		
		private BattleType(final String type)
		{
			m_type = type;
		}
		
		@Override
		public String toString()
		{
			return m_type;
		}
		
		// if it has the word "Raid" in it, then it is a bombing battle
		public boolean isBombingRun()
		{
			return m_type.indexOf("Raid") != -1;
		}
		
		// if it has the word "Air" in it, then it is an air battle
		public boolean isAirPreBattleOrPreRaid()
		{
			return m_type.indexOf("Air") != -1;
		}
	}
	
	/**
	 * Add a bunch of attacking units to the battle.
	 * 
	 * @param route
	 *            - attack route
	 * @param units
	 *            - attacking units
	 * @param targets
	 *            - Can be NULL if this does not apply. A list of defending units with the collection of attacking units targetting them mapped to each defending unit.
	 * @return attack change object
	 */
	public Change addAttackChange(Route route, Collection<Unit> units, HashMap<Unit, HashSet<Unit>> targets);
	
	/**
	 * There are two distinct super-types of battles: Bombing battles, and Fighting battles.
	 * There may be sub-types of each of these.
	 * 
	 * @return whether this battle is a bombing run
	 */
	public boolean isBombingRun();
	
	/**
	 * The type of battle occurring, example: MustFightBattle, StrategicBombingRaidBattle, etc.
	 * 
	 * @return
	 */
	public BattleType getBattleType();
	
	/**
	 * @return territory this battle is occurring in.
	 */
	public Territory getTerritory();
	
	/**
	 * Fight this battle.
	 * 
	 * @param bridge
	 *            - IDelegateBridge
	 */
	public void fight(IDelegateBridge bridge);
	
	/**
	 * @return whether this battle is over or not
	 */
	public boolean isOver();
	
	/**
	 * Call this method when units are lost in another battle.
	 * This is needed to remove dependent units who have been
	 * lost in another battle.
	 * 
	 * @param battle
	 *            - other battle
	 * @param units
	 *            - referring units
	 * @param bridge
	 *            - IDelegateBridge
	 */
	public void unitsLostInPrecedingBattle(IBattle battle, Collection<Unit> units, IDelegateBridge bridge, boolean withdrawn);
	
	/**
	 * Add a bombardment unit.
	 * 
	 * @param u
	 *            - unit to add
	 */
	public void addBombardingUnit(Unit u);
	
	/**
	 * @return whether battle is amphibious
	 */
	public boolean isAmphibious();
	
	/**
	 * This occurs when a move has been undone.
	 * 
	 * @param route
	 *            - attacking route
	 * @param units
	 *            - attacking units
	 */
	public void removeAttack(Route route, Collection<Unit> units);
	
	/**
	 * If we need to cancel the battle, we may need to perform some cleanup.
	 */
	public void cancelBattle(IDelegateBridge bridge);
	
	/**
	 * Test-method after an attack has been removed.
	 * 
	 * @return whether there are still units left to fight
	 */
	public boolean isEmpty();
	
	/**
	 * @param units
	 * @return units which are dependent on the given units.
	 */
	public Collection<Unit> getDependentUnits(Collection<Unit> units);
	
	/**
	 * @return units which are actually assaulting amphibiously
	 */
	public Collection<Unit> getAmphibiousLandAttackers();
	
	/**
	 * @return units which are actually bombarding
	 */
	public Collection<Unit> getBombardingUnits();
	
	/**
	 * @return what round this battle is in
	 */
	public int getBattleRound();
	
	/**
	 * @return units which are attacking
	 */
	public Collection<Unit> getAttackingUnits();
	
	/**
	 * @return units which are defending
	 */
	public Collection<Unit> getDefendingUnits();
	
	public List<Unit> getRemainingAttackingUnits();
	
	public List<Unit> getRemainingDefendingUnits();
	
	public WhoWon getWhoWon();
	
	public BattleResultDescription getBattleResultDescription();
	
	public PlayerID getAttacker();
	
	public PlayerID getDefender();
	
	public GUID getBattleID();
}
