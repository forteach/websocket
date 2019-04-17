package com.forteach.websocket.service.student.push;

import cn.hutool.core.util.StrUtil;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.AskQuestion;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.domain.OptQuestion;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.impl.ClassStudentService;
import com.forteach.websocket.service.impl.SingleQuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description:单题目信息，推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class SingleQuestionPush {

    @Resource
    private ClassStudentService classStudentService;

    /**
     * 学生交互操作类
     */
    @Resource
    private SingleQuestService singleQuestService;

    public List<String> getOpenRooms(){
        return classStudentService.getOpenRooms();
    }

    /**
     * 学生提问推送信息
     *
     * @param circleId
     * @param type     课堂获得类型 提问、任务
     * @return
     */
    public List<ToStudentPush> singleQuestion(final String circleId, final QuestionType type) {

        // TODO 课堂互动方式  选人 抢答 ,是否需要将当前活动题目，根据课堂获得类型TYPE值区分数据
        final String intarcet = singleQuestService.getNowQuestInteractive(circleId);

        //判断课堂互动方式是否为空
        if (StrUtil.isNotBlank(intarcet)) {
            //获得提问方式的题目编号
            final String questId = singleQuestService.getNowQuestId(type, circleId, intarcet);
            //获得当前题目选中的学生
            final String stus = singleQuestService.getQuestNoReceiveSelectStu(circleId);

            //获得当前题目的交互方式  选人 抢答
            final String interactive = singleQuestService.getNowQuestInteractive(circleId);
            //人员参与 小组 个人
            final String category = singleQuestService.getNowQuestCategory(circleId);
            //课堂任课老师
            final String teacherId = singleQuestService.getRoomTeacherId(circleId);

            //根据所选的学生，对比Session数据是否在线，并获得学生推送详情
            if (intarcet.equals(SingleQueKey.ASK_INTERACTIVE_RAISE) && StrUtil.isBlank(stus)) {
                //给所有加入课堂的学生推送题目
                return singleQuestService.getClassStus(circleId).stream()
                        .filter(id -> !id.equals(teacherId))
                        .filter(id -> null != SESSION_MAP.get(id))
                        .filter(id -> SESSION_MAP.get(id).isOpen())
                        //创建推送数据
                        .map(uid -> TStudentToPush(uid, questId, interactive, category))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                //给老师所选的人员推送题目
                return Arrays.asList(stus.split(",")).stream()
                        .filter(id -> null != SESSION_MAP.get(id))
                        .filter(id -> SESSION_MAP.get(id).isOpen())
                        //创建推送数据
                        .map(uid -> TStudentToPush(uid, questId, interactive, category))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

        } else {
            return null;
        }

    }

    /**
     * 单题推送题目处理
     * * @param uid 学生编号
     *
     * @param questid     题目编号
     * @param interactive 交互方式  选人、举手、抢答
     * @param category    小组 个人
     * @return
     */
    private ToStudentPush TStudentToPush(String uid, String questid, String interactive, String category) {

        //是学生推送学生信息
            return ToStudentPush.builder()
                    .uid(uid)
                    .askQuestion(achieveQuestion(questid, interactive, category))
                    .build();
    }

    /**
     * // * @param circleid  课堂编号
     *
     * @param interactive 题目的回答方式
     * @param questionId     提问的问题编号
     * @param category    个人、小组回答
     * @return 返回题目信息
     */
    public AskQuestion achieveQuestion(String questionId, String interactive, String category) {
        //前端发送过来的时间阀值
        String cut = "";
        //创建题目信息
        OptQuestion optQuestion = selected(singleQuestService.getBigQuestion(questionId));
        //返回提问信息
        return buildAskQuestion(cut, optQuestion, interactive, category);
    }

    /**
     * 选人方式题目推送
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion selected(BigQuestion bigQuestion) {
        return new OptQuestion(SingleQueKey.ASK_QUESTIONS_SELECTED, bigQuestion);
    }


    /**
     * 构建提问问题返回值
     *
     * @param cut
     * @param optQuestion
     * @param interactive
     * @return
     */
    private AskQuestion buildAskQuestion(String cut, OptQuestion optQuestion, String interactive, String category) {
        if (optQuestion != null) {
            return new AskQuestion<>(cut, optQuestion, interactive, category);
        } else {
            return null;
        }
    }
}
