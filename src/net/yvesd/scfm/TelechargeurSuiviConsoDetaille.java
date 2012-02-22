package net.yvesd.scfm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

public class TelechargeurSuiviConsoDetaille extends TelechargeurGenerique {

	private static final String URL_CIBLE = "https://mobile.free.fr/moncompte/ajax.php?page=consotel_current_month";

	public TelechargeurSuiviConsoDetaille(CanWaitForStream activity,
			DefaultHttpClient httpClient) {

		super(activity, httpClient);
	}

	@Override
	protected String getUrlCible() {
		return URL_CIBLE;
	}

	@Override
	protected String lectureDonnees(BasicHttpContext mHttpContext, String login)
			throws IOException, ClientProtocolException {

		HttpPost httpPost = new HttpPost(getUrlCible());

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", login));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0400, 100));
			return null;
		}

		HttpResponse getResponse = httpClient.execute(httpPost, mHttpContext);

		String donneesLues = litDonnesDepuisHttpResponse(getResponse);

		return donneesLues;

	}

}
