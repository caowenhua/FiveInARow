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
        pieceRadius = (realRectf.right - realRectf.left)/32;

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
                pieceRadius/4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 3 / 15,
                realRectf.top + realWidth / 30 + realWidth * 11 / 15,
                pieceRadius/4, paint);


        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 11 / 15,
                realRectf.top + realWidth / 30 + realWidth * 3 / 15,
                pieceRadius/4, paint);


        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 11 / 15,
                realRectf.top + realWidth / 30 + realWidth * 11 / 15,
                pieceRadius/4, paint);

        canvas.drawCircle(realRectf.left + realWidth / 30 + realWidth * 7 / 15,
                realRectf.top + realWidth / 30 + realWidth * 7 / 15,
                pieceRadius/4, paint);
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
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(event.getX() >= realRectf.left && event.getX() <= realRectf.right &&
                    event.getY() >= realRectf.top && event.getY() <= realRectf.bottom){
                addPiece((int)(event.getX()-realRectf.left / 15), (int)(event.getY()-realRectf.top/15), true);
            }
        }
        return super.onTouchEvent(event);
    }

    public void addPiece(int row, int column, boolean isBlack){
        Piece piece = new Piece();
        piece.setColumn(column);
        piece.setIsBlack(isBlack);
        piece.setRow(row);
        pieceList.add(piece);
        invalidate();
    }

    public interface OnClickChessListener{
        void onClickChess(int row, int column);
    }

    public void setOnClickChessListener(OnClickChessListener onClickChessListener) {
        this.onClickChessListener = onClickChessListener;
    }
}
