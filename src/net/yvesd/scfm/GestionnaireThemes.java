package net.yvesd.scfm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GestionnaireThemes {

	Activity activity;
	SharedPreferences settings;

	public GestionnaireThemes(Activity activity) {
		this.activity = activity;
		settings = activity.getSharedPreferences(
				SuiviConsoFreeMobileActivity.PREFS_NAME, 0);
	}

	/**
	 * Bascule du theme noir au thème blanc ou vice versa
	 */
	protected void basculerTheme() {

		int theme = settings.getInt(
				SuiviConsoFreeMobileActivity.PREF_KEY_THEME,
				android.R.style.Theme_Light);

		int newTheme = android.R.style.Theme_Light;

		if (theme == android.R.style.Theme_Light)
			newTheme = android.R.style.Theme_Black;
		else
			newTheme = android.R.style.Theme_Light;

		Editor editor = settings.edit();
		editor.putInt(SuiviConsoFreeMobileActivity.PREF_KEY_THEME, newTheme);
		editor.commit();
	}

	public void chargerThemeChoisi() {
		int theme = settings.getInt(
				SuiviConsoFreeMobileActivity.PREF_KEY_THEME,
				android.R.style.Theme_Light);

		activity.setTheme(theme);
	}

}
