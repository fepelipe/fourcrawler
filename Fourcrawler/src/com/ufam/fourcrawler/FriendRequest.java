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
	private ArrayList<User> arrayUsers;
	
	private User userObj;
	
	private Integer switchCaseFunction; //0- initialize | 1- fillGraph | otherwise- setAdapter

	public FriendRequest(FriendsActivity activity, User userObj, String friendsString, Integer switchCaseFunction) {
		this.activity = activity;
		this.friendsString = friendsString;
		this.switchCaseFunction = switchCaseFunction;
		this.userObj = userObj;
	}

	@Override
	protected Void doInBackground(Void... params) {

		arrayUsers = new ArrayList<User>();
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

			// Log.d("Friends Response", response);

			Util util = new Util();
			try {
				// Array de User
				arrayUsers = util.getFriends(response);
				arrayUsername = new ArrayList<String>();
				arrayId = new ArrayList<Integer>();

				for (User user : arrayUsers) {
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
		// Chama a fun�‹o que seta na tela a lista de amigos.
		super.onPostExecute(result);

		switch (switchCaseFunction) {
			case 0:
				activity.initializeGraph(arrayUsers);
				break;
			case 1:
				activity.fillGraph(userObj, arrayUsers);
				break;
			default:
				break;
		}
		
		activity.counterUpdate();
	
	}
}