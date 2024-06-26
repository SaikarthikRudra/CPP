package com.exavalu.customer.product.portal.service.distanceapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceMain {
	
	@Autowired
	private DistanceServiceAPI distanceServiceAPI;
	
	public Map<String, Object> getNearestLocations(String location) {
	    List<String> warehouseLocations = Arrays.asList("Kolkata", "Bangalore", "Mumbai", "Hyderabad");
	    List<Double> distances = distanceServiceAPI.getDistances(location, warehouseLocations);

	    // Find the minimum distance
	    double minDistance = distances.stream().min(Double::compare).orElse(Double.MAX_VALUE);
	    
	    int minIndex = distances.indexOf(minDistance);
	    String nearestLocation = warehouseLocations.get(minIndex);
	    
	    // Remove the minimum distance from the list
	    List<Double> distancesWithoutMin = new ArrayList<>(distances);
	    distancesWithoutMin.remove(minDistance);
	    
	    // Find the second minimum distance
	    double secondMinDistance = distancesWithoutMin.stream().min(Double::compare).orElse(Double.MAX_VALUE);

	    // Find the index of the second minimum distance
	    int secondMinIndex = distances.indexOf(secondMinDistance);
	    
	    // Get the corresponding location for the second minimum distance
	    String secondNearestLocation = warehouseLocations.get(secondMinIndex);
	    
	    //-third nearest location----------------------------------------------
	    // Remove the second minimum distance from the list
	    List<Double> distancesWithoutSecondMin = new ArrayList<>(distancesWithoutMin);
	    distancesWithoutSecondMin.remove(secondMinDistance);
	    
	    // Find the third minimum distance
	    double thirdMinDistance = distancesWithoutSecondMin.stream().min(Double::compare).orElse(Double.MAX_VALUE);

	    // Find the index of the third minimum distance
	    int thirdMinIndex = distances.indexOf(thirdMinDistance);
	    
	    // Get the corresponding location for the third minimum distance
	    String thirdNearestLocation = warehouseLocations.get(thirdMinIndex);
	    
	  //-Fourth nearest location----------------------------------------------
	    // Remove the third minimum distance from the list
	    List<Double> distancesWithoutThirdMin = new ArrayList<>(distancesWithoutSecondMin);
	    distancesWithoutThirdMin.remove(thirdMinDistance);
	    
	    // Find the third minimum distance
	    double FourthMinDistance = distancesWithoutThirdMin.stream().min(Double::compare).orElse(Double.MAX_VALUE);

	    // Find the index of the third minimum distance
	    int FourthMinIndex = distances.indexOf(FourthMinDistance);
	    
	    // Get the corresponding location for the third minimum distance
	    String FourthNearestLocation = warehouseLocations.get(FourthMinIndex);
	    

	    Map<String, Object> response = Map.of(
	    		
	        "NearestLocation", nearestLocation,
	        "NearestLocationDistance", minDistance,
	        "SecondNearestLocation", secondNearestLocation,
	        "SecondNearestLocationDistance", secondMinDistance,
	        "ThirdNearestLocation", thirdNearestLocation,
	        "ThirdNearestLocationDistance", thirdMinDistance,
	        "FourthNearestLocation", FourthNearestLocation,
	        "FourthNearestLocationDistance", FourthMinDistance
	        
	    );

	    return response;
	}

}
