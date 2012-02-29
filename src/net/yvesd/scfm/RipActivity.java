package net.yvesd.scfm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RipActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

		Button market = (Button) findViewById(R.id.market);
		addClickListener(market, "market://details?id=net.yvesd.scfm");

		Button source = (Button) findViewById(R.id.source);
		addClickListener(source,
				"https://github.com/yvesd/SuiviConsoFreeMobile/");
	}

	private void addClickListener(Button market, final String uri) {
		market.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				try {
					startActivity(i);
				} catch (Exception e) {
					// ...
				}
			}
		});
	}
}
