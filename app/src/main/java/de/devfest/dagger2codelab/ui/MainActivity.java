package de.devfest.dagger2codelab.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import de.devfest.dagger2codelab.DaggerApp;
import de.devfest.dagger2codelab.R;
import de.devfest.dagger2codelab.data.api.GitHubApi;
import de.devfest.dagger2codelab.data.response.UserResponse;
import de.devfest.dagger2codelab.ui.adapter.UsersListAdapter;
import de.devfest.dagger2codelab.ui.adapter.UsersListAdapter.UserListListener;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements UserListListener {

	@Inject
	GitHubApi service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((DaggerApp)getApplication()).getApplicationComponent().inject(this);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		RecyclerView usersListView = (RecyclerView) findViewById(R.id.list);
		usersListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		final UsersListAdapter adapter = new UsersListAdapter(this);
		usersListView.setAdapter(adapter);

		service.getUsers()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<UserResponse>>() {
					@Override
					public void call(List<UserResponse> userResponses) {
						adapter.addItems(userResponses);
					}
				});
	}

	@Override
	public void onUserClicked(String login) {
		startActivity(RepositoriesActivity.createIntent(this, login));
	}
}
