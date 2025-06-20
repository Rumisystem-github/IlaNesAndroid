package su.rumishistem.android.ilanesandroid.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.fasterxml.jackson.databind.JsonNode;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.R;

public class MainActivity extends AppCompatActivity {
	private MainActivity CTX = this;

	private JsonNode SelfUser;
	private String Token;

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Token = IPCHTTP.getToken();
					SelfUser = IPCHTTP.getSelf();
				} catch (Error ERR) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(CTX).setTitle("るみ鯖サービスが起動していません").setMessage("一回るみ鯖アプリを起動してみてください！").setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface DI, int I) {
									System.exit(1);
								}
							}).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.main_activity);
	}
}
