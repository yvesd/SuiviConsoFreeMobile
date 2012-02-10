package net.yvesd.scfm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditAccountActivity extends Activity {

	private EditText loginAbo;
	private EditText pwdAbo;
	private EditText pseudoAbo;
	private Button boutonValider;
	private Button boutonSupprimer;
	private SharedPreferences params;
	private Button boutonRetour;

	public static final String LOGIN_ABO_CLE = "net.yvesd.scfm.LOGIN_ABO_KEY";

	public static final String PWD_ABO_CLE = "net.yvesd.scfm.PWD_ABO_KEY";

	public static final String PSEUDO_ABO_CLE = "net.yvesd.scfm.PSEUDO_ABO_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editaccount);

		Bundle bundle = this.getIntent().getExtras();
		String loginAbo = bundle.getString(LOGIN_ABO_CLE);

		params = getSharedPreferences(SuiviConsoFreeMobileActivity.PREFS_NAME,
				0);

		this.loginAbo = (EditText) findViewById(R.id.loginAbo);
		this.pwdAbo = (EditText) findViewById(R.id.pwdAbo);
		this.pseudoAbo = (EditText) findViewById(R.id.pseudoAbo);
		this.boutonValider = (Button) findViewById(R.id.save);
		this.boutonSupprimer = (Button) findViewById(R.id.delete);
		this.boutonRetour = (Button) findViewById(R.id.retoursanssauvegarder);

		this.loginAbo.setText(loginAbo);
		this.pwdAbo.setText(params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PWD_ABO + loginAbo,
				""));
		this.pseudoAbo.setText(params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PSEUDO_ABO
						+ loginAbo, ""));

		this.boutonValider.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enregistrer();
			}
		});

		this.boutonSupprimer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				supprimer();
			}
		});

		this.boutonRetour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				retourSansSauvegarder();
			}
		});
	}

	/**
	 * Enregistre les modifications apportées à ce compte
	 */
	protected void enregistrer() {

		SharedPreferences.Editor ed = params.edit();
		String login = loginAbo.getText().toString();

		ed.putString(SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PWD_ABO
				+ login, pwdAbo.getText().toString());
		ed.putString(SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PSEUDO_ABO
				+ login, pseudoAbo.getText().toString());

		ajouterCompteAListe(ed, login);

		ed.commit();

		finish();
	}

	/**
	 * Supprime le compte
	 */
	protected void supprimer() {

		SharedPreferences.Editor ed = params.edit();
		String login = loginAbo.getText().toString();

		if (params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEY_DERNIER_COMPTE, "")
				.equals(login)) {

			// On supprime le compte actuellement sélectionné, il faut donc
			// perdre la trace du dernier compte consulté
			ed.remove(SuiviConsoFreeMobileActivity.PREF_KEY_DERNIER_COMPTE);
		}

		// Suppression du mot de passe
		ed.remove(SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PWD_ABO + login);

		// Suppression du pseudo
		ed.remove(SuiviConsoFreeMobileActivity.PREF_KEYPREFIX_PSEUDO_ABO
				+ login);

		supprimerCompteDeListe(ed, login);

		ed.commit();

		finish();
	}

	protected void retourSansSauvegarder() {
		finish();
	}

	/**
	 * Ajoute loginPropose a la liste des comptes connus, mais uniquement s'il
	 * est nouveau
	 * 
	 * @param ed
	 *            editeur de paramètres
	 * @param loginPropose
	 *            Login qui est peut-être nouveau
	 */
	protected void ajouterCompteAListe(Editor ed, String loginPropose) {

		String listeComptes = params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEY_LISTE_COMPTES, "");

		if ("".equals(listeComptes)) {
			listeComptes = loginPropose;
		} else {

			String[] tableaListeComptes = listeComptes.split(",");
			boolean chercheEncore = true;

			for (int i = 0; i < tableaListeComptes.length && chercheEncore; i++) {
				String compte = tableaListeComptes[i];
				if (compte.equals(loginPropose)) {
					// C'est un doublon
					chercheEncore = false;
				}
			}

			if (chercheEncore) {
				// C'est un nouveau
				listeComptes += "," + loginPropose;
			}
		}

		// Sauvegarde finale de la liste des comptes
		ed.putString(SuiviConsoFreeMobileActivity.PREF_KEY_LISTE_COMPTES,
				listeComptes);
	}

	/**
	 * Supprime le compte de la liste des comptes connus
	 * 
	 * @param ed
	 *            editeur de préférences
	 * @param loginASupprimer
	 *            Compte à retirer de la liste des comptes connus
	 */
	protected void supprimerCompteDeListe(Editor ed, String loginASupprimer) {
		String listeComptes = params.getString(
				SuiviConsoFreeMobileActivity.PREF_KEY_LISTE_COMPTES, "");
		String nouvelleListeComptes = "";

		if ("".equals(listeComptes)) {
			// liste des comptes déjà vide => on ne peut pas supprimer le login.
			// On la laisse en l'état, c'est très bien comme ça
			nouvelleListeComptes = listeComptes;

		} else {

			String[] tableaListeComptes = listeComptes.split(",");
			boolean chercheEncore = true;

			for (int i = 0; i < tableaListeComptes.length && chercheEncore; i++) {
				String compte = tableaListeComptes[i];
				if (compte.equals(loginASupprimer)) {
					// Le compte à supprimer a été trouvé
					chercheEncore = false;

					// On va le supprimer du tableau des comptes
					tableaListeComptes[i] = "";
				}
			}

			// Reconstruction de la liste des comptes
			if (tableaListeComptes.length > 0
					&& !("".equals(tableaListeComptes[0])))
				nouvelleListeComptes += tableaListeComptes[0];

			for (int i = 1; i < tableaListeComptes.length; i++) {

				if (!("".equals(tableaListeComptes[i])))
					nouvelleListeComptes += "," + tableaListeComptes[i];
			}
		}

		// Pfiou
		ed.putString(SuiviConsoFreeMobileActivity.PREF_KEY_LISTE_COMPTES,
				nouvelleListeComptes);
	}
}
