package me.fiveinarow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.fiveinarow.bean.Piece;

/**
 * Created by caowenhua on 2015/11/29.
 */

public class GobangView extends View {

    private float realWidth;
    private float pieceRadius;
    private RectF realRectf;

    private Paint paint;
    private OnClickChessListener onClickChessListener;

    private List<Piece> pieceList;
    private boolean isCanClick;
    private boolean isBlackChess;

    private int[][] chess;// 1 black -1 white 0 none

    public GobangView(Context context) {
        super(context);
        init();
    }

    public GobangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GobangView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);

        pieceList = new ArrayList<>();
        isCanClick = true;
        isBlackChess = true;

        chess = new int[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                chess[i][j] = 0;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(w > h){
            realRectf = new RectF((w-h)/2 + 3, 3, (w+h)/2 - 3, h - 3);
        }
        else{
            realRectf = new RectF(3, (h-w)/2 + 3, w - 3, (w+h)/2 - 3);
        }

        realWidth = realRectf.right - realRectf.left;
        pieceRadius = (realRectf.right - realRectf.left)/32 - 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.parseColor("#ff6d00"));
        canvas.drawRect(realRectf, paint);

        paint.setColor(Color.parseColor("#333333"));
        for (int i = 0; i < 15; i++) {
            canvas.drawLine(realRectf.left + realWidth/30 , realRectf.top + realWidth/30 + realWidth*i / 15 ,
                    realRectf.right - realWidth/30, realRectf.top + realWidth/30 + realWidth*i / 15, paint);
        }

        for (int i = 0; i < 15; i++) {
            canvas.drawLine(realRectf.left + realWidth/30 + realWidth*i / 15 , realRectf.top + realWidth/30,
                    realRectf.left + realWidth/30 + realWidth*i / 15 , realRectf.bottom - realWidth/30 , paint);
        }

        drawPoint(canvas);

        for (int i = 0; i < pieceList.size(); i++) {
            drawPiece(canvas, pieceList.get(i).getRow(), pieceList.get(i).getColumn(), pieceList.get(i).isBlack());
        }
    }

    private void drawPoint(Canvas canvas){
        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 3 / 15,
                realRectf.top + realWidth / 30 + realWidth * 3 / 15,
                pieceRadius / 4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 3 / 15,
                realRectf.top + realWidth / 30 + realWidth * 11 / 15,
                pieceRadius / 4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 11 / 15,
                realRectf.top + realWidth / 30 + realWidth * 3 / 15,
                pieceRadius / 4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 11 / 15,
                realRectf.top + realWidth / 30 + realWidth * 11 / 15,
                pieceRadius / 4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 7 / 15,
                realRectf.top + realWidth / 30 + realWidth * 7 / 15,
                pieceRadius / 4, paint);
    }

    private void drawPiece(Canvas canvas ,int row, int column, boolean isBlack){
        if(isBlack){
            paint.setColor(Color.BLACK);
        }
        else{
            paint.setColor(Color.WHITE);
        }
        canvas.drawCircle(realRectf.left + realWidth/30 + realWidth*column / 15,
                realRectf.top + realWidth/30 + realWidth*row / 15,
                pieceRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isCanClick){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(event.getX() >= realRectf.left && event.getX() <= realRectf.right &&
                        event.getY() >= realRectf.top && event.getY() <= realRectf.bottom){
                    int row = (int)((event.getY()-realRectf.top)/(realWidth/15));
                    int column = (int)((event.getX()-realRectf.left) / (realWidth/15));
                    boolean isAddSuccess = false;
                    if(chess[row][column] == 0){
                        isAddSuccess = true;
                    }
                    if(isAddSuccess){
                        addPiece((int)((event.getY()-realRectf.top)/(realWidth/15)),
                                (int)((event.getX()-realRectf.left) / (realWidth / 15)), isBlackChess);
                    }
                    if(onClickChessListener != null){
                        onClickChessListener.onClickChess(row, column, isAddSuccess);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void addPiece(Piece piece){
        if(piece.isBlack()){
            chess[piece.getRow()][piece.getColumn()] = 1;
        }
        else {
            chess[piece.getRow()][piece.getColumn()] = -1;
        }
        pieceList.add(piece);
        invalidate();
    }

    public void addPiece(int row, int column, boolean isBlack){
        if(isBlack){
            chess[row][column] = 1;
        }
        else {
            chess[row][column] = -1;
        }
        Piece piece = new Piece();
        piece.setColumn(column);
        piece.setIsBlack(isBlack);
        piece.setRow(row);
        pieceList.add(piece);
        invalidate();
    }

    private boolean judge(Piece piece){
        if(piece.getRow() < 4){
            //左上角
            if(piece.getColumn() < 4){
                for (int i = 0; i <= piece.getColumn(); i++) {
                    for (int j = i; j < i + 5; j++) {
                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
                for (int i = 0; i <= piece.getRow(); i++) {
                    for (int j = i; j < i + 5; j++) {
                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
                for (int i = 0; i <= ((piece.getRow() < piece.getColumn()) ? piece.getRow() : piece.getColumn()); i++) {
                    for (int j = 0; j < 5; j++) {
                        if(chess[i+j][piece.getColumn()-piece.getRow()+i+j] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
            }
            //右上角
            else if(piece.getColumn() > 10){
                for (int i = 14; i >= piece.getColumn(); i--) {
                    for (int j = i; j > i - 5 ; j--) {
                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i - 4){
                            return true;
                        }
                    }
                }
                for (int i = 0; i <= piece.getRow(); i++) {
                    for (int j = i; j < i + 5; j++) {
                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
                for (int i = 0; i < ((piece.getRow() < 14 - piece.getColumn()) ? piece.getRow() : 14 - piece.getColumn()); i++) {
                    for (int j = 0; j < 5; j++) {
                        if(chess[i+j][14 - piece.getColumn()-piece.getRow()+i+j] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
            }
            else{
                for (int i = piece.getColumn() - 4; i <= piece.getColumn(); i++) {
                    for (int j = i; j < i + 5; j++) {
                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
                for (int i = 0; i <= piece.getRow(); i++) {
                    for (int j = i; j < i + 5; j++) {
                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
                            break;
                        }
                        if(j == i + 4){
                            return true;
                        }
                    }
                }
                
            }
        }
        else if(piece.getRow() > 10){
            if(piece.getColumn() < 4){

            }
            else if(piece.getColumn() > 10){

            }
            else{

            }
        }
        else{
            if(piece.getColumn() < 4){

            }
            else if(piece.getColumn() > 10){

            }
            else{

            }
        }
        return false;
    }

    private int getIsBlack(boolean isBlack){
        if(isBlack){
            return 1;
        }
        else {
            return 0;
        }
    }

    public void restartChess(){
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                chess[i][j] = 0;
            }
        }
        pieceList.clear();
        invalidate();
    }

    public void setIsBlackChess(boolean isBlackChess) {
        this.isBlackChess = isBlackChess;
    }

    public void setIsCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    public interface OnChessWinListener{
        void onChessWin(boolean isBlackWin);
    }

    public interface OnClickChessListener{
        void onClickChess(int row, int column, boolean isAddSuccess);
    }

    public void setOnClickChessListener(OnClickChessListener onClickChessListener) {
        this.onClickChessListener = onClickChessListener;
    }
}
