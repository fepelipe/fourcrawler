package com.ufam.fourcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class AccessToken extends AsyncTask<Void, Void, Void> {
    private String code;
    private String token;
    private String tokenUrl;
    private Handler handler;

    public AccessToken(String code, String tokenUrl) {
	this.code = code;
	this.tokenUrl = tokenUrl;
    }

    public String getToken() {
	return token;
    }

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
	    String response = streamToString(urlConnection.getInputStream());
	    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
		    .nextValue();
	    token = jsonObj.getString("access_token");
	    handler.sendMessage(handler.obtainMessage(what, 1, 0));
	    Log.i("Token Recebido", token);
	} catch (MalformedURLException malForException) {
	    what = 1;
	    Log.d("ErroMalFormed", "Erro");
	} catch (IOException ioException) {
	    what = 1;
	    Log.d("ErroIO", "Erro");
	} catch (JSONException jsonException) {
	    what = 1;
	    Log.d("ErroJSON", "Erro");
	}

	return null;
    }

    public String streamToString(final InputStream inputStream)
	    throws IOException {
	String stringAux = "";

	if (inputStream != null) {
	    final StringBuilder stbuilder = new StringBuilder();
	    String line;

	    try {
		final BufferedReader reader = new BufferedReader(
			new InputStreamReader(inputStream));

		while ((line = reader.readLine()) != null) {
		    stbuilder.append(line);
		}

		reader.close();
	    } finally {
		inputStream.close();
	    }

	    stringAux = stbuilder.toString();
	}

	return stringAux;
    }
}