package game.slidepuzzle.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ImageSquareSlicer {

	private Bitmap initialBitmap;
	private int squareSize, gridSize;
	private List<Bitmap> slices;
	private int lastSliceServed;
	private List<Integer> squareOrder;
	List<NumberBitmap> numberSlices = null;
	private Context context;

	public ImageSquareSlicer(Bitmap initialBitmap, int gridSize, Context context) {
		super();
		this.initialBitmap = initialBitmap;
		this.gridSize = gridSize;
		this.squareSize = initialBitmap.getWidth() / gridSize;
		this.context = context;
		slices = new LinkedList<Bitmap>();
		sliceInitialBitmap();
	}

	private void sliceInitialBitmap() {
		int x, y;
		Bitmap bitmap;
		lastSliceServed = 0;
		for (int rowI = 0; rowI < gridSize; rowI++) {
			for (int colI = 0; colI < gridSize; colI++) {
				if (rowI == gridSize - 1 && colI == gridSize - 1) {
					continue;
				} else {
					x = rowI * squareSize;
					y = colI * squareSize;
					bitmap = Bitmap.createBitmap(initialBitmap, x, y,
							squareSize, squareSize);
					Canvas canvas = new Canvas(bitmap);
					Paint paint = new Paint();
					paint.setColor(Color.parseColor("#ffffff"));
					int end = squareSize - 1;
					canvas.drawLine(0, 0, 0, end, paint);
					canvas.drawLine(0, end, end, end, paint);
					canvas.drawLine(end, end, end, 0, paint);
					canvas.drawLine(end, 0, 0, 0, paint);
					slices.add(bitmap);
				}
			}
		}
		initialBitmap = null;
	}

	public void randomizeImageSquares() {
		numberSlices = new LinkedList<NumberBitmap>();
		for (int i = 0; i < slices.size(); i++) {
			numberSlices.add(new NumberBitmap(slices.get(i), i));
		}
		Collections.shuffle(numberSlices);
		slices = new LinkedList<Bitmap>();
		for (int i = 0; i < numberSlices.size(); i++) {
			slices.add(numberSlices.get(i).getBitmap());
		}
		slices.add(null);
		numberSlices.add(null);
		squareOrder = null;
	}

	public void setImageSquareOrder(List<Integer> order) {
		List<Bitmap> newSlices = new LinkedList<Bitmap>();
		for (int o : order) {
			if (o < slices.size()) {
				newSlices.add(slices.get(o));
			} else {
				// empty slice
				newSlices.add(null);
			}
		}
		squareOrder = order;
		slices = newSlices;
	}

	public ImageSquareView getImageSquare() {
		ImageSquareView square = null;
		if (numberSlices.size() > 0) {
			int originalIndex;
			if (squareOrder == null) {
				originalIndex = lastSliceServed++;
			} else {
				originalIndex = squareOrder.get(lastSliceServed++);
			}
			if (originalIndex < 15) {
				square = new ImageSquareView(context, numberSlices.get(0)
						.getNumber());
			} else {
				square = new ImageSquareView(context, originalIndex);
			}
			if (numberSlices.get(0) == null) {
				square.setEmpty(true);
				square.setImageBitmap(null);
			} else {
				square.setImageBitmap(numberSlices.remove(0).getBitmap());
			}
		}
		return square;
	}

}
