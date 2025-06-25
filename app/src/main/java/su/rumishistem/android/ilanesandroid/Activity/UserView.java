package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.JsonNode;

import su.rumishistem.android.ilanesandroid.Adapter.IllustListAdapter;
import su.rumishistem.android.ilanesandroid.Adapter.UserIllustListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class UserView extends AppCompatActivity {
	private Context CTX;
	private String UID = null;
	private String Token;

	@Override
	protected void onStart() {
		super.onStart();

		CTX = this;

		Intent INT = getIntent();
		UID = INT.getStringExtra("UID");
	}

	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.user_view);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Token = IPCHTTP.getToken();

				JsonNode Result = API.RunGet("User?ID=" + UID + "&TYPE=NONE", Token);
				if (!Result.get("STATUS").asBoolean()) return;

				JsonNode User = Result.get("ACCOUNT");
				JsonNode IllustList = Result.get("ILLUST");

				Bitmap Icon = UserIconManager.Get(User.get("UID").asText());

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//上のやつ
						Toolbar TB = findViewById(R.id.Toolbar);
						TB.setTitle(User.get("NAME").asText());

						//アイコン
						ImageView UserIcon = findViewById(R.id.user_icon);
						UserIcon.setImageBitmap(Icon);

						//名前
						TextView UserName = findViewById(R.id.user_name);
						UserName.setText(User.get("NAME").asText());

						//イラスト一覧
						UserIllustListAdapter Adapter = new UserIllustListAdapter(CTX, IllustList, User);
						ListView IllustListView = findViewById(R.id.illust_list);
						IllustListView.setAdapter(Adapter);
						setListViewHeightBasedOnChildren(IllustListView);
					}
				});
			}
		}).start();
	}

	public static void setListViewHeightBasedOnChildren(ListView LV) {
		ListAdapter Adapter = LV.getAdapter();
		if (Adapter == null) return;

		int TotalHeight = 0;
		for (int I = 0; I < Adapter.getCount(); I++) {
			View Item = Adapter.getView(I, null, LV);
			Item.measure(
				View.MeasureSpec.makeMeasureSpec(LV.getWidth(), View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
			);
			TotalHeight += Item.getMeasuredHeight();
		}

		ViewGroup.LayoutParams Params = LV.getLayoutParams();
		Params.height = TotalHeight + (LV.getDividerHeight() * (LV.getCount() - 1));
		LV.setLayoutParams(Params);
		LV.requestLayout();
	}
}
