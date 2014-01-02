package com.ufam.fourcrawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

public class MainActivity extends Activity {
    private Resources resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	resource = getResources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    public void foursquareAuth(View view) {
	Intent intent = FoursquareOAuth.getConnectIntent(this,
		resource.getString(R.string.CLIENT_ID));
	startActivityForResult(intent, 9000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (requestCode) {
	case 9000:
	    AuthCodeResponse codeResponse = FoursquareOAuth
		    .getAuthCodeFromResult(resultCode, data);
	    String code = codeResponse.getCode();
	    tokenURL = "https://foursquare.com/oauth2/access_token"
		    + "&client_id=" + resource.getString(R.string.CLIENT_ID)
		    + "&client_secret="
		    + resource.getString(R.string.CLIENT_SECRET)
		    + "&redirect_uri=" + "https://fourcrawler.com/hello";
	    break;
	}
    }

    private class AccessToken extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {

	    int what = 0;
	    try {
		final URL url = new URL(tokenUrl + "&code=" + code);
		final HttpURLConnection urlConnection = (HttpURLConnection) url
			.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoInput(true);
		urlConnection.connect();
		final JSONObject jsonObj = (JSONObject) new JSONTokener(
			new FoursquareUtils().streamToString(urlConnection
				.getInputStream())).nextValue();
		tokenAcess = jsonObj.getString("access_token");
		mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
	    } catch (MalformedURLException malForException) {
		what = 1;
	    } catch (IOException ioException) {
		what = 1;
	    } catch (JSONException jsonException) {
		what = 1;
	    }
	    // SessionApp.setCheckinClick(false);
	    return null;
	}

    }

    private void getAccessToken(final String code) {
	new AccessToken.execute();
    }
}
