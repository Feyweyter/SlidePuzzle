package game.slidepuzzle.util;

import game.slidepuzzle.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentContainer;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.FloatEvaluator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;

public class SlicedImageView extends RelativeLayout implements OnTouchListener {

	public static final int GRID_SIZE = 4; // 4x4

	private Bitmap original = null;
	private Chronometer mChronometer;
	private DialogEndGame showGameResultAndAsk;

	private static String dateFormatMaket = "dd.MM.yyyy";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			dateFormatMaket);
	private FragmentManager fragmentManager = null;

	public enum Direction {
		X, Y
	};

	private int imageSquareSize;
	private ArrayList<ImageSquareView> imageSquares;
	private ImageSquareView emptyImageSquare, moveImageSquare;
	private boolean boardCreated;
	private RectF gameboardRect;
	private PointF lastDragPoint;
	private ArrayList<GameImageSquareMotion> currentMotionWriters;
	private LinkedList<Integer> imageSquareOrder = null;

	public SlicedImageView(Context context, AttributeSet attrSet) {
		super(context, attrSet);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!boardCreated) {
			detectGameboardSizes();
			fillTiles();
			boardCreated = true;
		}
	}

	private void detectGameboardSizes() {
		int viewWidth = getWidth();
		int viewHeight = getHeight();

		if (viewWidth > viewHeight) {
			imageSquareSize = viewHeight / GRID_SIZE;
		} else {
			imageSquareSize = viewWidth / GRID_SIZE;
		}
		int gameboardSize = imageSquareSize * GRID_SIZE;

		int gameboardTop = viewHeight / 2 - gameboardSize / 2;
		int gameboardLeft = viewWidth / 2 - gameboardSize / 2;
		gameboardRect = new RectF(gameboardLeft, gameboardTop, gameboardLeft
				+ gameboardSize, gameboardTop + gameboardSize);
	}

	public void setChronometer(Chronometer chronometer) {
		this.mChronometer = chronometer;
	}

	public void setImage(Bitmap original) {
		this.original = original;
	}

	public void fillTiles() {
		removeAllViews();

		if (original == null) {
			Drawable bitmapDrawable = getResources().getDrawable(
					R.drawable.raccoon);
			original = ((BitmapDrawable) bitmapDrawable).getBitmap();
		}
		ImageSquareSlicer squareSlicer = new ImageSquareSlicer(original,
				GRID_SIZE, getContext());

		if (imageSquareOrder == null) {
			squareSlicer.randomizeImageSquares();
		} else {
			squareSlicer.setImageSquareOrder(imageSquareOrder);
		}

		imageSquares = new ArrayList<ImageSquareView>();
		for (int rowI = 0; rowI < GRID_SIZE; rowI++) {
			for (int colI = 0; colI < GRID_SIZE; colI++) {
				ImageSquareView imageSquare;
				if (imageSquareOrder == null) {
					imageSquare = squareSlicer.getImageSquare();
				} else {
					imageSquare = squareSlicer.getImageSquare();
				}
				imageSquare.coordinate = new Coordinate(colI, rowI);
				if (imageSquare.isEmpty()) {
					emptyImageSquare = imageSquare;
				}
				placeImageSquare(imageSquare);
				imageSquares.add(imageSquare);
			}
		}
	}

	public Bitmap getOriginalBitmap() {
		return this.original;
	}

	private void placeImageSquare(ImageSquareView imageSquare) {
		Rect tileRect = rectForCoordinate(imageSquare.coordinate);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				imageSquareSize, imageSquareSize);
		params.topMargin = tileRect.top;
		params.leftMargin = tileRect.left;
		addView(imageSquare, params);
		imageSquare.setOnTouchListener(this);
	}

	public boolean onTouch(View v, MotionEvent event) {
		ImageSquareView touchedTile = (ImageSquareView) v;
		if (touchedTile.isEmpty()
				|| !touchedTile.isInRowOrColumnOf(emptyImageSquare)) {
			return false;
		} else {
			if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
				// start of the gesture
				moveImageSquare = touchedTile;
				currentMotionWriters = getImageSquaresBetweenEmptySquareAndImageSquare(moveImageSquare);
				moveImageSquare.numberOfDrags = 0;
			} else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
				// during the gesture
				if (lastDragPoint != null) {
					followFinger(event);
				}
				lastDragPoint = new PointF(event.getRawX(), event.getRawY());
			} else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
				checkGameIsOver(getImageSquareOrder());
				currentMotionWriters = getImageSquaresBetweenEmptySquareAndImageSquare(moveImageSquare);
				if (lastDragMoved() || isClick()) {
					animateImageSquaresToEmptySpace();
				} else {
					animateImageSquaresBack();
				}
				currentMotionWriters = null;
				lastDragPoint = null;

				moveImageSquare = null;

			}
			return true;
		}
	}

	private boolean checkEndOfGame(LinkedList<Integer> listOrders) {
		for (int i = 0; i < listOrders.size() - 2; i++) {
			if (listOrders.get(i) != i) {
				return false;
			}
		}
		return true;
	}

	private boolean lastDragMoved() {
		if (lastDragPoint != null && currentMotionWriters != null
				&& currentMotionWriters.size() > 0) {
			GameImageSquareMotion firstMotionDescriptor = currentMotionWriters
					.get(0);
			if (firstMotionDescriptor.axialDelta > imageSquareSize / 2) {
				return true;
			}
		}
		return false;
	}

	public void setFragmentManager(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}

	@SuppressLint("NewApi")
	private void checkGameIsOver(LinkedList<Integer> list) {
		if (checkEndOfGame(list)) {
			mChronometer.stop();

			showGameResultAndAsk = new DialogEndGame(getMillis());
			showGameResultAndAsk.show(fragmentManager, "showGameResult");

			ReadWriteResults readWriteResults = new ReadWriteResults(
					getContext());
			String dateString = dateFormat.format(new Date());
			String dateGameTime = MillisecondsToMinSec
					.parseMilliSeconds(getMillis()) + " " + dateString;
			readWriteResults.addRecord(dateGameTime);
			readWriteResults.WriteRecords();
		}
	}

	public long getMillis() {
		long elapsedMillis = SystemClock.elapsedRealtime()
				- mChronometer.getBase();
		return elapsedMillis;
	}

	private boolean isClick() {
		if (lastDragPoint == null) {
			return true;
		}

		if (currentMotionWriters != null && currentMotionWriters.size() > 0
				&& moveImageSquare.numberOfDrags < 10) {
			GameImageSquareMotion firstMotionWriter = currentMotionWriters
					.get(0);

			if (firstMotionWriter.axialDelta < imageSquareSize / 20) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Follows finger while dragging all currently moved tiles. Allows movement
	 * only along x axis for row and y axis for column.
	 * 
	 * @param event
	 */
	private void followFinger(MotionEvent event) {
		boolean impossibleMove = true;
		float dxEvent = event.getRawX() - lastDragPoint.x;
		float dyEvent = event.getRawY() - lastDragPoint.y;
		ImageSquareView tile;
		moveImageSquare.numberOfDrags++;
		for (GameImageSquareMotion descriptor : currentMotionWriters) {
			tile = descriptor.imageSquare;
			Pair<Float, Float> xy = getXYFromEvent(tile, dxEvent, dyEvent,
					descriptor.direction);

			RectF candidateRect = new RectF(xy.first, xy.second, xy.first
					+ tile.getWidth(), xy.second + tile.getHeight());
			ArrayList<ImageSquareView> tilesToCheck = null;
			if (tile.coordinate.row == emptyImageSquare.coordinate.row) {
				tilesToCheck = allTilesInRow(tile.coordinate.row);
			} else if (tile.coordinate.column == emptyImageSquare.coordinate.column) {
				tilesToCheck = allImageSquaresInColumn(tile.coordinate.column);
			}

			boolean possibleCandidate = (gameboardRect.contains(candidateRect));
			boolean collides = collidesWithImageSquares(candidateRect, tile,
					tilesToCheck);

			impossibleMove = impossibleMove && (!possibleCandidate || collides);
		}
		if (!impossibleMove) {

			for (GameImageSquareMotion descriptor : currentMotionWriters) {
				tile = descriptor.imageSquare;
				Pair<Float, Float> xy = getXYFromEvent(tile, dxEvent, dyEvent,
						descriptor.direction);
				tile.setXY(xy.first, xy.second);
			}
		}
	}

	private Pair<Float, Float> getXYFromEvent(ImageSquareView tile,
			float dxEvent, float dyEvent, Direction direction) {
		float dxTile = 0, dyTile = 0;
		if (direction == Direction.X) {
			dxTile = tile.getXPos() + dxEvent;
			dyTile = tile.getYPos();
		}
		if (direction == Direction.Y) {
			dyTile = tile.getYPos() + dyEvent;
			dxTile = tile.getXPos();
		}
		return new Pair<Float, Float>(dxTile, dyTile);
	}

	private boolean collidesWithImageSquares(RectF candidateRect,
			ImageSquareView tile, ArrayList<ImageSquareView> tilesToCheck) {
		RectF otherImageSquareRect;
		for (ImageSquareView otherTile : tilesToCheck) {
			if (!otherTile.isEmpty() && otherTile != tile) {
				otherImageSquareRect = new RectF(otherTile.getXPos(),
						otherTile.getYPos(), otherTile.getXPos()
								+ otherTile.getWidth(), otherTile.getYPos()
								+ otherTile.getHeight());
				if (RectF.intersects(otherImageSquareRect, candidateRect)) {
					return true;
				}
			}
		}
		return false;
	}

	private void animateImageSquaresToEmptySpace() {
		emptyImageSquare.setXY(moveImageSquare.getXPos(),
				moveImageSquare.getYPos());
		emptyImageSquare.coordinate = moveImageSquare.coordinate;
		ObjectAnimator animator;
		for (final GameImageSquareMotion motionDescriptor : currentMotionWriters) {
			animator = ObjectAnimator.ofObject(motionDescriptor.imageSquare,
					motionDescriptor.direction.toString(),
					new FloatEvaluator(), motionDescriptor.from,
					motionDescriptor.to);
			animator.setDuration(16);
			animator.addListener(new AnimatorListener() {

				public void onAnimationStart(Animator animation) {
				}

				public void onAnimationCancel(Animator animation) {
				}

				public void onAnimationRepeat(Animator animation) {
				}

				public void onAnimationEnd(Animator animation) {
					motionDescriptor.imageSquare.coordinate = motionDescriptor.finalCoordinate;
					motionDescriptor.imageSquare.setXY(
							motionDescriptor.finalRect.left,
							motionDescriptor.finalRect.top);
				}
			});
			animator.start();
		}
	}

	private void animateImageSquaresBack() {
		ObjectAnimator animator;
		if (currentMotionWriters != null) {
			for (final GameImageSquareMotion motionWriter : currentMotionWriters) {
				animator = ObjectAnimator.ofObject(motionWriter.imageSquare,
						motionWriter.direction.toString(),
						new FloatEvaluator(), motionWriter.currentPosition(),
						motionWriter.originalPosition());
				animator.setDuration(16);
				animator.addListener(new AnimatorListener() {

					public void onAnimationStart(Animator animation) {
					}

					public void onAnimationCancel(Animator animation) {
					}

					public void onAnimationRepeat(Animator animation) {
					}

					public void onAnimationEnd(Animator animation) {
						motionWriter.imageSquare.setXY(
								motionWriter.originalRect.left,
								motionWriter.originalRect.top);
					}
				});
				animator.start();
			}
		}
	}

	private ArrayList<GameImageSquareMotion> getImageSquaresBetweenEmptySquareAndImageSquare(
			ImageSquareView imageSquare) {
		ArrayList<GameImageSquareMotion> descriptors = new ArrayList<GameImageSquareMotion>();
		Coordinate coordinate, finalCoordinate;
		ImageSquareView foundTile;
		GameImageSquareMotion motionWriter;
		Rect finalRect, currentRect;
		float axialDelta;
		if (imageSquare.isToRightOf(emptyImageSquare)) {
			// add all tiles left of the tile
			for (int i = imageSquare.coordinate.column; i > emptyImageSquare.coordinate.column; i--) {
				coordinate = new Coordinate(imageSquare.coordinate.row, i);
				foundTile = (imageSquare.coordinate.matches(coordinate)) ? imageSquare
						: getImageSquareAtCoordinate(coordinate);
				finalCoordinate = new Coordinate(imageSquare.coordinate.row,
						i - 1);
				currentRect = rectForCoordinate(foundTile.coordinate);
				finalRect = rectForCoordinate(finalCoordinate);
				axialDelta = Math.abs(foundTile.getXPos() - currentRect.left);
				motionWriter = new GameImageSquareMotion(foundTile,
						Direction.X, foundTile.getXPos(), finalRect.left);
				motionWriter.finalCoordinate = finalCoordinate;
				motionWriter.finalRect = finalRect;
				motionWriter.axialDelta = axialDelta;
				descriptors.add(motionWriter);
			}
		} else if (imageSquare.isToLeftOf(emptyImageSquare)) {
			for (int i = imageSquare.coordinate.column; i < emptyImageSquare.coordinate.column; i++) {
				coordinate = new Coordinate(imageSquare.coordinate.row, i);
				foundTile = (imageSquare.coordinate.matches(coordinate)) ? imageSquare
						: getImageSquareAtCoordinate(coordinate);
				finalCoordinate = new Coordinate(imageSquare.coordinate.row,
						i + 1);
				currentRect = rectForCoordinate(foundTile.coordinate);
				finalRect = rectForCoordinate(finalCoordinate);
				axialDelta = Math.abs(foundTile.getXPos() - currentRect.left);
				motionWriter = new GameImageSquareMotion(foundTile,
						Direction.X, foundTile.getXPos(), finalRect.left);
				motionWriter.finalCoordinate = finalCoordinate;
				motionWriter.finalRect = finalRect;
				motionWriter.axialDelta = axialDelta;
				descriptors.add(motionWriter);
			}
		} else if (imageSquare.isAbove(emptyImageSquare)) {
			for (int i = imageSquare.coordinate.row; i < emptyImageSquare.coordinate.row; i++) {
				coordinate = new Coordinate(i, imageSquare.coordinate.column);
				foundTile = (imageSquare.coordinate.matches(coordinate)) ? imageSquare
						: getImageSquareAtCoordinate(coordinate);
				finalCoordinate = new Coordinate(i + 1,
						imageSquare.coordinate.column);
				currentRect = rectForCoordinate(foundTile.coordinate);
				finalRect = rectForCoordinate(finalCoordinate);
				axialDelta = Math.abs(foundTile.getYPos() - currentRect.top);
				motionWriter = new GameImageSquareMotion(foundTile,
						Direction.Y, foundTile.getYPos(), finalRect.top);
				motionWriter.finalCoordinate = finalCoordinate;
				motionWriter.finalRect = finalRect;
				motionWriter.axialDelta = axialDelta;
				descriptors.add(motionWriter);
			}
		} else if (imageSquare.isBelow(emptyImageSquare)) {
			for (int i = imageSquare.coordinate.row; i > emptyImageSquare.coordinate.row; i--) {
				coordinate = new Coordinate(i, imageSquare.coordinate.column);
				foundTile = (imageSquare.coordinate.matches(coordinate)) ? imageSquare
						: getImageSquareAtCoordinate(coordinate);
				finalCoordinate = new Coordinate(i - 1,
						imageSquare.coordinate.column);
				currentRect = rectForCoordinate(foundTile.coordinate);
				finalRect = rectForCoordinate(finalCoordinate);
				axialDelta = Math.abs(foundTile.getYPos() - currentRect.top);
				motionWriter = new GameImageSquareMotion(foundTile,
						Direction.Y, foundTile.getYPos(), finalRect.top);
				motionWriter.finalCoordinate = finalCoordinate;
				motionWriter.finalRect = finalRect;
				motionWriter.axialDelta = axialDelta;
				descriptors.add(motionWriter);
			}
		}
		return descriptors;
	}

	private ImageSquareView getImageSquareAtCoordinate(Coordinate coordinate) {
		for (ImageSquareView tile : imageSquares) {
			if (tile.coordinate.matches(coordinate)) {
				return tile;
			}
		}
		return null;
	}

	private ArrayList<ImageSquareView> allTilesInRow(int row) {
		ArrayList<ImageSquareView> tilesInRow = new ArrayList<ImageSquareView>();
		for (ImageSquareView tile : imageSquares) {
			if (tile.coordinate.row == row) {
				tilesInRow.add(tile);
			}
		}
		return tilesInRow;
	}

	private ArrayList<ImageSquareView> allImageSquaresInColumn(int column) {
		ArrayList<ImageSquareView> tilesInColumn = new ArrayList<ImageSquareView>();
		for (ImageSquareView tile : imageSquares) {
			if (tile.coordinate.column == column) {
				tilesInColumn.add(tile);
			}
		}
		return tilesInColumn;
	}

	private Rect rectForCoordinate(Coordinate coordinate) {
		int gameboardY = (int) Math.floor(gameboardRect.top);
		int gameboardX = (int) Math.floor(gameboardRect.left);
		int top = (coordinate.row * imageSquareSize) + gameboardY;
		int left = (coordinate.column * imageSquareSize) + gameboardX;
		return new Rect(left, top, left + imageSquareSize, top
				+ imageSquareSize);
	}

	public LinkedList<Integer> getImageSquareOrder() {
		LinkedList<Integer> tileLocations = new LinkedList<Integer>();
		for (int rowI = 0; rowI < GRID_SIZE; rowI++) {
			for (int colI = 0; colI < GRID_SIZE; colI++) {
				ImageSquareView tile = getImageSquareAtCoordinate(new Coordinate(
						colI, rowI));
				tileLocations.add(tile.originalIndex);
			}
		}
		return tileLocations;
	}

	public void setImageSquareOrder(LinkedList<Integer> squares) {
		this.imageSquareOrder = squares;
	}

	public class GameImageSquareMotion {

		public Rect finalRect, originalRect;
		public Direction direction;
		public ImageSquareView imageSquare;
		public float from, to, axialDelta;
		public Coordinate finalCoordinate;

		public GameImageSquareMotion(ImageSquareView imageSquare,
				Direction direction, float from, float to) {
			super();
			this.imageSquare = imageSquare;
			this.from = from;
			this.to = to;
			this.direction = direction;
			this.originalRect = rectForCoordinate(imageSquare.coordinate);
		}

		public float currentPosition() {
			if (direction == Direction.X) {
				return imageSquare.getXPos();
			} else if (direction == Direction.Y) {
				return imageSquare.getYPos();
			}
			return 0;
		}

		public float originalPosition() {
			if (direction == Direction.X) {
				return originalRect.left;
			} else if (direction == Direction.Y) {
				return originalRect.top;
			}
			return 0;
		}

	}

}
