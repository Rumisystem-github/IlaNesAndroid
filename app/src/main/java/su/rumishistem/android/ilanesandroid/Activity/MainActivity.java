package su.rumishistem.android.ilanesandroid.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import su.rumishistem.android.ilanesandroid.R;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.main_activity);
	}
}
