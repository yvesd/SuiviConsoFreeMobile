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
	public static DonnesCompteur[] interpret(String htmlData) {

		List<DonnesCompteur> list = new ArrayList<DonnesCompteur>();

		Document d = Jsoup.parse(htmlData);

		ajouterTousElementsClasse(list, d, "infosLigneDetail");

		ajouterTousElementsClasse(list, d, "resumeConso");

		DonnesCompteur donnesCompteurN = new DonnesCompteur(
				"<br><font color=#C00000><b>NATIONAL</b></font");
		list.add(donnesCompteurN);

		extraireDetailsConso(list, d, "national");

		DonnesCompteur donnesCompteurI = new DonnesCompteur(
				"<br><font color=#C00000><b>INTERNATIONAL</b></font");
		list.add(donnesCompteurI);

		extraireDetailsConso(list, d, "international");

		DonnesCompteur donnesCompteurT = new DonnesCompteur(
				"<br><font color=#C00000><b>MONTANT TOTAL</b></font");
		list.add(donnesCompteurT);

		extraireMontantTotal(list, d);

		// Conversion en tableau
		DonnesCompteur[] tab = new DonnesCompteur[list.size()];
		int i = 0;
		for (DonnesCompteur dc : list) {
			tab[i++] = dc;
		}

		return tab;
	}

	private static void extraireMontantTotal(List<DonnesCompteur> list,
			Document d) {

		Elements elements = d.select(".montant p");
		StringBuffer sb = new StringBuffer();

		Iterator<Element> it = elements.iterator();

		if (it.hasNext()) {
			String txt = it.next().text();
			sb.append(txt);
		}

		while (it.hasNext()) {
			String txt = it.next().text();
			sb.append("<br>");
			sb.append(txt);
		}

		DonnesCompteur c = new DonnesCompteur(sb.toString());
		list.add(c);
	}

	private static void ajouterTousElementsClasse(List<DonnesCompteur> list,
			Document d, String classeCSS) {
		Elements infos1 = d.select("." + classeCSS);
		for (Element e : infos1) {
			String texte = e.text();
			if (!("".equals(texte))) {
				DonnesCompteur c = new DonnesCompteur(texte);
				list.add(c);
			}
		}

	}

	private static void extraireDetailsConso(List<DonnesCompteur> list,
			Document d, String cssClass) {

		Elements titres = d.select("." + cssClass + " .titreDetail");
		Elements cD = d.select("." + cssClass + " .consoDetail");

		Iterator<Element> itTitres = titres.iterator();
		Iterator<Element> itCD = cD.iterator();

		GestionIcones gi = new GestionIcones();
		while (itTitres.hasNext()) {
			Element elementTitre = itTitres.next();

			String titre = elementTitre.text();
			StringBuffer sb = new StringBuffer();
			GestionIcones.IconeCouleur ic = gi.trouveIconePour(titre);

			sb.append("<b>" + titre + "</b>");

			if (itCD.hasNext()) {
				Element e = itCD.next();
				String cdTexte = e.text();
				if (!("".equals(cdTexte))) {
					sb.append("<br>");
					sb.append(cdTexte);
				}
			}

			list.add(new DonnesCompteur(sb.toString(), ic.icone,
					ic.nomClePreference, ic.couleurDefaut));
		}

		extraireHorsForfait(list, d, cssClass);

	}

	private static void extraireHorsForfait(List<DonnesCompteur> list,
			Document d, String cssc) {

		Elements hF = d.select("." + cssc + " .horsForfait");
		StringBuffer hfSb = new StringBuffer();
		Iterator<Element> itHF = hF.iterator();
		if (itHF.hasNext()) {
			hfSb.append("<b>Hors forfait</b><br>");
			String text = itHF.next().text();
			text = supprimerMentionHorsForfait(text);
			hfSb.append(text);
		} else {
			hfSb.append("Pas d'information de suivi conso à afficher. Début du mois ?");
		}

		while (itHF.hasNext()) {
			Element e = itHF.next();

			String text = e.text();
			if (!("".equals(text))) {
				text = supprimerMentionHorsForfait(text);
				hfSb.append("<br>");
				hfSb.append(text);
			}
		}
		list.add(new DonnesCompteur(
				hfSb.toString(),
				R.drawable.ic_euro,
				SuiviConsoFreeMobileActivity.PREF_KEY_COULEUR_ICONE_HORSFORFAIT,
				GestionIcones.COULEUR_DEFAUT_HF));
	}

	private static String supprimerMentionHorsForfait(String text) {

		text = text.replaceAll("Hors forfait", "");
		text = text.trim();
		return text;
	}
}
