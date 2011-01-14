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

package games.strategy.engine.data;

import games.strategy.util.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeRouteFinder
{
    private final GameMap m_map;
    private final Match<Territory> m_bestCondition;
    private final Match<Territory> m_nextBestCondition;
    private final Match<Territory> m_otherwiseCondition;

    /**
     * This class can find composite routes between two territories, which is useful for AI non-combat move routes targeting enemy ters behind enemy lines, etc.
     *
     * I'll try my best to explain how it works:
     * Imagine that you want to get from somewhere in the southwest to somewhere in the northeast, and you want to walk on low ground as much as possible. (Valleys)
     * You start out by figuring which path conditions are most favorable, and you decide mountains are the worst, hills are okay, and valleys are the best.
     * So, you start at the start(let's say you somehow have access to a satellite image), and you try to find the most favorable path-segment that will get you closer to the destination.
     * You find a path segment through the valley that will get you closer to the destination, so you remember this and add it to you're planned route. (Since you know this is the most favorable path type)
     * Now you see that after the path through the valley, there is a path through the mountains that will bring you even closer to the destination.
     * You remember this mountain path, but you want to find a valley or hill route(being more favorable) that will bring you closer to the destination instead of this path by mountain.
     * You find no valley route that brings you closer(you already followed valley as far as possible), but you do find a hilly route that brings you closer, so you add that to your planned route instead of the mountain path.
     * After this hilly path, you see that you must use a mountain path to get any closer to the destination, so you add this mountain path to your planned route.
     * After the mountain, you see there is an open valley path directly to the destination, so you add it to your planned route.
     * Now that the route is complete, you start out on your journey and make it to your home safe and sound. :)
     *
     * This is how this route finding method here works, except that it uses Route matches instead of path types, such as the following:
     *     Best: Land owned by us, Next Best: Land that is passable, Otherwise: Any Land
     *
     * @param map
     * @param bestCond - Most favorable route condition
     * @param nextBestCond - Next best
     * @param otherwiseCond - Match that is used to fill any gaps the other two cannot match
     */
    public CompositeRouteFinder(GameMap map, Match<Territory> bestCond, Match<Territory> nextBestCond, Match<Territory> otherwiseCond)
    {
        m_map = map;
        m_bestCondition = bestCond;
        m_nextBestCondition = nextBestCond;
        m_otherwiseCondition = otherwiseCond;
    }

    public Route findRoute(Territory start, Territory end)
    {
        Territory farthestPoint = start;
        List<Route> routeSegments = new ArrayList<Route>();
        while (farthestPoint != end)
        {
            Route bestRouteThatBringsUsCloser = null;
            Route oldRoute = m_map.getRoute(farthestPoint, end, m_otherwiseCondition);
            if(oldRoute == null)
                return null;
            int oldDistanceFromEnd = oldRoute.getLength();

            Route otherwiseRouteSegment = GetNextRouteSegment(farthestPoint, end, m_otherwiseCondition);
            if (otherwiseRouteSegment != null && otherwiseRouteSegment.getLength() > 0)
            {
                if(m_map.getRoute(otherwiseRouteSegment.getEnd(), end, m_otherwiseCondition).getLength() < oldDistanceFromEnd)
                    bestRouteThatBringsUsCloser = otherwiseRouteSegment;
            }
            
            Route nextBestMatchRouteSegment = GetNextRouteSegment(farthestPoint, end, m_nextBestCondition);
            if (nextBestMatchRouteSegment != null && nextBestMatchRouteSegment.getLength() > 0)
            {
                if(m_map.getRoute(nextBestMatchRouteSegment.getEnd(), end, m_otherwiseCondition).getLength() < oldDistanceFromEnd)
                    bestRouteThatBringsUsCloser = nextBestMatchRouteSegment;
            }

            Route bestMatchRouteSegment = GetNextRouteSegment(farthestPoint, end, m_bestCondition);
            if (bestMatchRouteSegment != null && bestMatchRouteSegment.getLength() > 0)
            {
                if(m_map.getRoute(bestMatchRouteSegment.getEnd(), end, m_otherwiseCondition).getLength() < oldDistanceFromEnd)
                    bestRouteThatBringsUsCloser = bestMatchRouteSegment;
            }

            if(bestRouteThatBringsUsCloser == null)
                break; //This shouldn't ever happen, as the otherwise match should always return a complete, valid route

            routeSegments.add(bestRouteThatBringsUsCloser);
            farthestPoint = bestRouteThatBringsUsCloser.getEnd();
        }

        Route route = CombineRoutes(routeSegments);
        return route;
    }
    private Route CombineRoutes(List<Route> routes)
    {
        List<Territory> routeTers = new ArrayList<Territory>();
        for (Route route : routes)
        {
            if (route != null && route.getLength() > 0)
            {
                for (Territory ter : route.getTerritories())
                {
                    if (routeTers.size() > 0 && ter.equals(routeTers.get(routeTers.size() - 1))) //If this ter is already there
                        continue;
                    routeTers.add(ter);
                }
            }
        }
        return new Route(routeTers);
    }
    private Route GetNextRouteSegment(Territory farthestPoint, Territory dest, Match<Territory> condition)
    {
        List<Territory> connectedTersMatchingCond = GetConnectedTersMatching(farthestPoint, condition);

        Territory nextRoutePoint = GetClosestToTer(connectedTersMatchingCond, dest, m_otherwiseCondition);
        return m_map.getRoute(farthestPoint, nextRoutePoint, condition);
    }
    private List<Territory> GetConnectedTersMatching(Territory ter, Match<Territory> condition)
    {
        Match<Territory> betterMatch = LevelUpMatch(condition);
        List<Territory> connectedTersMatchingCond = new ArrayList<Territory>();
        List<Territory> matchingNeighbors = new ArrayList<Territory>(m_map.getNeighbors(ter, condition));

        List<Territory> toProcess = new ArrayList<Territory>();
        for (Territory processingTer : matchingNeighbors)
        {
            if (!betterMatch.equals(condition) && betterMatch.match(processingTer))
                continue; //If we're currently adding to the route with a low-favor match, stop adding when we reach a better match
            toProcess.add(processingTer);
            connectedTersMatchingCond.add(processingTer);
        }
        while(toProcess.size() > 0)
        {
            List<Territory> nextSet = new ArrayList<Territory>();
            for(Territory processingTer : toProcess)
            {
                for(Territory matchingNeighbor : m_map.getNeighbors(processingTer, condition))
                {
                    if(connectedTersMatchingCond.contains(matchingNeighbor))
                        continue;
                    if(!betterMatch.equals(condition) && betterMatch.match(matchingNeighbor))
                        continue; //If we're currently adding to the route with a low-favor match, stop adding when we reach a better match
                    nextSet.add(matchingNeighbor);
                    connectedTersMatchingCond.add(matchingNeighbor);
                }
            }
            toProcess = nextSet;
        }
        return connectedTersMatchingCond;
    }
    private Match<Territory> LevelUpMatch(Match<Territory> match)
    {
        if(match.equals(m_otherwiseCondition))
            return m_nextBestCondition;
        else if(match.equals(m_nextBestCondition))
            return m_bestCondition;
        else
            return match;
    }
    private Territory GetClosestToTer(List<Territory> ters, Territory destination, Match<Territory> routeMatch)
    {
        Territory closestTer = null;
        int closestTerDistance = Integer.MAX_VALUE;
        for(Territory ter : ters)
        {
            Route route = m_map.getRoute(ter, destination, routeMatch);
            if (route.getLength() < closestTerDistance)
            {
                closestTer = ter;
                closestTerDistance = route.getLength();
            }
        }
        return closestTer;
    }
}