package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.domain.AchieveJoin;
import com.forteach.websocket.domain.Students;
import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.service.StudentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.forteach.websocket.service.WsService.SESSION_MAP;


/**
 * @Description:推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class ClassStudentPush {

    @Resource
    private TeacherInteractImpl TeacherInteract;

    @Resource
    private StudentsService studentsService;



    /**
     * 将课堂加入的学生，生成教师图送数据
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public List<ToTeacherPush> getClassStudent(String circleId,String teachseId) {

        //构建推送对象信息集合
        return Arrays.asList(teachseId).stream()
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())
                .map(tid->buildTeacherToPush(tid,circleId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    /**
     * 构建需要推送的信息(教师端)
     *
     * @param uid
     * @return
     */
    private ToTeacherPush buildTeacherToPush(String uid,String circleId) {
        // 获取要推送的用户身份信息 teacher student

            return ToTeacherPush.builder()
                    .uid(uid)
                    //学生加入课堂信息
                    .achieveJoin(achieveInteractiveStudents(uid,circleId))
                    .build();

    }


    public AchieveJoin achieveInteractiveStudents(String uid,String circleId) {

        List<Students> list = TeacherInteract.getInteractiveStudents(circleId, uid)
                .stream().map(stuId ->studentsService.findStudentsBrief(stuId))
                .collect(Collectors.toList());
        return new AchieveJoin(list);
    }

//    /**
//     * 查找互动学生信息
//     *
//     * @param uCircle
//     * @return
//     */
//    private List<Students> findInteractiveStudents(String uCircle) {
//
//        return interact.getSets(interactiveClassKey(uCircle))
//                .stream()
//                .map(id -> studentsService.findStudentsBrief(id))
//                .collect(Collectors.toList());
//    }

}
