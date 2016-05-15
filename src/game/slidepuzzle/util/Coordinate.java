package game.slidepuzzle.util;


public class Coordinate {

	public int row;
	public int column;

	public Coordinate(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public boolean matches(Coordinate coordinate) {
		return coordinate.row == row && coordinate.column == column;
	}
	
	public boolean isAboveSquare(Coordinate coordinate) {
		return getAxisWith(coordinate) && (row < coordinate.row);
	}

	public boolean isBelowSquare(Coordinate coordinate) {
		return getAxisWith(coordinate) && (row > coordinate.row);
	}

	public boolean getAxisWith(Coordinate coordinate) {
		return (row == coordinate.row || column == coordinate.column);
	}

	public boolean isToRightOfSquare(Coordinate coordinate) {
		return getAxisWith(coordinate) && (column > coordinate.column);
	}

	public boolean isToLeftOfSquare(Coordinate coordinate) {
		return getAxisWith(coordinate) && (column < coordinate.column);
	}

	
	
}