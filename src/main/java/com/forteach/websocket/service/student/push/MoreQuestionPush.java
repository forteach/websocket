package com.forteach.websocket.service.student.push;

import com.forteach.websocket.common.QuestionType;
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
 * @Description:多题目信息推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class MoreQuestionPush {

    /**
     * 学生交互操作类
     */
    @Resource
    private StuInteractImpl stuInteract;

    /**
     * 学生提问推送信息
     * @param circleId
     * @return
     */
    public List<ToStudentPush> MoreQuestion(final String circleId,final QuestionType type ){

//        final String intarcet=stuInteract.getNowQuestInteractive(circleId);

//        if(intarcet!=null&&!"".equals(intarcet)){
            //获得当前课堂发布多题目列表（比如：练习册、调查等类型发布）
            final List<String> questionIdList=stuInteract.getNowQuestId(type,circleId);
            //获得当前多题目列表选中的学生
            final String stus= stuInteract.getMoreQuestNoReceiveSelectStu(circleId);

//            //获得当前题目的交互方式  选人 抢答
//            final String interactive=stuInteract.getNowQuestInteractive(circleId);
//            //暂时设定，需要从redis里面去除该值 小组 个人
//            final String category=stuInteract.getNowQuestCategory(circleId);

            final String teacherId=stuInteract.getRoomTeacherId(circleId);

            //根据所选的学生，对比Session数据是否在线，并获得学生推送详情
//            if(intarcet.equals(Dic.ASK_INTERACTIVE_RAISE)&&"".equals(stus)){
                //给所有加入课堂的学生推送题目
//
//                return stuInteract.getClassStus(circleId).stream()
//                        .filter(id->!id.equals(teacherId))
//                       .filter(id -> null != SESSION_MAP.get(id))
//                        .filter(id -> SESSION_MAP.get(id).isOpen())
//                        //创建推送数据
//                        .map(uid->TStudentToPush(uid,questId, interactive, category))
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList());
//            }else{
                //给老师所选的人员推送题目
                return Arrays.asList(stus.split(",")).stream()
                        .filter(id -> null != SESSION_MAP.get(id))
                        .filter(id -> SESSION_MAP.get(id).isOpen())
                        //创建推送数据
                        .map(uid->TStudentToPush(uid,questionIdList))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
//            }

//        }else{
//            return null;
//        }

    }

    /**
     * 推送学生数据对象构造
     ** @param uid 学生编号
     * @param moreQuestId 多题目编号
     * @return
     */
    private ToStudentPush TStudentToPush(String uid,List<String> moreQuestId) {
                return ToStudentPush.builder()
                        .uid(uid)
                        //推送的题目列表
                        .askBook(achieveQuestion(moreQuestId))
                        .build();
    }

    /**
     *
    // * @param circleid  课堂编号
     * @param moreQuestId   多题目编号
     * @return  返回题目信息
     */
    public AskBook<BigQuestion> achieveQuestion(List<String> moreQuestId){
        //前端发送过来的时间阀值
        String cut="";
        //返回提问信息
        return buildAskBook(cut, getMoreQuestInfo(moreQuestId));
    }

    /**
     * 获得题目列表详情
     *
     * @param moreQuestId  多题目详情
     * @return
     */
    private List<BigQuestion>  getMoreQuestInfo(List<String> moreQuestId) {
        //TODO 提问获得题目详情内容，几种交互方式都一样，是否需要合并
       return moreQuestId.stream()
                .map(questionId->stuInteract.getBigQuestion(questionId))
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
