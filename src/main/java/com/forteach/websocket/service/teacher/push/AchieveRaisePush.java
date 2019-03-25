package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.domain.AchieveRaise;
import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description:学生回答推送给老师
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class AchieveRaisePush {

    @Resource
    private TeacherInteractImpl teacherInteract;

    public List<ToTeacherPush> getAchieveRaise(String circleId) {
        final String teachseId=teacherInteract.getRoomTeacherId(circleId);
        //构建推送对象信息集合
        return Arrays.asList(teachseId).stream()
                .filter(Objects::nonNull)
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())
                .map(tid->buildTeacherToPush(tid,circleId))
                //推送数据为空的话，终止流
                .filter(obj->obj.getAchieveRaise()!=null)
                .collect(Collectors.toList());

    }

    /**
     * 将课堂加入的学生回答数据，推送给老师
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public ToTeacherPush buildTeacherToPush(String uid,String circleId) {
        //创建回答信息
        return ToTeacherPush.builder()
                .uid(uid)
                //学生回答信息(BigQuestion)
                .achieveRaise(achieveRaise(circleId))
                .build();

    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public AchieveRaise achieveRaise(final String circleId) {
        //获得回答cut随机值
//        String uRandom = "";
        //获得题目ID
        final String questionId =teacherInteract.getNowQuestionId(circleId);

        final String questionType=teacherInteract.getNoQuestionType(circleId);
        if (questionId == null){
            return null;
        }

        final String teachseId=teacherInteract.getRoomTeacherId(circleId);
        //获得学生的回答信息
        return  teacherInteract.achieveRaise(circleId, questionId, questionType,teachseId);
    }

}
