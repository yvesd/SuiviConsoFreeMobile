package net.yvesd.scfm;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class DataRecuperatorMock
		extends
		AsyncTask<DataRecuperatorParams, DataRecuperatorMock.ProgressUpdate, List<String>> {

	SuiviConsoFreeMobileActivity scfma;
	List<String> messages = new ArrayList<String>();

	public DataRecuperatorMock(SuiviConsoFreeMobileActivity scfma) {
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
	protected void onProgressUpdate(
			DataRecuperatorMock.ProgressUpdate... values) {
		super.onProgressUpdate(values);

		for (ProgressUpdate pu : values) {
			scfma.addToProgress(pu.getRes(), pu.getArgs());
			scfma.setProgressStatus(pu.getProgress());
		}
	}

	/**
	 * ************************ MOCKED METHOD. DO NOT SHIP ***********
	 * 
	 * @param param
	 * @return
	 */
	private String downloadConsoData(DataRecuperatorParams param) {

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return "";
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
