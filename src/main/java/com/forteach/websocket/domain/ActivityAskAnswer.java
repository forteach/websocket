package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 活动 (任务,风暴,卷子) 回答信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  13:29
 */
@Data
@Document(collection = "activityAskAnswer")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class ActivityAskAnswer extends BaseEntity {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 问题库类别
     * 不需要传值 后台赋值 问题库类别  bigQuestion(考题 练习)/ brainstormQuestion (头脑风暴题库) /" +
     * " surveyQuestion(问卷题库) / taskQuestion (任务题库)
     */
    private String libraryType;

    /**
     * 答案评价 主观题 教师给出的答案评价
     */
    private String evaluate;

    /**
     * 课堂id
     */
    private String circleId;

    /**
     * 答案列表
     */
    private List<InteractiveSheetAnsw> answList;


}
