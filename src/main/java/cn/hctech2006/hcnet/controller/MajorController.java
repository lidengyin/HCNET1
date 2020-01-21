package cn.hctech2006.hcnet.controller;

import ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysMajor;
import cn.hctech2006.hcnet.bean.SysTeam;
import cn.hctech2006.hcnet.service.MajorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "专业信息接口")
@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private MajorService majorService;
    @ApiOperation(value = "根据状态获取全部专业信息",notes = "根据状态获取全部信息")
    @ApiImplicitParam(type = "query",name = "delFlag", value = "删除标志,0位正常,1位删除")
    @PostMapping("/find/all")
    public RespBean findAll(Byte delFlag){
        try{
            List<SysMajor> sysMajors = majorService.findAllMajor(delFlag);
            return RespBean.ok("获取成功",sysMajors);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取失败");
        }
    }
    @ApiOperation(value = "上传专业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "major",value = "专业名",required = true)
    })
    @PostMapping("/upload")
    public RespBean uploadMajor(SysMajor sysMajor){
        if(sysMajor.getMajor() == null){
            return RespBean.error("参数不全");
        }
        try{
            sysMajor.setDelFlag((byte)0);
            majorService.saveMajor(sysMajor);
            return RespBean.ok("专业信息上传成功",sysMajor);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("专业信息上传失败");
        }
    }
    @ApiOperation(value = "修改专业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "修改专业信息的ID",required = true),
            @ApiImplicitParam(type = "query",name = "major",value = "专业名"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0为正常,1位删除")
    })
    @PostMapping("/update")
    public RespBean updateMajor(SysMajor newMajor){


        try{
            //获取原始信息
            System.out.println("delFlag:"+newMajor.getDelFlag());
            System.out.println("major:"+newMajor.getMajor());
            SysMajor sysMajor = majorService.findMajorById(newMajor.getId());
            if(newMajor.getDelFlag() == null){
                newMajor.setDelFlag(sysMajor.getDelFlag());
            }
            if(newMajor.getMajor() == null){
                newMajor.setMajor(sysMajor.getMajor());
            }
            majorService.updateMajor(newMajor);
            return RespBean.ok("专业修改成功",newMajor);

        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("专业修改失败");
        }
    }
    @ApiOperation(value = "根据ID查找专业")
    @ApiImplicitParam(type = "query",name = "id",value = "专业ID",required = true)
    @PostMapping("/find")
    public RespBean findMajorById(Integer id){
        if(id == null){
            return RespBean.error("缺少参数");
        }
        try{
            SysMajor sysMajor = majorService.findMajorById(id);
            return RespBean.ok("查找专业成功",sysMajor);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("查找参数失败");
        }
    }

    @ApiOperation(value = "专业可用性修改",notes = "专业可用性修改")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id",value = "专业序号",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志，0正常，1删除",required = true)
    })
    @PostMapping("/update/delFlag")
    public RespBean updateStatus(SysMajor sysMajor){
        try{
            majorService.updateStatus(sysMajor);
            return RespBean.ok("修改成功",sysMajor);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改失败");
        }
    }


}
