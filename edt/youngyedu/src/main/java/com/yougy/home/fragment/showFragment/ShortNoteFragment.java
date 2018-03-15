package com.yougy.home.fragment.showFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Label;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView;

import java.util.concurrent.Executors;

/**
 * Created by jiangliang on 2016/7/11.
 * 便签编辑页面
 */
public class ShortNoteFragment extends BFragment implements View.OnClickListener, NoteBookView.OnDoOptListener, NoteBookView.OnUndoOptListener, TextWatcher {
    private ImageView mCancelIv;
    private ImageView mSaveIv;
    private ImageView mInputByGestureIv;
    private ImageView mInputBySoftIv;
    private ImageView mUndoIv;
    private ImageView mRedoIv;
    private EditText mEditText;
    private View mRoot;
    private NoteBookView mShortNoteBookView;
    private Context mContext;
    private Label label;
    private static final String TAG = "ShortNoteFragment";
    private String mLableText = "";
    private boolean mLableTextChange = false;
    private boolean mEnableUndoChange = false;


    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.note_layout, container, false);
        LogUtils.i("onCreateView");
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCancelIv = (ImageView) mRoot.findViewById(R.id.cancel_label);
        mSaveIv = (ImageView) mRoot.findViewById(R.id.save_label);
        mInputByGestureIv = (ImageView) mRoot.findViewById(R.id.input_gesture);
        mInputBySoftIv = (ImageView) mRoot.findViewById(R.id.input_soft);
        mUndoIv = (ImageView) mRoot.findViewById(R.id.undo);
        mRedoIv = (ImageView) mRoot.findViewById(R.id.redo);
        mEditText = (EditText) mRoot.findViewById(R.id.edit_text);
        mEditText.addTextChangedListener(this);
        mShortNoteBookView = (NoteBookView) mRoot.findViewById(R.id.booknote);
        byte[] bytes = label.getBytes();
        if (label != null && bytes != null) {
            LogUtils.e(TAG, "label bytes' size is : " + bytes.length);
            mShortNoteBookView.drawBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
        mShortNoteBookView.setReDoOptListener(this);
        mShortNoteBookView.setUndoOptListener(this);
        mUndoIv.setEnabled(false);
        mRedoIv.setEnabled(false);

        mInputByGestureIv.setEnabled(false);
        mInputBySoftIv.setEnabled(true);
        mCancelIv.setOnClickListener(this);
        mSaveIv.setOnClickListener(this);
        mSaveIv.setEnabled(true);
        mInputByGestureIv.setOnClickListener(this);
        mInputBySoftIv.setOnClickListener(this);
        mUndoIv.setOnClickListener(this);
        mRedoIv.setOnClickListener(this);
        mInputByGestureIv.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        EpdController.leaveScribbleMode(mShortNoteBookView);
        switch (v.getId()) {
            case R.id.cancel_label:
                showDialog();
                break;
            case R.id.save_label:
                save();
                break;
            case R.id.input_gesture:
                showGestureInput();
                updateIvStatus(true);
                break;
            case R.id.input_soft:
                showSoftInput();
                updateIvStatus(false);
                break;
            case R.id.undo:
                mShortNoteBookView.undo();
                break;
            case R.id.redo:
                mShortNoteBookView.redo();
                break;
        }
    }

    private void updateIvStatus(boolean flag) {
        mInputByGestureIv.setSelected(flag);
        mInputByGestureIv.setEnabled(!flag);
        mInputBySoftIv.setSelected(!flag);
        mInputBySoftIv.setEnabled(flag);
    }

    private void showGestureInput() {
        mInputByGestureIv.setEnabled(false);
        mInputBySoftIv.setEnabled(true);
        mShortNoteBookView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        popupInputMethodWindow();
    }


    private void showSoftInput() {
        mInputByGestureIv.setEnabled(true);
        mInputBySoftIv.setEnabled(false);
        mShortNoteBookView.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        if (null != label && !TextUtils.isEmpty(label.getText())) {
            mLableText = label.getText();
            mEditText.setText(label.getText());
        }
        mEditText.requestFocus();
        popupInputMethodWindow();
    }

    /**
     * 弹出虚拟键盘
     */
    private Handler handler = new Handler();
    private InputMethodManager imm;

    private void popupInputMethodWindow() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    private void save() {
        LogUtils.e(TAG, "save.................");
        if (null != addLabelListener) {
            addLabelListener.addLabel(label);
        }

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mShortNoteBookView.save(label);
            }
        });
        hideInputMethodWindow();
        hideView();
        label.setText(mEditText.getText() == null ? "" : mEditText.getText().toString());
        LogUtils.e(TAG, "save label is : " + label);

    }

    /**
     * 隐藏虚拟键盘
     */
    private void hideInputMethodWindow() {
        if (mEditText.getVisibility() == View.VISIBLE && null != imm && imm.isActive(mEditText)) {
            //因为是在fragment下，所以用了getView()获取view，也可以用findViewById（）来获取父控件
            getView().requestFocus();//强制获取焦点，不然getActivity().getCurrentFocus().getWindowToken()会报错
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            imm.restartInput(mEditText);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.give_up_paper);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mShortNoteBookView.clear();
                if (null != deleteLabelListener) {
                    deleteLabelListener.deleteLabel();
                }
                hideInputMethodWindow();
                hideView();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.e("ShortNoteFragment", "hidden is : " + hidden);
        if (!hidden && mEditText.getVisibility() == View.VISIBLE) {
            mEditText.requestFocus();
            popupInputMethodWindow();
        }
    }

    private void hideView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    @Override
    public void disenableReDoView() {
        mRedoIv.setEnabled(false);
    }

    @Override
    public void enableReDoView() {
        mRedoIv.setEnabled(true);
    }

    @Override
    public boolean isEnableRedo() {
        return mRedoIv.isEnabled();
    }

    @Override
    public void disenableUndoView() {
        mUndoIv.setEnabled(false);
    }

    @Override
    public void enableUndoView() {
        mUndoIv.setEnabled(true);
        mEnableUndoChange = true;
        setSaveImgage();

    }

    private void setSaveImgage() {
        if (mLableTextChange || mEnableUndoChange) {

            mSaveIv.setImageDrawable(UIUtils.getDrawable(R.drawable.edu_img_lable_save_select));
        } else {
            mSaveIv.setImageDrawable(UIUtils.getDrawable(R.drawable.edu_img_lable_minimize_select));
        }
    }

    @Override
    public boolean isEnableUndo() {
        return mUndoIv.isEnabled();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mLableTextChange = !mLableText.equalsIgnoreCase(s.toString().trim());
        setSaveImgage();
    }

    public interface DeleteLabelListener {
        void deleteLabel();
    }

    public interface AddLabelListener {
        void addLabel(Label label);
    }

    private DeleteLabelListener deleteLabelListener;
    private AddLabelListener addLabelListener;

    public void setAddLabelListener(AddLabelListener addLabelListener) {
        this.addLabelListener = addLabelListener;
    }

    public void setDeleteLabelListener(DeleteLabelListener deleteLabelListener) {
        this.deleteLabelListener = deleteLabelListener;
    }
}
