package com.yougy.common.eventbus;

/**
 * EventBus 常量
 */
public class EventBusConstant {

    /**
     * eventbus
     */
    public static final String EVENT_WIIF = "event_wiif";
    public static final String EVENT_NETDIALOG_DISMISS = "event_netdialog_dismiss";
    public static final String EVENTBUS_POWER= "eventbus_power";

    public static final String EVENT_PROMOTION = "event_promotion";

    public static final String  current_text_book = "current_text_book" ;

    public static final String  all_text_book = "all_text_book";

    public static final String  current_coach_book = "current_coach_book" ;

    public static final String  all_coach_book = "all_coach_book" ;

    public static final String  current_reference_book = "current_reference_book" ;

    public static final String  current_note = "current_note";

    public static final String  all_notes = "all_notes";

    public static final String current_home_work = "current_home_work" ;

    public static final String  all_home_work = "all_home_work" ;

    public static final String  lately_answer = "lately_answer" ;

    public static final String  all_answer = "all_answer" ;

    public static final String  serch_reference = "serch_reference" ;

    public static final String  alter_note = "alter_note" ;

    public static final String  delete_note = "delete_note" ;

    public static final String DELETE_PHOTO_EVENT = "delete_photo_event";
    public static final String  add_note = "add_note";
    public static final String  need_refresh = "need_refresh";
    public static final String  answer_event = "answer_event";
    public static final String  task_event = "task_event";


    /***********************************************问答****************************************************/

    /**正在显示 问答*/
    public static final String EVENT_ANSWERING_SHOW = "event_answering_show";
    /**退出手绘模式要通知 asweractivity*/
    public static final String EVENT_ANSWERING_RESULT = "event_answering_result";

    public static final String EVENT_ANSWERING_PUASE = "event_answering_puase";
    /*****************************************************************************************************/



    /******************************************锁*********************************************************/
    /**清除activity锁*/
    public static final String EVENT_CLEAR_ACTIIVTY_ORDER = "event_clear_actiivty_order";
    /**清除学科锁*/
    public static final String EVENT_CLEAR_SUBJECT_ORDER = "event_clear_subject_order";

    /**开启activity锁*/
    public static final String EVENT_START_ACTIIVTY_ORDER = "event_start_actiivty_order";
    /**开启学科锁*/
    public static final String EVENT_START_SUBJECT_ORDER = "event_start_subject_order";



    /**EVENT_START_ACTIIVTY_ORDER 接收后返回 结果 EVENT_START_ACTIIVTY_ORDER_RESULT*/
    public static final String EVENT_START_ACTIIVTY_ORDER_RESULT = "event_start_actiivty_order_result";

    /**关掉LockerActivity 恢复手写模式*/
    public static final String EVENT_LOCKER_ACTIVITY_PUSE = "event_locker_activity_puse";

    /***************************************************************************************************/
}
