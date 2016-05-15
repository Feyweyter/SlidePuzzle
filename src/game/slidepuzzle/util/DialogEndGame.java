package game.slidepuzzle.util;

import game.slidepuzzle.NewGameActivity;
import game.slidepuzzle.OptionsActivity;
import game.slidepuzzle.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

@SuppressLint("NewApi")
public class DialogEndGame extends DialogFragment implements OnClickListener {

	private long millis = 0;

	public DialogEndGame(long millis) {
		this.millis = millis;
	}

	@SuppressLint("NewApi")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String messageText = getString(R.string.your_result) + " "
				+ MillisecondsToMinSec.parseMilliSeconds(millis);
		messageText += "\n";
		messageText += getString(R.string.end_game);
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
				.setTitle("New game").setPositiveButton(R.string.yes, this)
				.setNegativeButton(R.string.no, this).setMessage(messageText);
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		Intent intent = new Intent(getActivity(), OptionsActivity.class);
		switch (which) {
		case Dialog.BUTTON_POSITIVE:
			intent = new Intent(getActivity(), NewGameActivity.class);
			startActivity(intent);
			break;
		case Dialog.BUTTON_NEGATIVE:
			intent = new Intent(getActivity(), OptionsActivity.class);
			startActivity(intent);
			break;
		}

	}

}
