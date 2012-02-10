package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * Activité principale du programme
 * 
 * @author Yves Dessertine <yves.dessertine2@gmail.com>
 */
public class SuiviConsoFreeMobileActivity extends ListActivity {

	public static final String PREFS_NAME = "SuiviConsoFreeMobilePrefs";
	public static final String PREF_KEY_LISTE_COMPTES = "liste_comptes";
	public static final String PREF_KEY_DERNIER_COMPTE = "dernier_compte";
	public static final String PREF_KEYPREFIX_PWD_ABO = "pwd_abo.";

	String loginAbo = "";
	String pwdAbo = "";
	ProgressDialog progressDialog;
	List<String> progressMessages = new ArrayList<String>(); // TODO refactor
	SharedPreferences settings;

	@Override
	public void onResume() {
		super.onResume();

		// Restaurer préférences ou lancer première configuration
		settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.contains(PREF_KEY_DERNIER_COMPTE)) {

			loginAbo = settings.getString(PREF_KEY_DERNIER_COMPTE, "");
			pwdAbo = settings.getString(PREF_KEYPREFIX_PWD_ABO + loginAbo, "");

		} else {
			// Pour éviter que, lorsque l'on supprime le compte actuellement
			// affiché, le soft croie encore qu'il a un compte (alors que plus
			// aucun compte n'est sélectionné)
			loginAbo = "";
			pwdAbo = "";
		}

		lancerRequete();
	}

	protected void lancerRequete() {
		displayData(new String[] {});
		progressMessages.clear();

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle(getString(R.string.app_name));
		progressDialog.setMessage(""); // Contournement d'un problème : pas de
										// messages sinon
		progressDialog.show();

		// Arrêt si aucun compte n'est sélectionné
		if ("".equals(loginAbo)) {
			addToProgress(getString(R.string.log_aucuncompte));
			setProgressStatus(100);

			return;
		}

		addToProgress("Identification auprès de Free Mobile");
		setProgressStatus(12);

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
			displayed += "* " + message + "\n";
		}
		progressDialog.setMessage(displayed);
	}

	protected void addToProgress(int res, Object... args) {
		addToProgress(getString(res, args));
	}

	protected void setProgressStatus(int p) {
		progressDialog.setProgress(p);
	}

	public void handleResult(List<String> results) {
		try {
			String rawHtmlData = results.get(0);

			if (rawHtmlData == null)
				return;

			String[] interpret = DataInterpreter.interpret(rawHtmlData);

			displayData(interpret);
			progressDialog.dismiss();

		} catch (Exception e) {
			addToProgress(getString(R.string.log_suiviconsoillisible));
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeGroup(1);
		String[] comptes = settings.getString(PREF_KEY_LISTE_COMPTES, "")
				.split(",");
		for (String compte : comptes) {
			if (!(compte.equals("")))
				menu.add(1, Integer.valueOf(compte), Menu.NONE,
						getString(R.string.menuitem_voircompte, compte));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editAccounts:

			Intent intent = new Intent(this, EditAccountsActivity.class);

			startActivity(intent);

			return true;

		case R.id.raffraichir:

			lancerRequete();

			return true;

		default:

			loginAbo = String.valueOf(item.getItemId());
			pwdAbo = settings.getString(PREF_KEYPREFIX_PWD_ABO + loginAbo, "");

			SharedPreferences.Editor ed = settings.edit();
			ed.putString(PREF_KEY_DERNIER_COMPTE, loginAbo);
			ed.commit();

			lancerRequete();

			return true;
		}
	}

}