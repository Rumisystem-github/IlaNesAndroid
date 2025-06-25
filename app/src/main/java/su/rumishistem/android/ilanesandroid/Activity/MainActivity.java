package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.navigation.NavigationView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.Nullable;

import su.rumishistem.android.ilanesandroid.Activity.Fragment.HomeFragment;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class MainActivity extends AppCompatActivity {
	private MainActivity CTX = this;
	private DrawerLayout DL;
	private ActionBarDrawerToggle Toggle;
	private ActivityResultLauncher<Intent> ResultLauncher;

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

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ChangeFragment(new HomeFragment(CTX));
						}
					});
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

		//戻ってきた時用のやつ
		ResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
			@Override
			public void onActivityResult(ActivityResult Result) {
				if (Result.getResultCode() == RESULT_OK) {
					Intent Data = Result.getData();
				}
			}
		});

		Toolbar TB = findViewById(R.id.HomeToolbar);
		setSupportActionBar(TB);

		DL = findViewById(R.id.MainDrawableLayout);
		NavigationView NavView = findViewById(R.id.HomeNavView);

		Toggle = new ActionBarDrawerToggle(
				CTX,
				DL,
				TB,
				R.string.open,
				R.string.close
		);
		DL.addDrawerListener(Toggle);
		Toggle.syncState();

		NavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
				androidx.fragment.app.Fragment Fragment = null;
				int ItemID = Item.getItemId();

				if (ItemID == R.id.menu_home) {
					Fragment = new HomeFragment(CTX);
				} else if (ItemID == R.id.menu_profile) {
					Intent INT = new Intent(CTX, UserView.class);
					INT.putExtra("UID", SelfUser.get("UID").asText());
					CTX.startActivity(INT);
					return true;
				}

				if (Fragment != null) ChangeFragment(Fragment);
				DL.closeDrawer(GravityCompat.START);
				return true;
			}
		});
	}

	private void ChangeFragment(androidx.fragment.app.Fragment Fragment) {
		if (Fragment == null) throw new Error("フラグメントがNull");

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, Fragment)
				.commit();
	}

	public String getToken() {
		return Token;
	}
}
