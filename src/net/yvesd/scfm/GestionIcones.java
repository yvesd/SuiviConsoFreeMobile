package net.yvesd.scfm;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

public class GestionIcones {

	/**
	 * #348017 Medium Spring Green
	 */
	final public static int COULEUR_DEFAUT_VOIX = Color.rgb(52, 128, 23);

	/**
	 * #2B60DE Royal Blue
	 */
	final public static int COULEUR_DEFAUT_DATA = Color.rgb(43, 96, 222);

	/**
	 * #FF6600 Orange(tm)
	 */
	final public static int COULEUR_DEFAUT_NUM_SPECIAUX = Color
			.rgb(255, 102, 0);

	/**
	 * #5CB3FF Steel Blue1
	 */
	final public static int COULEUR_DEFAUT_MESSAGES = Color.rgb(92, 179, 255);

	/**
	 * #FDD017 Gold1
	 */
	final public static int COULEUR_DEFAUT_HF = Color.rgb(253, 208, 23);

	/**
	 * Gris 50%
	 */
	final public static int COULEUR_DEFAUT_INCONNU = Color.rgb(128, 128, 128);

	/**
	 * Correspondances d'icônes et de couleurs
	 */
	private Map<String, IconeCouleur> CORRESP = new HashMap<String, IconeCouleur>();

	public GestionIcones() {
		CORRESP.put("Conso VOIX", new IconeCouleur(R.drawable.ic_voix,
				COULEUR_DEFAUT_VOIX));
		CORRESP.put("Appels numéros spéciaux", new IconeCouleur(
				R.drawable.ic_voix_spec, COULEUR_DEFAUT_NUM_SPECIAUX));
		CORRESP.put("Conso SMS", new IconeCouleur(R.drawable.ic_messages,
				COULEUR_DEFAUT_MESSAGES));
		CORRESP.put("Conso MMS", new IconeCouleur(R.drawable.ic_messages,
				COULEUR_DEFAUT_MESSAGES));
		CORRESP.put("Conso DATA", new IconeCouleur(R.drawable.ic_data,
				COULEUR_DEFAUT_DATA));
	}

	public IconeCouleur trouveIconePour(String texte) {
		for (Map.Entry<String, IconeCouleur> e : CORRESP.entrySet()) {
			if (texte.equals(e.getKey())) {
				IconeCouleur value = e.getValue();
				IconeCouleur iconeCouleur = new IconeCouleur(value.icone,
						value.couleur);
				return iconeCouleur;
			}
		}
		return new IconeCouleur(R.drawable.ic_inconnu, COULEUR_DEFAUT_INCONNU);
	}

	@Deprecated
	static class IconeCouleur {
		public IconeCouleur(int icone, int couleur) {
			super();
			this.icone = icone;
			this.couleur = couleur;
		}

		public int icone;
		public int couleur;
	}
}
