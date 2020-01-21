package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.GroupService;
import cn.hctech2006.hcnet.service.ImgService;
import cn.hctech2006.hcnet.service.MemService;
import com.github.pagehelper.Page;
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

@Api(tags = "组别查询接口")
@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private MemService memService;
    @ApiOperation(value = "分页获取所有组别",notes = "获取所有组别")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "pageNum",value = "当前页数",required = true),
            @ApiImplicitParam(type = "query",name = "pageSize",value = "每页大小",required = true)
    })
    @PostMapping("/all")
    public RespBean findAllGroup(){
        try{
            List<SysGroup> groups = groupService.findAllList();
            for(SysGroup sysGroup : groups){

                List<SysMem> sysMemList = memService.findMemByGroupinGroup(sysGroup.getId().toString());
                sysGroup.setMemList(sysMemList);
            }
            return RespBean.ok("获取所有组别成功",groups);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取组别失败");
        }
    }
    @ApiOperation(value = "团队组别上传",notes = "团队组别上传")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "groupName",value = "组别名",required = true),
            @ApiImplicitParam(type = "query",name = "groupIntro",value = "组别介绍",required = true),
            //@ApiImplicitParam(type = "query",name = "file",value = "组别图片",required = true),
            @ApiImplicitParam(type = "query",name = "groupIsenabled",value = "是否显示",required = true),
            @ApiImplicitParam(type = "query",name = "groupCt",value = "图片文件时间",required = true)
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean saveGroup(SysGroup sysGroup, @ApiParam(value = "组别图片",required = true) MultipartFile file, HttpServletRequest request){
        if(file.isEmpty()){
            return RespBean.error("团队图片为空，请上传图片");
        }
        if(sysGroup.getGroupIntro() == null || sysGroup.getGroupName() == null || sysGroup.getGroupIsenabled() == null || sysGroup.getGroupCt() ==null){
            return RespBean.error("组别介绍或组别名或是否显示为空,请填写完整");
        }

        try{
            sysGroup.setGroupHead(findImgName(file,sysGroup.getGroupCt(),request));
            //sysGroup.setGroupIsenabled("1");
            groupService.saveGroup(sysGroup);
            return RespBean.ok("组别保存成功",sysGroup);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("组别保存失败");
        }
    }

    /**
     * 团队组别修改
     * @param newGroup
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "团队组别修改",notes = "团队组别修改,groupIsenabled=1为可用,为0是不可用")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "组别序号",required = true),
            @ApiImplicitParam(type = "query",name = "groupName",value = "组别名"),
            @ApiImplicitParam(type = "query",name = "groupIntro",value = "组别介绍"),
            //@ApiImplicitParam(type = "query",name = "file",value = "组别图片"),
            @ApiImplicitParam(type = "query",name = "groupIsenabled",value = "组别是否可用")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean updateGroup(SysGroup newGroup, @ApiParam(value = "组别图片") MultipartFile file, HttpServletRequest request)throws Exception{
        System.out.println(newGroup.getGroupName());
        if(newGroup.getId() == null){
            return RespBean.error("组别序号为空，请填写序号");
        }
        SysGroup sysGroup;
        try{
            sysGroup = groupService.findGroupById(newGroup.getId());
            newGroup.setGroupCt(sysGroup.getGroupCt());
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("无效的序号");
        }
        try{
            //newGroup.setGroupIsenabled(sysGroup.getGroupIsenabled());
            if(file == null){
                newGroup.setGroupHead(sysGroup.getGroupHead());
            }else{
                newGroup.setGroupHead(findImgName(file,newGroup.getGroupCt(),request));
            }
            if(newGroup.getGroupName() == null){

                newGroup.setGroupName(sysGroup.getGroupName());
            }
            if(newGroup.getGroupIntro() == null){
                newGroup.setGroupIntro(sysGroup.getGroupIntro());
            }
            if(newGroup.getGroupIsenabled() == null){
                newGroup.setGroupIsenabled(sysGroup.getGroupIsenabled());
            }
            groupService.updateGroup(newGroup);
            return RespBean.ok("修改组别成功",newGroup);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改组别失败");
        }

    }

    /**
     * 修改组别状态
     * @param sysGroup
     * @return
     */
    @ApiOperation(value = "修改组别状态",notes = "groupIsenabled=1为显示,为0是不显示")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id",value = "要关闭的组别ID",required = true),
            @ApiImplicitParam(type = "query",name = "groupIsenabled",value = "是否显示组别",required = true)
    })
    @PostMapping("/update/enable")
    public RespBean CloseGroupByIsEnabled(SysGroup sysGroup){
        if(sysGroup.getId() == null || sysGroup.getGroupIsenabled() == null){
            return RespBean.error("缺少参数,请填写完整");
        }
        try{
            groupService.updateIsEnabled(sysGroup);
            if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                System.out.println("不可用,清空组下成员:"+sysGroup.getId().toString());
                List<SysMem> sysMems = memService.findMemByGroupinGroup(sysGroup.getId()+"");
                for(SysMem sysMem : sysMems){
                    System.out.println(sysMem.getMemName());
                    sysMem.setMemGroup("0");

                    memService.updateMem(sysMem);
                }
            }
            return RespBean.ok("组别显示状态修改成功",sysGroup);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("组别显示状态修改失败");

        }

    }

    /**
     * 获取所有不可用状态组别
     * @return
     */
    @ApiOperation(value = "获取所有不可用组别状态",notes = "获取所有不可用组别状态,无参数")
    @PostMapping("/find/unable")
    public RespBean findAllUnabledGroup(){
        try{
            List<SysGroup> sysGroups =  groupService.findAllunabledGroup();
            return RespBean.ok("是否可用修改成功",sysGroups);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("是否可用修改失败");
        }
    }
    /**
     * 无条件获取所有组别
     * @return
     */
    @ApiOperation(value = "无条件获取所有组别",notes = "无条件获取所有组别,无参数")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum", value = "当前页",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize", value = "页码",required = true)
    })
    @PostMapping("/find/allno")
    public RespBean findAllNOroup(Integer pageNum ,Integer pageSize){
        try{
            Page<SysGroup> sysGroups =  groupService.findAllListNo(pageNum, pageSize);
            return RespBean.ok("是否可用修改成功",sysGroups);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("是否可用修改失败");
        }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    /**
     * 工具类:风采图片保存
     * @param multipartFile
     * @param request
     * @return
     */
    public String findImgName(MultipartFile multipartFile,Date imgDate, HttpServletRequest request)throws IOException {
        //文件
        SysImg sysImg = new SysImg();
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/group/Img/";
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
            imgService.saveImg(sysImg);
            return newName;
        }catch (Exception e){
        e.printStackTrace();
        return null;
    }
}
    @ApiOperation(value = "流式返回图片",notes = "流式返回图片")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "图片ID")
    })
    @GetMapping("/showimg/{id}")
    public RespBean showImage(Integer id, HttpServletRequest request, HttpServletResponse response)throws IOException{
        System.out.println("id:"+id);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //获得系统根目录

       SysGroup sysGroup = groupService.findGroupById(id);
        String format = sdf.format(sysGroup.getGroupCt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/group/Img/";
        File file = new File(realPath+format,sysGroup.getGroupHead());
        BufferedImage bi = ImageIO.read(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi,"jpg",out);
        try{
            out.flush();
        }finally {
            out.close();
        }
        return null;
    }

}
