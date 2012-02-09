package net.yvesd.scfm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterIdsActivity extends Activity {

	private EditText loginAbo;
	private EditText pwdAbo;
	private Button valider;

	public static final String LOGIN_ABO_KEY = "net.yvesd.scfm.LOGIN_ABO_KEY";

	public static final String PWD_ABO_KEY = "net.yvesd.scfm.PWD_ABO_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_id);

		Bundle bundle = this.getIntent().getExtras();
		String loginAbo = bundle.getString(LOGIN_ABO_KEY);
		String pwdAbo = bundle.getString(PWD_ABO_KEY);

		this.loginAbo = (EditText) findViewById(R.id.loginAbo);
		this.pwdAbo = (EditText) findViewById(R.id.pwdAbo);
		this.valider = (Button) findViewById(R.id.save);

		this.loginAbo.setText(loginAbo);
		this.pwdAbo.setText(pwdAbo);

		this.valider.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				save();
			}
		});
	}

	private void save() {
		Intent intent = new Intent();
		intent.putExtra(LOGIN_ABO_KEY, this.loginAbo.getText().toString());
		intent.putExtra(PWD_ABO_KEY, this.pwdAbo.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
}
