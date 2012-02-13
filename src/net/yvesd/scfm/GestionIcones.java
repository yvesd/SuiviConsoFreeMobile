package net.yvesd.scfm;

import java.util.HashMap;
import java.util.Map;

public class GestionIcones {

	private Map<String, Integer> CORRESP = new HashMap<String, Integer>();

	public GestionIcones() {
		CORRESP.put("voix", R.drawable.compteur_voix);
		CORRESP.put("Numéros speciaux *", R.drawable.compteur_voix);
		CORRESP.put("renvoi d'appel", R.drawable.compteur_voix);
		CORRESP.put("sms", R.drawable.compteur_messages);
		CORRESP.put("mms", R.drawable.compteur_messages);
		CORRESP.put("data", R.drawable.compteur_data);
		CORRESP.put("hors forfait voix", R.drawable.compteur_horsforfait);
		CORRESP.put("hors forfait sms", R.drawable.compteur_horsforfait);
		CORRESP.put("hors forfait mms", R.drawable.compteur_horsforfait);
		CORRESP.put("hors forfait data", R.drawable.compteur_horsforfait);
	}

	public Integer trouveIconePour(String texte) {
		for (Map.Entry<String, Integer> e : CORRESP.entrySet()) {
			if (texte.startsWith(e.getKey()))
				return e.getValue();
		}
		return R.drawable.comptur_inconnu;
	}
}
