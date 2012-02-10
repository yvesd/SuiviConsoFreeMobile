package net.yvesd.scfm;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class EditAccountsActivity extends ListActivity {

	private SharedPreferences params;
	ListView lv;

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.editaccounts);
		lv = getListView();

		params = getSharedPreferences(SuiviConsoFreeMobileActivity.PREFS_NAME,
				0);

		String[] comptes;

		String comptesString = params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEY_LISTE_COMPTES, "");
		if ("".equals(comptesString))
			comptes = new String[] {};
		else
			comptes = comptesString.split(",");

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, comptes));

		Button boutonCreerCompte = (Button) findViewById(R.id.createAccountButton);
		Button boutonRetour = (Button) findViewById(R.id.retour);

		boutonCreerCompte.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				creerOuMajCompte("");
			}
		});

		boutonRetour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				retour();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tv = (TextView) view;
				String login = tv.getText().toString();
				creerOuMajCompte(login);
			}
		});
	}

	private void creerOuMajCompte(String login) {
		Bundle bundle = new Bundle();

		bundle.putString(EditAccountActivity.LOGIN_ABO_CLE, login);

		Intent intent = new Intent(this, EditAccountActivity.class);
		intent.putExtras(bundle);

		startActivity(intent);
	}

	protected void retour() {
		finish();
	}
}
