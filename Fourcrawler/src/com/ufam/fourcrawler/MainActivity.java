package com.ufam.fourcrawler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

public class MainActivity extends Activity {
	private Resources resource;
	private String tokenUrl;
	private String code;
	private String token;
	private static final int REQUEST_CODE_FSQ_CONNECT = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resource = getResources();

		// TOKEN: Verificação inicial
		SharedPreferences strings = getSharedPreferences("strings",
				MODE_PRIVATE);
		token = strings.getString("token", "null");
		Log.i("Token Inicial", token);
		
		// Se houver um Token, ele vai direto para FriendsActivity.
		if (token != "null") {
			Intent intent = new Intent(this, FriendsActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Se não houver token, pode-se fazer a request de um token em AccessToken
	// Esse é o método chamado ao apertar o botão "Iniciar autenticação"
	public void foursquareAuth(View view) {
		Intent intent = FoursquareOAuth.getConnectIntent(this,
				resource.getString(R.string.CLIENT_ID));
		if (!FoursquareOAuth.isPlayStoreIntent(intent)) {
			startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_FSQ_CONNECT:
			AuthCodeResponse codeResponse = FoursquareOAuth
					.getAuthCodeFromResult(resultCode, data);
			code = codeResponse.getCode();
			tokenUrl = "https://foursquare.com/oauth2/access_token"
					+ "?client_id=" + resource.getString(R.string.CLIENT_ID)
					+ "&client_secret="
					+ resource.getString(R.string.CLIENT_SECRET)
					+ "&grant_type=authorization_code";
			AccessToken accessToken = new AccessToken(this, code, tokenUrl);
			accessToken.execute();
			break;
		}
	}

}
