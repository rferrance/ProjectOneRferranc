package edu.vt.ece4564.AssignmentOne.rferranc;


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MainActivity extends Activity
	implements AsyncResponse{
	private Button searchButton_;
	private EditText searchParameters_;
	private TextView resultView_;
	private TextView instructionsView_;
	private CheckBox chkWoot, chkTech, chkWine, chkAcc, chkDeals;
	HttpTask normUrlPuller = new HttpTask();
	HttpTask wineUrlPuller = new HttpTask();
	ExpandableListView listView_;
	private static final String WOOT_URL_BASE = "http://api.woot.com/2/events.json?site=";
	private static final String WOOT_API_KEY = "&key=c87d461cadc342d882950184376a68f7";
	private static final String WOOT_URL_NORM = "www.woot.com";
	private static final String WOOT_URL_WINE = "wine.woot.com";
	private static final String WOOT_URL_TECH = "tech.woot.com";
	private static final String WOOT_URL_HOME = "home.woot.com";
	private static final String WOOT_URL_ACC = "accessories.woot.com";
	private static final String WOOT_URL_SPORT = "sport.woot.com";
	private static final String WOOT_URL_TOOLS = "tools.woot.com";
	private static final String WOOT_URL_DEALS = "http://deals.woot.com/deals/search?q=";
	

	String wootHtml;
	JSONArray jsonString;
	ArrayList<WootEvent> eventList; // List of events
	ArrayList<WootEvent> otherSiteList; // List of other sites
	ArrayList<Parent> arrayParents; // List shown on gui
	ArrayList<Parent> allDealsDisplay; // List of all deals
	ArrayList<String> curSites; // List of sites to be displayed
	ArrayList<String> allSites; // List of sites loaded
	boolean isComputing, isSearchingDeals;
	String searchFor; // String to search for
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		normUrlPuller.interfaceNotify = this;
		setContentView(R.layout.activity_main);
		searchParameters_ = (EditText) findViewById(R.id.editText1);
		searchButton_ = (Button) findViewById(R.id.button1);
		instructionsView_ = (TextView) findViewById(R.id.textView1);
		resultView_ = (TextView) findViewById(R.id.textView2);
		listView_ = (ExpandableListView) findViewById(R.id.expandableListView1);
		instructionsView_.setText("Type search term below.");
		chkWoot = (CheckBox) findViewById(R.id.checkBox1);
		chkTech = (CheckBox) findViewById(R.id.checkBox2);
		chkWine = (CheckBox) findViewById(R.id.checkBox3);
		chkDeals = (CheckBox) findViewById(R.id.checkBox4);
		curSites = new ArrayList<String>();
		curSites.add(WOOT_URL_NORM); // Start it out with the woot norm url
		allSites = new ArrayList<String>();
		allSites.add(WOOT_URL_NORM); // Start it out with the woot norm url
		// Get the string of the html code
		normUrlPuller.execute(WOOT_URL_BASE + WOOT_URL_NORM + WOOT_API_KEY);
		isComputing = true; // Set computing variable
		isSearchingDeals = false;
		eventList = new ArrayList<WootEvent>();
		searchFor = "";
		
		setChkBoxListeners(); // Set up check box listeners
		resultView_.setText("Loading Woot Data"); // Tell the user the page is loading
		searchButton_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check that the textedit isn't empty
				String search = searchParameters_.getText().toString();
				if(isSearchingDeals) {
					addRemoveSite(true, (WOOT_URL_DEALS + search));
					searchFor = search;
				}
				if((!eventList.isEmpty()) && (!isComputing)) {
					resultView_.setText("Searching...");
					threadedRequest(search); // Search for the string
				} else if(isComputing) {
					resultView_.setText("Error, loading webpage please wait.");
				} else {
					resultView_.setText("Error no woot data, please wait a moment and retry.");
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 * Threaded search request method, finds the search string in the list of deals
	 */
	public void threadedRequest(String req) {
		final String aSearchString = req;
		Runnable myBackgroundTask = new Runnable() {
			// UI update runnable
			Runnable myUiUpdate = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 
					if((arrayParents != null) && (!arrayParents.isEmpty())) {
			            listView_.setAdapter(new MyCustomAdapter(MainActivity.this,arrayParents));
						MainActivity.this.resultView_.setText("Search complete.");
					} else {
			            listView_.setAdapter(new MyCustomAdapter(MainActivity.this,arrayParents));
						MainActivity.this.resultView_.setText("No deals found.");
					}
					isComputing = false;
				}
			};

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// Find the search term in the HTML
				// Update the UI
				fillDealArray(aSearchString);
				MainActivity.this.runOnUiThread(myUiUpdate);
			}
		};
		Thread t = new Thread(myBackgroundTask);
		t.start(); // Start the new thread
	}

	
	/*
	 * Fill the array of parents to be listed on the app
	 */
	public void fillDealArray(String search) {
		if(!eventList.isEmpty()) {
			arrayParents = new ArrayList<Parent>();
			makeParents();
	        // Loop through all events in eventlist
			for(WootEvent w: eventList) {
				 // JSONObject obj;
				 // JSONArray obja;
				 String price = "";
				 // Make a temp parent for wootPlus
				 Parent p = new Parent();
				 p.setArrayChildren(new ArrayList<String>());
				 if(w.getType().equals("WootPlus")) { // if wootplus, add the matching sub deals
					 for(String s: w.getItems()) {
						 if(s.toLowerCase().contains(search.toLowerCase()) ||
								 w.getTitle().toLowerCase().contains(search.toLowerCase())) {
							 p.addArrayChild(s);
						 }
					 }
				 }
				 price = w.getPrice();
				 
				 /*
				 for(int i = 0; i < w.getOffers().length(); i++) {
					try {
						obj = w.getOffers().getJSONObject(i);
						if(obj.has("Items")) {
							obja = obj.getJSONArray("Items");
							for(int j = 0; j < obja.length(); j++) {
								if(obja.getJSONObject(j).has("SalePrice")) {
									price = "$" + obja.getJSONObject(j).getString("SalePrice");	
								}
							} 
							if((obj.has("Title") && obj.getString("Title").toLowerCase().contains(search.toLowerCase())) 
									|| (p.getTitle().toLowerCase().contains(search.toLowerCase()))) {
								p.addArrayChild(price + ": " + obj.getString("Title"));
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}						
				}*/
				
				if(curSites.contains(w.getSite()) && (w.getTitle().toLowerCase().contains(search.toLowerCase())
						|| !p.getArrayChildren().isEmpty())) {
					if(w.getType().equals("Daily")) {
						arrayParents.get(0).addArrayChild(price + ": " + w.getTitle() + " (" + w.getSite() + ")");
					} else if(w.getType().equals("Moofi")) {
						arrayParents.get(1).addArrayChild(price + ": " + w.getTitle() + " (" + w.getSite() + ")");
					} else if(w.getType().equals("Reckoning")) {
						arrayParents.get(2).addArrayChild(w.getTitle() + " (" + w.getSite() + ")");
					} else if(w.getType().equals("WootOff")) {
						arrayParents.get(3).addArrayChild(price + ": " + w.getTitle() + " (" + w.getSite() + ")");
					} else if(w.getType().equals("Deals")) {
						arrayParents.get(4).addArrayChild(w.getTitle() + " (" + w.getSite() + ")");
					} else if(w.getType().equals("WootPlus")) {
						p.setTitle(w.getTitle() + " (" + w.getSite() + ")");
						arrayParents.add(p);
					}
				}
			}
			
			// Remove any empty parents
			for(int i = (arrayParents.size()-1);i >= 0;i--) {
				if(arrayParents.get(i).getArrayChildren().isEmpty()) {
					arrayParents.remove(i);
				}
			}
		}
		else {
			arrayParents = new ArrayList<Parent>();
		}
	}
	
	/*
	 * Method that adds the parents 
	 * Made to reduce clutter
	 */
	public void makeParents() {
		Parent parent = new Parent();
        parent.setTitle("Daily Deals ");
        parent.setArrayChildren(new ArrayList<String>());
        arrayParents.add(parent);
        parent = new Parent();
        parent.setTitle("Mofi Deals ");
        parent.setArrayChildren(new ArrayList<String>());
        arrayParents.add(parent);
        parent = new Parent();
        parent.setTitle("Reckoning Deals ");
        parent.setArrayChildren(new ArrayList<String>());
        arrayParents.add(parent);
        parent = new Parent();
        parent.setTitle("Woot Offs ");
        parent.setArrayChildren(new ArrayList<String>());
        arrayParents.add(parent);
        parent = new Parent();
        parent.setTitle("Community Deals ");
        parent.setArrayChildren(new ArrayList<String>());
        arrayParents.add(parent);
	}
	
	/*
	 * Add or remove a sites deals to the deals list
	 */
	public void addRemoveSite(boolean add, String url) {
		if(add && !isComputing) { // Add deals from site
			if(allSites.contains(url) && (!url.contains(WOOT_URL_DEALS))) { // Sites deals are loaded
				curSites.add(url);
				threadedRequest("");  // Display all deals
			} else if (!url.contains(WOOT_URL_DEALS)) { // Does not have it loaded
				allSites.add(url);
				curSites.add(url);
				normUrlPuller = new HttpTask();
				normUrlPuller.interfaceNotify = this;
				resultView_.setText("Loading " + url);
				normUrlPuller.execute(WOOT_URL_BASE + url + WOOT_API_KEY);
				isComputing = true; // Set computing variable
			} else {
				allSites.add(url);
				curSites.add(url);
				normUrlPuller = new HttpTask();
				normUrlPuller.interfaceNotify = this;
				resultView_.setText("Loading " + url);
				normUrlPuller.execute(url);
				isComputing = true; // Set computing variable
			}
		} else if (!isComputing) { // Remove deals from site
			resultView_.setText("Removing " + url);
			curSites.remove(url);
			threadedRequest("");  // Display all deals
		} else {
			resultView_.setText("Please wait for the last action to finish.");
		}
	}

	/*
	 * Inform the user the page has loaded and list all deals
	 * (non-Javadoc)
	 * @see com.example.projectonerferranc.AsyncResponse#processFinish(java.lang.String)
	 */
	@Override
	public void processFinish() {
		// TODO Auto-generated method stub
		resultView_.setText("Loading Complete");
		
		if(eventList.isEmpty()) {
			eventList = normUrlPuller.getEvents();
			wootHtml = normUrlPuller.getResult();
			
			// normUrlPuller = new HttpTask();
			// normUrlPuller.interfaceNotify = this;
			// normUrlPuller.execute(WOOT_URL_BASE + WOOT_URL_WINE + WOOT_API_KEY);
			// resultView_.setText("Loading...");
		} else {
			otherSiteList = normUrlPuller.getEvents();
			wootHtml = normUrlPuller.getResult();
			for(WootEvent w: otherSiteList) {
				eventList.add(w);
			}
		}
		if(normUrlPuller.getEvents().isEmpty() && wootHtml != "") { // It retrieved Html for search
			parseDealsHtml();
		} else {
			wootHtml = normUrlPuller.getResult();
			threadedRequest("");  // Display all deals
		}
		normUrlPuller.clearVars(); // Clear the variables
	}
	
	/*
	 * Set up listeners for the check boxes
	 */
	public void setChkBoxListeners() {
		// Click listener for woot checkbox
		chkWoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isComputing) {
					chkWoot.setChecked(!((CheckBox) v).isChecked());
				}
				if (((CheckBox) v).isChecked()) {
					addRemoveSite(true, WOOT_URL_NORM);
				}
				else {
					addRemoveSite(false, WOOT_URL_NORM);
				}
			}
		});
		// Click listener for wine checkbox
		chkWine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isComputing) {
					chkWoot.setChecked(!((CheckBox) v).isChecked());
				}
				if (((CheckBox) v).isChecked()) {
					addRemoveSite(true, WOOT_URL_WINE);
				}
				else {
					addRemoveSite(false, WOOT_URL_WINE);
				}
			}
		});
		// Click listener for tech checkbox
		chkTech.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isComputing) {
					chkWoot.setChecked(!((CheckBox) v).isChecked());
				}
				if (((CheckBox) v).isChecked()) {
					addRemoveSite(true, WOOT_URL_TECH);
				}
				else {
					addRemoveSite(false, WOOT_URL_TECH);
				}
			}
		});
		// Click listener for home checkbox
		chkDeals.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) { // Box is checked
					isSearchingDeals = true;
				}
				else { // Box not checked
					isSearchingDeals = false;
				}
			}
		});
	}
	
	public void parseDealsHtml() {
		String temp = wootHtml.substring(wootHtml.indexOf("forumList deal clearfix"));
		for(String s: temp.split("forumList deal clearfix")) {
			WootEvent w = new WootEvent();
			w.setSite("deals.woot.com");
			w.setType("Deals");
			w.setTitle(s.substring(s.indexOf(("<img alt=\"")+11),s.indexOf("\"",s.indexOf("<img alt=\"")+11)));
			eventList.add(w);
		}
		threadedRequest(searchFor);
	}
	
}
