package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.common.ClassRoomKey;
import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.StudentsService;
import com.forteach.websocket.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.ASK_CIRCLE_ANSWER_DID;

/**
 * @Description:学生回答推送给老师
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class AchieveAnswerPush {

    @Resource
    private TeacherInteractImpl teacherInteract;

    @Resource
    private StudentsService studentsService;


    /**
     * 将课堂加入的学生回答数据，推送给老师
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public ToTeacherPush getAchieveAnswer(final String circleId) {
        //获得需要课堂的教师ID
        final String teachseId=teacherInteract.getRoomTeacherId(circleId);

        if(teachseId!=null&&!teachseId.equals("")){
            AchieveAnswer achieveAnswer=achieveAnswer(circleId);
            if(achieveAnswer!=null)   {
                //创建回答信息
                return ToTeacherPush.builder()
                        .uid(teachseId)
                        //学生回答信息(BigQuestion)
                        .achieveAnswer(achieveAnswer)
                        .build();
            }
        }
        return null;

    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public AchieveAnswer achieveAnswer(final String circleId) {
        //获得回答cut随机值
//        String uRandom = "";
        //获得题目ID
        final String questionId =teacherInteract.getNowQuestionId(circleId);
        //获得学生的回答信息
        List<Students> students = peopleAnswer(circleId, questionId, QuestionType.TiWen);
        if(students!=null) {
            return buildAchieveAnswer(students);
        }
        return null;
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveAnswer buildAchieveAnswer(final List<Students> students) {
        return new AchieveAnswer(students);
    }

    /**
     * 获取回答的学生情况
     *
     * @param uCircle
     * @param questionId
     * @return
     */
    private List<Students> peopleAnswer(final String uCircle, final String questionId, final QuestionType type) {
        if(questionId!=null&&!questionId.equals("")){
            final String teachseId=TeacherInteract.getRoomTeacherId(uCircle);
            return TeacherInteract.getAnswerStu(uCircle,questionId,type.name(),teachseId).stream().map(stuid -> {
                //查询redis 筛选是否回答情况
                Students student = studentsService.findStudentsBrief(stuid);
                //学生回答的答案
                String askAnswerInfo=teacherInteract.getQuestAnswer(uCircle,questionId,type.name(),stuid);
                //获得学生的批改结果
                String piGaiResult=teacherInteract.piGaiResult(uCircle,questionId,type.name(),stuid);
                //创建学生回答推送对象
                return new CircleAnswer(uCircle,questionId,student, ASK_CIRCLE_ANSWER_DID, askAnswerInfo,piGaiResult);

            }).collect(Collectors.toList());
        }
       return null;
    }

}
