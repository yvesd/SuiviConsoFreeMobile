package net.yvesd.scfm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RipActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		supprimerPreferences();

		setContentView(R.layout.main_layout);

		Button espaceAbonne = (Button) findViewById(R.id.espaceAbonne);
		addClickListener(this, espaceAbonne,
				"https://mobile.free.fr/moncompte/");

		Button officialApps = (Button) findViewById(R.id.officialApps);
		addClickListener(this, officialApps, "market://search?q=pub:FreeMobile");

		Button market = (Button) findViewById(R.id.market);
		addClickListener(this, market, "market://details?id=net.yvesd.scfm");

		Button quitter = (Button) findViewById(R.id.quitter);
		quitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * Delete the application preferences (login and password ). They are not
	 * used anymore starting from v19
	 */
	private void supprimerPreferences() {

		try {
			SharedPreferences settings = getSharedPreferences(
					"SuiviConsoFreeMobilePrefs", 0);
			Editor ed = settings.edit();
			ed.clear();
			ed.commit();
		} catch (Exception e) {
			// Ne rien faire ici
		}
	}

	/**
	 * Adds an OnClickListener with an Intent of ACTION_VIEW, and the Uri
	 * specified
	 * 
	 * @param context
	 *            The application context. Used to display toast in case of
	 *            failure
	 * @param targetButton
	 *            the button which will recieve the OnClickListener
	 * @param uri
	 *            The target Uri, which is displayed when the user clicks the
	 *            button
	 */
	private void addClickListener(final Context context, Button targetButton,
			final String uri) {
		targetButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				try {
					startActivity(i);
				} catch (Exception e) {
					Toast.makeText(context, R.string.action_non_dispo,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
