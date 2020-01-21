package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysImg;
import cn.hctech2006.hcnet.service.ImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Api(tags = "图片传输接口")
@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImgService imgService;
    /**
     * 上传图片
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    @ApiOperation(value = "上传图片", notes = "上传图片")
    //@ApiImplicitParam(type = "query", name = "uploadFile",value = "图片文件",required = true)
    @ApiImplicitParam(type = "query",name = "imgCt",value = "创建时间",required = true,dataType = "java.util.Date")
    @PostMapping(value = "/upload",headers="content-type=multipart/form-data")
    public RespBean uploadImg(@ApiParam(value = "上传图片",required = true) MultipartFile uploadFile,SysImg newImg, HttpServletRequest request)throws IOException {
        //文件
        SysImg sysImg = new SysImg();
        //图片绝对路径
        String realPath = request.getSession().getServletContext().getRealPath("/upload/uploadImg/");
        String realPath1 = ResourceUtils.getURL("").getPath()+"upload/uploadImg/";
        String realPath2 = ResourceUtils.getURL("").getPath()+"upload/uploadImg/";
        System.out.println("realPath:"+realPath);
        System.out.println("realPath1:"+realPath1);
        System.out.println("realPath2:"+realPath2);
        //格式化时间,以时间分类
        String format = sdf.format(newImg.getImgCt());
        //获得文件所在目录
        File folder1 = new File(realPath1 + format);
        System.out.println("absoultPath:"+folder1.getAbsolutePath());
        System.out.println("realPath:"+folder1.getPath());
        //添加目录
        if(!folder1.isDirectory()){
            folder1.mkdirs();
        }
        //初始图片名
        String oldName = uploadFile.getOriginalFilename();
        System.out.println(oldName);
        String oldName1 = uploadFile.getOriginalFilename();
        //新的图片名
        String newName = UUID.randomUUID().toString()+
                oldName.substring(oldName.lastIndexOf("."),oldName.length());
        try{
            //转移加载文件
            //uploadFile.transferTo(new File(folder, newName));
            System.out.println("folder:"+folder1.getPath());
            uploadFile.transferTo(new File(folder1,newName));
            //uploadFile.transferTo(new File(folder2,newName));
            //图片相对路径
            String imgRelatePath = request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+"/upload/uploadImg/"+format+newName;
            String imgRelatePath1 = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+""+"/upload/uploadImg/"+format+newName;
            //设置文件名
            sysImg.setImgName(newName);
            //设置文件URL
            sysImg.setImgUrl(imgRelatePath);
            //设置文件上传时间
            sysImg.setImgCt(newImg.getImgCt());
            imgService.saveImg(sysImg);
            return RespBean.ok("图片上传成功","newImage:"+newName);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("图片上传失败");
        }
    }
    @ApiOperation(value = "显示图片")
    //@ApiImplicitParam(type = "query",name = "id",value = "图片ID",required = true)
    @GetMapping("/showimage/{id}")
   public RespBean showPhoto(HttpServletRequest request, HttpServletResponse response,@PathVariable("id")Integer id ) throws Exception{
        System.out.println("id:"+id);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //获得系统的根目录
        SysImg sysImg = imgService.getImgById(id);
        Date imgCt = sysImg.getImgCt();
        String format = sdf.format(imgCt);
        //获取/jar/upload/uplodimg/+format的根目录
        String realPath1 = ResourceUtils.getURL("").getPath()+"upload/uploadImg/";
        File file = new File(realPath1+format,sysImg.getImgName());
        BufferedImage bi = ImageIO.read(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi,"jpg",out);
        try{
            out.flush();
        }catch (Exception e){
            return RespBean.error("错误");
        }finally {
            out.close();
        }
        return null;
    }
}
