package game.slidepuzzle.util;

import game.slidepuzzle.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.widget.Toast;

public class ReadWriteResults {

	Context mContext;

	private String FILE_RECORDS_PATH = "memory-records";

	File file = new File(FILE_RECORDS_PATH);

	ArrayList<String> records = new ArrayList<String>();

	public ReadWriteResults(Context context) {
		mContext = context;

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				//Toast.makeText(mContext, R.string.error, Toast.LENGTH_LONG).show();
			}
		}

		try {
			FileInputStream fis = mContext.openFileInput(FILE_RECORDS_PATH);
			ObjectInputStream inputStream = new ObjectInputStream(fis);
			records = (ArrayList<String>) inputStream.readObject();
			inputStream.close();
		} catch (Exception e) {
			Toast.makeText(mContext, R.string.error, Toast.LENGTH_LONG).show();
		}

	}

	public void WriteRecords() {
		try {
			FileOutputStream fos = mContext.openFileOutput(FILE_RECORDS_PATH,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(records);
			os.close();
		} catch (Exception e) {
			//Toast.makeText(mContext, R.string.error, Toast.LENGTH_LONG).show();
		}
		return;
	}

	public void addRecord(String str) {
		records.add(str);

		Collections.sort(records);

		if (records.size() > 10) {
			for (int i = 10; i < records.size(); i++) {
				records.remove(i);
			}
		}

		return;
	}

	public ArrayList<String> getRecords() {
		return records;
	}

	public void clearResults() {
		records = new ArrayList<String>();
		//ContextWrapper cw = new ContextWrapper(mContext);
		//File directory = cw.getDir("files", Context.MODE_PRIVATE);
		//file = new File(directory, FILE_RECORDS_PATH);
		if (file.exists()) {
			file.delete();
		}

	}
}
