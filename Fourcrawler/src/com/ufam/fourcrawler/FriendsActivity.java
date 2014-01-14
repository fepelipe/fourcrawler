package com.ufam.fourcrawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class FriendsActivity extends Activity {
	private String token;
	private static String usersString = "https://api.foursquare.com/v2/users/self/friends";
	private ListView listView;
	private HashMap<User, ArrayList<User>> graph;

	private int friendNumber;
	private int friendCounterGraph;

	private String amigo1Spinner;
	private String amigo2Spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		// Constru�‹o da URL para friends do usu‡rio "self"
		// A URL passada a FriendRequest Ž a utilizada para fazer a request
		SharedPreferences strings = getSharedPreferences("strings",
				MODE_PRIVATE);
		token = strings.getString("token", "null");
		usersString = usersString + "?oauth_token=" + token + "&limit=15";
		Log.i("Friends URL", usersString);

		FriendRequest friendRequest = new FriendRequest(this, null,
				usersString, 0);
		friendRequest.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}

	@SuppressLint("UseSparseArrays")
	public void initializeGraph(ArrayList<User> arrayUsers) {

		User self = new User();
		self.id = 30004723;
		self.firstName = "Richard";
		self.lastName = "Lopes";

		graph = new HashMap<User, ArrayList<User>>();
		graph.put(self, new ArrayList<User>());

		friendNumber = arrayUsers.size();

		TextView counterFriends = (TextView) findViewById(R.id.counterFriends);
		counterFriends.setText("Amigos: " + friendNumber);

		// Primeiro Nível: Meus amigos
		for (User user : arrayUsers) {
			graph.get(self).add(user);
			getFriends(user);
			// after: fillGraph is called

		}
	}

	public void viewGraph(View view) {
		Log.i("final", graph.toString());

	}

	// Usa esse mŽtodo para conseguir os amigos dos amigos.
	public void getFriends(User userObj) {
		String friendsString = String
				.format("https://api.foursquare.com/v2/users/%d/friends?oauth_token=%s&limit=15",
						userObj.id, token);

		FriendRequest friendRequest = new FriendRequest(this, userObj,
				friendsString, 1);
		friendRequest.execute();
	}

	public User getByName(String name)
	{
		for(User u : graph.keySet())
		{
			if(u.firstName == name)
				return u; 
		}
		
		return null;
	}
	
	public Boolean isContained(Integer id, Collection<User> l) {
		for (User u : l) {
			if (u.id == id)
				return true;
		}

		return false;
	}

	public User getById(Integer id, Set<User> l) {
		for (User key : l) {
			if (key.id == id) {
				return key;
			}
		}
		return null;
	}

	public void fillGraph(User user, ArrayList<User> arrayUsers) {
		// Segundo Nível: Amigos de amigos

		if (!graph.containsKey(user))
			graph.put(user, new ArrayList<User>());

		for (User u : arrayUsers) {
			graph.get(user).add(u);

			if (!isContained(u.id, graph.keySet()))
				graph.put(u, new ArrayList<User>());
		}

		TextView totalPeople = (TextView) findViewById(R.id.totalPeople);
		totalPeople.setText("Total: " + graph.size());
	}

	// Esse mŽtodo Ž chamado por FriendRequest para criar a lista de amigos na
	// tela
	public void counterUpdate() {

		TextView counterGraph = (TextView) findViewById(R.id.counterGraph);
		counterGraph.setText("Grafos: " + friendCounterGraph++);

		if (friendCounterGraph == friendNumber) {
			ArrayList<String> arrayUsername = new ArrayList<String>();

			for (User u : graph.keySet()) {
				arrayUsername.add(u.firstName);
			}

			// arrayUsername DEVE TER TODOS OS NOMES!!!

			ArrayAdapter<String> adapterFriends = new ArrayAdapter<String>(
					this, android.R.layout.simple_list_item_1, arrayUsername);

			final Spinner spinnerAmigo1 = (Spinner) findViewById(R.id.spinner1);
			final Spinner spinnerAmigo2 = (Spinner) findViewById(R.id.spinner2);

			spinnerAmigo1.setAdapter(adapterFriends);
			spinnerAmigo2.setAdapter(adapterFriends);

			OnItemSelectedListener spinnerListener1 = new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amigo1Spinner = (String) parent.getItemAtPosition(position);
					
					getFriendPath(amigo1Spinner, amigo2Spinner);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			};

			OnItemSelectedListener spinnerListener2 = new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amigo2Spinner = (String) parent.getItemAtPosition(position);
					
					getFriendPath(amigo1Spinner, amigo2Spinner);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			};
			
			spinnerAmigo1.setOnItemSelectedListener(spinnerListener1);
			spinnerAmigo2.setOnItemSelectedListener(spinnerListener2);

		}

	}

	
	public void getFriendPath(String friend1, String friend2) {
		if (friend1 != null && friend2 != null)
		{
			User friend1User = getByName(friend1);
			User friend2User = getByName(friend2);
			
			ArrayList<User> friend1List = graph.get(friend1User);
			
			TextView textViewCaminho = (TextView) findViewById(R.id.caminho);
			
			if(friend1List.contains(friend2User))
			{
				//grau 1
				textViewCaminho.setText("Caminho: 1");
			}
			else
			{
				for(User friend : friend1List)
				{
					if(graph.get(friend).contains(friend2User))
					{
						//grau 2
						textViewCaminho.setText("Caminho: 2 => " + friend.firstName);
						return;
					}
				}
				
				textViewCaminho.setText("Não há caminho!");
				
			}
			
			Log.i("Amigos", friend1 + " " + friend2);		
		}
	}
}