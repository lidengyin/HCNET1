package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.PageResult;
import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysAtm;
import cn.hctech2006.hcnet.bean.SysTeam;
import cn.hctech2006.hcnet.mapper.SysAtmMapper;
import cn.hctech2006.hcnet.service.AtmService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Api(tags = "团队氛围接口")
@RestController
@RequestMapping("/atm")
public class AtmController {
    @Autowired
    private AtmService atmService;
    @ApiOperation(value = "上传团队氛围", notes = "团队氛围上传")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "atmName", value = "氛围名", required = true),
            @ApiImplicitParam(type = "query", name = "atmDetail", value = "氛围细节",required = true),
            @ApiImplicitParam(type = "query", name = "createTime", value = "创建时间", required = true)
    })
    @PostMapping("/upload")
    public RespBean uploadAtm(SysAtm sysAtm){
        try{
            sysAtm.setCreateBy("李登印");
            sysAtm.setLastUpdateBy("李登印");
            sysAtm.setLastUpdateTime(new Date());
            sysAtm.setDelFlag((byte)0);
            atmService.uploadAtm(sysAtm);
            return RespBean.ok("上传团队氛围成功",sysAtm);

        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("上传团队氛围失败");
        }
    }
    @ApiOperation(value = "根据可用性获取全部团队氛围", notes = "根据可用性获取全部团队氛围")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value =  "分页行数",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
    })

    @PostMapping("/find/all")
    public RespBean findAll(Integer pageNum, Integer pageSize, Byte delFlag){
        try{
            Page<SysAtm> atms =  atmService.findAllAtm(pageNum, pageSize, delFlag);
            PageResult pageResult = new PageResult();
            List<SysAtm> atmsl = atms.getResult();
            pageResult.setContent(atmsl);
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setTotalPages(atms.getPages());
            pageResult.setTotalSize(atms.getTotal());
            return RespBean.ok("获取全部团队氛围成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取全部团队氛围失败");
        }
    }
    @ApiOperation(value = "根据ID获取团队氛围", notes = "根据ID获取团队氛围")
    @ApiImplicitParam(type = "query", name = "id", value = "氛围ID",required = true)
    @PostMapping("/find/one")
    public RespBean findById(Integer id){
        try{
            SysAtm sysAtm = atmService.findById(id);
            return RespBean.ok("根据ID获取成功",sysAtm);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("根据ID获取失败");
        }
    }
    @ApiOperation(value = "是否删除氛围", notes = "是否删除氛围")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id", value = "氛围编号",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志", required = true)

    })
    @PostMapping("/update/del")
    public RespBean upateDelFlag(SysAtm sysAtm){
        try{
            atmService.updateDelFlag(sysAtm);
            return RespBean.ok("修改成功",sysAtm);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改失败");
        }
    }
    @ApiOperation(value = "修改团队氛围", notes = "团队氛围修改")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id", value = "氛围编号", required = true),
            @ApiImplicitParam(type = "query", name = "atmName", value = "氛围名"),
            @ApiImplicitParam(type = "query", name = "atmDetail", value = "氛围细节"),
            @ApiImplicitParam(type = "query", name = "createTime", value = "创建时间"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志，0正常，1删除")
    })
    @PostMapping("/update/all")
    public RespBean updateAtm(SysAtm sysAtm){
        try{
            //获取之前的Atm
            SysAtm atm = atmService.findById(sysAtm.getId());
            if(sysAtm.getAtmDetail() == null){
                sysAtm.setAtmDetail(atm.getAtmDetail());
            }
            if(sysAtm.getAtmName() == null){
                sysAtm.setAtmName(atm.getAtmName());
            }
            if(sysAtm.getCreateTime() == null){
                sysAtm.setCreateTime(atm.getCreateTime());
            }
            if(sysAtm.getDelFlag() == null){
                sysAtm.setDelFlag(atm.getDelFlag());
            }
            sysAtm.setCreateBy("李登印");
            sysAtm.setLastUpdateBy("李登印");
            sysAtm.setLastUpdateTime(new Date());
            atmService.updateAtm(sysAtm);
            return RespBean.ok("修改团队氛围成功",sysAtm);

        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改团队氛围失败");
        }
    }
}
