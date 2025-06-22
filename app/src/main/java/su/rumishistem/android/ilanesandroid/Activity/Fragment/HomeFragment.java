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
import su.rumishistem.android.ilanesandroid.Adapter.IllustListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.R;

public class HomeFragment extends Fragment {
	private MainActivity Parent;

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
				JsonNode Return = API.RunGet("Home?SORT=DESC", Parent.getToken());
				System.out.println(Return);

				Parent.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Context CTX = requireContext();

						ListView LV = V.findViewById(R.id.home_follow);

						IllustListAdapter Adapter = new IllustListAdapter(CTX, Return.get("FOLLOW"));
						LV.setAdapter(Adapter);
					}
				});
			}
		}).start();
	}
}
