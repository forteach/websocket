package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/5  23:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Students {

    private String id;

    private String name;

    private String portrait;

}
