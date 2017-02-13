/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projection.spacereduction.seamcarving;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Windows
 */
public class RectGraph {
    
    private List<Rectangle2D.Double> items;
    private Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> similarity;
    private Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> distance;

    private RectGraphCache cache;

    public RectGraph(List<Rectangle2D.Double> items, Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> distance) {
        this.items = items;
        this.similarity = similarity;

        initializeDistances();
        cache = new RectGraphCache(items, similarity, distance);
    }

    public List<Rectangle2D.Double> getItems() {
        return items;
    }

    public Map<SeamPair<Rectangle2D.Double, Rectangle2D.Double>, Double> getSimilarity() {
        return similarity;
    }

    public double distance(Rectangle2D.Double r1, Rectangle2D.Double r2) {
        return distance.get(new SeamPair<>(r1, r2));
    }

    public double weightedDegree(Rectangle2D.Double r) {
        return cache.weightedDegree(r);
    }

    public double shortestPath(Rectangle2D.Double r1, Rectangle2D.Double r2) {
        return cache.shortestPath(r1, r2);
    }

    public Integer[] nonZeroAdjacency(Rectangle2D.Double r) {
        return cache.nonZeroAdjacency(r);
    }

    public Rectangle2D.Double[] convertWordsToArray() {
        return items.toArray(new Rectangle2D.Double[items.size()]);
    }

    public double[][] convertSimilarityToArray() {
        double[][] result = new double[items.size()][items.size()];
        for( int i = 0; i < items.size(); ++i )
            for( int j = 0; j < items.size(); ++j ) {
                SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(items.get(i), items.get(j));
                result[i][j] = similarity.get(rp);
            }

        return result;
    }

    private void initializeDistances() {
        distance = new HashMap<>();
        for( int i = 0; i < items.size(); ++i )
            for( int j = 0; j < items.size(); ++j ) {
                SeamPair<Rectangle2D.Double, Rectangle2D.Double> rp = new SeamPair<>(items.get(i), items.get(j));
                double sim = similarity.get(rp);
                double dist = LayoutUtils.idealDistanceConverter(sim);
                distance.put(rp, dist);
            }
    }

    public void reorderWords(int startIndex) {
        int n = items.size();
        List<Rectangle2D.Double> path = new ArrayList<>();
        for( int i = 0; i < n; ++i )
            path.add(items.get((i + startIndex + 1) % n));

        for( int i = 0; i < n; ++i )
            items.set(i, path.get(i));      	
    }
    
}
