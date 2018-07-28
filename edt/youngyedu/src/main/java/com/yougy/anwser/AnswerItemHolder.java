package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yougy.common.utils.LogUtils;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangliang on 2018-7-28.
 */

public class AnswerItemHolder extends RecyclerView.ViewHolder {
    private ItemAnswerChooseGridviewBinding itemBinding;
    private List<Content_new> textReplyList;
    AnswerItemHolder(View itemView,List<Content_new> textReplyList) {
        super(itemView);
        itemBinding = DataBindingUtil.bind(itemView);
        this.textReplyList = textReplyList;
    }

    public AnswerItemHolder setAnswer(ParsedQuestionItem.Answer answer) {
        if (answer instanceof ParsedQuestionItem.TextAnswer) {
            itemBinding.textview.setText(((ParsedQuestionItem.TextAnswer) answer).text);
            //选择题选择的结果
            ArrayList<String> checkedAnswerList = new ArrayList<String>();
            for (int i = 0; i < textReplyList.size(); i++) {
                String replyResult = textReplyList.get(i).getValue();
                checkedAnswerList.add(replyResult);
                LogUtils.e(getClass().getName(),"replyResult is : " + replyResult);
            }
            if (ListUtil.conditionalContains(checkedAnswerList, nodeInList -> nodeInList.equals(((ParsedQuestionItem.TextAnswer) answer).text))) {
                LogUtils.e(getClass().getName(),"select true ............ ");
                itemBinding.checkbox.setSelected(true);
                itemBinding.textview.setSelected(true);
            } else {
                LogUtils.e(getClass().getName(),"select false ............ ");
                itemBinding.textview.setSelected(false);
                itemBinding.checkbox.setSelected(false);
            }
        } else {
            itemBinding.textview.setText("格式错误");
            itemBinding.checkbox.setSelected(false);
        }
        return this;
    }

    public void setChooeseStyle(int size) {
        int rid;
        switch (size) {
            case 2:
                rid = R.drawable.btn_check_liangdaan;
                break;
            case 3:
                rid = R.drawable.btn_check_sandaan;
                break;
            case 4:
                rid = R.drawable.btn_check_sidaan;
                break;
            case 5:
                rid = R.drawable.btn_check_wudaan;
                break;
            default:
                rid = R.drawable.btn_check_liudaan;
                break;
        }
        itemBinding.checkbox.setBackgroundResource(rid);
    }
}