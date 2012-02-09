package net.yvesd.scfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class SuiviConsoFreeMobileActivity extends ListActivity {

	public static final String PREFS_NAME = "SuiviConsoFreeMobilePrefs";
	public static final String PREF_KEY_LOGIN_ABO = "login_abo";
	public static final String PREF_KEY_PWD_ABO = "pwd_abo";

	public static final int REQUEST_ENTER_ID = 42;

	String loginAbo;
	String pwdAbo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Toast.makeText(
				getApplicationContext(),
				"Suivi conso Free Mobile, version 1.\nConnexion au site web Free...",
				Toast.LENGTH_LONG).show();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		loginAbo = settings.getString(PREF_KEY_LOGIN_ABO, "66666666");
		pwdAbo = settings.getString(PREF_KEY_PWD_ABO, "");

		// Instantiate the custom HttpClient
		DefaultHttpClient client = new MyHttpClient(getApplicationContext());

		BasicHttpContext mHttpContext = new BasicHttpContext();
		CookieStore mCookieStore = new BasicCookieStore();
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

		HttpPost post = new HttpPost(
				"https://mobile.free.fr/moncompte/index.php?page=suiviconso");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login_abo", loginAbo));
		nameValuePairs.add(new BasicNameValuePair("pwd_abo", pwdAbo));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			Toast.makeText(getApplicationContext(), "ERREUR 0100",
					Toast.LENGTH_LONG).show();
			return;
		}

		HttpResponse postResponse;
		try {
			postResponse = client.execute(post, mHttpContext);
			HttpEntity responseEntity = postResponse.getEntity();
			InputStream is = responseEntity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String l;
			StringBuffer sb1 = new StringBuffer();
			while ((l = in.readLine()) != null) {
				sb1.append(l);
				sb1.append("\n");
			}

			String erreur = ExtracteurErreur.litErreur(sb1.toString());

			if (erreur == null) {
				Toast.makeText(
						getApplicationContext(),
						"Connexion au site de Free Mobile OK. Lecture du suivi consommation...",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Site de Free Mobile Trouvé, mais impossible de s'enregistrer : "
								+ erreur, Toast.LENGTH_LONG).show();

				Toast.makeText(getApplicationContext(),
						"Appuyez sur MENU pour renseigner les identifiants",
						Toast.LENGTH_LONG).show();

				return;
			}

			HttpGet httpGet = new HttpGet(
					"https://mobile.free.fr/moncompte/index.php?page=suiviconso");
			HttpResponse getResponse = client.execute(httpGet, mHttpContext);

			HttpEntity responseEntity2 = getResponse.getEntity();
			InputStream is2 = responseEntity2.getContent();
			BufferedReader in2 = new BufferedReader(new InputStreamReader(is2));

			StringBuffer sb = new StringBuffer();
			String l2;
			while ((l2 = in2.readLine()) != null) {
				sb.append(l2);
			}

			String[] interpret = DataInterpreter.interpret(sb.toString());

			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
					interpret));

		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(), "ERREUR 0200",
					Toast.LENGTH_LONG).show();
			return;
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"ERREUR 0300. Vérifiez votre connexion à Internet",
					Toast.LENGTH_LONG).show();
			return;
		}

		// ListView lv = getListView();
		// lv.setTextFilterEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.enterIds:
			Bundle bundle = new Bundle();

			bundle.putString(EnterIdsActivity.LOGIN_ABO_KEY, loginAbo);
			bundle.putString(EnterIdsActivity.PWD_ABO_KEY, pwdAbo);

			Intent intent = new Intent(this, EnterIdsActivity.class);
			intent.putExtras(bundle);

			startActivityForResult(intent, REQUEST_ENTER_ID);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENTER_ID) {
			if (resultCode == RESULT_OK) {
				String newLoginAbo = data
						.getStringExtra(EnterIdsActivity.LOGIN_ABO_KEY);
				String newPwdAbo = data
						.getStringExtra(EnterIdsActivity.PWD_ABO_KEY);

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

				SharedPreferences.Editor ed = settings.edit();
				ed.putString(PREF_KEY_LOGIN_ABO, newLoginAbo);
				ed.putString(PREF_KEY_PWD_ABO, newPwdAbo);
				ed.commit();

				Toast.makeText(getApplicationContext(),
						"OK. Relancez l'application", Toast.LENGTH_LONG).show();
			}
		}
	}

}