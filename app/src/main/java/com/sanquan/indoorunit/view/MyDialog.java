package com.sanquan.indoorunit.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.sanquan.indoorunit.R;


public class MyDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "MyDialog";

    private  int layout_id;

    private EditText ed_phoneNumber;
    private TextView tv_name;
    private TextView tvUserId;
    private TextView tvBuildingInfo;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " );
        layout_id = getArguments().getInt("layout_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(layout_id,null);
        Log.e(TAG, "onCreateView: ");
        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_bg));

        return view;

    }

    public void setPhoneInfo(String phoneNum,String userName){
        if (ed_phoneNumber != null && tv_name != null) {
            ed_phoneNumber.setText(phoneNum);
            tv_name.setText(userName);
            Log.e(TAG, "setPhoneInfo: phone: "+phoneNum+",userName: "+userName );
        }
    }
    private OnDialogItemClickListener onDialogItemClickListener;
    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener){
        this.onDialogItemClickListener = onDialogItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onDialogItemClickListener != null){
            onDialogItemClickListener.onDialogItemClickListener(v.getId());
        }
    }

    public interface OnDialogItemClickListener{
        void onDialogItemClickListener(int id);
    }
    public static MyDialog newInstance(int num) {
        Log.e(TAG, "newInstance: " );
        MyDialog f = new MyDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("layout_id", num);
        f.setArguments(args);
        return f;
    }
}
