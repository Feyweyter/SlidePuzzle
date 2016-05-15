package game.slidepuzzle;

import game.slidepuzzle.util.ReadWriteResults;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Toast;

public class ClearResultsActivity extends SherlockActivity {

	final int DIALOG_EXIT = 1;

	public void showDialogAboutClearingResults(View v) {
		showDialog(DIALOG_EXIT);
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_EXIT) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.clear_results);
			adb.setMessage(R.string.answer_clear_results);
			adb.setIcon(android.R.drawable.ic_dialog_info);
			adb.setPositiveButton(R.string.yes, myClickListener);
			adb.setNegativeButton(R.string.no, myClickListener);
			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	OnClickListener myClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				clearResults();
				Toast.makeText(getApplicationContext(), R.string.done,
						Toast.LENGTH_LONG).show();
				break;
			case Dialog.BUTTON_NEGATIVE:
				break;
			}
		}
	};

	private void clearResults() {
		ReadWriteResults readWriteResults = new ReadWriteResults(
				getApplicationContext());
		readWriteResults.clearResults();
	}
}
