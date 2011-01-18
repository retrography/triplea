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
 * MovePanel.java
 *
 * Created on December 4, 2001, 6:59 PM
 */

package games.strategy.triplea.ui;

import games.strategy.engine.data.GameData;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Route;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.gamePlayer.IPlayerBridge;
import games.strategy.triplea.attatchments.UnitAttachment;
import games.strategy.triplea.delegate.Matches;
import games.strategy.triplea.delegate.MoveValidator;
import games.strategy.triplea.delegate.dataObjects.MoveDescription;
import games.strategy.triplea.delegate.dataObjects.MustMoveWithDetails;
import games.strategy.triplea.delegate.remote.IMoveDelegate;
import games.strategy.triplea.util.UnitCategory;
import games.strategy.triplea.util.UnitSeperator;
import games.strategy.util.CompositeMatch;
import games.strategy.util.CompositeMatchAnd;
import games.strategy.util.CompositeMatchOr;
import games.strategy.util.InverseMatch;
import games.strategy.util.Match;
import games.strategy.util.Util;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author  Sean Bridges
 * @version 1.0
 */
public class MovePanel extends ActionPanel
{
    
    private static final String MOVE_PANEL_CANCEL = "movePanel.cancel";

    private static final Logger s_logger = Logger.getLogger(MovePanel.class.getName());
    
    private boolean m_listening = false;
    private JLabel m_actionLabel = new JLabel();
    private MoveDescription m_moveMessage;
    //access only through getter and setter!
    private Territory m_firstSelectedTerritory;
    private IPlayerBridge m_bridge;

    private List<Territory> m_forced;
    private boolean m_nonCombat;
    private UndoableMovesPanel m_undableMovesPanel;
    
    private Point m_mouseSelectedPoint;
    private Point m_mouseCurrentPoint;
    
    //use a LinkedHashSet because we want to know the order
    private final Set<Unit> m_selectedUnits = new LinkedHashSet<Unit>();
    
    //the must move with details for the currently selected territory
    //note this is kep in sync because we do not modify m_selectedTerritory directly
    //instead we only do so through the private setter
    private MustMoveWithDetails m_mustMoveWithDetails = null;

    /** Creates new MovePanel */
    public MovePanel(GameData data, MapPanel map)
    {
        super(data, map);
        CANCEL_MOVE_ACTION.setEnabled(false);

         m_undableMovesPanel = new UndoableMovesPanel(data, this);
    }

    private JComponent leftBox(JComponent c)
    {
        Box b = new Box(BoxLayout.X_AXIS);
        b.add(c);
        b.add(Box.createHorizontalGlue());
        return b;
    }

    public void display(final PlayerID id, final boolean nonCombat)
    {
        
        super.display(id);
        m_nonCombat = nonCombat;
        
        SwingUtilities.invokeLater(new Runnable()
        {
        
            public void run()
            {
                removeAll();
                m_actionLabel.setText(id.getName() +
                                      (nonCombat ? " non combat" : " combat") + " move");
                add(leftBox(m_actionLabel));
                add(leftBox(new JButton(CANCEL_MOVE_ACTION)));
                add(leftBox(new JButton(DONE_MOVE_ACTION)));
                add(Box.createVerticalStrut(15));


                add(m_undableMovesPanel);
                add(Box.createGlue());

                SwingUtilities.invokeLater(REFRESH);
        
            }
        
        });
        
    }

    private IMoveDelegate getDelegate()
    {
        return (IMoveDelegate) m_bridge.getRemote();
    }
    
    private void updateMoves()
    {
        m_undableMovesPanel.setMoves(getDelegate().getMovesMade());
    }

    public MoveDescription waitForMove(IPlayerBridge bridge)
    {
        setUp(bridge);
        
        
        waitForRelease();
        
        cleanUp();
        
        MoveDescription rVal = m_moveMessage;
        m_moveMessage = null;
        return rVal;
        
    }

    private void setUp(final IPlayerBridge bridge)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                s_logger.fine("setup");
                
                setFirstSelectedTerritory(null);
                m_forced = null;
                m_bridge = bridge;
                updateMoves();

                if(m_listening)
                    throw new IllegalStateException("Not listening");
                m_listening = true;
                
                getMap().addMapSelectionListener(MAP_SELECTION_LISTENER);
                getMap().addUnitSelectionListener(UNIT_SELECTION_LISTENER);
                getMap().addMouseOverUnitListener(MOUSE_OVER_UNIT_LISTENER);
                
                String key = MOVE_PANEL_CANCEL;
                getRootPane().getActionMap().put(key, CANCEL_MOVE_ACTION);
                getRootPane().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0), key);
            }
        
        });

        
    }

    private void cleanUp()
    {

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                s_logger.fine("cleanup");
                
                if(!m_listening)
                    throw new IllegalStateException("Not listening");
                m_listening = false;
                
                
                getMap().removeMapSelectionListener(MAP_SELECTION_LISTENER);
                getMap().removeUnitSelectionListener(UNIT_SELECTION_LISTENER);
                getMap().removeMouseOverUnitListener(MOUSE_OVER_UNIT_LISTENER);
                
                getMap().setUnitHighlight(null, null);
                
                m_bridge = null;
                m_selectedUnits.clear();
                updateRouteAndMouseShadowUnits(null);
                
                CANCEL_MOVE_ACTION.setEnabled(false);
                JComponent rootPane = getRootPane();
                if(rootPane != null)
                {
                    
                    rootPane.getInputMap().put(KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0),null);     
                }
                m_forced = null;
                
                removeAll();
                REFRESH.run();

            }
        
        });
        


    }


    private final AbstractAction doneMove = new AbstractAction("Done")
    {
        public void actionPerformed(ActionEvent e)
        {
            if(m_undableMovesPanel.getCountOfMovesMade() == 0)
            {
                int rVal = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent( MovePanel.this), "Are you sure you dont want to move?", "End Move", JOptionPane.YES_NO_OPTION);
                if(rVal != JOptionPane.YES_OPTION)
                {
                    return;
                }

            }
            
            m_moveMessage = null;
            release();
            
        }
    };
    
    private final Action DONE_MOVE_ACTION = new WeakAction("Done", doneMove);

    public void cancelMove()
    {
        CANCEL_MOVE_ACTION.actionPerformed(null);
    }

    public void setActive(boolean active)
    {
      super.setActive(active);
      SwingUtilities.invokeLater(new Runnable()
    {
    
        public void run()
        {
            CANCEL_MOVE_ACTION.actionPerformed(null);
        }
    
    });
      
    }

    
    private final Action cancelMove =  new AbstractAction(
        "Cancel")
    {
        public void actionPerformed(ActionEvent e)
        {
            setFirstSelectedTerritory(null);
            m_forced = null;
            m_selectedUnits.clear();
            updateRouteAndMouseShadowUnits(null);
            
            this.setEnabled(false);
            getMap().setMouseShadowUnits(null);
            
        }};
    
    private final AbstractAction CANCEL_MOVE_ACTION = new WeakAction("Cancel", cancelMove);

    /**
     * Return the units that to be unloaded for this route.
     * If needed will ask the user what transports to unload.
     * This is needed because the user needs to be able to select what transports to unload
     * in the case where some transports have different movement, different
     * units etc
     */
    private Collection<Unit> getUnitsToUnload(Route route, Collection<Unit> unitsToMove)
    {
      List<Unit> candidateTransports = new ArrayList<Unit>();
      
      Collection<UnitCategory> unitsToMoveCategories = UnitSeperator.categorize(unitsToMove);
      
      //get the transports with units that we own
      //these transports can be unloaded
      Iterator<Unit> mustMoveWithIter = m_mustMoveWithDetails.getMustMoveWith().keySet().iterator();
      while (mustMoveWithIter.hasNext())
      {
        Unit transport = mustMoveWithIter.next();
        Collection<Unit> transporting = m_mustMoveWithDetails.getMustMoveWith().get(transport);
        if(transporting == null)
          continue;

        //are some of the transported units of the same type we want to unload?
        if(!Util.someIntersect(UnitSeperator.categorize(transporting), unitsToMoveCategories))
            continue;
        
        candidateTransports.add(transport);
      }

      if(candidateTransports.size() == 0)
          return Collections.emptyList();
      
      //just one transport, dont bother to ask
      if(candidateTransports.size() == 1)
          return unitsToMove;
      
      CompositeMatch<Unit> unloadable = getUnloadableMatch();
      
      if(unitsToMove.size() == route.getStart().getUnits().countMatches(unloadable))
          return unitsToMove;

      //are the transports all of the same type
      //if they are, then don't ask
      Collection<UnitCategory> categories = UnitSeperator.categorize(candidateTransports, m_mustMoveWithDetails.getMustMoveWith(), m_mustMoveWithDetails.getMovement() );
      if(categories.size() == 1)
          return unitsToMove;
     
      // choosing what transports to unload
      UnitChooser chooser = new UnitChooser(candidateTransports, m_mustMoveWithDetails.getMustMoveWith(), m_mustMoveWithDetails.getMovement(), m_bridge.getGameData(), getMap().getUIContext());
      chooser.setTitle("What transports do you want to unload");
            
      int option = JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                                chooser, "What transports do you want to unload",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE, null, null, null);
       
      
      if(option != JOptionPane.OK_OPTION)
        return Collections.emptyList();

      Collection<Unit> choosenTransports = chooser.getSelected();
      
      Collection<Unit> allUnitsInSelectedTransports = new ArrayList<Unit>();
      Iterator choosenIter = choosenTransports.iterator();
      while (choosenIter.hasNext())
      {
        Unit transport = (Unit)choosenIter.next();
        Collection<Unit> transporting =  m_mustMoveWithDetails.getMustMoveWith().get(transport);
        if(transporting != null)
          allUnitsInSelectedTransports.addAll(transporting);
      }
      
      //all our units
      allUnitsInSelectedTransports = Match.getMatches(allUnitsInSelectedTransports, Matches.unitIsOwnedBy(getCurrentPlayer()));
      
      //we have selected some units before asking what transports to unload
      //now we have selected all our units in the transports we want to unload.
      //we must now find enough units in the selected transports to fulfill our 
      //original movement quota
      List<Unit> rVal = new ArrayList<Unit>();
      
      for(Unit selected : unitsToMove)
      {
          for(Unit candidate : allUnitsInSelectedTransports)
          {
              if(selected.getType().equals(candidate.getType()) &&
                 selected.getHits() == candidate.getHits())
              {
                  allUnitsInSelectedTransports.remove(candidate);
                  rVal.add(candidate);
                  break;
              }
              
          }
          
      }
      return rVal;
      
    }

    private CompositeMatch<Unit> getUnloadableMatch()
    {
        //are we unloading everything? if we are then we dont need to select the transports
          CompositeMatch<Unit> unloadable = new CompositeMatchAnd<Unit>();
          unloadable.add(Matches.unitIsOwnedBy(getCurrentPlayer()));
          unloadable.add(Matches.UnitIsLand);
          if(m_nonCombat)
              unloadable.add(new InverseMatch<Unit>(Matches.UnitIsAA));
        return unloadable;
    }
    
    /**
     * Allow the user to select specific units, if for example some units
     * have different movement
     */
    private void allowSpecificUnitSelection(Collection<Unit> units, Route route)
    {
        CompositeMatch<Unit> selectableUnits = new CompositeMatchAnd<Unit>();
        selectableUnits.add(Matches.unitIsOwnedBy(getCurrentPlayer()));
        selectableUnits.add(Matches.UnitIsNotFactory);
        if(!m_nonCombat)
            selectableUnits.add(new InverseMatch<Unit>( Matches.UnitIsAA));
        if(route.getEnd().isWater())
            selectableUnits.add(Matches.UnitIsNotLand);
        else
            selectableUnits.add(Matches.UnitIsNotSea);
        
        Collection<Unit> ownedUnits = getFirstSelectedTerritory().getUnits().getMatches(selectableUnits);
        
        boolean mustQueryUser = false;
        
        Set<UnitCategory> categories = UnitSeperator.categorize(ownedUnits, m_mustMoveWithDetails.getMustMoveWith(), m_mustMoveWithDetails.getMovement());
        
        for(UnitCategory category1 : categories)
        {
            //we cant move these, dont bother to check
            if(category1.getMovement() == 0)
                continue;
            
            for(UnitCategory category2 : categories)
            {
                //we cant move these, dont bother to check
                if(category2.getMovement() == 0)
                    continue;
                
                //if we find that two categories are compatable, and some units
                //are selected from one category, but not the other
                //then the user has to refine his selection
                if(category1 != category2 &&
                   category1.getType() == category2.getType() &&
                   (
                           category1.getMovement() != category2.getMovement() ||
                           !Util.equals(category2.getDependents(), category1.getDependents())
                   ) 
                   )
                {
                    //if we are moving all the units from both categories, then nothing to choose
                    if(units.containsAll(category1.getUnits()) &&
                       units.containsAll(category2.getUnits()))
                        continue;
                    //if we are moving some of the units from either category, then we need to stop
                    if(!Util.intersection(category1.getUnits(), units).isEmpty() ||
                       !Util.intersection(category2.getUnits(), units).isEmpty() )
                    {
                        mustQueryUser = true;
                    }
                }
            }
            
        }
        
        if(mustQueryUser)
        {
            CompositeMatch<Unit> rightUnitTypeMatch = new CompositeMatchOr<Unit>();
            for(Unit unit : units)
            {
                rightUnitTypeMatch.add(Matches.unitIsOfType(unit.getType()));
            }
            List<Unit> candidateUnits = Match.getMatches(ownedUnits, rightUnitTypeMatch);
            
            
            UnitChooser chooser = new UnitChooser(candidateUnits, units, m_mustMoveWithDetails.getMustMoveWith(),
                    m_mustMoveWithDetails.getMovement(), m_bridge.getGameData(), false, getMap().getUIContext());

            
            
            String text = "Select units to move from " + getFirstSelectedTerritory() + ".";
            int option = JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                            chooser, text,
                                            JOptionPane.OK_CANCEL_OPTION,
                                            JOptionPane.PLAIN_MESSAGE, null, null, null);
            
            if(option != JOptionPane.OK_OPTION)
            {
                units.clear();
                return;
            }
            
            
            units.clear();
            units.addAll(chooser.getSelected(false));
        }
            
        //add the dependent units
        for(Unit unit : new ArrayList<Unit>(units))
        {
            Collection<Unit> forced = m_mustMoveWithDetails.getMustMoveWith().get(unit);
            if(forced != null)
            {
                units.addAll(forced);
            }
        }
    }

    private Route getRoute(Territory start, Territory end)
    {
        if (m_forced == null)
            return getRouteNonForced(start, end);
        else
            return getRouteForced(start, end);
    }

    /**
     * Get the route inculding the territories that we are forced to move through.
     */
    private Route getRouteForced(Territory start, Territory end)
    {
        if (m_forced == null || m_forced.size() == 0)
            throw new IllegalStateException("No forced territories:" + m_forced +
                                            " end:" + end + " start:" + start);

        Iterator<Territory> iter = m_forced.iterator();

        Territory last = getFirstSelectedTerritory();
        Territory current = null;

        Route total = new Route();
        total.setStart(last);

        while (iter.hasNext())
        {
            current = iter.next();
            Route add = getData().getMap().getRoute(last, current);

            Route newTotal = Route.join(total, add);
            if (newTotal == null)
                return total;

            total = newTotal;
            last = current;
        }

        if (!end.equals(last))
        {

            Route add = getRouteNonForced(last, end);
            Route newTotal = Route.join(total, add);
            if (newTotal != null)
                total = newTotal;
        }

        return total;
    }

    /**
     * Get the route ignoring forced territories
     */
    private Route getRouteNonForced(Territory start, Territory end)
    {
        Route defaultRoute = getData().getMap().getRoute(start, end);
        if (defaultRoute == null)
            throw new IllegalStateException("No route between:" + start +
                                            " and " + end);

        //if the start and end are in water, try and get a water route
        //dont force a water route, since planes may be moving
        if (start.isWater() && end.isWater())
        {
            Route waterRoute = getData().getMap().getRoute(start, end,
                Matches.TerritoryIsWater);
            if (waterRoute != null &&
                waterRoute.getLength() == defaultRoute.getLength())
                return waterRoute;
        }


	// No aa guns on route predicate
        CompositeMatch<Territory> noAA = new CompositeMatchOr<Territory>();
        noAA.add(new InverseMatch<Territory>(Matches.territoryHasEnemyAA(
            getCurrentPlayer(), getData())));
        //ignore the destination
        noAA.add(Matches.territoryIs(end));

	// No neutral countries on route predicate
        Match<Territory> noEmptyNeutral = new InverseMatch<Territory>(new CompositeMatchAnd<Territory>(Matches.TerritoryIsNuetral, Matches.TerritoryIsEmpty));

	// No neutral countries nor AA guns on route predicate
	Match<Territory> noNeutralOrAA = new CompositeMatchAnd<Territory>(noAA, noEmptyNeutral);

	// Try to avoid both AA guns and neutral territories
	Route noAAOrNeutralRoute = getData().getMap().getRoute(start, end, noNeutralOrAA);
        if (noAAOrNeutralRoute != null &&
            noAAOrNeutralRoute.getLength() == defaultRoute.getLength())
	  return noAAOrNeutralRoute;

        // Try to avoid aa guns
        Route noAARoute = getData().getMap().getRoute(start, end, noAA);
        if (noAARoute != null &&
            noAARoute.getLength() == defaultRoute.getLength())
	  return noAARoute;

	// Try to avoid neutral countries
        Route noNeutralRoute = getData().getMap().getRoute(start, end, noEmptyNeutral);
        if (noNeutralRoute != null &&
            noNeutralRoute.getLength() == defaultRoute.getLength())
	  return noNeutralRoute;

        return defaultRoute;
    }
    
    
    private Collection<Unit> getUnitsThatCanMoveOnRoute(Collection<Unit> units, final Route route)
    {
        if(route.getLength() == 0)
            return new ArrayList<Unit>(units);
                
        
        Match<Unit> enoughMovement = new Match<Unit>()
        {
            public boolean match(Unit u)
            {
                return m_mustMoveWithDetails.getMovement().getInt(u) >= route.getLength();
            }
        }; 
        
        CompositeMatch<Unit> match = new CompositeMatchAnd<Unit>();
        
        //we have to allow land units that have no movement to unload
        if(MoveValidator.isUnload(route) && route.getLength() == 1)
        {
            //the expression here is land || (!land && hasEnoughMovement)
            
            CompositeMatch<Unit> landOrCanMove = new CompositeMatchOr<Unit>();
            landOrCanMove.add(Matches.UnitIsLand);
            
            CompositeMatch<Unit> notLandAndCanMove = new CompositeMatchAnd<Unit>();
            notLandAndCanMove.add(enoughMovement);
            notLandAndCanMove.add(Matches.UnitIsNotLand);
            landOrCanMove.add(notLandAndCanMove);
            
            
            match.add(landOrCanMove);
        }
        else 
            match.add(enoughMovement);
        
        
        
        if(!m_nonCombat)
            match.add(new InverseMatch<Unit>(Matches.UnitIsAA));
        
        if(route.getEnd() != null)
        {
            boolean water = route.getEnd().isWater();
            boolean load = MoveValidator.isLoad(route);
            if(water && ! load)
                match.add(Matches.UnitIsNotLand);
            if(!water)
                match.add(Matches.UnitIsNotSea);
        }
        
        
        return Match.getMatches(units, match);
    }

    /**
     * Route can be null.
     */
    private void updateRouteAndMouseShadowUnits(final Route route)
    {
        getMap().setRoute(route, m_mouseSelectedPoint, m_mouseCurrentPoint);
        if(route == null)
            getMap().setMouseShadowUnits(null);
        else
        {
            getMap().setMouseShadowUnits( getUnitsThatCanMoveOnRoute(m_selectedUnits, route));
        }
    }

    /**
     * Allow the user to select what transports to load.
     * 
     * If null is returned, the move should be cancelled.
     */
    private Collection<Unit> getTransportsToLoad(Route route, Collection<Unit> unitsToLoad)
    {
      Match<Unit> alliedTransports = new CompositeMatchAnd<Unit>(Matches.UnitIsTransport, Matches.alliedUnit(getCurrentPlayer(), m_bridge.getGameData()));
      Collection<Unit> transports = Match.getMatches(route.getEnd().getUnits().getUnits(), alliedTransports);
      //nothing to choose
      if(transports.isEmpty())
        return Collections.emptyList();
      //only one, no need to choose
      if(transports.size() == 1)
        return transports;

      MustMoveWithDetails endMustMoveWith = getDelegate().getMustMoveWith(route.getEnd(), route.getEnd().getUnits().getUnits());
      
      //all the same type, dont ask unless we have more than 1 unit type
      if(UnitSeperator.categorize(transports, endMustMoveWith.getMustMoveWith(), endMustMoveWith.getMovement()).size() == 1 
         && unitsToLoad.size() == 1     )
          return transports;
          
      int minTransportCost = 5;
      for(Unit unit : unitsToLoad)
      {
          minTransportCost = Math.min(minTransportCost, UnitAttachment.get(unit.getType()).getTransportCost());
      }
      
      
      List<Unit> candidateTransports = new ArrayList<Unit>();

      //find the transports with space left
      Iterator transportIter = transports.iterator();
      while (transportIter.hasNext())
      {
        Unit transport = (Unit) transportIter.next();
        Collection transporting = (Collection) endMustMoveWith.getMustMoveWith().get(transport);
        int cost = MoveValidator.getTransportCost(transporting);
        int capacity = UnitAttachment.get(transport.getType()).getTransportCapacity();
        if(capacity >= cost + minTransportCost)
          candidateTransports.add(transport);
      }

      if(candidateTransports.size() <= 1)
        return candidateTransports;


      // choosing what units to LOAD.
      UnitChooser chooser = new UnitChooser(candidateTransports, endMustMoveWith.getMustMoveWith(), endMustMoveWith.getMovement(), m_bridge.getGameData(),  getMap().getUIContext());
      chooser.setTitle("What transports do you want to load");
      int option = JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                                chooser, "What transports do you want to load",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE, null, null, null);
      if (option != JOptionPane.OK_OPTION)
        return null;


      return chooser.getSelected(false);
    }

    private void sortByDecreasingMovement(List<Unit> units, Territory territory)
    {
        if(units.isEmpty())
            return;
        
         if(!getFirstSelectedTerritory().equals(territory))
             throw new IllegalStateException("Wrong selected territory");
         
         Comparator<Unit> increasingMovement = new Comparator<Unit>()
         {
             public int compare(Unit u1, Unit u2)
             {
                 int left1 = m_mustMoveWithDetails.getMovement().getInt(u1);
                 int left2 = m_mustMoveWithDetails.getMovement().getInt(u2);

                 return left2 - left1;                 
             }
         };
         
         Collections.sort(units,increasingMovement);
    }
    
    private final UnitSelectionListener UNIT_SELECTION_LISTENER = new UnitSelectionListener()
    {
    
        public void unitsSelected(List<Unit> units, Territory t, MouseDetails me)
        {
            if(!m_listening)
                return;
            
            //check if we can handle this event, are we active?
            if(!getActive())
                return;
           if(t == null)
               return;
          

            
            boolean rightMouse = me.isRightButton();
            boolean noSelectedTerritory = (m_firstSelectedTerritory == null);
            boolean isFirstSelectedTerritory = (m_firstSelectedTerritory == t);

            //de select units            
            if(rightMouse && !noSelectedTerritory)
                deselectUnits(units, t, me);
            //select units            
            else if(!rightMouse && ( noSelectedTerritory || isFirstSelectedTerritory))
                selectUnitsToMove(units, t, me);
            else if(!rightMouse && me.isControlDown() && !isFirstSelectedTerritory)
               selectWayPoint(t);
            else if(!rightMouse && !noSelectedTerritory && !isFirstSelectedTerritory)
                selectEndPoint(t);
            
        }

        private void selectUnitsToMove(List<Unit> units, Territory t, MouseDetails me)
        {
            
            //are any of the units ours, note - if no units selected thats still ok
            for(Unit unit : units)
            {
                if(!unit.getOwner().equals(getCurrentPlayer()))
                {
                    return;
                }
            }

            CompositeMatchAnd<Unit> unitsToMoveMatch = new CompositeMatchAnd<Unit>();
            unitsToMoveMatch.add(Matches.unitIsOwnedBy(getCurrentPlayer()));
            if(m_nonCombat)
                unitsToMoveMatch.add(Matches.UnitIsNotFactory);
            else
            {
                unitsToMoveMatch.add(Matches.UnitIsNotFactory);
                unitsToMoveMatch.add(Matches.UnitIsNotAA);
            }
            

            
            if(units.isEmpty() && m_selectedUnits.isEmpty())
            {
                if(!me.isShiftDown())
                {
                    
                    
                    List<Unit> unitsToMove = t.getUnits().getMatches(unitsToMoveMatch
                            );
                    
                    
                    
                    if(unitsToMove.isEmpty())
                        return;
                    
                    String text = "Select units to move from " + t.getName();
                    
                    //MustMoveWithDetails mustMoveWith = getDelegate().getMustMoveWith(t, unitsToMove);
                    UnitChooser chooser = new UnitChooser(unitsToMove, m_selectedUnits,
                            null, null 
                            ,getData(),  false, getMap().getUIContext() );
                                        
                    int option = JOptionPane.showOptionDialog(getTopLevelAncestor(),
                            chooser, text,
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null, null, null);
                    
                    if (option != JOptionPane.OK_OPTION)
                        return;
                    if(chooser.getSelected(false).isEmpty())
                        return;
                    
                    m_selectedUnits.addAll(chooser.getSelected(false));
                }

            }
            
            
            if(getFirstSelectedTerritory() == null)
            {
                setFirstSelectedTerritory(t);
                
                m_mouseSelectedPoint = me.getMapPoint();
                m_mouseCurrentPoint = me.getMapPoint();
                
                CANCEL_MOVE_ACTION.setEnabled(true);                
            }
            
            //add all
            if(me.isShiftDown())
            {
                CompositeMatch<Unit> ownedNotFactory = unitsToMoveMatch;
                m_selectedUnits.addAll(t.getUnits().getMatches(ownedNotFactory));
            }
            else if(me.isControlDown())
            {
                m_selectedUnits.addAll(units);
            }
            //add one
            else
            {
                //we want to select units with the most movement
                sortByDecreasingMovement(units,t);
                
                for(Unit unit : units)
                {
                    if(!m_selectedUnits.contains(unit))
                    {
                        m_selectedUnits.add(unit);
                        break;
                    }
                }
            }
            
            updateRouteAndMouseShadowUnits(getRoute(getFirstSelectedTerritory(),
                    getFirstSelectedTerritory()));
        }

        private void deselectUnits(List<Unit> units, Territory t, MouseDetails me)
        {         
            
            
            //we have right clicked on a unit stack in a different territory
            if(t != m_firstSelectedTerritory)
                units = Collections.emptyList();
            
            //no unit selected, remove the most recent
            if(units.isEmpty())
            {
                if(me.isControlDown())
                    m_selectedUnits.clear();
                else
                    //remove the last element
                    m_selectedUnits.remove( new ArrayList<Unit>(m_selectedUnits).get(m_selectedUnits.size() -1 ) );
            }
            //we have actually clicked on a specific unit
            else
            {
                //remove all if control is down
                if(me.isControlDown())
                {
                    m_selectedUnits.removeAll(units);
                }
                //remove one
                else
                {
                    //remove those with the least movement first
                    sortByDecreasingMovement(units, t);
                    Collections.reverse(units);
                    
                    for(Unit unit : units)
                    {
                        if(m_selectedUnits.contains(unit))
                        {
                            m_selectedUnits.remove(unit);
                            break;
                        }
                    }
                }
            }
            
            //nothing left, cancel move
            if(m_selectedUnits.isEmpty())
                CANCEL_MOVE_ACTION.actionPerformed(null);
            else
                getMap().setMouseShadowUnits(m_selectedUnits);
        
                
        }
        
        private void selectWayPoint(Territory territory)
        {
            if (m_forced == null)
                m_forced = new ArrayList<Territory>();

            if (!m_forced.contains(territory))
                m_forced.add(territory);

            updateRouteAndMouseShadowUnits(getRoute(getFirstSelectedTerritory(),
                                 getFirstSelectedTerritory()));
        }
        
        private void selectEndPoint(Territory territory)
        {
            Route route = getRoute(getFirstSelectedTerritory(), territory);
            Collection<Unit> units = getUnitsThatCanMoveOnRoute(m_selectedUnits, route);
            if (units.size() == 0)
            {
                CANCEL_MOVE_ACTION.actionPerformed(null);
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(MovePanel.this), "No units have enough movement");
                return;
            }
            else
            {
    
                Collection<Unit> transports = null;
                if(MoveValidator.isLoad(route) && Match.someMatch(units, Matches.UnitIsLand))
                {
                  transports = getTransportsToLoad(route, units);
                  if(transports == null)
                      return;
                }
                else if(MoveValidator.isUnload(route))
                {
                    List<Unit> unloadAble = Match.getMatches(m_selectedUnits,getUnloadableMatch());
                    
                    Collection<Unit> canMove = new ArrayList<Unit>(getUnitsToUnload(route, unloadAble));
                    canMove.addAll(Match.getMatches(m_selectedUnits, new InverseMatch<Unit>(getUnloadableMatch())));
                    if(canMove.isEmpty())
                    {
                        CANCEL_MOVE_ACTION.actionPerformed(null);
                        return; 
                    }
                    else
                    {
                        m_selectedUnits.clear();
                        m_selectedUnits.addAll(canMove);
                    }
                }
                else
                {
                    allowSpecificUnitSelection(units, route);
                    
                    if(units.isEmpty())
                    {
                        CANCEL_MOVE_ACTION.actionPerformed(null);
                        return;
                    }
                    
                }
    
                MoveDescription message = new MoveDescription(units, route, transports);
                m_moveMessage = message;
                setFirstSelectedTerritory(null);
                m_forced = null;
                updateRouteAndMouseShadowUnits(null);
                release();
            }    
        }
        
        
    
    };
    
    
    private final MouseOverUnitListener MOUSE_OVER_UNIT_LISTENER = new MouseOverUnitListener()
    {

        public void mouseEnter(List<Unit> units, Territory territory, MouseDetails me)
        {
            if(!m_listening)
                return;
            
            boolean someOwned = Match.someMatch(units, Matches.unitIsOwnedBy(getCurrentPlayer()));
            boolean isCorrectTerritory = m_firstSelectedTerritory == null || m_firstSelectedTerritory == territory;
            if(someOwned && isCorrectTerritory)
                getMap().setUnitHighlight(units, territory);
            else
                getMap().setUnitHighlight(null, null);
        }
        
    };

    
    private final MapSelectionListener MAP_SELECTION_LISTENER = new
        DefaultMapSelectionListener()
    {
        public void territorySelected(Territory territory, MouseDetails me)
        {
           
        }
        
        public void mouseMoved(Territory territory, MouseDetails me)
        {
            if(!m_listening)
                return;
            
            if (getFirstSelectedTerritory() != null && territory != null)
            {
                m_mouseCurrentPoint= me.getMapPoint();
                updateRouteAndMouseShadowUnits(getRoute(getFirstSelectedTerritory(), territory));
            }
        }
    };

    public String toString()
    {
        return "MovePanel";
    }

    public void undoMove(int moveIndex)
    {
        //clean up any state we may have
        CANCEL_MOVE_ACTION.actionPerformed(null);
        getMap().setRoute(null);

        //undo the move
        String error = getDelegate().undoMove(moveIndex);
        if (error != null)
        {
            JOptionPane.showMessageDialog(getTopLevelAncestor(),
                                          error,
                                          "Could not undo move",
                                          JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            updateMoves();
        }

    }

    private void setFirstSelectedTerritory(Territory firstSelectedTerritory)
    {
        if(firstSelectedTerritory == m_firstSelectedTerritory)
            return;
        
        m_firstSelectedTerritory = firstSelectedTerritory;
        if(m_firstSelectedTerritory == null)
        {
            m_mustMoveWithDetails = null;
        }
        else
        {
            m_mustMoveWithDetails = getDelegate().getMustMoveWith(firstSelectedTerritory, firstSelectedTerritory.getUnits().getUnits());
        }
    }

    private Territory getFirstSelectedTerritory()
    {
        return m_firstSelectedTerritory;
    }

}





/**
 * Avoid holding a strong reference to the action
 * fixes a memory leak in swing.  
 */
class WeakAction extends AbstractAction
{
    private final WeakReference<Action> m_delegate;
    
    WeakAction(String name,Action delegate)
    {
        super(name);
        m_delegate = new WeakReference<Action>(delegate);
    }
    
    
    public void actionPerformed(ActionEvent e)
    {
        Action a = m_delegate.get();
        if(a != null)
            a.actionPerformed(e);
        
    }
    
}