package com.forteach.websocket.service.student.push;

import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.impl.ClassStudentService;
import com.forteach.websocket.service.impl.MoreQuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description:多题目信息推送给学生
 * @author: zjw
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class MoreQuestionPush {

    @Resource
    private ClassStudentService classStudentService;

    /**
     * 学生交互操作类
     */
    @Resource
    private MoreQuestService moreQuestService;

    public List<String> getOpenRooms(){
        return classStudentService.getOpenRooms();
    }

    /**
     *
     * @param circleId 课堂ID
     * @param type  课堂活动类型  练习、问卷
     * @return
     */
    public List<ToStudentPush> moreQuestion(final String circleId, final QuestionType type) {
        //练习册子的唯一ID
       String bookId= moreQuestService.getMoreQuestBookId(type, circleId);

        //获得当前课堂发布多题目列表（比如：练习册、调查等类型发布）
        final List<String> questionIdList = moreQuestService.getNowMoreQuestId(type, circleId);
        if (questionIdList == null || questionIdList.size() == 0) {
            return null;
        }
        //获得当前多题目列表选中的学生
        final String stus = moreQuestService.getMoreQuestNoReceiveSelectStu(type,circleId);
        if (stus == null || stus.equals("")) {
            return null;
        }
        //给老师所选的人员推送题目
        return Arrays.asList(stus.split(",")).stream()
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())
                //TODO 需要创建练习册的临时唯一标识ID
                .filter(Objects::nonNull)
                //过滤重复推送的练习册ID
                .filter(stuId->moreQuestService.filterStu(circleId,bookId,stuId))
                //创建推送数据
                .map(uid -> TStudentToPush(uid, questionIdList))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


    }

    /**
     * 推送学生数据对象构造
     * * @param uid 学生编号
     *
     * @param moreQuestId 多题目编号
     * @return
     */
    private ToStudentPush TStudentToPush(String uid, List<String> moreQuestId) {
        return ToStudentPush.builder()
                .uid(uid)
                //推送的题目列表
                .askBook(achieveQuestion(moreQuestId))
                .build();
    }

    /**
     * // * @param circleid  课堂编号
     *
     * @param moreQuestId 多题目编号
     * @return 返回题目信息
     */
    public AskBook<BigQuestion> achieveQuestion(List<String> moreQuestId) {
        //前端发送过来的时间阀值
        String cut = "";
        //返回提问信息
        return buildAskBook(cut, getMoreQuestInfo(moreQuestId));
    }

    /**
     * 获得题目列表详情
     *
     * @param moreQuestId 多题目详情
     * @return
     */
    private List<BigQuestion> getMoreQuestInfo(List<String> moreQuestId) {
        //TODO 提问获得题目详情内容，几种交互方式都一样，是否需要合并
        return moreQuestId.stream()
                .map(questionId -> moreQuestService.getBigQuestion(questionId))
                .collect(Collectors.toList());
    }

    /**
     * 构建提问问题返回值
     *
     * @param cut
     * @param moreQuestInfo
     * @return
     */
    private AskBook buildAskBook(String cut, List<BigQuestion> moreQuestInfo) {
        if (moreQuestInfo != null) {
            return new AskBook<BigQuestion>(cut, moreQuestInfo);
        } else {
            return null;
        }
    }
}
