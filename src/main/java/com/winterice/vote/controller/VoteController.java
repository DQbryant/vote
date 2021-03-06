package com.winterice.vote.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winterice.vote.annotation.Auth;
import com.winterice.vote.interceptor.LoginInterceptor;
import com.winterice.vote.mapper.GroupMapper;
import com.winterice.vote.mapper.UserMapper;
import com.winterice.vote.mapper.VoteMapper;
import com.winterice.vote.pojo.*;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dqbryant
 * @create 2020/11/7 10:23
 */
@CrossOrigin
@RestController
@RequestMapping("/vote/")
public class VoteController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    VoteMapper voteMapper;
    @Autowired
    GroupMapper groupMapper;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    @PostMapping("vote")
    @Auth
<<<<<<< HEAD
    public ResponseJson<Object> vote(@RequestBody() VoteVo voteVo, HttpServletResponse response){
        int[] ids = voteVo.voted;
        String uid = response.getHeader("userId");
=======
    public ResponseJson<Object> vote(@RequestBody() VoteVo voteVo){
        Integer[] ids = voteVo.voted;
        String uid = LoginInterceptor.getUserId();
>>>>>>> 203d22a0aa1d734a68ebd3120ea6fdf8a24c00d0
        User user = userMapper.selectById(uid);
        if(user.getHasVoted() == 1){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }else {
            for (int groupId:ids){
                Vote vote = new Vote(uid, groupId);
                if(voteMapper.insert(vote) == 1){
                    if(groupMapper.addNum(groupId) != 1){
                        return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
                    }
                }else {
                    return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
                }
            }
            user.setHasVoted(1);
            userMapper.updateById(user);
            return new ResponseJson<>(ResultCode.SUCCESS);
        }
    }
    @GetMapping("getVoteList")
    @Auth("admin")
    public ResponseJson<PageResult<UserVo>> getVoteList(@RequestParam(defaultValue = "1", required = false)int pageNum,@RequestParam(defaultValue = "15",required = false) int pageSize){
        Page<User> page = new Page<>(pageNum,pageSize);
        IPage<User> voteIPage = userMapper.selectPage(page,null);
        List<User> userList = voteIPage.getRecords();
        List<UserVo> userVoList = new LinkedList<>();
        for (User user:userList){
            userVoList.add(new UserVo(user, voteMapper.getVoteList(user.getUid())));
        }
        PageResult<UserVo> pageResult = new PageResult<>(userVoList);
        pageResult.setCurrent(voteIPage.getCurrent());
        pageResult.setSize(voteIPage.getSize());
        pageResult.setTotal(voteIPage.getTotal());
        return new ResponseJson<>(ResultCode.SUCCESS, pageResult);
    }
    @GetMapping("getVote")
    @Auth()
    public ResponseJson<GroupVo> getVote(){
        return new ResponseJson<>(ResultCode.SUCCESS, new GroupVo(groupMapper.selectAll()));
    }
    @GetMapping("reset")
    @Auth("admin")
    public ResponseJson<String> reset(){
        if(userMapper.reset() && groupMapper.reset()>0 && voteMapper.delete(null)>0){
            return new ResponseJson<>(ResultCode.SUCCESS);
        }else {
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
    }
}
