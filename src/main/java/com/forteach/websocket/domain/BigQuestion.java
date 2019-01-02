package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "bigQuestion")
public class BigQuestion<T> extends AbstractExamEntity {

    protected String paperInfo;

    protected List<T> examChildren;

    protected String type;

    protected String chapterId;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    private int relate;

    /**
     * 难易度id
     */
    private String levelId;

    /**
     * 知识点id
     */
    private String knowledgeId;

    public BigQuestion() {
    }
}
