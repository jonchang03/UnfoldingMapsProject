package module3;

//Java utilities libraries
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
//Parsing library
import parsing.ParseFeed;
//Processing library
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Jonathan Chang
 * Date: August 30, 2016
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	// The List you will populate with new SimplePointMarkers
    private List<Marker> markers = new ArrayList<Marker>();
    
    // map markers to corresponding magnitudes
    private HashMap<Marker, Float> magnitudeMap = new HashMap<Marker, Float>();
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// Here is an example of how to use Processing's color method to generate 
    // an int that represents the color yellow.  
    private int blue = color(0, 0, 255);
    private int yellow = color(255, 255, 0);
    private int red = color(255, 0, 0);
    private int black = color(0, 0, 0);
    
    // int to represent marker sizes
    private int small = 5;
    private int medium = 10;
    private int large = 15;

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    //TODO: Add code here as appropriate
	    for (PointFeature f: earthquakes) {
	    	// create marker and add it to the list of markers
	    	Marker newMarker = createMarker(f);
	    	markers.add(newMarker);
	    	// use marker as key and corresponding magnitude as value on hash map
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());	   
	    	magnitudeMap.put(newMarker, mag);
	    }
	    map.addMarkers(markers); // add markers to the map
	    styleMarkers();	// style the markers
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		SimplePointMarker m = new SimplePointMarker(feature.getLocation());
		m.setRadius(10); // set radius of each marker to 10!
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}
	
	// style markers based on instructions in step 4
	private void styleMarkers() {
		for (Marker m : markers) {
			Float mag = magnitudeMap.get(m); // use the marker as the key to retrieve the magnitude
			System.out.println(mag);
	    	if (mag < 4.0) {
	    		m.setColor(blue);
	    		((SimplePointMarker) m).setRadius(small);
	    	} 
	    	else if (mag > 4.0 && mag < 4.9) {
	    		m.setColor(yellow);
	    		((SimplePointMarker) m).setRadius(medium);
	    	} 
	    	else {
	    		m.setColor(red);
	    		((SimplePointMarker) m).setRadius(large);
	    	}
			
		}
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	// match the key shown in the instructions
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		// rect(a, b, c, d): a = x-coordinate; b = y-coordinate; c = width; d = height
		fill(255, 250, 240);
		rect(20, 50, 160, 250);		
		fill(black);
		text("Earthquake Key", 50, 70);
		
		// ellipse(a, b, c, d): a = x-coordinate; b = y-coordinate; c = width; d = height
		fill(red);
		ellipse(50, 107, 15, 15);
		fill(black);
		text("5.0+ Magnitude", 70, 110);
		
		fill(yellow);
		ellipse(50, 145, 10, 10);
		fill(black);
		text("4.0+ Magnitude", 70, 150);
		
		fill(blue);
		ellipse(50, 187, 5, 5);
		fill(black);
		text("Below 4.0", 70, 190);
	}
}
