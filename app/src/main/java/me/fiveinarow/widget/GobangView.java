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
    private OnChessClickListener onClickChessListener;
    private OnChessWinListener onChessWinListener;
    private OnChessRollbackListener onChessRollbackListener;

    private List<Piece> pieceList;
    private boolean isCanClick;
    private boolean isBlackChess;

    private int[][] chess;// 1 black 2 white 0 none

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
                        onClickChessListener.onChessClick(row, column, isAddSuccess);
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
            chess[piece.getRow()][piece.getColumn()] = 2;
        }
        pieceList.add(piece);
        invalidate();

        if(isWin(chess, getIsBlack(piece.isBlack()))){
            setIsCanClick(false);
            if(onChessWinListener != null){
                onChessWinListener.onChessWin(piece.isBlack());
            }
        }
    }

    public void addPiece(int row, int column, boolean isBlack){
        if(isBlack){
            chess[row][column] = 1;
        }
        else {
            chess[row][column] = 2;
        }
        Piece piece = new Piece();
        piece.setColumn(column);
        piece.setIsBlack(isBlack);
        piece.setRow(row);
        pieceList.add(piece);
        invalidate();

        if(isWin(chess, getIsBlack(piece.isBlack()))){
            setIsCanClick(false);
            if(onChessWinListener != null){
                onChessWinListener.onChessWin(piece.isBlack());
            }
        }
    }

    public void rollback(){
        if(pieceList.size() == 0){
            if(onChessRollbackListener != null){
                onChessRollbackListener.onChessRollbackFailed("没有可后退的步数了");
            }
        }
        else{
            Piece piece = pieceList.get(pieceList.size() - 1);
            pieceList.remove(pieceList.size() - 1);
            chess[piece.getRow()][piece.getColumn()] = 0;
            if(onChessRollbackListener != null){
                onChessRollbackListener.onChessRollbackSuccess(piece.getRow(), piece.getColumn(), piece.isBlack());
            }
            invalidate();
        }
    }

//    private boolean judge(Piece piece){
//        if(piece.getRow() < 4){
//            //左上角
//            if(piece.getColumn() < 4){
//                for (int i = 0; i <= piece.getColumn(); i++) {
//                    for (int j = i; j < i + 5; j++) {
//                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//                for (int i = 0; i <= piece.getRow(); i++) {
//                    for (int j = i; j < i + 5; j++) {
//                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//                for (int i = 0; i <= ((piece.getRow() < piece.getColumn()) ? piece.getRow() : piece.getColumn()); i++) {
//                    for (int j = 0; j < 5; j++) {
//                        if(chess[i+j][piece.getColumn()-piece.getRow()+i+j] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//            }
//            //右上角
//            else if(piece.getColumn() > 10){
//                for (int i = 14; i >= piece.getColumn(); i--) {
//                    for (int j = i; j > i - 5 ; j--) {
//                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i - 4){
//                            return true;
//                        }
//                    }
//                }
//                for (int i = 0; i <= piece.getRow(); i++) {
//                    for (int j = i; j < i + 5; j++) {
//                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//                for (int i = 0; i < ((piece.getRow() < 14 - piece.getColumn()) ? piece.getRow() : 14 - piece.getColumn()); i++) {
//                    for (int j = 0; j < 5; j++) {
//                        if(chess[i+j][14 - piece.getColumn()-piece.getRow()+i+j] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//            }
//            else{
//                for (int i = piece.getColumn() - 4; i <= piece.getColumn(); i++) {
//                    for (int j = i; j < i + 5; j++) {
//                        if(chess[piece.getRow()][j] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//                for (int i = 0; i <= piece.getRow(); i++) {
//                    for (int j = i; j < i + 5; j++) {
//                        if(chess[j][piece.getColumn()] != getIsBlack(piece.isBlack())){
//                            break;
//                        }
//                        if(j == i + 4){
//                            return true;
//                        }
//                    }
//                }
//
//            }
//        }
//        else if(piece.getRow() > 10){
//            if(piece.getColumn() < 4){
//
//            }
//            else if(piece.getColumn() > 10){
//
//            }
//            else{
//
//            }
//        }
//        else{
//            if(piece.getColumn() < 4){
//
//            }
//            else if(piece.getColumn() > 10){
//
//            }
//            else{
//
//            }
//        }
//        return false;
//    }

    private int getIsBlack(boolean isBlack){
        if(isBlack){
            return 1;
        }
        else {
            return 2;
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

    private boolean isWin(int[][] qipan,int color){
        boolean colsWcoln = false;
        int colors = (int) Math.pow(color, 5);
        for(int row = 0;row<15;row++){
            for(int col=0;col<15;col++){
                //第一种
                if(row<=10 && col<4){
                //int x = qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]//→
                    int x = qipan[col][row]*qipan[col+1][row]*qipan[col+2][row]*qipan[col+3][row]*qipan[col+4][row];//→
                    int y = qipan[col][row]*qipan[col+1][row+1]*qipan[col+2][row+2]*qipan[col+3][row+3]*qipan[col+4][row+4]; //→
                    int z = qipan[col][row]*qipan[col][row+1]*qipan[col][row+2]*qipan[col][row+3]*qipan[col][row+4]; //→
                    if(x == colors || y == colors || z == colors){
                        colsWcoln = true;
                    }
                }
                //第二种
                if(row<=10 && col>=4 && col<=10 ){
                    int x = qipan[col][row]*qipan[col+1][row]*qipan[col+2][row]*qipan[col+3][row]*qipan[col+4][row];//→
                    int y = qipan[col][row]*qipan[col+1][row+1]*qipan[col+2][row+2]*qipan[col+3][row+3]*qipan[col+4][row+4]; //→
                    int z = qipan[col][row]*qipan[col][row+1]*qipan[col][row+2]*qipan[col][row+3]*qipan[col][row+4]; //→
                    int m = qipan[col][row]*qipan[col-1][row]*qipan[col-2][row]*qipan[col-3][row]*qipan[col-4][row];//→
                    int n = qipan[col][row]*qipan[col-1][row+1]*qipan[col-2][row+2]*qipan[col-3][row+3]*qipan[col-4][row+4];//→
                    if(x == colors || y == colors || z == colors || m == colors || n == colors){
                        colsWcoln = true;
                    }
                }
                //第三种
                if(row<=10 && col>10 ){
                    int z = qipan[col][row]*qipan[col][row+1]*qipan[col][row+2]*qipan[col][row+3]*qipan[col][row+4]; //→
                    int m = qipan[col][row]*qipan[col-1][row]*qipan[col-2][row]*qipan[col-3][row]*qipan[col-4][row];//→
                    int n = qipan[col][row]*qipan[col-1][row+1]*qipan[col-2][row+2]*qipan[col-3][row+3]*qipan[col-4][row+4];//→
                    if( z == colors || m == colors || n==colors){
                        colsWcoln = true;
                    }
                }
                //第四种
                if(row>10 && col<4){
                    int x = qipan[col][row]*qipan[col+1][row]*qipan[col+2][row]*qipan[col+3][row]*qipan[col+4][row];//→
                    if(x == colors){
                        colsWcoln = true;
                    }
                }
                //第五种
                if(row>10 && col>=4 && col<=10){
                    int x = qipan[col][row]*qipan[col+1][row]*qipan[col+2][row]*qipan[col+3][row]*qipan[col+4][row];//→
                    int m = qipan[col][row]*qipan[col-1][row]*qipan[col-2][row]*qipan[col-3][row]*qipan[col-4][row];//→
                    if(x == colors || m == colors)
                    {
                        colsWcoln = true;
                    }
                }
                //第六种
                if(row>10 && col>10)
                {
                    int m = qipan[col][row]*qipan[col-1][row]*qipan[col-2][row]*qipan[col-3][row]*qipan[col-4][row];//→
                    if(m == colors ){
                        colsWcoln = true;
                    }
                }
            }
        }
        return colsWcoln;
    }

    public void setIsBlackChess(boolean isBlackChess) {
        this.isBlackChess = isBlackChess;
    }

    public void setIsCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    public interface OnChessRollbackListener{
        void onChessRollbackSuccess(int row, int column, boolean isBlack);
        void onChessRollbackFailed(String reason);
    }

    public interface OnChessWinListener{
        void onChessWin(boolean isBlackWin);
    }

    public interface OnChessClickListener{
        void onChessClick(int row, int column, boolean isAddSuccess);
    }

    public void setOnChessClickListener(OnChessClickListener onClickChessListener) {
        this.onClickChessListener = onClickChessListener;
    }

    public void setOnChessWinListener(OnChessWinListener onChessWinListener) {
        this.onChessWinListener = onChessWinListener;
    }

    public void setOnChessRollbackListener(OnChessRollbackListener onChessRollbackListener) {
        this.onChessRollbackListener = onChessRollbackListener;
    }
}
