package net.yvesd.scfm;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class OptionActivity extends Activity {

	public static final int RC_VOIX = 1;
	public static final int RC_NUM_SPECS = 2;
	public static final int RC_SMS_MMS = 3;
	public static final int RC_DATA = 4;
	public static final int RC_HF = 5;
	public static final int DIALOG_TELECHERGER_OI_COLORPICKER = 1;

	protected SharedPreferences params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		params = getSharedPreferences(SuiviConsoFreeMobileActivity.PREFS_NAME,
				0);

		GestionnaireThemes gt = new GestionnaireThemes(this);
		gt.chargerThemeChoisi();

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {

		super.onResume();

		params = getSharedPreferences(SuiviConsoFreeMobileActivity.PREFS_NAME,
				0);

		setContentView(R.layout.options);

		// Conso voix
		mettreEnPlaceOption(R.id.choixcouleur_icone_voix,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_VOIX,
				R.drawable.ic_voix, GestionIcones.COULEUR_DEFAUT_VOIX, RC_VOIX,
				R.id.bouton_couleur_icone_voix,
				R.id.bouton_retablir_couleur_icone_voix);

		// Conso numspec
		mettreEnPlaceOption(
				R.id.choixcouleur_icone_numspec,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_NUM_SPECIAUX,
				R.drawable.ic_voix_spec,
				GestionIcones.COULEUR_DEFAUT_NUM_SPECIAUX, RC_NUM_SPECS,
				R.id.bouton_couleur_icone_numspec,
				R.id.bouton_retablir_couleur_icone_numspec);

		// Conso sms mms
		mettreEnPlaceOption(R.id.choixcouleur_icone_sms_mms,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_SMS_MMS,
				R.drawable.ic_messages, GestionIcones.COULEUR_DEFAUT_MESSAGES,
				RC_SMS_MMS, R.id.bouton_couleur_icone_sms_mms,
				R.id.bouton_retablir_couleur_icone_sms_mms);

		// Conso data
		mettreEnPlaceOption(R.id.choixcouleur_icone_data,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_DATA,
				R.drawable.ic_data, GestionIcones.COULEUR_DEFAUT_DATA, RC_DATA,
				R.id.bouton_couleur_icone_data,
				R.id.bouton_retablir_couleur_icone_data);

		// Conso HF
		mettreEnPlaceOption(
				R.id.choixcouleur_icone_hf,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_HORSFORFAIT,
				R.drawable.ic_euro, GestionIcones.COULEUR_DEFAUT_HF, RC_HF,
				R.id.bouton_couleur_icone_hf,
				R.id.bouton_retablir_couleur_icone_hf);

		// Retour
		Button retour = (Button) findViewById(R.id.bouton_retour_choix_couleur);
		retour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void mettreEnPlaceOption(int idIconePrevisualisation,
			String clePreferenceCouleur, int idRessourceIcone,
			int couleurDefaut, int requestCode, int idBoutonChoisirCouleur,
			int idBoutonRetablirCouleur) {

		afficherIconeColoree(idIconePrevisualisation, clePreferenceCouleur,
				idRessourceIcone, couleurDefaut);

		ajouterListenerBouton(idBoutonChoisirCouleur, clePreferenceCouleur,
				couleurDefaut, requestCode);

		ajouterListerBoutonRetablir(idBoutonRetablirCouleur,
				idIconePrevisualisation, clePreferenceCouleur, couleurDefaut,
				idRessourceIcone);
	}

	protected void ajouterListerBoutonRetablir(final int idBouton,
			final int idIconePrevisualisation, final String nomClePreference,
			final int couleurDefaut, final int idRessourceIcone) {

		Button boutonCouleur = (Button) findViewById(idBouton);
		boutonCouleur.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				enregistrerPreference(nomClePreference, couleurDefaut);
				afficherIconeColoree(idIconePrevisualisation, nomClePreference,
						idRessourceIcone, couleurDefaut);
			}
		});

	}

	protected void afficherIconeColoree(int idIconePrevisualisation,
			String clePreferenceCouleur, int idRessourceIcone, int couleurDefaut) {

		ImageView icone = (ImageView) findViewById(idIconePrevisualisation);

		int couleur;
		Resources r = getResources();
		Drawable drawbl = r.getDrawable(idRessourceIcone);

		if (clePreferenceCouleur != null) {
			couleur = params.getInt(clePreferenceCouleur, couleurDefaut);
		} else {
			couleur = couleurDefaut;
		}
		ColorFilter cf = new LightingColorFilter(couleur, Color.BLACK);
		drawbl.setColorFilter(cf);

		icone.setImageDrawable(drawbl);
	}

	protected void ajouterListenerBouton(int idBouton,
			final String clePreferenceCouleur, final int couleurDefaut,
			final int requestCode) {

		Button boutonCouleur = (Button) findViewById(idBouton);
		boutonCouleur.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				demanderCouleur(clePreferenceCouleur, couleurDefaut,
						requestCode);

			}
		});
	}

	protected void demanderCouleur(String clePreferenceCouleur,
			int couleurDefaut, int requestCode) {

		int couleurPrecedente = params.getInt(clePreferenceCouleur,
				couleurDefaut);

		Intent intent = new Intent("org.openintents.action.PICK_COLOR");
		intent.putExtra("org.openintents.extra.COLOR", couleurPrecedente);

		PackageManager pm = getPackageManager();

		List<ResolveInfo> listActivites = pm.queryIntentActivities(intent, 0);

		if (listActivites.isEmpty()) {

			showDialog(DIALOG_TELECHERGER_OI_COLORPICKER);
			return;
		}

		startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		int nouvelleCouleur = data.getExtras().getInt(
				"org.openintents.extra.COLOR", Color.GRAY);

		switch (requestCode) {
		case RC_VOIX:
			enregistrerPreference(
					SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_VOIX,
					nouvelleCouleur);
			break;

		case RC_NUM_SPECS:
			enregistrerPreference(
					SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_NUM_SPECIAUX,
					nouvelleCouleur);
			break;

		case RC_SMS_MMS:
			enregistrerPreference(
					SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_SMS_MMS,
					nouvelleCouleur);
			break;

		case RC_DATA:
			enregistrerPreference(
					SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_DATA,
					nouvelleCouleur);
			break;

		case RC_HF:
			enregistrerPreference(
					SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_HORSFORFAIT,
					nouvelleCouleur);
			break;

		default:
			break;
		}
	}

	protected void enregistrerPreference(String cle, int valeur) {

		Editor e = params.edit();
		e.putInt(cle, valeur);
		e.commit();
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_TELECHERGER_OI_COLORPICKER:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					R.string.label_telecharger_openintent_colorpicker)
					.setCancelable(false)
					.setPositiveButton(
							R.string.button_telecharger_oi_colorpicker,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									Intent goToMarket = null;
									goToMarket = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("market://details?id=org.openintents.colorpicker"));
									startActivity(goToMarket);
								}
							})
					.setNegativeButton(
							R.string.button_ne_pas_telecharger_oi_colorpicker,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			return alert;

		default:
			return super.onCreateDialog(id);
		}

	}
}
