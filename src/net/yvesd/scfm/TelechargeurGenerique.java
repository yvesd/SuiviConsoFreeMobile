package net.yvesd.scfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import android.os.AsyncTask;
import android.util.Log;

public abstract class TelechargeurGenerique
		extends
		AsyncTask<DataRecuperatorParams, TelechargeurGenerique.ProgressUpdate, List<String>> {

	/**
	 * URL de connexion par défaut
	 */
	private static final String URL_INITIALE_CONNEXION = "https://mobile.free.fr/moncompte/index.php?page=suiviconso";

	CanWaitForStream activity;
	List<String> messages = new ArrayList<String>();

	protected DefaultHttpClient httpClient;

	public TelechargeurGenerique(CanWaitForStream activity,
			DefaultHttpClient httpClient) {
		this.activity = activity;
		this.httpClient = httpClient;
	}

	protected abstract String getUrlCible();

	protected String getUrlConnexion() {
		return URL_INITIALE_CONNEXION;
	}

	@Override
	public List<String> doInBackground(DataRecuperatorParams... params) {

		List<String> results = new ArrayList<String>();
		for (DataRecuperatorParams param : params) {
			String s = downloadConsoData(param);
			results.add(s);
		}

		return results;
	}

	@Override
	protected void onProgressUpdate(
			TelechargeurGenerique.ProgressUpdate... values) {
		super.onProgressUpdate(values);

		for (ProgressUpdate pu : values) {
			activity.addToProgress(pu.getRes(), pu.getArgs());
			activity.setProgressStatus(pu.getProgress());
		}
	}

	protected String recupererPageConnexion(BasicHttpContext mHttpContext) {

		HttpGet get = new HttpGet(getUrlConnexion());
		HttpResponse getResponse;
		StringBuffer sb1 = new StringBuffer();
		try {
			getResponse = httpClient.execute(get, mHttpContext);
			HttpEntity responseEntity = getResponse.getEntity();
			InputStream is = responseEntity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String l;
			while ((l = in.readLine()) != null) {
				sb1.append(l);
				sb1.append("\n");
			}
		} catch (Exception e) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0600, 0));
			return "";
		}

		return sb1.toString();
	}

	private String downloadConsoData(DataRecuperatorParams param) {

		BasicHttpContext mHttpContext = new BasicHttpContext();
		CookieStore mCookieStore = new BasicCookieStore();
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

		String pageConnexion = recupererPageConnexion(mHttpContext);

		String identifiantCode = getScrambledIdent(pageConnexion,
				param.getLoginAbo());

		HttpPost post = new HttpPost(getUrlConnexion());

		post.addHeader("Referer", URL_INITIALE_CONNEXION);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs
				.add(new BasicNameValuePair("login_abo", identifiantCode));
		nameValuePairs
				.add(new BasicNameValuePair("pwd_abo", param.getPwdAbo()));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0100, 100));
			return null;
		}

		HttpResponse postResponse;
		try {
			postResponse = httpClient.execute(post, mHttpContext);
			HttpEntity responseEntity = postResponse.getEntity();
			InputStream is = responseEntity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String l;
			StringBuffer sb1 = new StringBuffer();
			while ((l = in.readLine()) != null) {
				sb1.append(l);
				sb1.append("\n");
			}

			String erreur = ExtracteurErreur.litErreur(sb1.toString());

			if (erreur == null) {
				publishProgress(new ProgressUpdate(R.string.log_lecturedonnees,
						66));
			} else {
				publishProgress(new ProgressUpdate(
						R.string.log_identificationimpossible, 100, erreur));

				publishProgress(new ProgressUpdate(
						R.string.log_instructionconfig, 100));

				return null;
			}

			String donnesLues = lectureDonnees(mHttpContext,
					param.getLoginAbo());

			return donnesLues;

		} catch (ClientProtocolException e) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0200, 100));
			return null;
		} catch (IOException e) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0300, 100));
			return null;
		}
	}

	protected String getScrambledIdent(String pageConnexion, String login) {
		Map<Integer, Integer> map = getIdentMap(pageConnexion);
		String scrambledLogin = "";
		for (int i = 0; i < login.length(); i++) {
			String charac = login.substring(i, i + 1);
			Integer characInteger = Integer.valueOf(charac);
			if (map.containsKey(characInteger)) {
				scrambledLogin += map.get(characInteger);
			}
		}
		Log.d("", "Scrambled login : " + scrambledLogin);
		return scrambledLogin;
	}

	/**
	 * <p>
	 * Méthode qui va réellement effectuer la requête "métier" sur le site Free.
	 * Elle doit également retourner les données.
	 * 
	 * <p>
	 * Cette méthode est destinée à être surchargée selon le type de requêtes
	 * qu'il y à a faire, et selon les paramètres a insérer dans la requête (en
	 * particulier dans les cas de HTTP POST). La méthode ici est une
	 * implémentation par défaut HTTP GET.
	 * 
	 * @param mHttpContext
	 *            Contexte HTTP (transporte notamment les cookies
	 * @return Données lues
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected String lectureDonnees(BasicHttpContext mHttpContext, String login)
			throws IOException, ClientProtocolException {

		HttpGet httpGet = new HttpGet(getUrlCible());

		HttpResponse getResponse = httpClient.execute(httpGet, mHttpContext);

		String donneesLues = litDonnesDepuisHttpResponse(getResponse);

		return donneesLues;
	}

	/**
	 * Constitue une string à partir des de la HttpResponse
	 * 
	 * @param getResponse
	 * @return
	 * @throws IOException
	 */
	protected String litDonnesDepuisHttpResponse(HttpResponse getResponse)
			throws IOException {
		HttpEntity responseEntity2 = getResponse.getEntity();
		InputStream is2 = responseEntity2.getContent();
		BufferedReader in2 = new BufferedReader(new InputStreamReader(is2));

		StringBuffer sb = new StringBuffer();
		String l2;
		while ((l2 = in2.readLine()) != null) {
			sb.append(l2);
		}
		return sb.toString();
	}

	@Override
	protected void onPostExecute(List<String> result) {
		super.onPostExecute(result);
		activity.handleResult(result);
	}

	static class ProgressUpdate {
		private int res;
		private int progress;
		private Object[] args;

		public ProgressUpdate(int res, int progress, Object... args) {
			super();
			this.res = res;
			this.progress = progress;
			this.args = args;
		}

		public int getRes() {
			return res;
		}

		public int getProgress() {
			return progress;
		}

		public Object[] getArgs() {
			return args;
		}
	}

	/**
	 * 
	 * @param fluxHtml
	 * @return Map Clé : numéro dans l'identifiant, Valeur : position
	 */
	public Map<Integer, Integer> getIdentMap(String fluxHtml) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		List<String> matches = new ArrayList<String>();

		Pattern p1 = Pattern
				.compile("(ident_addNumber\\([0-9]+,\\s*[0-9]+\\))");

		Matcher m1 = p1.matcher(fluxHtml);
		while (m1.find()) {
			matches.add(m1.group(1));
		}

		for (String s : matches) {
			Pattern p2 = Pattern
					.compile("ident_addNumber\\(([0-9]+),\\s*([0-9]+)\\)");
			Matcher m2 = p2.matcher(s);
			while (m2.find()) {
				String a1 = m2.group(1);
				String a2 = m2.group(2);
				Integer ai1 = Integer.valueOf(a1);
				Integer ai2 = Integer.valueOf(a2);

				map.put(ai1, ai2);
				Log.d("", ai1 + " ==> " + ai2);
			}
		}

		return map;
	}
}
