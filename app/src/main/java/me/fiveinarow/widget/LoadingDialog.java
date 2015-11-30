package me.fiveinarow.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import me.fiveinarow.R;


/**
 * Created by caowenhua on 2015/10/12.
 */
public class LoadingDialog extends Dialog {

    private TextView tv_content;

    /**
     * 弹出一个显示一个字符串的dialog，自动调用show
     * @param context
     * @param content	显示内容
     */
    public LoadingDialog(Context context, String content) {
        super(context, R.style.IOSScaleDialog);
        init();
        tv_content.setText(content);
        show();
    }

    /**
     * 弹出一个显示一个字符串的dialog，自动调用show
     * @param context
     * @param content	显示内容
     * @param needToShow 是否需要执行show
     */
    public LoadingDialog(Context context, String content, boolean needToShow) {
        super(context, R.style.IOSScaleDialog);
        init();
        tv_content.setText(content);
        if(needToShow){
            show();
        }
    }

    private void init(){
        setContentView(R.layout.dialog_loading);
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    public void show() {
        try{
            super.show();
        }
        catch(Exception e){
        }
    }

    @Override
    public void dismiss() {
        try{
            super.dismiss();
        }
        catch(Exception e){
        }
    }

}
