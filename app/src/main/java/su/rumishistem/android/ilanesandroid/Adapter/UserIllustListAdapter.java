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
import su.rumishistem.android.ilanesandroid.Module.IllustThumbnailManager;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class UserIllustListAdapter extends BaseAdapter {
	private Context CTX;
	private JsonNode ItemList;
	private JsonNode User;
	private LayoutInflater Inflater;

	public UserIllustListAdapter(Context CTX, JsonNode ItemList, JsonNode User) {
		this.CTX = CTX;
		this.ItemList = ItemList;
		this.User = User;
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
		UserName.setText(User.get("NAME").asText());

		new Thread(new Runnable() {
			@Override
			public void run() {
				//アイコン
				Bitmap Icon = UserIconManager.Get(User.get("UID").asText());
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

		return ConvertView;
	}
}
