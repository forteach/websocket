package com.forteach.websocket.domain;

import com.forteach.websocket.web.vo.DataDatumVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description: 学生答题情况
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CircleAnswer extends Students {

    private String circleId;

    private String questionId;
    /**
     * 回答状态
     * １未回答
     * ２已回答
     */
    private String state;

    /**
     * 获得题目内容
     */
    private String askAnswerInfo;

    /**
     * 批改结果
     */
    private boolean piGaiResult;

    /**
     * 附件信息
     */
    private List<DataDatumVo> fileList;

    public CircleAnswer(String circleId,String questionId,Students students, String state, String askAnswerInfo,boolean piGaiResult, List<DataDatumVo> fileList) {
        BeanUtils.copyProperties(students, this);
        this.circleId=circleId;
        this.questionId=questionId;
        this.piGaiResult=piGaiResult;
        this.state = state;
        this.askAnswerInfo = askAnswerInfo;
        this.fileList = fileList;
    }
}
