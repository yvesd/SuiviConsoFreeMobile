package net.yvesd.scfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

public class DataRecuperator
		extends
		AsyncTask<DataRecuperatorParams, DataRecuperator.ProgressUpdate, List<String>> {

	private static final String URL_SUIVICONSO_FREEMOBILE = "https://mobile.free.fr/moncompte/index.php?page=suiviconso";

	SuiviConsoFreeMobileActivity scfma;
	List<String> messages = new ArrayList<String>();

	public DataRecuperator(SuiviConsoFreeMobileActivity scfma) {
		this.scfma = scfma;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
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
	protected void onProgressUpdate(DataRecuperator.ProgressUpdate... values) {
		super.onProgressUpdate(values);

		for (ProgressUpdate pu : values) {
			scfma.addToProgress(pu.getRes(), pu.getArgs());
			scfma.setProgressStatus(pu.getProgress());
		}
	}

	private String downloadConsoData(DataRecuperatorParams param) {
		// Instantiate the custom HttpClient
		DefaultHttpClient client = new MyHttpClient(scfma);

		BasicHttpContext mHttpContext = new BasicHttpContext();
		CookieStore mCookieStore = new BasicCookieStore();
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

		HttpPost post = new HttpPost(URL_SUIVICONSO_FREEMOBILE);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login_abo", param
				.getLoginAbo()));
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
			postResponse = client.execute(post, mHttpContext);
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
				publishProgress(new ProgressUpdate(R.string.log_lecturesc, 66));
			} else {
				publishProgress(new ProgressUpdate(
						R.string.log_identificationimpossible, 100, erreur));

				publishProgress(new ProgressUpdate(
						R.string.log_instructionconfig, 100));

				return null;
			}

			HttpGet httpGet = new HttpGet(URL_SUIVICONSO_FREEMOBILE);
			HttpResponse getResponse = client.execute(httpGet, mHttpContext);

			HttpEntity responseEntity2 = getResponse.getEntity();
			InputStream is2 = responseEntity2.getContent();
			BufferedReader in2 = new BufferedReader(new InputStreamReader(is2));

			StringBuffer sb = new StringBuffer();
			String l2;
			while ((l2 = in2.readLine()) != null) {
				sb.append(l2);
			}

			return sb.toString();

		} catch (ClientProtocolException e) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0200, 100));
			return null;
		} catch (IOException e) {
			publishProgress(new ProgressUpdate(R.string.log_erreur0300, 100));
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<String> result) {
		super.onPostExecute(result);
		scfma.handleResult(result);
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
}
