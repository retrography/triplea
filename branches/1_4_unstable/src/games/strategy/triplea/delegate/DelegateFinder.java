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
 * DelegateFinder.java
 * 
 * Created on November 28, 2001, 2:58 PM
 */

package games.strategy.triplea.delegate;

import games.strategy.engine.data.GameData;
import games.strategy.engine.delegate.IDelegate;

/**
 * 
 * @author Sean Bridges
 * @version 1.0
 */
public class DelegateFinder
{
	private static final IDelegate findDelegate(GameData data, String delegate_name)
	{
		IDelegate delegate = data.getDelegateList().getDelegate(delegate_name);
		if (delegate == null)
			throw new IllegalStateException(delegate_name + " delegate not found");
		return delegate;
	}
	
	public static final BattleDelegate battleDelegate(GameData data)
	{
		return (BattleDelegate) findDelegate(data, "battle");
	}
	
	public static final MoveDelegate moveDelegate(GameData data)
	{
		return (MoveDelegate) findDelegate(data, "move");
		
	}
	
	public static final PlaceDelegate placeDelegate(GameData data)
	{
		return (PlaceDelegate) findDelegate(data, "place");
		
	}
	
	public static final TechnologyDelegate techDelegate(GameData data)
	{
		return (TechnologyDelegate) findDelegate(data, "tech");
	}
	
	public static final GivePUsDelegate givePUsDelegate(GameData data)
	{
		return (GivePUsDelegate) findDelegate(data, "givePUs");
	}
	
}