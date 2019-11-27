package com.forteach.websocket.service.teacher.push;

import com.alibaba.fastjson.JSONObject;
import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.Key.TeachAnswerKey;
import com.forteach.websocket.service.impl.AchieveAnswerService;
import com.forteach.websocket.service.impl.ClassStudentService;
import com.forteach.websocket.service.impl.StudentsService;
import com.forteach.websocket.util.StringUtil;
import com.forteach.websocket.web.vo.DataDatumVo;
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
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class TeachAnswerPush {

    @Resource
    private ClassStudentService classStudentService;

    @Resource
    private AchieveAnswerService achieveAnswerService;

    @Resource
    private StudentsService studentsService;



    public List<String> getOpenRooms(){
        return classStudentService.getOpenRooms();
    }

    public List<ToTeacherPush> getAchieveAnswer(String circleId,final String questionType) {
        final String teachseId=classStudentService.getRoomTeacherId(circleId);
        //构建推送对象信息集合
        return Arrays.asList(teachseId).stream()
                .filter(Objects::nonNull)
//                .filter(id -> null != SESSION_MAP.get(id))
//                .filter(id -> SESSION_MAP.get(id).isOpen())
                .map(tid->buildTeacherToPush(tid,circleId,questionType))
                //推送数据为空的话，终止流 achieveAnswer
                .filter(obj-> obj != null && obj.getAchieveAnswer()!=null)
                .collect(Collectors.toList());

    }

    /**
     * 将课堂加入的学生回答数据，推送给老师
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public ToTeacherPush buildTeacherToPush(final String teacherId,final String circleId,final String questionType) {

        if(StringUtil.isNotEmpty(teacherId)){
            AchieveAnswer achieveAnswer=achieveAnswer(circleId,questionType);
            if(achieveAnswer!=null)   {
                //创建回答信息
                return ToTeacherPush.builder()
                        .uid(teacherId)
                        //学生回答信息(BigQuestion)
                        .achieveAnswer(achieveAnswer)
                        .questionType(questionType)
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
    public AchieveAnswer achieveAnswer(final String circleId,String questionType) {
        //获得回答cut随机值
//        String uRandom = "";
        //获得题目ID
        final String questionId =achieveAnswerService.getNowQuestionId(circleId);
        //获得学生的回答信息
        List<Students> students = peopleAnswer(circleId, questionId, questionType);
        if(students!=null&&students.size()>0) {
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
    private List<Students> peopleAnswer(final String uCircle, final String questionId, final String questionType) {
        if(StringUtil.isNotEmpty(questionId)){
            final String teacherId=classStudentService.getRoomTeacherId(uCircle);
            return achieveAnswerService.getAnswerStu(uCircle,questionId,questionType,teacherId).stream().map(stuid -> {
                //查询redis 筛选是否回答情况
                Students student = studentsService.findStudentsBrief(stuid);
                //学生回答的答案
                String askAnswerInfo=achieveAnswerService.getQuestAnswer(uCircle,questionId,questionType,stuid);
                //获得学生的批改结果
                String piGaiResult=achieveAnswerService.piGaiResult(uCircle,questionId,questionType,stuid);
                //题目回答的附件信息
                List<DataDatumVo> fileList = achieveAnswerService.findFileList(uCircle, questionId, questionType, stuid);
                //创建学生回答推送对象
                return new CircleAnswer(uCircle,questionId,student, TeachAnswerKey.ASK_CIRCLE_ANSWER_DID, askAnswerInfo,"true".equals(piGaiResult) ? true : false, fileList);

            }).collect(Collectors.toList());
        }
       return null;
    }

}
