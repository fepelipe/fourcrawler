package com.ufam.fourcrawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class AccessToken extends AsyncTask<Void, Void, Void> {
	private Context contexto;
	private String code;
	private String token;
	private String tokenUrl;

	public AccessToken(Context contexto, String code, String tokenUrl) {
		this.contexto = contexto;
		this.code = code;
		this.tokenUrl = tokenUrl;
	}

	public String getToken() {
		return token;
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {
			Log.d("Token", "Request started!");
			
			final URL url = new URL(tokenUrl + "&code=" + code);
			final HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			String response = Util.streamToString(urlConnection.getInputStream());
			Log.d("Token Response", response);
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

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		SharedPreferences strings = contexto.getSharedPreferences(
				"strings", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = strings.edit();
		editor.putString("token", token);
		editor.commit();
		Log.i("Token Armazenado", token);
	}
}