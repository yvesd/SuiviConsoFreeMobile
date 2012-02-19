package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activité principale du programme
 * 
 * @author Yves Dessertine <yves.dessertine2@gmail.com>
 */
public class SuiviConsoFreeMobileActivity extends ListActivity {

	public static final String PREFS_NAME = "SuiviConsoFreeMobilePrefs";
	public static final String PREF_KEY_LISTE_COMPTES = "liste_comptes";
	public static final String PREF_KEY_DERNIER_COMPTE = "dernier_compte";
	public static final String PREF_KEY_THEME = "theme";
	public static final String PREF_KEY_COULEUR_ICONE_VOIX = "couleur_icone.conso_voix";
	public static final String PREF_KEY_COULEUR_ICONE_NUM_SPECIAUX = "couleur_icone.conso_num_speciaux";
	public static final String PREF_KEY_COULEUR_ICONE_SMS_MMS = "couleur_icone.conso_sms_mms";
	public static final String PREF_KEY_COULEUR_ICONE_DATA = "couleur_icone.conso_data";
	public static final String PREF_KEY_COULEUR_ICONE_HORSFORFAIT = "couleur_icone.hors_forfait";
	public static final String PREF_KEYPREFIX_PWD_ABO = "pwd_abo.";
	public static final String PREF_KEYPREFIX_PSEUDO_ABO = "pseudo_abo.";
	protected static final String CLE_BUNDLE_SAUVEGARDE_ETAT = "net.yvesd.scfm.donneesConso";

	String loginAbo = "";
	String pwdAbo = "";
	ProgressDialog progressDialog;
	List<String> progressMessages = new ArrayList<String>(); // TODO refactor
	SharedPreferences settings;
	Map<MenuItem, String> menuItemMap = new HashMap<MenuItem, String>();

	/**
	 * Donnés de suivi conso à afficher
	 */
	DonnesCompteur[] donneesConso = new DonnesCompteur[] {};

	@Override
	public void onCreate(Bundle configurationSauvegardee) {

		chargerPreferences();

		GestionnaireThemes gt = new GestionnaireThemes(this);
		gt.chargerThemeChoisi();

		super.onCreate(configurationSauvegardee);

		chargerLoginPwd();

		if (configurationSauvegardee == null) {

			lancerRequete();

		} else {

			Parcelable[] donnesConsoParcel = configurationSauvegardee
					.getParcelableArray(CLE_BUNDLE_SAUVEGARDE_ETAT);

			if (donnesConsoParcel != null
					&& donnesConsoParcel instanceof DonnesCompteur[]) {

				donneesConso = (DonnesCompteur[]) donnesConsoParcel;
				displayData(donneesConso);

			} else {
				// TODO comprendre pourquoi on arrive dans ce cas
				// (ClassCastException : ne peut caster un Parcelable[] en
				// DonneesConso[]). Est-ce parce que la Activity est tuée ?
				//
				// Contournement : on relance la requête, comme lors du premier
				// démarrage

				lancerRequete();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		chargerPreferences();

		// Ceci afin de gérer correctement la suppression de compte. Si
		// l'utilisateur supprime le compte actuellement sélectionné, il faut
		// que ce changement soit répercuté dans cette activité. Ajouté en v8
		chargerLoginPwd();

		displayData(donneesConso); // TODO refactor. Doublon avec handleResult
	}

	/**
	 * Charge le login/password depuis les préférences
	 */
	private void chargerLoginPwd() {
		// Restaurer préférences ou lancer première configuration
		if (settings.contains(PREF_KEY_DERNIER_COMPTE)) {

			loginAbo = settings.getString(PREF_KEY_DERNIER_COMPTE, "");
			pwdAbo = settings.getString(PREF_KEYPREFIX_PWD_ABO + loginAbo, "");

		} else {
			// Pour éviter que, lorsque l'on supprime le compte actuellement
			// affiché, le soft croie encore qu'il a un compte (alors que plus
			// aucun compte n'est sélectionné)
			loginAbo = "";
			pwdAbo = "";

			Toast.makeText(this, R.string.infobulle_appuyezmenu,
					Toast.LENGTH_LONG).show();
		}
	}

	protected void chargerPreferences() {
		settings = getSharedPreferences(PREFS_NAME, 0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelableArray(CLE_BUNDLE_SAUVEGARDE_ETAT, donneesConso);
	}

	protected void lancerRequete() {
		displayData(new DonnesCompteur[] {});
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

		addToProgress("Identification auprès de Free Mobile"); // TODO
																// externalize
																// string
		setProgressStatus(12);

		DataRecuperator dataRecuperator = new DataRecuperator(this);
		// DataRecuperatorMock dataRecuperator = new DataRecuperatorMock(this);
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

			donneesConso = DataInterpreter.interpret(rawHtmlData);

			displayData(donneesConso);
			progressDialog.dismiss();

		} catch (Exception e) {
			Log.w("", "Suivi conso illisible", e);
			addToProgress(getString(R.string.log_suiviconsoillisible));
			setProgressStatus(100);
			return;
		}

	}

	private void displayData(DonnesCompteur[] interpret) {

		setListAdapter(new ArrayAdapter<DonnesCompteur>(this,
				R.layout.list_item, R.id.compteur, interpret) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = super.getView(position, convertView, parent);
				}

				TextView tv = (TextView) convertView;

				DonnesCompteur donnesCompteur = getItem(position);

				if (donnesCompteur.getRessourceId() == null) {

					tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

				} else {

					Drawable drawbl;

					Resources r = getResources();
					drawbl = r.getDrawable(donnesCompteur.getRessourceId());

					// POC colorize TODO REFACTOR
					String nomClePreference = donnesCompteur
							.getNomClePreference();
					int couleur;
					if (nomClePreference != null) {
						couleur = settings.getInt(nomClePreference,
								donnesCompteur.getCouleurDefaut());
					} else {
						couleur = donnesCompteur.getCouleurDefaut();
					}
					ColorFilter cf = new LightingColorFilter(couleur,
							Color.BLACK);
					drawbl.setColorFilter(cf);
					// Fin POC colorize

					tv.setCompoundDrawablesWithIntrinsicBounds(drawbl, null,
							null, null);
				}

				tv.setText(donnesCompteur.getTexte());
				return tv;
			}

		});
	}

	static class ViewVolder {
		Drawable drawable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menuItemMap.clear();
		menu.removeGroup(1);
		String[] comptes = settings.getString(PREF_KEY_LISTE_COMPTES, "")
				.split(",");
		for (String compte : comptes) {
			if (!("".equals(compte))) {

				String pseudo = settings.getString(PREF_KEYPREFIX_PSEUDO_ABO
						+ compte, "");
				String nomAffichage = "";

				if ("".equals(pseudo))
					nomAffichage = compte;
				else
					nomAffichage = pseudo;

				MenuItem mi = menu.add(1, Menu.NONE, Menu.FIRST,
						getString(R.string.menuitem_voircompte, nomAffichage));
				mi.setIcon(android.R.drawable.ic_menu_view);
				menuItemMap.put(mi, compte);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (menuItemMap.containsKey(item)) {
			// C'est une entrée de menu de visualisation d'un compte

			loginAbo = menuItemMap.get(item);
			pwdAbo = settings.getString(PREF_KEYPREFIX_PWD_ABO + loginAbo, "");

			SharedPreferences.Editor ed = settings.edit();
			ed.putString(PREF_KEY_DERNIER_COMPTE, loginAbo);
			ed.commit();

			lancerRequete();

			return true;
		}

		switch (item.getItemId()) {
		case R.id.editAccounts:

			Intent intent = new Intent(this, EditAccountsActivity.class);

			startActivity(intent);

			return true;

		case R.id.basculerTheme:

			GestionnaireThemes gt = new GestionnaireThemes(this);
			gt.basculerTheme();
			rechargerActivite();
			return true;

		case R.id.options:
			Intent intent2 = new Intent(this, OptionActivity.class);
			startActivity(intent2);

		default:

			return false;
		}
	}

	protected void rechargerActivite() {

		// Les lignes commentées sont utilisables a partir de l'API 5

		Intent intent = getIntent();
		// overridePendingTransition(0, 0);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		// overridePendingTransition(0, 0);
		startActivity(intent);

	}
}