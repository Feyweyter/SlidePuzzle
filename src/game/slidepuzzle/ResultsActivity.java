package game.slidepuzzle;

import game.slidepuzzle.util.ReadWriteResults;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ResultsActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setLayoutParams(new TableLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		tableLayout.setStretchAllColumns(true);
		tableLayout.setBackgroundResource(R.drawable.bg);

		TableRow tableRow = new TableRow(this);

		TextView textView = new TextView(this);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		textView.setText(R.string.spent_time);
		textView.setBackgroundColor(Color.YELLOW);
		tableRow.addView(textView);

		textView = new TextView(this);
		textView.setText(R.string.date);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		tableRow.addView(textView);
		textView.setBackgroundColor(Color.YELLOW);

		tableLayout.addView(tableRow);

		ReadWriteResults readWriteResults = new ReadWriteResults(this);
		List<String> records = readWriteResults.getRecords();

		if (records != null) {
			boolean isColoredGrey = false;
			for (String str : records) {
				tableRow = new TableRow(this);
				
				String[] arr = str.split(" ");
				textView = new TextView(this);
				textView.setText(arr[0]);
				if (isColoredGrey) {
					textView.setBackgroundColor(Color.GRAY);
				} else{
					textView.setBackgroundColor(Color.WHITE);
				}
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(12);
				tableRow.addView(textView);

				textView = new TextView(this);
				textView.setText(arr[1]);
				if (isColoredGrey) {
					textView.setBackgroundColor(Color.GRAY);
				}else{
					textView.setBackgroundColor(Color.WHITE);
				}
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(12);
				tableRow.addView(textView);
				isColoredGrey = !isColoredGrey;

				tableLayout.addView(tableRow);
				tableLayout.setBackgroundColor(Color.WHITE);
			}
		}

		setContentView(tableLayout);
	}

}
