package com.forteach.websocket.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 互动活动答题卡
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  14:51
 */
@Data
public class InteractiveSheetAnsw {

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 答案
     */
    private String answer;

    private Date date;

}
