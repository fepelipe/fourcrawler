package com.ufam.fourcrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendsActivity extends Activity {
	private String token;
	private static String usersString = "https://api.foursquare.com/v2/users/self/friends";
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		// Construção da URL para friends do usuário "self"
		// A URL passada a FriendRequest é a utilizada para fazer a request
		SharedPreferences strings = getSharedPreferences("strings",
				MODE_PRIVATE);
		token = strings.getString("token", "null");
		usersString = usersString + "?oauth_token=" + token;
		Log.i("Friends URL", usersString);

		FriendRequest friendRequest = new FriendRequest(this, usersString);
		friendRequest.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}

	// Usa esse método para conseguir os amigos dos amigos.
	public void getFriends(int id) {
		String friendsString = String
				.format("https://api.foursquare.com/v2/users/%d/friends?oauth_token=%s",
						id, token);
		FriendRequest friendRequest = new FriendRequest(this, friendsString);
		friendRequest.execute();
	}

	// Esse método é chamado por FriendRequest para criar a lista de amigos na
	// tela
	public void setAdapter(ArrayList<String> arrayUsername) {
		ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayUsername);
		listView = (ListView) findViewById(R.id.list_friends);
		listView.setAdapter(adapterFriends);
	}
}
