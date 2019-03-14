package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;
import java.util.List;

/**
 * @Description: 问题
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:57
 */
@Data
public abstract class BaseEntity {

    @Id
    protected String id;

    protected String uDate;

}

@EqualsAndHashCode(callSuper = true)
@Data
class QuestionExamEntity<T> extends BaseEntity {

    protected Double score;

    /**
     * 创作老师
     */
    protected String teacherId;

    protected String paperInfo;

    protected List<T> examChildren;

    /**
     * 类型
     */
    protected String type;

    /**
     * 章节id
     */
    protected String chapterId;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    @Transient
    private int relate;

    /**
     * 难易度id
     */
    private String levelId;

    /**
     * 知识点id
     */
    private String knowledgeId;

    /**
     * 关键词
     */
    @Indexed
    private List<String> keyword;


}
