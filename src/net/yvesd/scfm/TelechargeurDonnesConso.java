package net.yvesd.scfm;

import org.apache.http.impl.client.DefaultHttpClient;

public class TelechargeurDonnesConso extends TelechargeurGenerique {

	private static final String URL_CIBLE = "https://mobile.free.fr/moncompte/index.php?page=suiviconso";

	public TelechargeurDonnesConso(CanWaitForStream activity,
			DefaultHttpClient httpClient) {

		super(activity, httpClient);
	}

	@Override
	protected String getUrlCible() {
		return URL_CIBLE;
	}

}
