package io.github.heathensoft.astar;

import io.github.heathensoft.storage.generic.Heap;
import io.github.heathensoft.storage.primitive.IntStack;
import io.github.heathensoft.utility.GridPoint;
import io.github.heathensoft.utility.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


class Search implements Runnable {
    
    private final PathRequest request;
    
    public Search(PathRequest request) {
        this.request = request;
    }
    
    
    @Override
    public void run() {
        SearchArea searchArea = request.searchArea();
        GridPoint startPoint = request.start();
        GridPoint stopPoint = request.stop();
        // initial capacity is somewhat arbitrary
        int initialCap = startPoint.distance(stopPoint) * 4;
        initialCap = Math.min(searchArea.areaSize(), initialCap);
        Heap<SearchNode> open = new Heap<>(initialCap);
        Set<SearchNode> closed = new HashSet<>(initialCap);
        SearchNode startNode = new SearchNode(request.start());
        SearchNode targetNode = new SearchNode(request.stop());
        open.add(startNode);
        SearchNode currentNode;
        while (open.notEmpty()) {
            currentNode = open.pop();
            closed.add(currentNode);
            if (currentNode.equals(targetNode)) {
                boolean collapse = request.collapsePath();;
                IntStack path = currentNode.retracePath(collapse);
                request.resolve(path.array());
                return;
            } int[][] adjacent = Utils.adjacent;
            for (int i = 0; i < 8; i++) {
                int[] offset = adjacent[i];
                int offsetX = offset[0];
                int offsetY = offset[1];
                int x, y; // diagonal move
                if ((offsetX + offsetY) % 2 == 0) {
                    int fromX = currentNode.getX();
                    int fromY = currentNode.getY();
                    x = fromX + offsetX;
                    y = fromY + offsetY;
                    if (searchArea.notTraversable(x,y)) continue;
                    if (searchArea.notTraversable(fromX,y)) continue;
                    if (searchArea.notTraversable(x,fromY)) continue;
                } else { // orthogonal (not diagonal) move
                    x = currentNode.getX() + offsetX;
                    y = currentNode.getY() + offsetY;
                    if (searchArea.notTraversable(x,y)) continue;
                } SearchNode neighbour = new SearchNode(x,y);
                if (closed.contains(neighbour)) continue;
                int movementCost = SearchNode.getDistance(currentNode,neighbour) + currentNode.getGCost() + neighbour.movePenalty();
                boolean notInOpenSet = !open.contains(neighbour);
                if (movementCost < neighbour.getGCost() || notInOpenSet){
                    neighbour.setGCost(movementCost);
                    neighbour.setHCost(SearchNode.getDistance(neighbour,targetNode));
                    neighbour.setMovePenalty(searchArea.movementPenalty(neighbour.getX(),neighbour.getY()));
                    neighbour.setParent(currentNode);
                    if (notInOpenSet) open.add(neighbour);
                    else open.update(neighbour);
                }
            }
        } request.resolve();
    }
}
