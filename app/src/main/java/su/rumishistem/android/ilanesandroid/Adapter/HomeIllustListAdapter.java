package su.rumishistem.android.ilanesandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import su.rumishistem.android.ilanesandroid.Activity.IllustView;
import su.rumishistem.android.ilanesandroid.Activity.UserView;
import su.rumishistem.android.ilanesandroid.Module.IllustThumbnailManager;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class HomeIllustListAdapter extends BaseAdapter {
	private Context CTX;
	private JsonNode ItemList;
	private LayoutInflater Inflater;

	public HomeIllustListAdapter(Context CTX, JsonNode ItemList) {
		this.CTX = CTX;
		this.ItemList = ItemList;
		this.Inflater = LayoutInflater.from(CTX);
	}

	@Override
	public int getCount() {
		return ItemList.size();
	}

	@Override
	public Object getItem(int I) {
		return ItemList.get(I);
	}

	@Override
	public long getItemId(int I) {
		return I;
	}

	@Override
	public View getView(int I, View ConvertView, ViewGroup Parent) {
		JsonNode Row = ItemList.get(I);

		if (ConvertView == null) {
			ConvertView = Inflater.inflate(R.layout.illust_item, Parent, false);
		}

		ImageView Thumbnail = ConvertView.findViewById(R.id.thumbnail);
		TextView Title = ConvertView.findViewById(R.id.title);
		ImageView UserIcon = ConvertView.findViewById(R.id.user_icon);
		TextView UserName = ConvertView.findViewById(R.id.user_name);

		Title.setText(Row.get("TITLE").asText());
		UserName.setText(Row.get("ACCOUNT").get("NAME").asText());

		new Thread(new Runnable() {
			@Override
			public void run() {
				//アイコン
				Bitmap Icon = UserIconManager.Get(Row.get("ACCOUNT").get("UID").asText());
				((android.app.Activity) CTX).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						UserIcon.setImageBitmap(Icon);
					}
				});

				//サムネ
				Bitmap Original = IllustThumbnailManager.Get(Row.get("ID").asText());
				if (!(Original == null || Original.getWidth() <= 0 || Original.getHeight() <= 0)) {
					int TargetWidth = 180;
					int TargetHeight = 195;
					int Width = Original.getWidth();
					int Height = Original.getHeight();
					float Scale = Math.min((float)TargetWidth / Width, (float)TargetHeight / Height);
					int ScaledWidth = Math.round(Width * Scale);
					int ScaledHeight = Math.round(Height * Scale);

					if (!(ScaledWidth <= 0 || ScaledHeight <= 0)) {
						Bitmap ScaledBitmap = Bitmap.createScaledBitmap(Original, ScaledWidth, ScaledHeight, true);
						((android.app.Activity) CTX).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Thumbnail.setImageBitmap(ScaledBitmap);
							}
						});
					}
				}
			}
		}).start();

		//イラストを開く
		Thumbnail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent INT = new Intent(CTX, IllustView.class);
				INT.putExtra("ID", Row.get("ID").asText());
				CTX.startActivity(INT);
			}
		});

		//ユーザーを開く
		UserIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OpenUserView(Row.get("ACCOUNT").get("UID").asText());
			}
		});
		UserName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OpenUserView(Row.get("ACCOUNT").get("UID").asText());
			}
		});

		return ConvertView;
	}

	private void OpenUserView(String UID) {
		Intent INT = new Intent(CTX, UserView.class);
		INT.putExtra("UID", UID);
		CTX.startActivity(INT);
	}
}
