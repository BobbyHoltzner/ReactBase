package com.lassieproject;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import heron_systems.Facility.Buildings;
import heron_systems.Facility.POITypes;
import heron_systems.Facility.PersonnelTypes;
import heron_systems.LassieCore.CampusWrapper;
import heron_systems.LassieCore.ClockTime;
import heron_systems.LassieCore.DaysOfWeek;
import heron_systems.LassieCore.Location;
import heron_systems.LassieCore.NavigationParadigm;
import heron_systems.LassieCore.NavigationRequest;
import heron_systems.LassieCore.NavigationReturn;
import heron_systems.LassieCore.ObjectKeys.BuildingKey;
import heron_systems.LassieCore.ObjectKeys.POIKey;
import heron_systems.LassieCore.SearchRequest;
import heron_systems.LassieCore.SearchReturnWithNavigation;

public class LassieCoreModule extends ReactContextBaseJavaModule {
	private boolean mShuttingDown = false;

	public String Testing(){
		CampusWrapper campus = new CampusWrapper();
		String str = "testing";
		return str;
	}


	public LassieCoreModule(ReactApplicationContext reactContext) {
		super(reactContext);
	}

	@Override
	public String getName() {
		return "LassieCoreModule";
	}

	@Override
	public void initialize() {
		super.initialize();
		mShuttingDown = false;
	}

	@Override
	public void onCatalystInstanceDestroy() {
		mShuttingDown = true;
	}


	 @Override
	 public Map<String, Object> getConstants() {
		final Map<String, Object> constants = new HashMap<>();
		return constants;
	 }

// Available as NativeModules.LassieCoreModule.processString
  @ReactMethod
  public void processString(String input, Callback callback) {

		CampusWrapper campus = new CampusWrapper();


		String str = "";


		heron_systems.Mapping.CampusAtlas atlas = new heron_systems.Mapping.CampusAtlas("");
		boolean exists = atlas.ImageFileExists("_2121EISENHOWER", 4);
		heron_systems.Mapping.MapImageFile mapImageFile = atlas.ImageFileFind("_2121EISENHOWER", 4);
		heron_systems.Mapping._2DCoordInteger pixel = atlas.ComputePixelCoordinates("_2121EISENHOWER", 4, new heron_systems.Mapping._2DCoordDouble(0.0, 0.0));
		heron_systems.Mapping._2DCoordDouble globalPos = atlas.ComputeGlobalCoordinates("_2121EISENHOWER", 4, pixel);


		NavigationParadigm navParadigm = new NavigationParadigm(false);

		BuildingKey buildingKey = campus.getBuildingKeyFromEnum(Buildings._2121EISENHOWER);

		Location myLocation = new Location(0.1, 0.2, 1, buildingKey);
		NavigationRequest navigationRequest = new NavigationRequest(myLocation, campus.getPersonnelTypeKeyFromEnum(PersonnelTypes.PATIENT), DaysOfWeek.MONDAY, new ClockTime(12,0));

		SearchRequest searchRequest = new SearchRequest("");


		// POIKey key = campus.getPOIKeyFromEnum(POITypes.VENDING_MACHINE);
		POIKey key = new POIKey(2);
		List<SearchReturnWithNavigation> poiReturn = campus.SearchForPOI(searchRequest, key, navigationRequest, navParadigm, Double.POSITIVE_INFINITY);

		if(poiReturn.size() == 0)
				str += "Nothing Found";
		else {

				NavigationReturn nav = poiReturn.get(0).getNav();
				NavigationParadigm navigationParadigm = new NavigationParadigm(false);
				navigationParadigm.setGenerateWaypoints(true);
				NavigationReturn nav2 = campus.FillParadigm(nav, new NavigationParadigm(true));

				nav = nav2;

				List<String> strList = nav.getTextDirections();

				for (Integer i = 0; i < strList.size(); i++) {
						str += strList.get(i);
						str += "\r\n";
				}

				str += "\r\n\r\n\r\n";

				List<List<Location>> waypointList = nav.getWaypoints();
				for (Integer i = 0; i < waypointList.size(); i++) {
						for (Integer j = 0; j < waypointList.get(i).size(); j++) {
								Location loc = waypointList.get(i).get(j);
								if (loc.type == Location.Types.INDOOR)
										str += loc.x.toString() + " " + loc.y.toString() + " " + loc.floor + " " + campus.getBuildingName(loc.buildingKey);
								if (loc.type == Location.Types.OUTDOOR)
										str += loc.x.toString() + " " + loc.y.toString();
								str += "\r\n";
						}
				}
		}





			callback.invoke(str);
  }


}
