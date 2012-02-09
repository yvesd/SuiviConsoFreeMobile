package net.yvesd.scfm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ExtracteurErreur {

	/**
	 * 
	 * @param htmlData
	 * @return null si pas d'erreur, message d'erreur sinon
	 */
	public static String litErreur(String htmlData) {
		Document d = Jsoup.parse(htmlData);

		Elements select = d.select("div#msgRetour");

		if (select != null && !(select.isEmpty())) {
			String s = select.first().text();

			if (s == null && "".equals(s))
				return null;
			else
				return s;
		} else
			return null;

	}
}
