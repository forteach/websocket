package com.forteach.websocket.common;

import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:21
 */
//@EqualsAndHashCode(callSuper = true)
@Data
public class BigQuestionGiveVo  {

    /**
     * 课堂id
     */
    private String circleId;

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 互动方式
     * <p>
     * race   : 抢答
     * raise  : 举手
     * select : 选则
     * vote   : 投票
     */
    private String interactive;

    /**
     * 设置课堂提问问题ID
     * @return
     */
    public String getRaceAnswerFlag() {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }


    /**
     * 课堂交互活动了icing，某个课堂上的某些课堂活动
     *
     * @return
     */
    public String getAskQuestionsId(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(circleId).concat(type.name());
    }

    public BigQuestionGiveVo(String questionId, String interactive) {
        this.questionId = questionId;
        this.interactive = interactive;
    }

    public BigQuestionGiveVo(String circleId, String interactive,String questionId) {
        this.circleId = circleId;
        this.questionId = questionId;
        this.interactive = interactive;
    }

    public BigQuestionGiveVo() {
    }
}
