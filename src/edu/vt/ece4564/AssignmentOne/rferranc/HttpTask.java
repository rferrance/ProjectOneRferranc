package edu.vt.ece4564.AssignmentOne.rferranc;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
 */
public class HttpTask extends AsyncTask<String, String, String> {
	String resultStr;
	ArrayList<WootEvent> eventList;
	public AsyncResponse interfaceNotify;
	
	@Override
    protected String doInBackground(String... url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(url[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
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
        return responseString;
    }
	
	/*
	 * Makes the array of deals to send to maintask (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        resultStr = new String(result);
        WootEvent w = null;
		int i = 0;
		JSONArray jsonString;
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
				w.setSite(obj.getString("Site"));
				eventList.add(w);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		String retStr;
		if(w == null) {
			retStr = "";
		}
		else {
			retStr = w.getSite();
		}*/
		
		interfaceNotify.processFinish();
    }
    
    public ArrayList<WootEvent> getEvents() {
    	return eventList;
    }
    
    public String getResult() {
    	return resultStr;
    }
}
	

