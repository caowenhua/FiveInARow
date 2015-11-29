package me.fiveinarow.bean;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class Piece {

    private int row;
    private int column;
    private boolean isBlack;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setIsBlack(boolean isBlack) {
        this.isBlack = isBlack;
    }
}
