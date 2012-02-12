package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataInterpreter {

	/**
	 * Interprète les données de la page Suivi Conso de Free en informations
	 * affichables à l'écran
	 * 
	 * @param htmlData
	 *            les données de la page Suivi Conso de Free
	 * @return informations affichables à l'écran
	 */
	public static String[] interpret(String htmlData) {

		List<String> list = new ArrayList<String>();
		Document d = Jsoup.parse(htmlData);
		Elements divs = d.select("div#contenuEA");
		Element inner01 = divs.get(0).select("div").get(0);
		Element inner02 = inner01.select("div").first().select("div").first();

		extraireInfosTitulaire(list, inner02);

		ajouterSeparateur(list);

		list.add(inner02.select("p.date").text());

		Element table = inner02.select("table").first();
		Elements tableLines = table.select("tr");

		extraireCompteursFrance(list, tableLines);

		extraireRecapitulatif(list, tableLines);

		// Conversion en tableau
		String[] tab = new String[list.size()];
		int i = 0;
		for (String str : list) {
			tab[i++] = str;
		}

		return tab;
	}

	private static void extraireRecapitulatif(List<String> list,
			Elements tableLines) {
		list.add(tableLines.get(3).text());
		list.add(tableLines.get(4).text());
		list.add(tableLines.get(5).text());
	}

	private static void extraireCompteursFrance(List<String> list,
			Elements tableLines) {
		Element row = tableLines.get(1);
		Element rowValeurs = tableLines.get(2);
		Elements titres = row.select("td");
		Elements valeurs = rowValeurs.select("td");

		Iterator<Element> itTitres = titres.iterator();
		Iterator<Element> itValeurs = valeurs.iterator();
		while (itTitres.hasNext()) {
			String s = "    " + itTitres.next().text() + " : ";
			s += itValeurs.hasNext() ? itValeurs.next().text() : "Inconnu";
			list.add(s);
		}
	}

	private static void ajouterSeparateur(List<String> list) {
		list.add(" ");
	}

	private static void extraireInfosTitulaire(List<String> list,
			Element inner02) {
		Element infosTitulaireConteneur = inner02.select(
				"p.informationsTitulaire").first();

		Elements infosTitulaire = infosTitulaireConteneur.select("label");
		for (Element e : infosTitulaire) {
			list.add(e.text());
		}
	}
}
