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
package games.puzzle.tictactoe.delegate.remote;

import games.strategy.engine.data.Territory;
import games.strategy.engine.message.IRemote;

/**
 * Implementing class is responsible for performing a turn in a Tic Tac Toe game.
 * 
 * @author Lane Schwartz
 * @version $LastChangedDate$
 */
public interface IPlayDelegate extends IRemote
{
	public String play(Territory play);
}