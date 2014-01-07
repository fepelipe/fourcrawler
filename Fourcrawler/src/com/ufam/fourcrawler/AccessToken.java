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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class AccessToken extends AsyncTask<Void, Void, Void> {
	private String code;
	private String token;
	private String tokenUrl;

	public AccessToken(String code, String tokenUrl) {
		this.code = code;
		this.tokenUrl = tokenUrl;
	}

	public String getToken() {
		return token;
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {
			final URL url = new URL(tokenUrl + "&code=" + code);
			final HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			String response = streamToString(urlConnection.getInputStream());
			Log.d("Response", response);
			JSONObject jsonObj = new JSONObject(response);
			token = jsonObj.optString("access_token");
			Log.i("Token Recebido", token);
		} catch (MalformedURLException malForException) {
			Log.d("ErroMalFormed", "Erro", malForException);
		} catch (IOException ioException) {
			Log.d("ErroIO", "Erro", ioException);
		} catch (JSONException jsonException) {
			Log.d("ErroJSON", "Erro", jsonException);
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