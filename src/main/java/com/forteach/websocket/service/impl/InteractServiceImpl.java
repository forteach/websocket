package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.ToPush;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.RedisInteract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.forteach.websocket.common.Dic.SUBSCRIBE_USER_STUDENT;
import static com.forteach.websocket.common.KeyStorage.INTERACTION_UID_SET_PREFIX;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description: 互动交互数据获取
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  9:38
 */
@Slf4j
@Service
public class InteractServiceImpl implements InteractService {

    @Resource
    private RedisInteract interact;

    @Resource
    private StudentToPush studentToPush;

    @Resource
    private TeachersToPush teachersToPush;

    /**
     * 获取课堂交互信息
     *
     * @return
     */
    @Override
    public List<ToPush> obtain() {

        Set<String> uid = interact.getSets(INTERACTION_UID_SET_PREFIX);
        if (uid != null && uid.size() > 0) {
            return uid.stream()
                    .filter(id -> null != SESSION_MAP.get(id))
                    .filter(id -> SESSION_MAP.get(id).isOpen())
                    .map(this::buildToPush)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 构建需要推送的信息
     *
     * @param uid
     * @return
     */
    private ToPush buildToPush(String uid) {
        String uType = interact.uidType(uid);
        if (SUBSCRIBE_USER_STUDENT.equals(uType)) {
            return ToPush.builder()
                    .uid(uid)
                    .askQuestion(studentToPush.achieveQuestion(uid))
                    .askSurvey(studentToPush.achieveSurvey(uid))
                    .askBrainstorm(studentToPush.achieveBrainstorm(uid))
                    .askTask(studentToPush.achieveTask(uid))
                    .askBook(studentToPush.achieveBook(uid))
                    .build();
        } else {
            return ToPush.builder()
                    .uid(uid)
                    .achieveAnswer(teachersToPush.achieveAnswer(uid))
                    .achieveRaise(teachersToPush.achieveRaise(uid))
                    .achieveJoin(teachersToPush.achieveInteractiveStudents(uid))
                    .achieveSurveyAnswer(teachersToPush.achieveSurveyAnswer(uid))
                    .achieveBrainstormAnswer(teachersToPush.achieveBrainstormAnswer(uid))
                    .achieveTaskAnswer(teachersToPush.achieveTaskAnswer(uid))
                    .achieveBookAnswer(teachersToPush.achieveBookAnswer(uid))
                    .build();
        }
    }
}
