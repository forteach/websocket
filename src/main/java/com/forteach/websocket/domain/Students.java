package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 学生信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/5  23:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Students {

    /**
     * 学生id
     */
    private String id;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 学生头像
     */
    private String portrait;

}
