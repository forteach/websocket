package com.forteach.websocket.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 18-11-28 15:24
 * @Version: 1.0
 * @Description:
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDatumVo implements Serializable {

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径地址
     */
    private String fileUrl;

    /**
     * 是否挂载文件参数只能是Y,N
     */
    private String mount;

    /**
     * 图片文件顺序下标
     */
    private Integer indexNum;
}
