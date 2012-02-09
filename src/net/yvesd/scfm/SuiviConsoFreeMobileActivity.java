package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SuiviConsoFreeMobileActivity extends ListActivity {

	public static final String PREFS_NAME = "SuiviConsoFreeMobilePrefs";
	public static final String PREF_KEY_LOGIN_ABO = "login_abo";
	public static final String PREF_KEY_PWD_ABO = "pwd_abo";

	public static final int REQUEST_ENTER_ID = 42;

	String loginAbo;
	String pwdAbo;
	ProgressDialog progressDialog;
	List<String> progressMessages = new ArrayList<String>(); // TODO REMOVE CRAP
																// HAS NOTHING
																// TO DO HERE

	@Override
	public void onResume() {
		super.onResume();

		displayData(new String[] {});
		progressMessages.clear();

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("Suivi conso Free Mobile v2");
		addToProgress("Identification sur le site Free Mobile");
		progressDialog.show();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		loginAbo = settings.getString(PREF_KEY_LOGIN_ABO, "66666666");
		pwdAbo = settings.getString(PREF_KEY_PWD_ABO, "");

		DataRecuperator dataRecuperator = new DataRecuperator(this);
		DataRecuperatorParams params = new DataRecuperatorParams();
		params.setLoginAbo(loginAbo);
		params.setPwdAbo(pwdAbo);
		dataRecuperator.execute(params);
	}

	protected void addToProgress(String... strings) {
		for (String value : strings)
			progressMessages.add(value);

		String displayed = "";
		for (String message : progressMessages) {
			displayed += " * " + message + "\n";
		}
		progressDialog.setMessage(displayed);

	}

	protected void setProgressStatus(int p) {
		progressDialog.setProgress(p);
	}

	public void handleResult(List<String> results) {
		try {
			String rawHtmlData = results.get(0);
			String[] interpret = DataInterpreter.interpret(rawHtmlData);

			displayData(interpret);
			progressDialog.dismiss();

		} catch (Exception e) {
			addToProgress("Suivi conso illisible du site Free. Contactez l'auteur");
			setProgressStatus(100);
			return;
		}

	}

	private void displayData(String[] interpret) {
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				interpret));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
			}
		}
	}
}