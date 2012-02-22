package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class AfficherSuiviconsoDetailleActivity extends Activity implements
		CanWaitForStream {

	ProgressDialog progressDialog;
	List<String> progressMessages = new ArrayList<String>(); // TODO refactor
	SharedPreferences settings;
	protected static final String CLE_BUNDLE_SAUVEGARDE_ETAT_DETAIL = "net.yvesd.scfm.detailConso";
	public static final String CLE_BUNDLE_LOGIN = "net.yvesd.scfm.detailConso.login";
	public static final String CLE_BUNDLE_PWD = "net.yvesd.scfm.detailConso.pwd";
	protected static final String NO_DATA = "Pas de conso détaillée à afficher. Erreur ou pas de conso détaillée disponible";

	/**
	 * Donnés de suivi conso à afficher
	 */
	String donnees = NO_DATA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		chargerPreferences();

		GestionnaireThemes gt = new GestionnaireThemes(this);
		gt.chargerThemeChoisi();

		super.onCreate(savedInstanceState);

		setContentView(R.layout.conso_detaillee);

		if (savedInstanceState == null) {
			lancerRequete();

		} else {

			String donneesSauvegardees = savedInstanceState
					.getString(CLE_BUNDLE_SAUVEGARDE_ETAT_DETAIL);

			if (donneesSauvegardees != null
					&& donneesSauvegardees instanceof String) {

				donnees = donneesSauvegardees;
				displayData(donnees);

			} else {
				// TODO Encore utile ici ?!
				lancerRequete();
			}

		}

		Button retour = (Button) findViewById(R.id.bouton_retour_consodet);
		retour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayData(donnees); // TODO refactor. Doublon avec handleResult
	}

	protected void chargerPreferences() {
		settings = getSharedPreferences(
				SuiviConsoFreeMobileActivity.PREFS_NAME, 0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(CLE_BUNDLE_SAUVEGARDE_ETAT_DETAIL, donnees);
	}

	protected void lancerRequete() {
		displayData("");
		progressMessages.clear();

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle(getString(R.string.app_name));
		progressDialog.setMessage(""); // Contournement d'un problème : pas de
										// messages sinon
		progressDialog.show();

		// Arrêt si aucun compte n'est sélectionné
		if ("".equals(getIntent().getExtras().getString(CLE_BUNDLE_LOGIN))) {
			addToProgress(getString(R.string.log_aucuncompte_consodet));
			setProgressStatus(100);

			return;
		}

		addToProgress("Identification auprès de Free Mobile"); // TODO
																// externalize
																// string
		setProgressStatus(12);

		TelechargeurSuiviConsoDetaille dataRecuperator = new TelechargeurSuiviConsoDetaille(
				this, new MyHttpClient(this));
		// DataRecuperatorMock dataRecuperator = new DataRecuperatorMock(this);
		DataRecuperatorParams params = new DataRecuperatorParams();
		params.setLoginAbo(getIntent().getExtras().getString(CLE_BUNDLE_LOGIN));
		params.setPwdAbo(getIntent().getExtras().getString(CLE_BUNDLE_PWD));
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

	@Override
	public void addToProgress(int res, Object... args) {
		addToProgress(getString(res, args));
	}

	@Override
	public void setProgressStatus(int p) {
		progressDialog.setProgress(p);
	}

	@Override
	public void handleResult(List<String> results) {

		String rawHtmlData = results.get(0);

		if (rawHtmlData == null)
			return;

		donnees = rawHtmlData;

		displayData(donnees);
		progressDialog.dismiss();
	}

	private void displayData(String donnees) {

		WebView webview = (WebView) findViewById(R.id.consoDetailleeView);

		WebSettings ws = webview.getSettings();
		ws.setBuiltInZoomControls(true);

		// http://stackoverflow.com/questions/3961589/android-webview-and-loaddata
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

		webview.loadData(header + donnees, "text/html", "UTF-8");
	}

}
