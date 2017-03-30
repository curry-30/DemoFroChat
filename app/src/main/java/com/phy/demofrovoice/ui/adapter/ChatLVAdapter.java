package com.phy.demofrovoice.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.phy.demofrovoice.R;
import com.phy.demofrovoice.gif.AnimatedGifDrawable;
import com.phy.demofrovoice.gif.AnimatedImageSpan;
import com.phy.demofrovoice.modle.ChatMessage;
import com.phy.demofrovoice.ui.view.CircleImageView;
import com.phy.demofrovoice.ui.view.MediaManager;
import com.phy.demofrovoice.utils.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class ChatLVAdapter extends BaseAdapter {
	private Context mContext;
	private List<ChatMessage> list;
	/** 弹出的更多选择框 */
	private PopupWindow popupWindow;

	/** 复制，删除 */
	private TextView copy, delete;

	private LayoutInflater inflater;
	/**
	 * 执行动画的时间
	 */
	protected long mAnimationTime = 150;

	private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
	private int mMaxItemWith;

	private static final int TEXT = 0;
	private static final int AUDIO = 1;

	public ChatLVAdapter(Context mContext, List<ChatMessage> list) {
		super();
		this.mContext = mContext;
		this.list = list;
		inflater = LayoutInflater.from(mContext);
		initPopWindow();
//		LogUtil.i("ChatLVAdapter的数据~"+list.toString());
		// 获取系统宽度
		WindowManager wManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wManager.getDefaultDisplay().getMetrics(outMetrics);
		mMaxItemWith = (int) (outMetrics.widthPixels * 0.7f);
		mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
	}

	public void setList(List<ChatMessage> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//初始化每个holder
		ViewHodlerForText  holder1 = null;
		ViewHodlerForAudio holder2 = null;

		int type = getItemViewType(position);

		if (convertView == null) {
			switch (type) {
				case TEXT:
					holder1 = new ViewHodlerForText();
					convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_lv_item, null);
					holder1.fromContainer = (ViewGroup) convertView.findViewById(R.id.chart_from_container);
					holder1.toContainer = (ViewGroup) convertView.findViewById(R.id.chart_to_container);
					holder1.fromContent = (TextView) convertView.findViewById(R.id.chatfrom_content);
					holder1.toContent = (TextView) convertView.findViewById(R.id.chatto_content);
					holder1.time = (TextView) convertView.findViewById(R.id.chat_time);
					holder1.fromIcon = (CircleImageView) convertView.findViewById(R.id.chatfrom_icon);
					holder1.toIcon = (CircleImageView) convertView.findViewById(R.id.chatto_icon);
					convertView.setTag(holder1);
					break;
				case AUDIO:
					convertView = inflater.inflate(R.layout.audio_lv_item, null, false);
					holder2 = new ViewHodlerForAudio();
					holder2.recorder_length_from = (FrameLayout) convertView.findViewById(R.id.recorder_length_from);
					holder2.recorder_length_to = (FrameLayout) convertView.findViewById(R.id.recorder_length_to);
					holder2.chat_from = (RelativeLayout) convertView.findViewById(R.id.chat_from_container);
					holder2.chat_to = (RelativeLayout) convertView.findViewById(R.id.chat_to_container);
					holder2.id_recorder_anim_from = convertView.findViewById(R.id.id_recorder_anim_from);
					holder2.id_recorder_anim_to = convertView.findViewById(R.id.id_recorder_anim_to);
					holder2.item_icon_from = (CircleImageView) convertView.findViewById(R.id.item_icon_from);
					holder2.item_icon_to = (CircleImageView) convertView.findViewById(R.id.item_icon_to);
					holder2.recorder_time_from = (TextView) convertView.findViewById(R.id.recorder_time_from);
					holder2.recorder_time_to = (TextView) convertView.findViewById(R.id.recorder_time_to);
					holder2.time = (TextView) convertView.findViewById(R.id.chat_time);
					convertView.setTag(holder2);
					break;
				default:
					break;
			}
		} else {
			switch (type) {
				case TEXT:
					holder1 = (ViewHodlerForText) convertView.getTag();
					break;
				case AUDIO:
					holder2 = (ViewHodlerForAudio) convertView.getTag();
					break;
			}
		}

         ChatMessage chatMessage = list.get(position);
		//当前item收到消息携带的时间
		String time = list.get(position).getMsg_date();
		//当年起始时间戳
		long timeYear = TimeUtil.getCurrentYearStartTime();
		//凌晨时间戳
		long timeZero = TimeUtil.getStartTime();
		//当前item消息时间的时间戳
		long timeCurr = TimeUtil.getTimeStamp(list.get(position).getMsg_date());

		//为布局设置数据
		switch (type) {

			case TEXT:

				if (list.get(position).getMsg_sender().equals(0+"")) {
					// 收到消息 from显示
					holder1.toContainer.setVisibility(View.GONE);
					holder1.fromContainer.setVisibility(View.VISIBLE);
					// 对内容做处理
					SpannableStringBuilder sb = handler(holder1.fromContent, list.get(position).getMsg_content());
					holder1.fromContent.setText(sb);
					//设置时间
					setTime(timeCurr, timeZero, timeYear,time, holder1.time, position);
				} else {
					// 发送消息 to显示
					holder1.toContainer.setVisibility(View.VISIBLE);
					holder1.fromContainer.setVisibility(View.GONE);
					// 对内容做处理
					SpannableStringBuilder sb = handler(holder1.toContent, list.get(position).getMsg_content());
					holder1.toContent.setText(sb);
					//设置时间
					setTime(timeCurr, timeZero, timeYear, time, holder1.time, position);
				}
//		hodler.fromContent.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		hodler.toContent.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});

				// 设置长按效果
				holder1.fromContent.setOnLongClickListener(new popAction(convertView, position, list.get(position).getMsg_sender()));
				holder1.toContent.setOnLongClickListener(new popAction(convertView, position, list.get(position).getMsg_sender()));
				break;
			case AUDIO:

				if (list.get(position).getMsg_sender().equals(0+"")) {
					// 收到消息 from显示
					holder2.chat_to.setVisibility(View.GONE);
					holder2.chat_from.setVisibility(View.VISIBLE);
					holder2.recorder_time_from.setText(Math.round(chatMessage.getTime())+"\"");
					ViewGroup.LayoutParams lParams=holder2.recorder_length_from.getLayoutParams();
					lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*chatMessage.getTime());
					holder2.recorder_length_from.setLayoutParams(lParams);
					//设置时间
					setTime(timeCurr, timeZero, timeYear,time, holder2.time, position);
				} else {
					// 发送消息 to显示
					holder2.chat_to.setVisibility(View.VISIBLE);
					holder2.chat_from.setVisibility(View.GONE);
					holder2.recorder_time_to.setText(Math.round(chatMessage.getTime())+"\"");
					ViewGroup.LayoutParams lParams=holder2.recorder_length_to.getLayoutParams();
					lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*chatMessage.getTime());
					holder2.recorder_length_to.setLayoutParams(lParams);
					//设置时间
					setTime(timeCurr, timeZero, timeYear, time, holder2.time, position);
				}
//		hodler.fromContent.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		hodler.toContent.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});

				// 设置长按效果
				break;
		}

		return convertView;
	}



	//设置时间
	private void setTime(long timeCurr, long timeZero, long timeYear, String time, TextView tv, int position){
		//去年的消息
		if(timeCurr < timeYear){
			clearRepetTime( timeCurr, tv, position);
			tv.setText(time.substring(0,16));
			//当前时间小于当天凌晨 - 今天前的消息（展示全格式）
		} else if(timeCurr < timeZero){
//				LogUtil.i("timeCurr ~ timeZero~"+timeCurr+"~"+timeZero);
			clearRepetTime( timeCurr, tv, position);
			tv.setText(time.substring(5,16));
		} else {//今天的消息 - （只展示时分）
			clearRepetTime( timeCurr, tv, position);
			tv.setText(time.substring(11,16));
		}

	}

	//去除间隔过近的消息时间（间隔5分钟内，消息只显示一个时间）
	private void clearRepetTime(long timeCurr, TextView tv, int position){
		if(position > 0) {
			//上一条消息获得时间
			long timeBefore = TimeUtil.getTimeStamp(list.get(position - 1).getMsg_date());
			//时间跨度过大就显示时间,反之隐藏时间控件
			if(timeCurr - timeBefore > 300){
				tv.setVisibility(View.VISIBLE);
			}else {
				tv.setVisibility(View.GONE);
			}
		}else {
			tv.setVisibility(View.VISIBLE);
		}
	}

	private SpannableStringBuilder handler(final TextView gifTextView, String content) {
		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			String tempText = m.group();
			try {
				String num = tempText.substring("#[face/png/f_static_".length(), tempText.length()- ".png]#".length());
				String gif = "face/gif/f" + num + ".gif";
				/**
				 * 如果open这里不抛异常说明存在gif，则显示对应的gif
				 * 否则说明gif找不到，则显示png
				 * */
				InputStream is = mContext.getAssets().open(gif);
				sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,new AnimatedGifDrawable.UpdateListener() {
							@Override
							public void update() {
								gifTextView.postInvalidate();
							}
						})), m.start(), m.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				is.close();
			} catch (Exception e) {
				String png = tempText.substring("#[".length(),tempText.length() - "]#".length());
				try {
					sb.setSpan(new ImageSpan(mContext, BitmapFactory.decodeStream(mContext.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return sb;
	}

	class ViewHodlerForText {
		CircleImageView fromIcon, toIcon;
		TextView fromContent, toContent, time;
		ViewGroup fromContainer, toContainer;
	}

	class ViewHodlerForAudio {
		RelativeLayout chat_from,chat_to;
		FrameLayout recorder_length_from,recorder_length_to;
		CircleImageView item_icon_from, item_icon_to;
		TextView recorder_time_from, recorder_time_to, time;
		View id_recorder_anim_from, id_recorder_anim_to;
	}

	/**
	 * 屏蔽listitem的所有事件
	 * */
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	/**
	 * 初始化弹出的pop
	 * */
	private void initPopWindow() {
		View popView = inflater.inflate(R.layout.chat_item_copy_delete_menu,
				null);
		copy = (TextView) popView.findViewById(R.id.chat_copy_menu);
		delete = (TextView) popView.findViewById(R.id.chat_delete_menu);
		popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0));
		// 设置popwindow出现和消失动画
		// popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
	}

	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent, int x, int y, final View view,
                        final int position, final String fromOrTo) {
		// 设置popwindow显示位置
		popupWindow.showAtLocation(parent, 0, x, y);
		// 获取popwindow焦点
		popupWindow.setFocusable(true);
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		// 为按钮绑定事件
		// 复制
		copy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				// 获取剪贴板管理服务
				ClipboardManager cm = (ClipboardManager) mContext
						.getSystemService(Context.CLIPBOARD_SERVICE);
				// 将文本数据复制到剪贴板
				cm.setText(list.get(position).getMsg_content()
				);
			}
		});
		// 删除
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				if (fromOrTo == "0") {
					// from
					leftRemoveAnimation(view, position);
				} else if (fromOrTo == "1") {
					// to
					rightRemoveAnimation(view, position);
				}
				 //删除该条聊天
//				 LocalChatManager.getInstance().deleteMessage(list.get(position));
				 list.remove(position);
				 notifyDataSetChanged();
			}
		});
		popupWindow.update();
		if (popupWindow.isShowing()) {

		}
	}

	/**
	 * 每个ITEM中more按钮对应的点击动作
	 * */
	public class popAction implements OnLongClickListener {
		int position;
		View view;
		String fromOrTo;

		public popAction(View view, int position, String fromOrTo) {
			this.position = position;
			this.view = view;
			this.fromOrTo = fromOrTo;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int[] arrayOfInt = new int[2];
			// 获取点击按钮的坐标
			v.getLocationOnScreen(arrayOfInt);
			int x = arrayOfInt[0];
			int y = arrayOfInt[1];
			// System.out.println("x: " + x + " y:" + y + " w: " +
			// v.getMeasuredWidth() + " h: " + v.getMeasuredHeight() );
			showPop(v, x, y, view, position, fromOrTo);
			return true;
		}
	}

	/**
	 * item删除动画
	 * */
	private void rightRemoveAnimation(final View view, final int position) {
		final Animation animation = (Animation) AnimationUtils.loadAnimation(
				mContext, R.anim.chatto_remove_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setAlpha(0);
				performDismiss(view, position);
				animation.cancel();
			}
		});

		view.startAnimation(animation);
	}

	/**
	 * item删除动画
	 * */
	private void leftRemoveAnimation(final View view, final int position) {
		final Animation animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.chatfrom_remove_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setAlpha(0);
				performDismiss(view, position);
				animation.cancel();
			}
		});

		view.startAnimation(animation);
	}

	/**
	 * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
	 * 
	 * @param dismissView
	 * @param dismissPosition
	 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(mAnimationTime);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				list.remove(dismissPosition);
				notifyDataSetChanged();
				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
				// 所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/**
	 * 聊天消息的类型
	 * 消息类型；格式为：类型/编码格式
	 * text/utf-8=文本
	 * image/jpeg=图片
	 * audio/mp3=音频
	 * video/mp4=视频
	 */
	@Override
	public int getItemViewType(int position) {
		String typeStr = list.get(position).getMsg_type();
		if(typeStr.equals("text/utf-8")){
			return TEXT;
		}else if(typeStr.equals("audio/mp3")) {
			return AUDIO;
		}
		return 0;
	}
}
