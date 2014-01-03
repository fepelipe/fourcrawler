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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	resource = getResources();

	SharedPreferences strings = getSharedPreferences("strings",
		MODE_PRIVATE);
	token = strings.getString("token", "null");
	Log.v("Token Inicial", token);
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
	if (!FoursquareOAuth.isPlayStoreIntent(intent)) {
	    startActivityForResult(intent, 9000);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (requestCode) {
	case 9000:
	    AuthCodeResponse codeResponse = FoursquareOAuth
		    .getAuthCodeFromResult(resultCode, data);
	    code = codeResponse.getCode();
	    tokenUrl = "https://foursquare.com/oauth2/access_token"
		    + "&client_id=" + resource.getString(R.string.CLIENT_ID)
		    + "&client_secret="
		    + resource.getString(R.string.CLIENT_SECRET)
		    + "&grant_type=authorization_code";
	    AccessToken accessToken = new AccessToken(code, tokenUrl);
	    accessToken.execute();

	    SharedPreferences strings = getSharedPreferences("strings",
		    MODE_PRIVATE);
	    SharedPreferences.Editor editor = strings.edit();
	    editor.putString("token", accessToken.getToken());
	    editor.commit();
	    Log.i("Token Armazenado", token);
	    break;
	}
    }

}
