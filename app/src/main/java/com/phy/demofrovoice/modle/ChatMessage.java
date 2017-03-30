package com.phy.demofrovoice.modle;


/**
 * 单聊天消息
 */
public class ChatMessage  {

    /**
     * 聊天消息的Id
     */
    private String msg_id;
    /**
     * 聊天消息的类型
     * 消息类型；格式为：类型/编码格式
     * text/utf-8=文本
     * image/jpeg=图片
     * audio/mp3=音频
     * video/mp4=视频
     */
    private String msg_type;

    /**
     * 消息内容
     * 非文本的多媒体消息内容(图片、音视频等)，则为多媒体内容的URL地址
     */
    private String msg_content;
    /**
     * 消息发送时间
     */
    private String msg_date;

    /**
     * 聊天对象的id--用于读取和单个对象聊天记录时使用
     * 电话号码
     */
    private String selectTag;
    /**
     * 消息发出者
     * 发送方Id
     * 电话号码
     */
    private String msg_sender;
    /**
     * 音视频持续时间
     */
    float time;
    /**
     * 消息文件地址
     */
    String filePathString;

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePathString() {
        return filePathString;
    }

    public void setFilePathString(String filePathString) {
        this.filePathString = filePathString;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getMsg_date() {
        return msg_date;
    }

    public void setMsg_date(String msg_date) {
        this.msg_date = msg_date;
    }

    public String getSelectTag() {
        return selectTag;
    }

    public void setSelectTag(String selectTag) {
        this.selectTag = selectTag;
    }

    public String getMsg_sender() {
        return msg_sender;
    }

    public void setMsg_sender(String msg_sender) {
        this.msg_sender = msg_sender;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msg_id='" + msg_id + '\'' +
                ", msg_type='" + msg_type + '\'' +
                ", msg_content='" + msg_content + '\'' +
                ", msg_date='" + msg_date + '\'' +
                ", selectTag='" + selectTag + '\'' +
                ", msg_sender='" + msg_sender + '\'' +
                ", time=" + time +
                ", filePathString='" + filePathString + '\'' +
                '}';
    }
}
