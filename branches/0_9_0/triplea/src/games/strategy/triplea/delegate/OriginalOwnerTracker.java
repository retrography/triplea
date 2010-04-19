/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*
 * OriginalOwnerTracker.java
 *
 * Created on December 10, 2001, 9:04 AM
 */

package games.strategy.triplea.delegate;

import java.io.*;
import java.util.*;
import games.strategy.engine.data.*;

/**
 *
 * @author  Sean Bridges
 * @version 1.0
 *
 * Tracks the original owner of things.
 * Needed since territories and factories must revert
 * to their original owner when captured from the enemy.
 */
public class OriginalOwnerTracker implements java.io.Serializable
{

	//maps object -> PlayerID
	//weak since we dont want to prevent dead units
	//from being gc'd
	private Map<Object, PlayerID> m_originalOwner = new WeakHashMap<Object, PlayerID>();

	/** Creates new OriginalOwnerTracker */
    public OriginalOwnerTracker()
	{
    }

	public void addOriginalOwner(Object obj, PlayerID player)
	{
		m_originalOwner.put(obj, player);
	}

	public void addOriginalOwner(Collection objects, PlayerID player)
	{
		Iterator iter = objects.iterator();
		while(iter.hasNext())
		{
			addOriginalOwner(iter.next(), player);
		}
	}

	public PlayerID getOriginalOwner(Object obj)
	{
		return m_originalOwner.get(obj);
	}

	private void writeObject(ObjectOutputStream os) throws IOException
	{
		os.writeObject(new HashMap<Object, PlayerID>(m_originalOwner));
	}

	@SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		m_originalOwner = new WeakHashMap<Object, PlayerID>((HashMap) in.readObject()  );
	}

    public Collection<Object> getOriginallyOwned(PlayerID player)
    {
        Collection<Object> rVal = new ArrayList<Object>();
        Iterator<Object> iter = m_originalOwner.keySet().iterator();
        while (iter.hasNext())
        {
            Object item = iter.next();
            if(m_originalOwner.get(item).equals(player))
            {
                rVal.add(item);
            }
        }
        return rVal;
    }

}