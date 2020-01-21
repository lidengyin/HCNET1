package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.GroupService;
import cn.hctech2006.hcnet.service.ImgService;
import cn.hctech2006.hcnet.service.ProService;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
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

@Api(tags = "项目信息接口")
@RestController
@RequestMapping("/pro")
public class ProController {
    @Autowired
    private ProService proService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private GroupService groupService;
    @ApiOperation(value = "获取全部项目信息",notes = "获取全部项目信息")
    @PostMapping("/all")
    public RespBean findAllPro(){
        try{
            List<SysPro> pros = proService.findAllPro();
            for(SysPro sysPro : pros){
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysPro.getProGroup()));
                sysPro.setProGrgoupZh(sysGroup.getGroupName());
            }
            return RespBean.ok("获取全部项目信息",pros);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取项目信息失败");
        }
    }
    @ApiOperation(value = "分组获取项目",notes = "分组获取项目")
    @ApiImplicitParam(type = "query",name = "group",value = "组别,枚举类型")
    @PostMapping("/group")
    public RespBean findProByGroup(String group){
        if(group == null){
            return RespBean.error("分组序号为空,请重新填写");
        }
        try{
            List<SysPro> pros = proService.findProByGroup(group);
            for(SysPro sysPro : pros){
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysPro.getProGroup()));
                sysPro.setProGrgoupZh(sysGroup.getGroupName());
            }
            return RespBean.ok("分组获取全部项目",pros);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("分组获取项目失败");
        }
    }
    @ApiOperation(value = "根据可用标志分页获取项目",notes = "根据可用标志分组获取项目")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "delFlag",value = "删除标志,0位正常1位删除",required = true),
            @ApiImplicitParam(type = "query", name = "pageNum", value = "页数",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize", value = "每页行数", required = true)
    })
    @PostMapping("/find/status")
    public RespBean findProByStatus(Byte delFlag, Integer pageNum, Integer pageSize){
        try{
            Page<SysPro> pros = proService.findByStatus(delFlag, pageNum, pageSize);
            for(SysPro sysPro : pros){
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysPro.getProGroup()));
                sysPro.setProGrgoupZh(sysGroup.getGroupName());
            }
            List<SysPro> sysPros = pros.getResult();
            PageResult pageResult = new PageResult();
            pageResult.setContent(sysPros);
            pageResult.setTotalSize(pros.getTotal());
            pageResult.setTotalPages(pros.getPages());
            pageResult.setPageSize(pageSize);
            pageResult.setPageNum(pageNum);

            return RespBean.ok("根据状态获取全部项目",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("根据状态获取项目失败");
        }
    }
    @ApiOperation(value = "项目上传",notes = "项目上传")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "proName",value = "项目名", required = true),
            @ApiImplicitParam(type = "query", name = "proDetail",value = "项目细节", required = true),
            @ApiImplicitParam(type = "query",name = "proGroup",value = "项目组别", required = true),
            @ApiImplicitParam(type = "query",name = "proIntro", value = "项目简介",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0为正常,1位删除", required = true),
            @ApiImplicitParam(type = "query",name = "proDate",value = "项目日期", required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query",name = "proCt",value = "图片目录日期",required = true,dataType = "java.util.Date")

    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean uploadPro(SysPro sysPro, @ApiParam(value = "项目图片",required = true) MultipartFile file, HttpServletRequest request){
        try{
            sysPro.setCreateTime(new Date());
            sysPro.setLastUpdateTime(new Date());
            sysPro.setLastUpdateBy("李登印");
            sysPro.setCreateBy("李登印");

            sysPro.setProImg(findImgName(file,sysPro.getProCt(),request));
            proService.savePro(sysPro);
            return RespBean.ok("项目上传成功",sysPro);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("项目上传失败");
        }

    }
    @ApiOperation(value = "项目修改",notes = "项目修改")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id",value = "项目序号", required = true),
            @ApiImplicitParam(type = "query",name = "proName",value = "项目名"),
            @ApiImplicitParam(type = "query",name = "proIntro", value = "项目简介"),
            @ApiImplicitParam(type = "query", name = "proDetail",value = "项目细节"),
            @ApiImplicitParam(type = "query",name = "proGroup",value = "项目组别"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0为正常,1位删除"),
            @ApiImplicitParam(type = "query",name = "proDate",value = "项目日期",dataType = "java.util.Date"),
            //@ApiImplicitParam(type = "query",name = "file",value = "项目图片")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean updatePro(SysPro newPro, HttpServletRequest request, @ApiParam(value = "项目图片") MultipartFile file)throws Exception{
        if(newPro.getId() == null){
            return RespBean.error("序号为空，请重新上传");
        }
        try{
            SysPro sysPro = proService.findProByid(newPro.getId());
            newPro.setProCt(sysPro.getProCt());
            if(newPro.getProName() == null){
                newPro.setProName(sysPro.getProName());
            }
            if(newPro.getProDetail() == null){
                newPro.setProDetail(sysPro.getProDetail());
            }
            if(newPro.getProName() == null){
                newPro.setProName(sysPro.getProName());
            }
            if(newPro.getProGroup() == null){
                newPro.setProGroup(sysPro.getProGroup());
            }
            if(newPro.getProDate() == null){
                newPro.setProDate(sysPro.getProDate());
            }
            if(file == null){
                newPro.setProImg(sysPro.getProImg());
            }else{
                newPro.setProImg(findImgName(file,newPro.getProCt(),request));
            }
            if(newPro.getDelFlag() == null){
                newPro.setDelFlag(sysPro.getDelFlag());
            }
            //固定属性
            newPro.setCreateBy(sysPro.getCreateBy());
            newPro.setCreateTime(sysPro.getCreateTime());
            newPro.setLastUpdateBy(sysPro.getLastUpdateBy());
            newPro.setLastUpdateTime(sysPro.getLastUpdateTime());
            proService.updatePro(newPro);
            return RespBean.ok("项目信息修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("项目信息修改失败");
        }
    }

    @ApiOperation(value = "根据ID获取相应的项目", notes = "根据ID获取相应的项目")
    @ApiImplicitParam(type = "query", name = "id", value = "项目ID", required = true)
    @PostMapping("/find/id")
    public RespBean findById(Integer id){
        try{
            SysPro sysPro = proService.findProByid(id);
            return RespBean.ok("获取成功",sysPro);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取失败");
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
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/pro/Img/";
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

        SysPro sysPro = proService.findProByid(id);
        String format = sdf.format(sysPro.getProCt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/pro/Img/";
        File file = new File(realPath+format,sysPro.getProImg());
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
