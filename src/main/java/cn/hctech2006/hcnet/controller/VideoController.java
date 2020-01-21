package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.GroupService;
import cn.hctech2006.hcnet.service.VideoService;
import cn.hctech2006.hcnet.util.HtmlSpirit;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.video.Video;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Api(tags = "视频传输接口")
@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private GroupService groupService;
    /**
     * 上传视频
     * @param video
     * @param uploadFile
     * @param request
     * @return
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
    @ApiOperation(value = "上传视频", notes = "上传视频,必须有视频类型格式的视频路径,否则只能自己写一个表单进行测试")
    @ApiImplicitParams({

            @ApiImplicitParam(type = "query", name = "videoName",value = "视频名",required = true),
            @ApiImplicitParam(type = "query", name = "videoDetail",value = "视频简介",required = true),
            @ApiImplicitParam(type = "query", name = "videoGroup",value = "视频组别",required = true),
            @ApiImplicitParam(type = "query", name = "createTime",value = "视频时间",required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query", name = "updateTime",value = "视频目录时间",required = true,dataType = "java.util.Date"),
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean uploadVideo(SysVideo video, @ApiParam(value = "视频文件",required = true) MultipartFile uploadFile, HttpServletRequest request)throws IOException{
        if(uploadFile.isEmpty() || video.getVideoName()==null || video.getVideoDetail()==null||video.getVideoGroup() == null||video.getCreateTime()==null||video.getUpdateTime()==null){
            return RespBean.error("上传视频文件,视频名或视频简介为空，请填写完整");
        }
        System.out.println("time"+video.getCreateTime());
        System.out.println("group:"+video.getVideoGroup());
        System.out.println("video:"+video.getVideoDetail());
        SysVideo sysVideo = new SysVideo();
        //获取绝对路径
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/video/video/";
        String format = sdf.format(video.getUpdateTime());
        File folder = new File(realPath+format);
        //目录不存在就建一个
        if(!(folder.isDirectory())){
            folder.mkdirs();
        }
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString()+oldName.substring(oldName.lastIndexOf("."));
        try{
            uploadFile.transferTo(new File(folder,newName));
            sysVideo.setCreateTime(video.getCreateTime());
            sysVideo.setYear(video.getCreateTime().toString().substring(video.getCreateTime().toString().length()-4));
            String imgName = UUID.randomUUID().toString()+".jpg";
            //存储绝对路径
            String imgRealPath = ResourceUtils.getURL("").getPath()+"upload/video/video/";
            String imgRealPath1 = ResourceUtils.getURL("").getPath()+"upload/video/Img/";
            fetchFrame(imgRealPath+format+newName,imgRealPath1+format,imgName,request);
            //保存视频
            sysVideo.setVideoImgUrl(imgName);
            sysVideo.setVideoName(video.getVideoName());
            sysVideo.setVideoUrl(newName);
            sysVideo.setVideoDetail(video.getVideoDetail());
            sysVideo.setVideoGroup(video.getVideoGroup());
            sysVideo.setUpdateTime(video.getUpdateTime());
            sysVideo.setDelIt("0");
            sysVideo.setDelFlag((byte)0);
            videoService.saveVideo(sysVideo);
            return RespBean.ok("保存成功",sysVideo);
        }catch(Exception e){
            e.printStackTrace();
            return RespBean.error("保存失败");
        }
    }
    /**
     * 更新视频
     * @param newVideo
     * @param uploadFile
     * @param request
     * @return
     */
    @ApiOperation(value = "更新视频", notes = "更新视频")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "视频序号",required = true),
            @ApiImplicitParam(type = "query", name = "videoName",value = "视频名"),
            @ApiImplicitParam(type = "query", name = "videoDetail",value = "视频简介"),
            @ApiImplicitParam(type = "query", name = "videoGroup",value = "视频组别"),
            @ApiImplicitParam(type = "query", name = "createTime",value = "视频时间",dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志，0为正常，1为删除")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean updateVideoById(SysVideo newVideo,@ApiParam(value = "视频文件") MultipartFile uploadFile, HttpServletRequest request)throws Exception{
        if(newVideo.getId() == null || newVideo.getId().equals("")){
            return RespBean.error("序号为空，请填写完整序号！！");
        }
        //更改前视频
        SysVideo sysVideo = videoService.findVideoById(newVideo.getId());
        newVideo.setUpdateTime(sysVideo.getUpdateTime());
        if(sysVideo == null){
            return RespBean.error("该视频序号不存在");
        }
        //更改后视频
        if(uploadFile!= null){
            //视频原始名
            String oldName = uploadFile.getOriginalFilename();
            //视频保存名
            String newName = UUID.randomUUID().toString()+oldName.substring(oldName.lastIndexOf("."));
            //以日期分类存储
            String format = sdf.format(newVideo.getUpdateTime());
            //视频物理真实路径
            String videoRealPath1 = ResourceUtils.getURL("").getPath()+"/upload/video/video/";
            //视频截图名
            String imgName = UUID.randomUUID().toString()+".jpg";
           //在指定文件目录下新建文件
            File folder = new File(videoRealPath1 + format);
            //如果文件夹不存在，新建文件夹
            if(!folder.isDirectory()){
                folder.mkdirs();
            }
            try{
                //指定文件转移
                uploadFile.transferTo(new File(folder,newName));
                //视频截图保存
                //存储绝对路径
                String imgRealPath = ResourceUtils.getURL("").getPath()+"upload/video/video/";
                String imgRealPath1 = ResourceUtils.getURL("").getPath()+"upload/video/Img/";
                fetchFrame(imgRealPath+format+newName,imgRealPath1+format,imgName,request);
                //保存视频访问地址
                newVideo.setVideoUrl(newName);
                //保存视频名称
                // newVideo.setVideoName(oldName);
                //保存视频截图访问URL
                newVideo.setVideoImgUrl(imgName);
                //最初发布者
                newVideo.setCreateBy(sysVideo.getCreateBy());
                //发布时间
                //newVideo.setCreateTime(sysVideo.getCreateTime());
                //修改时间
                newVideo.setUpdateTime(sysVideo.getUpdateTime());
                //保存数据库
                newVideo.setId(sysVideo.getId());
                if(newVideo.getVideoName() == null || newVideo.getVideoName().equals("")){
                    newVideo.setVideoName(sysVideo.getVideoName());
                }
                if(uploadFile == null){
                    newVideo.setVideoUrl(sysVideo.getVideoUrl());
                }
                if(newVideo.getVideoDetail() == null || newVideo.getVideoDetail().equals("")){
                    newVideo.setVideoDetail(sysVideo.getVideoDetail());
                    //newVideo.setVideoImgUrl(sysVideo.getVideoImgUrl());
                }
                if(newVideo.getVideoGroup() == null || newVideo.getVideoGroup().equals("")){
                    newVideo.setVideoGroup(sysVideo.getVideoGroup());
                }
                if(newVideo.getCreateTime() == null || newVideo.getCreateTime().equals("")){
                    newVideo.setCreateTime(sysVideo.getCreateTime());
                    newVideo.setYear(sysVideo.getYear());
                }else{
                    newVideo.setYear(newVideo.getCreateTime().toString().substring(newVideo.getCreateTime().toString().length()-4));
                }
                if(newVideo.getDelFlag() == null){
                    newVideo.setDelFlag(sysVideo.getDelFlag());
                }
                videoService.UpdateVideoById(newVideo);
                return RespBean.ok("视频修改成功",newVideo);
            }catch (Exception e){
                e.printStackTrace();
                return RespBean.error("视频修改失败");
            }
        }else{
            //如果需要不需要修改上传文件内容
            newVideo.setId(sysVideo.getId());
            newVideo.setVideoImgUrl(sysVideo.getVideoImgUrl());
            newVideo.setUpdateTime(sysVideo.getUpdateTime());
            if(newVideo.getVideoName() == null || newVideo.getVideoName().equals("")){
                newVideo.setVideoName(sysVideo.getVideoName());
            }
            if(uploadFile == null){
                newVideo.setVideoUrl(sysVideo.getVideoUrl());
            }
            if(newVideo.getVideoDetail() == null || newVideo.getVideoDetail().equals("")){
                newVideo.setVideoDetail(sysVideo.getVideoDetail());
            }
            if(newVideo.getCreateTime() == null || newVideo.getCreateTime().equals("")){
                newVideo.setCreateTime(sysVideo.getCreateTime());
                newVideo.setYear(sysVideo.getYear());
            }else{
                newVideo.setYear(newVideo.getCreateTime().toString().substring(newVideo.getCreateTime().toString().length()-4));
            }
            if(newVideo.getDelFlag() == null){
                newVideo.setDelFlag(sysVideo.getDelFlag());
            }
            try{
                videoService.UpdateVideoById(newVideo);
                return RespBean.ok("视频修改成功",newVideo);
            }catch (Exception e){
                e.printStackTrace();
                return RespBean.error("视频修改失败");
            }
        }
    }
    /**
     * 捕捉图片第一帧
     * @param videoFile,视频的绝对地址
     * @param imgRealPath,图片的绝对地址
     * @param request
     * @throws Exception
     */
    public void fetchFrame(String videoFile,String imgRealPath,String imgName, HttpServletRequest request)throws Exception{
//
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        frameGrabber.start();
        //解码长度
        int length = frameGrabber.getLengthInFrames();
        //时间
        int i = 0;
        Frame frame = null;
        while(i < length){
            //过滤前五帧，避免出现全黑的图片
            frame = frameGrabber.grabFrame();
            if(i > 10 && (frame.image != null)){
                break;
            }
            i ++;
        }
        // Frame frame = frameGrabber.grabFrame();
        BufferedImage bufferedImage = converter.convert(frame);
        //照片保存文件夹
        File targetFile = new File(imgRealPath+"/"+imgName);
        //文件夹不存在就新建
        if(!targetFile.isDirectory()){
            targetFile.mkdirs();
        }
        //写入文件夹,以jpg图片格式
        ImageIO.write(bufferedImage, "jpg", targetFile);
        //停止转化为帧
        frameGrabber.stop();
    }
    /**
     *视频列表分页显示
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/page")
    @ApiOperation(value = "根据可用性视频列表分页显示", notes = "根据可用性视频列表分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value = "分页行数",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志",required = true)
    })
    public RespBean findVideoByPaging(Integer pageNum, Integer pageSize, Byte delFlag){
        try{
            Page<SysVideo> videoPage = videoService.selectAllByPage(pageNum, pageSize, delFlag);
            List<SysVideo> videoList = videoPage.getResult();
            for(SysVideo video : videoList){
                String articleIntro = HtmlSpirit.delHTMLTag(video.getVideoDetail());
                video.setVideoIntro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
            }
            PageResult pageResult = new PageResult();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setTotalPages(videoPage.getPages());
            pageResult.setTotalSize(videoPage.getTotal());
            pageResult.setContent(videoList);
            return RespBean.ok("返回分页信息",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("分页失败");
        }
    }
    /**
     * 单一视频内容显示
     * @param id
     * @return
     */
    @PostMapping("/details")
    @ApiOperation(value = "单一视频内容显示", notes = "单一视频内容显示")
    @ApiImplicitParam(type = "query", name = "id",value = "视频序号",required = true)
    public RespBean findVideoById(Integer id){
        try{
            SysVideo sysVideo = videoService.findVideoById(id);
            String articleIntro = HtmlSpirit.delHTMLTag(sysVideo.getVideoDetail());
            sysVideo.setVideoIntro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
            return RespBean.ok("视频详情",sysVideo);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("解析失败");
        }
    }
    /**
     * 根据年份分页获取所有视频
     * @param pageNum
     * @param pageSize
     * @param year
     * @return
     */
    @ApiOperation(value = "根据可用性，年份分页获取所有视频", notes = "根据可用性，年份分页获取所有视品")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value = "分页行数",required = true),
            @ApiImplicitParam(type = "query",name = "year",value = "查询年份",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志，0为正常，1为删除",required = true)
    })
    @PostMapping("/age")
    public RespBean findVideoByAge(Integer pageNum, Integer pageSize, SysVideo newVideo){
        try{
            Page<SysVideo> sysVideo = videoService.findVideosByAge(pageNum,pageSize,newVideo);
            List<SysVideo> videos = sysVideo.getResult();
            for(SysVideo video : videos){
                String articleIntro = HtmlSpirit.delHTMLTag(video.getVideoDetail());
                video.setVideoIntro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
            }
            PageResult pageResult = new PageResult();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setContent(videos);
            pageResult.setTotalPages(sysVideo.getPages());
            pageResult.setTotalSize(sysVideo.getTotal());
            return RespBean.ok("根据年份分页获取视频列表",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取视频列表失败");
        }
    }
    /**
     * 根据组别分页获取所有视频
     * @param pageNum
     * @param pageSize
     * @param
     * @return
     */
    @ApiOperation(value = "根据可用性，组别分页获取所有视频", notes = "根据可用性组别分页获取所有视品")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value = "分页行数",required = true),
            @ApiImplicitParam(type = "query",name = "videoGroup",value = "查询组别",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志，0为正常，1为删除",required = true)
    })
    @PostMapping("/group")
    public RespBean findVideoByGroup(Integer pageNum, Integer pageSize, SysVideo newVideo){

        try{
            Page<SysVideo> sysVideoPage = videoService.findVideoByGroup(pageNum,pageSize,newVideo);
            List<SysVideo> videos = sysVideoPage.getResult();
            for(SysVideo sysVideo : videos){
                System.out.println("sysVideo.getVideoGroup"+sysVideo.getVideoGroup());
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysVideo.getVideoGroup()));
                sysVideo.setVideoGroupZh(sysGroup.getGroupName());
                String articleIntro = HtmlSpirit.delHTMLTag(sysVideo.getVideoDetail());
                sysVideo.setVideoIntro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
            }
            PageResult pageResult = new PageResult();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setTotalPages(sysVideoPage.getPages());
            pageResult.setTotalSize(sysVideoPage.getTotal());
            pageResult.setContent(videos);
            return RespBean.ok("分组获取视频成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("分组获取视频失败");
        }
    }

    /**
     * 获取视频所属所有年份
     * @return
     */
    @ApiOperation(value = "获取视频所属的所有年份",notes = "获取视频所属逇所有年份")
    @PostMapping("/ages")
    public RespBean findAllVideoAges(){
        List<String> ages = videoService.findAllAges();
        return RespBean.ok("获取视频全部所属年份",ages);
    }
    /**
     * 工具类:风采图片保存
     * @param multipartFile
     * @param request
     * @return
     */
    public String findImgName(MultipartFile multipartFile,Date imgDate, HttpServletRequest request)throws IOException {
        //文件
        SysImg sysImg = new SysImg();
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/video/Img/";
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


    @ApiOperation(value = "更新视频可用性", notes = "更新视频可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "视频序号",required = true),

            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志，0为正常，1为删除")
    })
    @PostMapping(value = "/update/delFlag",headers = "content-type=multipart/form-data")
    public RespBean updateStatus(SysVideo sysVideo){
        try{

            videoService.updateDelFlag(sysVideo);
            return RespBean.ok("更新成功");
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("更新失败");
        }
    }
    @ApiOperation(value = "流式返回视频",notes = "流式返回视频")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "视频ID")
    })
    @GetMapping("/showVideo/{id}")
    public RespBean showVideo(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response)throws IOException{
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("video/mp4");
        FileInputStream fis = null;
        OutputStream os = null ;
        System.out.println("id:"+id);
        SysVideo sysVideo = videoService.findVideoById(id);
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/video/video/";
        String format = sdf.format(sysVideo.getUpdateTime());
        File file = new File(realPath+format,sysVideo.getVideoUrl());
        fis = new FileInputStream(file);
        int size = fis.available(); // 得到文件大小
        byte data[] = new byte[size];
        fis.read(data); // 读数据
        fis.close();
        fis = null;
        response.setContentType("video/mp4"); // 设置返回的文件类型
        os = response.getOutputStream();
        os.write(data);
        try{
            os.flush();
        }finally {
            os.close();
        }
        return null;
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

        SysVideo sysVideo = videoService.findVideoById(id);
        String format = sdf.format(sysVideo.getUpdateTime());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/video/Img/";
        File file = new File(realPath+format,sysVideo.getVideoImgUrl());
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
