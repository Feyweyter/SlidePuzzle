package game.slidepuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OptionsActivity extends ClearResultsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
	}

	public void onClickExit(View view) {
		finish();
	}

	public void onClickResults(View view) {
		Intent intent = new Intent(OptionsActivity.this, ResultsActivity.class);
		startActivity(intent);
	}

	public void onClickNewGame(View view) {
		Intent intent = new Intent(OptionsActivity.this, NewGameActivity.class);
		startActivity(intent);
	}
	
	public void onClickClearResults(View view) {
		 showDialog(DIALOG_EXIT);
	}
	
	public void onClickAboutGame(View view) {
		Intent intent = new Intent(OptionsActivity.this, AboutGameActivity.class);
		startActivity(intent);
	}
	
	public void onClickRules(View view) {
		Intent intent = new Intent(OptionsActivity.this, RulesActivity.class);
		startActivity(intent);
	}
}
