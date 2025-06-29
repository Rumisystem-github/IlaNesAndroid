package su.rumishistem.android.ilanesandroid.Activity.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.JsonNode;

import su.rumishistem.android.ilanesandroid.Activity.MainActivity;
import su.rumishistem.android.ilanesandroid.Adapter.HomeIllustListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.R;

public class HomeFragment extends Fragment {
	private MainActivity Parent;

	private JsonNode FollowIllust = null;

	public HomeFragment(MainActivity ParentActivity) {
		this.Parent = ParentActivity;
	}

	@Override
	public View onCreateView(LayoutInflater Inflater, ViewGroup Container, Bundle SavedInstanceState) {
		return Inflater.inflate(R.layout.home_fragment, Container, false);
	}

	@Override
	public void onViewCreated(View V, Bundle SavedInstanceState) {
		super.onViewCreated(V, SavedInstanceState);

		new Thread(new Runnable() {
			@Override
			public void run() {
				JsonNode Home = API.RunGet("Home?SORT=DESC", Parent.getToken());
				FollowIllust = Home.get("FOLLOW");

				Parent.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ListView FollowLV = V.findViewById(R.id.home_follow);
						Context CTX = requireContext();

						HomeIllustListAdapter Adapter = new HomeIllustListAdapter(CTX, FollowIllust);
						FollowLV.setAdapter(Adapter);
					}
				});
			}
		}).start();
	}
}
