package me.fiveinarow.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import me.fiveinarow.R;


/**
 * Created by caowenhua on 2015/11/26.
 */

/**
 * title                        标题，默认""
 * contentText                  内容，默认""
 * leftButtonText               左按钮文字，默认"取消"
 * rightButtonText              右按钮文字，默认"确定"
 * isNeedToShow                 是否需要立即执行show()，默认false
 * isCancelable                 是否不可关闭，默认true
 * isCanceledOnTouchOutside     是否点击dialog外关闭，默认false
 * leftClickListener            左按钮点击监听器，默认null
 * rightClickListener           右按钮点击监听器，默认null
 * isLeftDismissAfterClick      是否点击左按钮后dismiss，默认true
 * isRightDismissAfterClick     是否点击右按钮后dismiss，默认true
 * themeId                      theme id，默认为 R.style.IOSScaleDialog
 */
public class TwoButtonDialog extends Dialog implements View.OnClickListener {

    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_left;
    private TextView tv_right;
    private OnDialogButtonClickListener leftListener;
    private OnDialogButtonClickListener rightListener;
    private boolean isLeftDismissAfterClick;
    private boolean isRightDismissAfterClick;

    private TwoButtonDialog(Builder builder) {
        super(builder.context, builder.themeId);
        setContentView(R.layout.dialog_two_button);

        initView();

        leftListener = builder.leftClickListener;
        rightListener = builder.rightClickListener;

        if(builder.title == null || builder.title.equals("")){
            tv_title.setVisibility(View.GONE);
        }
        else{
            tv_title.setText(builder.title == null ? "" : builder.title);
        }

        if(builder.contentText == null || builder.contentText.equals("")){
            if(!(builder.title == null || builder.title.equals(""))){
                tv_content.setVisibility(View.GONE);
            }
        }
        else{
            tv_content.setText(builder.contentText == null ? "" : builder.contentText);
        }

        tv_left.setText(builder.leftButtonText == null ? "" : builder.leftButtonText);
        tv_right.setText(builder.rightButtonText == null ? "" : builder.rightButtonText);
        setCancelable(builder.isCancelable);
        setCanceledOnTouchOutside(builder.isCanceledOnTouchOutside);
        isLeftDismissAfterClick = builder.isLeftDismissAfterClick;
        isRightDismissAfterClick = builder.isRightDismissAfterClick;

        if(builder.isNeedToShow){
            show();
        }
    }

    private void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);

        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == tv_left){
            if(leftListener != null){
                leftListener.onDialogButtonClick(v);
            }
            if(isLeftDismissAfterClick){
                dismiss();
            }
        }
        else if(v == tv_right){
            if(rightListener != null){
                rightListener.onDialogButtonClick(v);
            }
            if(isRightDismissAfterClick){
                dismiss();
            }
        }
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

    /**
     * 用于构建TwoButtonDialog的Builder类
     */
    public static class Builder{
        private Context context;
        private CharSequence title = "";
        private CharSequence contentText = "";
        private CharSequence leftButtonText = "取消";
        private CharSequence rightButtonText = "确定";
        private OnDialogButtonClickListener leftClickListener = null;
        private OnDialogButtonClickListener rightClickListener = null;
        private boolean isNeedToShow = false;
        private boolean isCanceledOnTouchOutside = false;
        private boolean isCancelable = true;
        private boolean isLeftDismissAfterClick = true;
        private boolean isRightDismissAfterClick = true;
        private int themeId = R.style.IOSScaleDialog;

        public Builder(Context context){
            this.context = context;
//            if(context == null){
//                throw new Exception("dialog's context is unallowed to null !!!!");
//            }
            title = "";
            contentText = "";
            leftButtonText = "取消";
            rightButtonText = "确定";
            isNeedToShow = false;
            isCancelable = true;
            isCanceledOnTouchOutside = false;
            leftClickListener = null;
            rightClickListener = null;
            isLeftDismissAfterClick = true;
            isRightDismissAfterClick = true;
            themeId = R.style.IOSScaleDialog;

        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setContentText(CharSequence contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setLeftButtonText(CharSequence leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public Builder setRightButtonText(CharSequence rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public Builder setLeftClickListener(OnDialogButtonClickListener leftClickListener) {
            this.leftClickListener = leftClickListener;
            return this;
        }

        public Builder setRightClickListener(OnDialogButtonClickListener rightClickListener) {
            this.rightClickListener = rightClickListener;
            return this;
        }

        public Builder setIsNeedToShow(boolean isNeedToShow) {
            this.isNeedToShow = isNeedToShow;
            return this;
        }

        public Builder setIsCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
            this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
            return this;
        }

        public Builder setIsCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setThemeId(int themeId) {
            this.themeId = themeId;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setIsLeftDismissAfterClick(boolean isLeftDismissAfterClick) {
            this.isLeftDismissAfterClick = isLeftDismissAfterClick;
            return this;
        }

        public Builder setIsRightDismissAfterClick(boolean isRightDismissAfterClick) {
            this.isRightDismissAfterClick = isRightDismissAfterClick;
            return this;
        }

        public TwoButtonDialog build(){
            return new TwoButtonDialog(this);
        }

        public Builder cloneBuilder(Builder builder){
            title = builder.title;
            contentText = builder.contentText;
            leftButtonText = builder.leftButtonText;
            rightButtonText = builder.rightButtonText;
            isNeedToShow = builder.isNeedToShow;
            isCancelable = builder.isCancelable;
            isCanceledOnTouchOutside = builder.isCanceledOnTouchOutside;
            leftClickListener = builder.leftClickListener;
            rightClickListener = builder.rightClickListener;
            isLeftDismissAfterClick = builder.isLeftDismissAfterClick;
            isRightDismissAfterClick = builder.isRightDismissAfterClick;
            themeId = builder.themeId;
            return this;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            stringBuilder.append("title:" + title);
            stringBuilder.append(", contentText:" + contentText);
            stringBuilder.append(", leftButtonText:" + leftButtonText);
            stringBuilder.append(", rightButtonText:" + rightButtonText);
            stringBuilder.append(", isNeedToShow:" + isNeedToShow);
            stringBuilder.append(", isCancelable:" + isCancelable);
            stringBuilder.append(", isCanceledOnTouchOutside:" + isCanceledOnTouchOutside);
            stringBuilder.append(", isLeftDismissAfterClick:" + isLeftDismissAfterClick);
            stringBuilder.append(", isRightDismissAfterClick:" + isRightDismissAfterClick);
            stringBuilder.append(", themeId:" + themeId);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    public interface OnDialogButtonClickListener {
        void onDialogButtonClick(View view);
    }
}
