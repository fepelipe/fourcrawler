package com.ufam.fourcrawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class FriendRequest extends AsyncTask<Void, Void, Void> {

	private FriendsActivity activity;
	private String friendsString;
	private ArrayList<String> arrayUsername;
	private ArrayList<Integer> arrayId;
	
	public FriendRequest(FriendsActivity activity, String friendsString) {
		this.activity = activity;
		this.friendsString = friendsString;
	}

	@Override
	protected Void doInBackground(Void... params) {

		ArrayList<User> arrayUser;
		URL friendsUrl;

		try {
			friendsUrl = new URL(friendsString);
			final HttpURLConnection urlConnection = (HttpURLConnection) friendsUrl
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();

			String response = Util.streamToString(urlConnection
					.getInputStream());

			Log.d("Friends Response", response);
			Util util = new Util();
			try {
				// Array de User
				arrayUser = util.getFriends(response);
				arrayUsername = new ArrayList<String>();
				for (User user : arrayUser) {
					// Array de IDs
					arrayId.add(user.id);
					
					// Array de nomes
					arrayUsername.add(user.firstName + " " + user.lastName);
					Log.d("Nome inserido", user.firstName);
				}
			} catch (JSONException jsonException) {
				Log.d("ErroJSON", "Erro", jsonException);
			}
		} catch (MalformedURLException malForException) {
			Log.d("ErroMalFormed", "Erro", malForException);
		} catch (IOException ioException) {
			Log.d("ErroIO", "Erro", ioException);
		}
		return null;

	}

	@Override
	protected void onPostExecute(Void result) {
		// Chama a função que seta na tela a lista de amigos.
		super.onPostExecute(result);
		activity.setAdapter(arrayUsername);
	}
}