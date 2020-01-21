package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysUser;
import cn.hctech2006.hcnet.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "登录接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "管理员登录",notes = "管理员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "userName", value = "用户名",required = true),
            @ApiImplicitParam(type = "query", name = "userPassword", value = "用户密码", required = true)
    })
    public RespBean login(SysUser user){
        try{
            SysUser sysUser = userService.findByUsernameAndPassword(user);
            if(sysUser == null){
                return RespBean.error("登录失败");
            }
            return RespBean.ok("获取成功",sysUser);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("登录失败");
        }
    }
}
