package edu.vt.ece4564.AssignmentOne.rferranc;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/*
 * This code is based on the code given at 
 * http://stackoverflow.com/questions/14418021/get-text-from-web-page-to-string
 */
public class HttpTask extends AsyncTask<String, String, String> {
	String resultStr;
	ArrayList<WootEvent> eventList;
	public AsyncResponse interfaceNotify;
	
	@Override
    protected String doInBackground(String... urls) {
		String response = "";
		StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    sb.append(s);
                }
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
		/*
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        StringBuilder sb = new StringBuilder();
        try {
            response = httpclient.execute(new HttpGet(url[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        } 
        return responseString; */
    }
	
	/*
	 * Makes the array of deals to send to maintask (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        resultStr = "";
        WootEvent w = null;
		int i = 0;
		JSONArray jsonString;
		if(!result.startsWith("<")) { // Result is JSON data
			try {
				jsonString = new JSONArray(result);
				JSONObject obj; // Make temp json object
				eventList = new ArrayList<WootEvent>();
				for(i = 0; i < jsonString.length(); i++) {
					obj = jsonString.getJSONObject(i);
					w = new WootEvent();
					w.setEndDate(obj.getString("EndDate"));
					w.setStartDate(obj.getString("StartDate"));
					w.setType(obj.getString("Type"));
					w.setTitle(obj.getString("Title"));
					w.setID(obj.getString("Id"));
					w.setOffers(obj.getJSONArray("Offers"));
					w.setItems(new ArrayList<String>());
					String price = "";
					JSONObject objb;
					JSONArray obja;
					for(int k = 0; k < w.getOffers().length(); k++) {
				
						objb = w.getOffers().getJSONObject(k);
						if(objb.has("Items")) {
							obja = objb.getJSONArray("Items");
							for(int j = 0; j < obja.length(); j++) {
								if(obja.getJSONObject(j).has("SalePrice")) {
									price = "$" + obja.getJSONObject(j).getString("SalePrice");	
								}
							} 
								if(w.getType().equals("WootPlus")) {
									w.addItem(price + ": " + objb.getString("Title"));
								}
								else {
									w.setPrice(price);
								}
							}
					
					}
					w.setOffers(null);
					w.setSite(obj.getString("Site"));
					eventList.add(w);
					obj = null;
				}
				jsonString = null;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else { // Result is HTML data
			eventList = new ArrayList<WootEvent>();
			resultStr = result;
		}
		
		interfaceNotify.processFinish();
    }
  
    public ArrayList<WootEvent> getEvents() {
    	return eventList;
    }
    
    public String getResult() {
    	return resultStr;
    }
    
    public void clearVars() {
    	resultStr = null;
    	eventList = null;
    }
}
	

