package me.fiveinarow.bean;

import java.io.Serializable;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class Piece implements Serializable{

    private int row;
    private int column;
    private boolean isBlack;

    public Piece() {
    }

    public Piece(String string) {
        String[] s = string.split(",");
        row = Integer.valueOf(s[0].substring(s[0].charAt(':')+1));
        column = Integer.valueOf(s[1].substring(s[1].charAt(':')+1));
        isBlack = Boolean.valueOf(s[2].substring(s[2].charAt(':')+1));
    }

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

    @Override
    public String toString() {
        return "row:"+row+",column:"+column+",isBlack"+isBlack;
    }


}
