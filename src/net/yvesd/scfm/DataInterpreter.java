package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class DataInterpreter {

	protected static final String ENTETE_LIGNE_ETRANGER = "A l'étranger";

	/**
	 * Interprète les données de la page Suivi Conso de Free en informations
	 * affichables à l'écran
	 * 
	 * @param htmlData
	 *            les données de la page Suivi Conso de Free
	 * @return informations affichables à l'écran
	 */
	public static DonnesCompteur[] interpret(String htmlData) {

		List<DonnesCompteur> list = new ArrayList<DonnesCompteur>();
		Document d = Jsoup.parse(htmlData);
		Elements divs = d.select("div#contenuEA");
		Element inner01 = divs.get(0).select("div").get(0);
		Element inner02 = inner01.select("div").first().select("div").first();

		extraireInfosTitulaire(list, inner02);

		ajouterSeparateur(list);

		list.add(new DonnesCompteur(inner02.select("p.date").text()));
		Element table = inner02.select("table").first();
		Elements tableLines = table.select("tr");

		extraireCompteursFrance(list, tableLines);

		int idxLigneRecap = 4;

		// Gestion spécifique, selon qu'il y ait ou non des informations de
		// conso à l'étranger
		if (contientCompteursEtranger(tableLines)) {
			extraireCompteursEtranger(list, tableLines);
			idxLigneRecap = 8;
		} else {
			idxLigneRecap = 4;
		}

		extraireRecapitulatif(list, tableLines, idxLigneRecap);

		// Conversion en tableau
		DonnesCompteur[] tab = new DonnesCompteur[list.size()];
		int i = 0;
		for (DonnesCompteur dc : list) {
			tab[i++] = dc;
		}

		return tab;
	}

	private static boolean contientCompteursEtranger(Elements tableLines) {

		try {

			Element ligneEnteteEtranger = tableLines.get(4);
			String ligneEnteteEtrangerS = ligneEnteteEtranger.text();

			if (ENTETE_LIGNE_ETRANGER.equals(ligneEnteteEtrangerS))
				return true;
			else
				return false;

		} catch (Exception e) {
			Log.w("",
					"Impossible de déterminer s'il y a des consos à l'étranger. Par défaut, on considère que non",
					e);
			return false;
		}
	}

	private static void extraireCompteursEtranger(List<DonnesCompteur> list,
			Elements tableLines) {

		extraireCompteurs(list, tableLines, 4);

	}

	/**
	 * 
	 * @param list
	 * @param tableLines
	 * @param idxLigneRecap
	 *            Index de la première ligne du récapitulatif. 4 dans le cas où
	 *            il n'y a pas de conso à l'étranger et 6 si'il y a de la conso
	 *            à l'étranger
	 */
	private static void extraireRecapitulatif(List<DonnesCompteur> list,
			Elements tableLines, int idxLigneRecap) {

		list.add(new DonnesCompteur(tableLines.get(idxLigneRecap).text()));
		list.add(new DonnesCompteur(tableLines.get(idxLigneRecap + 1).text()));
	}

	private static void extraireCompteursFrance(List<DonnesCompteur> list,
			Elements tableLines) {

		extraireCompteurs(list, tableLines, 0);
	}

	private static void extraireCompteurs(List<DonnesCompteur> list,
			Elements tableLines, int premiereLigne) {
		// En-tête
		Element ligneEntete = tableLines.get(premiereLigne);
		Elements entete = ligneEntete.select("td");
		Element enteteFrance = entete.get(0);
		list.add(new DonnesCompteur(enteteFrance.text() + " :"));

		// Compteurs
		Element row = tableLines.get(premiereLigne + 1);
		Element rowValeurs = tableLines.get(premiereLigne + 2);
		Elements titres = row.select("td");
		Elements valeurs = rowValeurs.select("td");

		Iterator<Element> itTitres = titres.iterator();
		Iterator<Element> itValeurs = valeurs.iterator();
		GestionIcones gi = new GestionIcones();
		while (itTitres.hasNext()) {
			String libelle = itTitres.next().text();
			Integer icone = gi.trouveIconePour(libelle);
			String s = libelle + " : ";
			s += itValeurs.hasNext() ? itValeurs.next().text() : "Inconnu";
			DonnesCompteur donnesCompteur = new DonnesCompteur(s);
			donnesCompteur.setRessourceId(icone);
			list.add(donnesCompteur);
		}

		// Récapitulatif intermédiaire
		list.add(new DonnesCompteur(tableLines.get(premiereLigne + 3).text()));
	}

	private static void ajouterSeparateur(List<DonnesCompteur> list) {
		list.add(new DonnesCompteur(" "));
	}

	private static void extraireInfosTitulaire(List<DonnesCompteur> list,
			Element inner02) {
		Element infosTitulaireConteneur = inner02.select(
				"p.informationsTitulaire").first();

		Elements infosTitulaire = infosTitulaireConteneur.select("label");
		for (Element e : infosTitulaire) {
			list.add(new DonnesCompteur(e.text()));
		}
	}
}
