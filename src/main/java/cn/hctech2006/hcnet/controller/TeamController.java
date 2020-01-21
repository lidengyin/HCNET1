package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysAchieve;
import cn.hctech2006.hcnet.bean.SysImg;
import cn.hctech2006.hcnet.bean.SysTeam;
import cn.hctech2006.hcnet.service.TeamService;
import io.swagger.annotations.*;
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
import java.util.UUID;

@Api(tags = "团队信息接口")
@RestController
@RequestMapping("/team")
public class TeamController {
    @Autowired
    private TeamService teamService;

    /**
     * 根据状态获取所有团队信息
     * @return
     */
    @ApiOperation(value = "根据状态获取所有团队信息",notes = "根据状态获取所有团队信息")
    @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0正常,1删除",required = true)
    @PostMapping("/all")
    public List<SysTeam> findAllTeam(Byte delFlag){
        return teamService.findAllTeam(delFlag);
    }
    /**
     * 团队信息上传
     * @param sysTeam
     * @return
     */
    @ApiOperation(value = "上传团队信息",notes = "上传团队信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "teamName",value = "团队名",required = true),
            @ApiImplicitParam(type = "query",name = "teamDetail",value = "团队简介",required = true),
            @ApiImplicitParam(type = "query",name = "teamUploadTime",value = "上传目录时间",required = true),

    })
    @PostMapping("/upload")
    public RespBean uploadTeam(SysTeam sysTeam, @ApiParam(value = "qq",required = true) MultipartFile qq, HttpServletRequest request){
        if(sysTeam.getTeamName() == null || sysTeam.getTeamDetail() == null){
            return RespBean.error("团队信息名或团队信息内容为空，请填写完整");
        }
        try{
            System.out.println(qq.getContentType());
            sysTeam.setTeamQq(findImgName(qq, sysTeam.getTeamUploadTime(), request));
            //sysTeam.setTeamQq(findImgName(qq[1], sysTeam.getTeamUploadTime(), request));
            //默认删除标志
            sysTeam.setDelFlag((byte)0);
            teamService.saveTeam(sysTeam);
            return RespBean.ok("团队信息上传成功",sysTeam);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("团队信息上传失败");
        }
    }

    /**
     * 团队信息更新
     * @param newTeam
     * @return
     */
    @ApiOperation(value = "更新团队信息",notes = "更新团队信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "团队序号",required = true),
            @ApiImplicitParam(type = "query",name = "teamName",value = "团队名"),
            @ApiImplicitParam(type = "query",name = "teamDetail",value = "团队简介"),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志，0正常，1删除")
    })
    @PostMapping("/update")
    public RespBean updateTeam(SysTeam newTeam, @ApiParam(value = "qq") MultipartFile qq, HttpServletRequest request)throws Exception{
        if(newTeam.getId() == null){
            return RespBean.error("序号为空，请重新填写");
        }
        try{
            //获取修改前团队信息
            SysTeam sysTeam = teamService.findTeamById(newTeam.getId());
            //获取固定属性
            newTeam.setTeamUploadTime(sysTeam.getTeamUploadTime());
            if(newTeam.getTeamDetail() == null){
                newTeam.setTeamDetail(sysTeam.getTeamDetail());
            }
            if(newTeam.getTeamName() == null){
                newTeam.setTeamName(sysTeam.getTeamName());
            }
            if(qq != null){
                newTeam.setTeamQq(findImgName(qq,sysTeam.getTeamUploadTime(),request));
            }else{
                newTeam.setTeamQq(sysTeam.getTeamQq());
            }
            if(newTeam.getDelFlag() == null){
                newTeam.setDelFlag(sysTeam.getDelFlag());
            }
            teamService.updateTeam(newTeam);
            return RespBean.ok("团队信息修改成功",newTeam);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("团队信息修改失败");
        }
    }
    @ApiOperation(value = "更新团队可用性",notes = "更新团队可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "团队序号",required = true),

            @ApiImplicitParam(type = "query",name = "delFlag",value = "删除标志,0正常,1删除")
    })
    @PostMapping("/update/delFlag")
    public RespBean updateFlag(SysTeam sysTeam){
        try{
            teamService.updateStatus(sysTeam);
            return RespBean.ok("修改成功",sysTeam);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改失败");
        }
    }
    /**
     * 工具类:风采图片保存
     * @param multipartFile
     * @param request
     * @return
     */
    public String findImgName(MultipartFile multipartFile, Date imgDate, HttpServletRequest request)throws IOException {
        //文件
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/team/Img/";
        //格式化时间,以时间分类
        String format = sdf.format(imgDate);
        //获得文件所在目录
        File folder = new File(realPath + format);
        //添加目录
        if(!folder.isDirectory()){
            folder.mkdirs();
        }
        //初始图片名
        String oldName = multipartFile.getOriginalFilename();
        //新的图片名
        String newName = UUID.randomUUID().toString()+
                oldName.substring(oldName.lastIndexOf("."),oldName.length());
        try{
            //转移加载文件
            multipartFile.transferTo(new File(folder, newName));

            return newName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    @ApiOperation(value = "流式返回图片",notes = "流式返回图片")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "图片ID")
    })
    @GetMapping("/showimg/{id}")
    public RespBean showImage(Integer id, HttpServletRequest request, HttpServletResponse response)throws IOException {
        System.out.println("id:"+id);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //获得系统根目录
        SysTeam sysTeam = teamService.findTeamById(id);
        String format = sdf.format(sysTeam.getTeamUploadTime());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/team/Img/";
        File file = new File(realPath+format,sysTeam.getTeamQq());
      //  File file1 = new File(realPath+format,sysTeam.getTeamWechat());
        BufferedImage bi = ImageIO.read(new FileInputStream(file));
      //  BufferedImage bi1 = ImageIO.read(new FileInputStream(file1));
        ServletOutputStream out = response.getOutputStream();
      //  ServletOutputStream out1 = response.getOutputStream();
        ImageIO.write(bi,"jpg",out);
       // ImageIO.write(bi1,"jpg",out1);
        try{
            out.flush();
        //    out1.flush();
        }finally {
            out.close();
         //   out1.close();
            //showImage1(id, request,response);
        }
        return null;
    }
    public RespBean showImage1(Integer id, HttpServletRequest request, HttpServletResponse response)throws IOException {
        System.out.println("id:"+id);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //获得系统根目录
        SysTeam sysTeam = teamService.findTeamById(id);
        String format = sdf.format(sysTeam.getTeamUploadTime());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/team/Img/";
        File file = new File(realPath+format,sysTeam.getTeamWechat());
        BufferedImage bi = ImageIO.read(new FileInputStream(file));
        //BufferedImage bi1 = ImageIO.read(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        //ServletOutputStream out1 = response.getOutputStream();
        ImageIO.write(bi,"jpg",out);
        //ImageIO.write(bi1,"jpg",out1);
        try{
            out.flush();
          //  out1.flush();
        }finally {
            out.close();
            //out1.close();
        }
        return null;
    }
}
