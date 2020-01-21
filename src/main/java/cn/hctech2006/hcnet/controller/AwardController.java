package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.AwardService;
import cn.hctech2006.hcnet.service.GroupService;
import cn.hctech2006.hcnet.service.ImgService;
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

@Api(tags = "获奖查询接口")
@RestController
@RequestMapping("/award")
public class AwardController {
    @Autowired
    private AwardService awardService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private GroupService groupService;
    @ApiOperation(value = "根据组别分页获取获奖情况")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value = "分页行数",required = true),
            @ApiImplicitParam(type = "query", name = "awardGroup",value = "查询组别",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
    })
    @PostMapping("/find/group")
    public RespBean findAwardByGroup(Integer pageNum, Integer pageSize, SysAward sysAward){
        if(pageNum == null || pageSize == null ||  pageNum <= 0 || pageSize <= 0){
            return RespBean.error("参数有误");
        }
       try{

           Page<SysAward> sysAwards = awardService.findAwardByGroup(pageNum,pageSize,sysAward);

           List<SysAward> awards = sysAwards.getResult();
           for(SysAward sysAward1 : awards){

               SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysAward1.getAwardGroup()));
               System.out.println("groupName:"+sysGroup.getGroupName());
               sysAward.setAwardGroupZh(sysGroup.getGroupName());
           }
           PageResult pageResult = new PageResult();
           pageResult.setPageNum(pageNum);
           pageResult.setPageSize(pageSize);
           pageResult.setContent(awards);
           pageResult.setTotalSize(sysAwards.getTotal());
           pageResult.setTotalPages(sysAwards.getPages());
           return RespBean.ok("成功分组分页获取获奖情况",pageResult);
       }catch (Exception e){
           e.printStackTrace();
           return RespBean.error("获取获奖情况失败");
       }
    }
    @ApiOperation(value = "根据可用性分页获取全部获奖情况")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value = "分页行数",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
    })
    @PostMapping("/find/all")
    public RespBean findALlAward(Integer pageNum, Integer pageSize,Byte delFlag){

        try{
           Page<SysAward> awardPage = awardService.findAllAward(pageNum, pageSize, delFlag);
            List<SysAward> awards = awardPage.getResult();
            for(SysAward sysAward : awards){
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysAward.getAwardGroup()));
                System.out.println("groupName:"+sysGroup.getGroupName());
                sysAward.setAwardGroupZh(sysGroup.getGroupName());
            }
            PageResult pageResult = new PageResult();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setContent(awards);
            pageResult.setTotalSize(awardPage.getTotal());
            pageResult.setTotalPages(awardPage.getPages());
            return RespBean.ok("成功分组分页获取获奖情况",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取获奖情况失败");
        }
    }
    @ApiOperation(value = "获奖情况上传",notes = "获奖情况上传")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "awardName",value = "获奖名",required = true),
            @ApiImplicitParam(type = "query", name = "awardDate",value = "获奖日期",required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query",name = "awardDetail",value = "获奖细节",required = true),
            @ApiImplicitParam(type = "query",name = "awardCt",value = "图片目录日期",required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query",name = "awardGroup",value = "获奖组别数字代替",required = true),
            //@ApiImplicitParam(type = "query",name = "file",value = "获奖图片",required = true)
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean uploadAward(SysAward sysAward, @ApiParam(value = "获奖照片",required = true) MultipartFile file, HttpServletRequest request){
        if(file.isEmpty()){

            return RespBean.error("图片资源为空,请上传图片");
        }
        System.out.println("Ct:"+sysAward.getAwardCt());
        if(sysAward.getAwardDate() == null || sysAward.getAwardDetail() == null || sysAward.getAwardName() == null|| sysAward.getAwardGroup() == null||sysAward.getAwardCt() == null){
            return RespBean.error("参数不完整,请重新填写");
        }
        try{
            sysAward.setAwardImg(findImgName(file,sysAward.getAwardCt(),request));
            sysAward.setDelFlag((byte)0);
            awardService.saveAward(sysAward);
            return RespBean.ok("获奖情况上传成功",sysAward);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获奖情况上传失败");
        }
    }
    @ApiOperation(value = "获奖情况更新",notes = "获奖情况更新")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "获奖序号",required = true),
            @ApiImplicitParam(type = "query",name = "awardName",value = "获奖名"),
            @ApiImplicitParam(type = "query", name = "awardDate",value = "获奖日期",dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query",name = "awardDetail",value = "获奖细节"),
            @ApiImplicitParam(type = "query",name = "awardGroup",value = "获奖组别,枚举类型"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
            //@ApiImplicitParam(type = "query",name = "file",value = "获奖图片")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean updateAward(SysAward newAward, @ApiParam(value = "获奖图片") MultipartFile file, HttpServletRequest request)throws Exception{
        if(newAward.getId() == null){
            return RespBean.error("请填写序号!!");
        }
        try{
        //获取修改以前获奖记录
            SysAward sysAward = awardService.findAwardById(newAward.getId());
            newAward.setAwardCt(sysAward.getAwardCt());
            if(file == null){
                newAward.setAwardImg(sysAward.getAwardImg());
            }else{
                newAward.setAwardImg(findImgName(file,newAward.getAwardCt(),request));
            }
            if(newAward.getAwardGroup() == null){
                newAward.setAwardGroup(sysAward.getAwardGroup());
            }
            if(newAward.getAwardDate() == null){
                newAward.setAwardDate(sysAward.getAwardDate());
            }
            if(newAward.getAwardDetail() == null){
                newAward.setAwardDetail(sysAward.getAwardDetail());
            }
            if(newAward.getAwardName() == null){
                newAward.setAwardName(sysAward.getAwardName());
            }
            if(newAward.getDelFlag() == null){
                newAward.setDelFlag(sysAward.getDelFlag());
            }
            awardService.updateAward(newAward);
            return RespBean.ok("获奖情况求改成功",newAward);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获奖情况修改失败");
        }
    }

    @ApiOperation(value = "根据id获取获奖信息", notes = "根据ID获取获奖信息")
    @ApiImplicitParam(type = "query", name = "id", value = "获奖编号",required = true)
    @PostMapping("/find/id")
    public RespBean findById(Integer id){
        try{
           SysAward sysAward = awardService.findAwardById(id);
            SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysAward.getAwardGroup()));
            System.out.println("groupName:"+sysGroup.getGroupName());
            sysAward.setAwardGroupZh(sysGroup.getGroupName());
           return RespBean.ok("获取成功", sysAward);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取失败");
        }
    }

    @ApiOperation(value = "获奖可用性更新",notes = "获奖可用性更新")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "获奖序号",required = true),

            @ApiImplicitParam(type = "query",name = "delFlag",value = "删除标志,0正常,1删除"),

    })
    @PostMapping(value = "/update/delFlag",headers = "content-type=multipart/form-data")
    public RespBean updateStatus(SysAward sysAward){
    try{
        awardService.updateStatus(sysAward);
        return RespBean.ok("修改成功",sysAward);
    }catch (Exception e){
        e.printStackTrace();
        return RespBean.error("修改失败");
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
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/award/Img/";
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
            //imgService.saveImg(sysImg);
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

        SysAward  sysAward = awardService.findAwardById(id);
        String format = sdf.format(sysAward.getAwardCt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/award/Img/";
        File file = new File(realPath+format,sysAward.getAwardImg());
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
