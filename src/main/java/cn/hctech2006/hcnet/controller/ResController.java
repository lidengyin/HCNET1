package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysImg;
import cn.hctech2006.hcnet.bean.SysPro;
import cn.hctech2006.hcnet.bean.SysRes;
import cn.hctech2006.hcnet.service.ImgService;
import cn.hctech2006.hcnet.service.ResService;
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

@Api(tags = "团队资源接口")
@RestController
@RequestMapping("/res")
public class ResController {
    @Autowired
    private ResService resService;
    @Autowired
    private ImgService imgService;

    /**
     * 获取全部资源
     * @return
     */
    @ApiOperation(value = "根据可用性获取全部资源",notes = "根据可用性获取全部资源")
    @ApiImplicitParam(type = "query",name = "delFlag", value = "删除标志,0位正常,1位删除")
    @PostMapping("/find/all")
    public List<SysRes> getAllRes(Byte delFlag){
        return resService.findAllRes(delFlag);
    }

    /**
     * 团队资源上传
     * @param sysRes
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "团队资源上传")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "resName",value = "资源名",required = true),
            @ApiImplicitParam(type = "query",name = "resDetail",value = "资源细节",required = true),
            @ApiImplicitParam(type = "query",name = "resCt",value = "图片目录时间",required = true,dataType = "java.util.Date")
           //@ApiImplicitParam(type = "query",name = "file",value = "资源图片",required = true)
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean uploadRes(SysRes sysRes, @ApiParam(value = "资源图片",required = true) MultipartFile file, HttpServletRequest request){
        if(file.isEmpty()){
            return RespBean.error("图片资源为空,请添加图片资源!");
        }
        if(sysRes.getResName() == null || sysRes.getResDetail() == null || sysRes.getResCt() ==null){
            return RespBean.error("资源名或资源简介为空,请填写完整!");
        }
        try{
            sysRes.setResImg(findImgName(file,sysRes.getResCt(),request));
            sysRes.setDelFlag((byte)0);
            resService.saveRes(sysRes);
            return RespBean.ok("上传团队资源成功",sysRes);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("团队资源上传失败");
        }

    }

    /**
     * 团队资源更新
     * @param newRes
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "团队资源更新")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "团队资源序号",required = true),
            @ApiImplicitParam(type = "query",name = "resName",value = "团队资源名"),
            @ApiImplicitParam(type = "query",name = "resDetail",value = "团队资源细节"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0正常,1删除")
            //@ApiImplicitParam(type = "query",name = "file",value = "团队资源图片")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean updateRes(SysRes newRes, @ApiParam(value = "资源图片") MultipartFile file, HttpServletRequest request)throws Exception{
        if(newRes.getId() == null){
            return RespBean.error("序号为空，请填写完整序号");
        }
        try{
           SysRes sysRes = resService.findResById(newRes.getId());
           newRes.setResCt(sysRes.getResCt());
           if(file == null){
               newRes.setResImg(sysRes.getResImg());
           }else{
               newRes.setResImg(findImgName(file,newRes.getResCt(),request));
           }
           if(newRes.getResDetail() == null){
               newRes.setResDetail(sysRes.getResDetail());
           }
           if(newRes.getResName() == null){
               newRes.setResName(sysRes.getResName());
           }
            if(newRes.getDelFlag() == null){
                newRes.setDelFlag(sysRes.getDelFlag());
            }
           resService.updateRes(newRes);
           return RespBean.ok("团队资源更新成功",newRes);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("团队资源更新失败");
        }

    }
    @ApiOperation(value = "更新资源可用性", notes = "更新资源额可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id", value = "资源ID", required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0正常,1删除",required = true)
    })
    @PostMapping("/update/del")
    public RespBean updateEnable(SysRes sysRes){
        try{
            resService.updateDelFlag(sysRes);
            return RespBean.ok("更新删除标志成功", sysRes);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("更新删除标志失败");
        }
    }
    @ApiOperation(value = "获取单一资源", notes = "获取单一资源")
    @ApiImplicitParam(type = "query", name = "id", value = "资源ID", required = true)
    @PostMapping("/find/one")
    public RespBean findOneRes(Integer id){
        try{
            SysRes sysRes = resService.findResById(id);
            return RespBean.ok("成功获取单一资源",sysRes);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取单一资源失败");
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
        SysRes sysRes = resService.findResById(id);
        String format = sdf.format(sysRes.getResCt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/pro/Img/";
        File file = new File(realPath+format,sysRes.getResImg());
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
