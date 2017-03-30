package com.phy.demofrovoice.ui.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.phy.demofrovoice.R;
import com.phy.demofrovoice.modle.ChatMessage;
import com.phy.demofrovoice.ui.adapter.ChatLVAdapter;
import com.phy.demofrovoice.ui.view.AudioRecordButton;
import com.phy.demofrovoice.ui.view.DropdownListView;
import com.phy.demofrovoice.ui.view.MediaManager;
import com.phy.demofrovoice.ui.view.MyEditText;
import com.phy.demofrovoice.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChatActivity extends AppCompatActivity implements DropdownListView.OnRefreshListenerHeader{

    @BindView(R.id.img_back_chatActivity)
    ImageView imgBackChatActivity;
    @BindView(R.id.tv_personName_chatActivity)
    TextView tvPersonNameChatActivity;
    @BindView(R.id.img_personInfo_chatActivity)
    ImageView imgPersonInfoChatActivity;
    @BindView(R.id.rel_titileBar)
    RelativeLayout relTitileBar;
    @BindView(R.id.lv_Chat)
    DropdownListView lvChat;
    @BindView(R.id.img_voice)
    ImageView imgVoice;
    @BindView(R.id.img_write)
    ImageView imgWrite;
    @BindView(R.id.edinput_sms)
    MyEditText edinputSms;
    @BindView(R.id.img_faces)
    ImageView imgFaces;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.btn_sendSms)
    Button btnSendSms;
    @BindView(R.id.face_viewpager)
    ViewPager faceViewpager;
    @BindView(R.id.face_dots_container)
    LinearLayout faceDotsContainer;
    @BindView(R.id.gv_addItems_chat)
    GridView gvAddItemsChat;
    @BindView(R.id.lin_bottom)
    LinearLayout linBottom;
    @BindView(R.id.lin_root)
    LinearLayout linRoot;
    /**
     * 表情选择框的容器
     */
    private LinearLayout chat_face_container;
    /**
     * 消息类型选择框的容器
     */
    private LinearLayout chat_add_container;
    AudioRecordButton btnVoice;
    private View viewanim;

    private Unbinder unbinder;
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatLVAdapter adapter = null;
    private String reply = ""; //模拟回复
    //Activity最外层的Layout视图
    private View activityRootView;
    private int usableHeightPrevious = 0;

    private static final String TEXT = "text/utf-8";
    private static final String AUDIO =  "audio/mp3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        unbinder = ButterKnife.bind(this);
        StatusBarUtil.setColor(ChatActivity.this, getResources().getColor(R.color.colorPrimary), 0);
        init();
    }

    //初始化
    private void init() {
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
        chat_add_container = (LinearLayout) findViewById(R.id.chat_add_container);
        activityRootView = findViewById(R.id.lin_root);
        edinputSms.addTextChangedListener(textWatcher);//文本框添加监听。监听文本变化状态
        btnVoice = (AudioRecordButton) findViewById(R.id.btn_voice);
        adapter = new ChatLVAdapter(this,messages);
        lvChat.setAdapter(adapter);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("~",messages.get(position).getMsg_sender()+"~");
                if(messages.get(position).getMsg_sender().equals("1")){
                    // 播放动画
                    if (viewanim!=null) {//让第二个播放的时候第一个停止播放
                        viewanim.setBackgroundResource(R.drawable.adj);
                        viewanim=null;
                    }
                    viewanim = view.findViewById(R.id.id_recorder_anim_to);
                    viewanim.setBackgroundResource(R.drawable.play);
                    AnimationDrawable drawable = (AnimationDrawable) viewanim.getBackground();
                    drawable.start();

                    // 播放音频
                    MediaManager.playSound(messages.get(position).getFilePathString(),
                            new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    viewanim.setBackgroundResource(R.drawable.adj);

                                }
                            });
                }

            }
        });

        btnVoice.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                // TODO Auto-generated method stub
                ChatMessage recorder = new ChatMessage();
                recorder.setMsg_type(AUDIO);
                recorder.setMsg_sender(1 + "");
                recorder.setFilePathString(filePath);
                recorder.setTime(seconds);
                String date = TimeUtil.unix2Date((int) TimeUtil.getGMTUnixTimeByCalendar() + "");
                recorder.setMsg_date(date);
                messages.add(recorder);
                adapter.notifyDataSetChanged();
                lvChat.setSelection(messages.size() - 1);
            }
        });

//        contentlayout是最外层布局
        activityRootView = linRoot.getChildAt(0);
        activityRootView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }});
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @OnClick({R.id.img_back_chatActivity, R.id.img_personInfo_chatActivity, R.id.img_voice, R.id.img_write, R.id.edinput_sms, R.id.btn_voice, R.id.img_faces, R.id.img_add, R.id.btn_sendSms})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back_chatActivity:
                onBackPressed();
                break;
            case R.id.img_personInfo_chatActivity:
                break;
            case R.id.img_voice:
                edinputSms.setVisibility(View.GONE);
                btnVoice.setVisibility(View.VISIBLE);
                imgWrite.setVisibility(View.VISIBLE);
                imgVoice.setVisibility(View.GONE);
                break;
            case R.id.img_write:
                edinputSms.setVisibility(View.VISIBLE);
                btnVoice.setVisibility(View.GONE);
                imgWrite.setVisibility(View.GONE);
                imgVoice.setVisibility(View.VISIBLE);
                break;
            case R.id.edinput_sms:
                if (chat_face_container.getVisibility() == View.VISIBLE
                        || chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_voice:
                break;
            case R.id.img_faces:
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                    chat_add_container.setVisibility(View.GONE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.img_add:
                hideSoftInputView();//隐藏软键盘
                if (chat_add_container.getVisibility() == View.GONE) {
                    chat_add_container.setVisibility(View.VISIBLE);
                    chat_face_container.setVisibility(View.GONE);
                } else {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_sendSms:
                reply = edinputSms.getText().toString();
                messages.add(getChatMsgTo(reply));
                adapter.notifyDataSetChanged();
                lvChat.setSelection(messages.size() - 1);//定位到当前消息
                //发送完成重置输入框
                edinputSms.setText("");
                break;
        }
    }

    /**
     * 发送的信息-接收到用户输入的信息，包装成ChatMessage对象，封装入消息发送者（1），和消息发送时间
     *
     * @param input
     * @return
     */
    private ChatMessage getChatMsgTo(String input) {
        ChatMessage msg = new ChatMessage();
        msg.setMsg_type(TEXT);
        msg.setMsg_content(input);
        msg.setMsg_sender(1 + "");
        String myPhone = "";
        String selectTag = "";
        msg.setSelectTag(selectTag);
        String date = TimeUtil.unix2Date((int) TimeUtil.getGMTUnixTimeByCalendar() + "");
        msg.setMsg_date(date);
        return msg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        unbinder.unbind();
    }
    //根据高度不同判断当前软键盘是否弹出
    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = activityRootView.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 键盘弹出
                lvChat.setSelection(messages.size() - 1);//定位到当前消息
            } else {
                // 键盘收起
                lvChat.setSelection(messages.size() - 1);//定位到当前消息
            }
            activityRootView.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }
    //计算高度
    private int computeUsableHeight() {
        Rect r = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    /**
     * 文本输入框的监听器 - 监听文本框的变化
     */
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            imgAdd.setVisibility(View.VISIBLE);
            btnSendSms.setVisibility(View.GONE);
            String str = edinputSms.getText().toString();//得到输入框改变后的文本
            if (TextUtils.isEmpty(edinputSms.getText().toString())) {//文本为空，显示添加图标
                imgAdd.setVisibility(View.VISIBLE);
                btnSendSms.setVisibility(View.GONE);
            } else {//文本不为空，显示发送按钮
                imgAdd.setVisibility(View.GONE);
                btnSendSms.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {


        }
    };

    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
