package com.sensis.voyager.app.routing.router;


import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sensis.voyager.app.routing.Leg;
import com.sensis.voyager.app.routing.Route;
import com.sensis.voyager.app.routing.google.DirectionServiceURLBuilder;
import com.sensis.voyager.app.routing.google.GoogleParser;
import com.sensis.voyager.app.routing.haversine.Haversine;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForceRouter extends AbstractRouter {
    private Map<Pair<String, String>, Double> weightMap = new HashMap<>();
    List<List<LatLng>> allPaths = new ArrayList<>();

    private BruteForceRouter(Builder builder) {
        super(builder);
    }

    @Override
    protected Route doInBackground(Void... params) {
        Route route = new Route();
        weightMap = buildWeightMapWithouGoogleDirectionsAPI(getAllPoints());
        permutations(getWaypoints(), new ArrayList<LatLng>());
        List<LatLng> bestWayPointsPath = findBestPath(allPaths, weightMap);
        route.setWaypointOrder(getWayPointOrder(bestWayPointsPath, getWaypoints()));
        List<LatLng> bestPath = addStartAndEndToPath(bestWayPointsPath);
        route.addPoints(bestPath);
        route.setLegs(getLegs(bestPath, weightMap));
        return route;
    }

    private Map<Pair<String, String>, Double> buildWeightMapWithouGoogleDirectionsAPI(List<LatLng> points) {
        Map<Pair<String, String>, Double> result = new HashMap<>();

        for (LatLng from : points) {
            for (LatLng to : points) {
                if (!from.equals(to)) {
                    double distance = Haversine.distance(from.latitude, from.longitude, to.latitude, to.longitude);
                    result.put(new Pair<>(from.toString(), to.toString()), distance);
                }
            }
        }

        return result;
    }

    private void permutations(List<LatLng> points, List<LatLng> path) {
        if (points.isEmpty()) {
            allPaths.add(path);
        } else {

            for (int i = 0; i < points.size(); i++) {
                List<LatLng> copyOfPoints = new ArrayList<>(points);
                List<LatLng> copyOfPath = new ArrayList<>(path);
                LatLng currentPoint = copyOfPoints.remove(i);
                copyOfPath.add(currentPoint);
                permutations(copyOfPoints, copyOfPath);
            }
        }

    }

    private List<LatLng> findBestPath(List<List<LatLng>> allPaths, Map<Pair<String, String>, Double> weightMap) {
        List<LatLng> bestPath = null;
        double bestCost = -1;

        for (List<LatLng> path : allPaths) {
            double cost = calculateCostForLegs(getLegs(path, weightMap));
            if (bestCost == -1 || bestCost > cost) {
                bestCost = cost;
                bestPath = path;
            }
        }
        return new ArrayList<>(bestPath);
    }

    private double calculateCostForLegs(List<Leg> legs) {
        double cost = 0;

        for (Leg leg : legs) {
            cost += leg.getDistanceValue();
        }
        return cost;
    }

    private List<LatLng> addStartAndEndToPath(List<LatLng> path) {
        List<LatLng> result = new ArrayList<>(path);
        result.add(0, getOrigin());
        result.add(getDestination());
        return result;
    }

    private List<LatLng> getAllPoints() {
        List<LatLng> points = new ArrayList<>();
        points.add(getOrigin());
        points.add(getDestination());
        for (LatLng point : getWaypoints()) {
            points.add(point);
        }
        return points;
    }

    private int[] getWayPointOrder(List<LatLng> path, List<LatLng> waypoints) {
        int[] order = new int[path.size()];

        for (int i = 0; i < order.length; i++) {
            order[i] = waypoints.indexOf(path.get(i));
        }
        return order;
    }

    private List<Leg> getLegs(List<LatLng> path, Map<Pair<String, String>, Double> weightMap) {
        List<Leg> result = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            Leg leg = new Leg();
            leg.setDistanceValue(weightMap.get(new Pair<>(path.get(i).toString(), path.get(i + 1).toString())));
            leg.setDistanceText(weightMap.get(new Pair<>(path.get(i).toString(), path.get(i + 1).toString())).toString());
            leg.setStartLocation(path.get(i));
            leg.setEndLocation(path.get(i + 1));
            result.add(leg);
        }
        return result;
    }

    public static class Builder extends AbstractRouter.Builder<Builder> {
        public BruteForceRouter build() {
            return new BruteForceRouter(this);
        }
    }

    private Map<Pair<String, String>, Leg> buildWeightMapByGoogleDirectionsAPI(List<LatLng> points) {
        Map<Pair<String, String>, Leg> result = new HashMap<>();

        for (LatLng from : points) {
            for (LatLng to : points) {
                if (!from.equals(to)) {
                    try {
                        String url = DirectionServiceURLBuilder.buildWithoutWaypoints(from, to, getTravelMode(), getUnitOption());
                        result.put(new Pair<>(from.toString(), to.toString()), GoogleParser.parse(url).getLegs().get(0));
                    } catch (IOException | JSONException e) {
                        Log.e("BruteForceRouter", e.getMessage(), e);
                        return null;
                    }
                }
            }
        }

        return result;
    }
}
