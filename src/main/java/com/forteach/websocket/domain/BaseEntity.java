package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:57
 */
@Data
public abstract class BaseEntity {

    @Id
    protected String id;

    @LastModifiedDate
    protected Date uDate;

}

@EqualsAndHashCode(callSuper = true)
@Data
class AbstractExamEntity extends BaseEntity {

    protected Double score;

    /**
     * 创作老师
     */
    protected String teacherId;
}
