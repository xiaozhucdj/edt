package com.onyx.android.sdk.ui.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.R;

/**
 * New Dialog For Onyx Apps
 * Use Params To Config Dialog Properties.
 * Params settings support builder mode,could set all properties in one line.
 * Created by solskjaer49 on 15/12/1 11:54 12:03.
 */
public class OnyxAlertDialog extends DialogFragment {
    static final String TAG = OnyxAlertDialog.class.getSimpleName();
    private RelativeLayout alertTittleBarLayout;
    private LinearLayout functionPanelLayout;
    private TextView tittleTextView, alertMessageView, pageSizeIndicator;
    private Button positiveButton, negativeButton, neutralButton;
    private View customContentView, topDividerLine, functionButtonDividerLine, bottomDivider, btnNeutralDivider;
    private Params params = new Params();
    protected DialogEventsListener eventsListener;

    public interface CustomViewAction {
        void onCreateCustomView(View customView, TextView pageIndicator);
    }

    public interface DialogButtonActionCallback {
        void onClick(View buttonView);
    }

    public interface DialogEventsListener {
        void onCancel(OnyxAlertDialog dialog, DialogInterface dialogInterface);

        void onDismiss(OnyxAlertDialog dialog, DialogInterface dialogInterface);
    }

    protected DialogButtonActionCallback buttonActionCallback;

    public void setDialogButtonActionCallback(DialogButtonActionCallback callback) {
        this.buttonActionCallback = callback;
    }

    public void setDialogEventsListener(DialogEventsListener listener) {
        this.eventsListener = listener;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        if (params.dialogHeight != ViewGroup.LayoutParams.WRAP_CONTENT
                || params.dialogWidth != ViewGroup.LayoutParams.WRAP_CONTENT) {
            getDialog().getWindow().setLayout(params.dialogWidth, params.dialogHeight);
        } else {
            getDialog().getWindow().setLayout(getDefaultWidth(params.isUsePercentageWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        getDialog().setCanceledOnTouchOutside(params.canceledOnTouchOutside);
    }

    protected int getDefaultWidth(boolean usePercentageWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return usePercentageWidth ? (dm.widthPixels * 7 / 10) :
                dm.widthPixels - getResources().getDimensionPixelSize(R.dimen.onyx_alert_dialog_width_margin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onyx_custom_alert_dialog, container, false);
        alertTittleBarLayout = (RelativeLayout) view.findViewById(R.id.dialog_tittleBar);
        tittleTextView = (TextView) alertTittleBarLayout.findViewById(R.id.textView_title);
        pageSizeIndicator = (TextView) alertTittleBarLayout.findViewById(R.id.page_size_indicator);
        alertMessageView = (TextView) view.findViewById(R.id.alert_msg_text);
        functionPanelLayout = (LinearLayout) view.findViewById(R.id.btn_function_panel);
        topDividerLine = view.findViewById(R.id.top_divider_line);
        bottomDivider = view.findViewById(R.id.bottom_divider_line);
        btnNeutralDivider = view.findViewById(R.id.button_panel_neutral_divider);
        positiveButton = (Button) view.findViewById(R.id.btn_ok);
        negativeButton = (Button) view.findViewById(R.id.btn_cancel);
        neutralButton = (Button) view.findViewById(R.id.btn_neutral);
        functionButtonDividerLine = view.findViewById(R.id.button_panel_divider);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        customByParams(view);
        return view;
    }

    private void customByParams(View parentView) {
        if (params.enableTittle) {
            alertTittleBarLayout.setVisibility(View.VISIBLE);
            tittleTextView.setText(params.tittleString);
        } else {
            topDividerLine.setVisibility(View.GONE);
            alertTittleBarLayout.setVisibility(View.GONE);
        }

        if (params.enableNegativeButton) {
            negativeButton.setVisibility(View.VISIBLE);
            if (params.negativeAction != null) {
                negativeButton.setOnClickListener(params.negativeAction);
            }
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        if (params.enablePositiveButton) {
            positiveButton.setVisibility(View.VISIBLE);
            if (params.positiveAction != null) {
                positiveButton.setOnClickListener(params.positiveAction);
            }
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        if (params.enableNeutralButton) {
            neutralButton.setVisibility(View.VISIBLE);
            btnNeutralDivider.setVisibility(View.VISIBLE);
            neutralButton.setText(params.neutralButtonText);
            if (params.neutralAction != null) {
                neutralButton.setOnClickListener(params.neutralAction);
            }
        } else {
            neutralButton.setVisibility(View.GONE);
            btnNeutralDivider.setVisibility(View.GONE);
        }

        if (!(params.enableNegativeButton && params.enablePositiveButton)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }

        if (params.enableFunctionPanel) {
            functionPanelLayout.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        } else {
            functionPanelLayout.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        pageSizeIndicator.setVisibility(params.enablePageIndicator ? View.VISIBLE : View.GONE);

        if (params.customLayoutResID != -1) {
            setCustomContentLayout(parentView, params.customLayoutResID,
                    params.customLayoutHeight, params.customLayoutWidth);
            params.customViewAction.onCreateCustomView(customContentView, pageSizeIndicator);
        } else {
            alertMessageView.setText(params.alertMsgString);
        }
    }

    private void setCustomContentLayout(View parentView, int layoutID, int layoutHeight, int layoutWidth) {
        //using custom Layout must define id at top level custom layout.
        alertMessageView.setVisibility(View.GONE);
        if (customContentView == null) {
            customContentView = getActivity().getLayoutInflater().inflate(layoutID, null);
            RelativeLayout parentLayout = (RelativeLayout) parentView;
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(layoutWidth,
                    layoutHeight);
            p.addRule(RelativeLayout.BELOW, R.id.top_divider_line);
            parentLayout.addView(customContentView, p);
            View bottomDivider = parentLayout.findViewById(R.id.bottom_divider_line);
            RelativeLayout.LayoutParams dividerParams = (RelativeLayout.LayoutParams) bottomDivider.getLayoutParams();
            dividerParams.addRule(RelativeLayout.BELOW, customContentView.getId());
            bottomDivider.setLayoutParams(dividerParams);
        }
    }

    protected void setPositiveButtonVisiable(int visiable) {
        positiveButton.setVisibility(visiable);
    }

    protected void setNegativeButtonVisiable(int visiable) {
        negativeButton.setVisibility(visiable);
    }

    protected void setNeutralButtonVisiable(int visiable) {
        neutralButton.setVisibility(visiable);
    }

    public static class Params {
        /**
         * use this class to setup dialog
         * all params have default values,just config the item which u really need is ok.
         *
         * @param enableTittle use this value to configure tittleBar visibility.
         * @param enableFunctionPanel use this value to configure FunctionPanel visibility.
         * @param enablePositiveButton use this value to configure Positive Button visibility.
         * @param enableNegativeButton use this value to configure Negative Button visibility.
         * @param enableNeutralButton use this value to configure NeutralButton Button visibility.
         * @param enablePageIndicator use this value to configure Page Indicator visibility.
         * @param canceledOnTouchOutside use this value to configure cancel this dialog when touch outside.
         * @param customLayoutResID use this value to configure Custom Layout ID.
         * @param customLayoutHeight use this value to configure Custom Layout Height.
         * @param customLayoutWidth use this value to configure Custom Layout Width.
         * @param dialogWidth use this value to configure Dialog Width.
         * @param dialogHeight use this value to configure Dialog Height.
         * @param tittleString use this value to configure Dialog tittle String.
         * @param alertMsgString use this value to configure Dialog message String.
         * @param positiveAction use this value to configure Positive Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param negativeAction use this value to configure Negative Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param neutralAction use this value to configure Neutral Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param customViewAction use this value to configure customView Action,when ur view is load,it would give u the view which u inject,
         * and the page indicator,u can setup action by findViewById in your custom view and do the custom action.
         * @param usePercentageWidth use this flag to configure use percentage width or not,
         * if not,dialog itself would use margin Left&Right in x dp,
         * which defines in values/dimens.xml/onyx_alert_dialog_width_margin.
         */
        boolean enableTittle = true;
        boolean enableFunctionPanel = true;
        boolean enablePositiveButton = true;
        boolean enableNegativeButton = true;
        boolean enableNeutralButton = false;
        boolean enablePageIndicator = false;
        boolean canceledOnTouchOutside = true;
        boolean usePercentageWidth = true;
        int customLayoutResID = -1;
        String neutralButtonText = "";

        int customLayoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int customLayoutWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        String tittleString = "";
        String alertMsgString = "";
        CustomViewAction customViewAction = new CustomViewAction() {
            @Override
            public void onCreateCustomView(View customView, TextView pageIndicator) {
                Log.i(TAG, "onCreateCustomView");
            }
        };
        View.OnClickListener positiveAction, negativeAction, neutralAction;

        public boolean isEnableTittle() {
            return enableTittle;
        }

        public Params setEnableTittle(boolean enableTittle) {
            this.enableTittle = enableTittle;
            return this;
        }

        public boolean isEnablePositiveButton() {
            return enablePositiveButton;
        }

        public Params setEnablePositiveButton(boolean enablePositiveButton) {
            this.enablePositiveButton = enablePositiveButton;
            return this;
        }

        public boolean isEnableNegativeButton() {
            return enableNegativeButton;
        }

        public Params setEnableNegativeButton(boolean enableNegativeButton) {
            this.enableNegativeButton = enableNegativeButton;
            return this;
        }

        public int getCustomLayoutResID() {
            return customLayoutResID;
        }

        public Params setCustomLayoutResID(int customLayoutResID) {
            this.customLayoutResID = customLayoutResID;
            return this;
        }

        public String getTittleString() {
            return tittleString;
        }

        public Params setTittleString(String tittleString) {
            this.tittleString = tittleString;
            return this;
        }

        public String getAlertMsgString() {
            return alertMsgString;
        }

        public Params setAlertMsgString(String alertMsgString) {
            this.alertMsgString = alertMsgString;
            return this;
        }

        public View.OnClickListener getNegativeAction() {
            return negativeAction;
        }

        public Params setNegativeAction(View.OnClickListener negativeAction) {
            this.negativeAction = negativeAction;
            return this;
        }

        public View.OnClickListener getPositiveAction() {
            return positiveAction;
        }

        public Params setPositiveAction(View.OnClickListener positiveAction) {
            this.positiveAction = positiveAction;
            return this;
        }

        public int getCustomLayoutHeight() {
            return customLayoutHeight;
        }

        public Params setCustomLayoutHeight(int customLayoutHeight) {
            this.customLayoutHeight = customLayoutHeight;
            return this;
        }

        public int getCustomLayoutWidth() {
            return customLayoutWidth;
        }

        public Params setCustomLayoutWidth(int customLayoutWidth) {
            this.customLayoutWidth = customLayoutWidth;
            return this;
        }

        public Params setCustomViewAction(CustomViewAction customViewAction) {
            this.customViewAction = customViewAction;
            return this;
        }

        public boolean isEnablePageIndicator() {
            return enablePageIndicator;
        }

        public Params setEnablePageIndicator(boolean enablePageIndicator) {
            this.enablePageIndicator = enablePageIndicator;
            return this;
        }

        public Params setDialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
            return this;
        }

        public Params setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
            return this;
        }

        public boolean isEnableFunctionPanel() {
            return enableFunctionPanel;
        }

        public Params setEnableFunctionPanel(boolean enableFunctionPanel) {
            this.enableFunctionPanel = enableFunctionPanel;
            return this;
        }

        public boolean isCanceledOnTouchOutside() {
            return canceledOnTouchOutside;
        }

        public Params setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public boolean isUsePercentageWidth() {
            return usePercentageWidth;
        }

        public Params setUsePercentageWidth(boolean usePercentageWidth) {
            this.usePercentageWidth = usePercentageWidth;
            return this;
        }

        public boolean isEnableNeutralButton() {
            return enableNeutralButton;
        }

        public Params setEnableNeutralButton(boolean enableNeutralButton) {
            this.enableNeutralButton = enableNeutralButton;
            return this;
        }

        public String getNeutralButtonText() {
            return neutralButtonText;
        }

        public Params setNeutralButtonText(String neutralButtonText) {
            this.neutralButtonText = neutralButtonText;
            return this;
        }

        public View.OnClickListener getNeutralAction() {
            return neutralAction;
        }

        public Params setNeutralAction(View.OnClickListener neutralAction) {
            this.neutralAction = neutralAction;
            return this;
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (eventsListener != null) {
            eventsListener.onCancel(OnyxAlertDialog.this, dialogInterface);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (eventsListener != null) {
            eventsListener.onDismiss(OnyxAlertDialog.this, dialogInterface);
        }
    }
}