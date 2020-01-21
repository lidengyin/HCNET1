package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.*;
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
@Api(tags = "成员信息接口")
@RestController
@RequestMapping("/mem")
public class MemController {
    @Autowired
    private MemService memService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private DirectService directService;
    @Autowired
    private MajorService majorService;
    /**
     * 分组获取成员信息
     * @param group
     * @return
     */
    @ApiOperation(value = "分组获取成员信息",notes = "分组获取成员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum", value = "页数",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize", value = "页码",required = true),
            @ApiImplicitParam(type = "query",name = "group",value = "分组数字",required = true)
    })
    @PostMapping("/find/group")
    public RespBean findMemByGroup(String group, Integer pageNum, Integer pageSize){
        if(group == null){
            return RespBean.error("请填写组别");
        }
        try{
            Page<SysMem> mems = memService.findMemByGroup(group,pageNum,pageSize);
            List<SysMem> mems1 = mems.getResult();
            for(SysMem sysMem: mems1){
                System.out.println("\n组员分组:"+sysMem.getMemDirect());
                System.out.println("\n组员方向:"+sysMem.getMemGroup());
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));
                if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                    sysMem.setMemDirectZh("无组别状态");
                    sysMem.setMemGroup("4");
                    memService.updateMem(sysMem);
                }else{
                    sysMem.setMemGroupZh(sysGroup.getGroupName());
                }
                SysDirect sysDirect = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
                sysMem.setMemDirectZh(sysDirect.getDirectName());

                //SysMajor sysMajor = majorService.findMajorById(Integer.parseInt(sysMem.getMemMajor()));
                //.setMemMajorZh(sysMajor.getMajor());
            }
            PageResult pageResult = new PageResult();
            pageResult.setContent(mems1);
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            pageResult.setTotalPages(mems.getPages());
            pageResult.setTotalSize(mems.getTotal());

            return RespBean.ok("成功获取该组全部组员",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
            return RespBean.error("获取组员失败");
        }
    }

    /**
     * 上传成员信息
     * @param sysMem
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "上传成员信息",notes = "上传成员信息")
    @ApiImplicitParams({

            @ApiImplicitParam(type = "query",name = "memName",value = "成员名",required = true),
            @ApiImplicitParam(type = "query",name = "memDetail",value = "成员介绍",required = true),
            @ApiImplicitParam(type = "query",name = "memDirect",value = "成员方向,枚举代替",required = true),
            @ApiImplicitParam(type = "query",name = "memGroup",value = "成员组别,枚举代替",required = true),
            @ApiImplicitParam(type = "query",name = "memMajor",value = "成员专业,枚举代替",required = true),
            @ApiImplicitParam(type = "query",name = "memBegin",value = "成员进入时间",required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query",name = "memCt",value = "图片目录时间",required = true,dataType = "java.util.Date")
            //@ApiImplicitParam(type = "query",name = "file",value = "成员图片",required = true)
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean uploadMem(SysMem sysMem, @ApiParam(value = "成员头像",required = true) MultipartFile file, HttpServletRequest request){
        //sysMem.getMemIsenable();
        sysMem.setMemIsenable("1");
        if(file.isEmpty()){
            return RespBean.error("上传文件为空,请重新上传文件");
        }
        if(sysMem.getMemName() == null || sysMem.getMemDirect() == null || sysMem.getMemGroup() == null || sysMem.getMemDetail() == null || sysMem.getMemBegin() == null ||sysMem.getMemCt()==null){
            return RespBean.error("参数不完整,请重新填写");
        }
        try{
            sysMem.setMemHead(findImgName(file,sysMem.getMemCt(),request));
            memService.saveMem(sysMem);
            return RespBean.ok("成员上传成功",sysMem);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("成员上传失败");
        }
    }
    /**
     * 修改成员信息
     * @param newMem
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "修改成员信息",notes = "修改成员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "成员序号",required = true),
            @ApiImplicitParam(type = "query",name = "memName",value = "成员名"),
            @ApiImplicitParam(type = "query",name = "memDetail",value = "成员介绍"),
            @ApiImplicitParam(type = "query",name = "memDirect",value = "成员方向,枚举代替"),
            @ApiImplicitParam(type = "query",name = "memGroup",value = "成员组别,枚举代替"),
            @ApiImplicitParam(type = "query",name = "memMajor",value = "成员专业,枚举代替"),
            @ApiImplicitParam(type = "query",name = "memBegin",value = "成员进入时间")
            //@ApiImplicitParam(type = "query",name = "file",value = "成员名")
    })
    @PostMapping(value = "/update/all",headers = "content-type=multipart/form-data")
    public RespBean updateMem(SysMem newMem, @ApiParam(value = "file") MultipartFile file, HttpServletRequest request)throws Exception{
        if(newMem.getId() == null){
            return RespBean.error("成员序号为空,请重新填写");
        }
        try{
            SysMem sysMem = memService.findMemById(newMem.getId());
            newMem.setMemIsenable(sysMem.getMemIsenable());
            newMem.setMemCt(sysMem.getMemCt());
            if(newMem.getMemName() == null){
                newMem.setMemName(sysMem.getMemName());
            }
            if(newMem.getMemDirect() == null){
                newMem.setMemDirect(sysMem.getMemDirect());
            }
            if(newMem.getMemGroup() == null){
                newMem.setMemGroup(sysMem.getMemGroup());
            }
            if(newMem.getMemMajor() == null){
                newMem.setMemMajor(sysMem.getMemMajor());
            }
            if(file == null ){
                newMem.setMemHead(sysMem.getMemHead());
            }else{
                System.out.println("文件不为空");
                newMem.setMemHead(findImgName(file,newMem.getMemCt(),request));
            }
            if(newMem.getMemBegin() == null){
                newMem.setMemBegin(sysMem.getMemBegin());
            }
            memService.updateMem(newMem);
            return RespBean.ok("成员修改成功",newMem);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("成员修改失败");
        }
    }

    /**
     * "分页获取全部未知组别的成员"
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页获取全部未知组别的成员",notes = "分页获取全部未知组别的成员,组别未知为4")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "pageNum",value = "分页页数",required = true),
            @ApiImplicitParam(type = "query",name = "pageSize", value = "分页行数",required = true)
    })
    @PostMapping("/find/ungroup")
    public RespBean findAllUnknownGroupMem(Integer pageNum, Integer pageSize){
        try{
            Page<SysMem> sysMems =  memService.findAllUnknownGroupMemByPage(pageNum, pageSize);
            List<SysMem> sysMemList = sysMems.getResult();
            for(SysMem sysMem : sysMems){
                String year = sysMem.getMemBegin().toString().substring(sysMem.getMemBegin().toString().length() - 4,sysMem.getMemBegin().toString().length())+"届";
                sysMem.setMemBeginZh(year);
                System.out.println("direct"+Integer.parseInt(sysMem.getMemDirect()));
                SysDirect sysDirect  = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
                System.out.println("group"+sysMem.getMemGroup());
                sysMem.setMemDirectZh(sysDirect.getDirectName());
                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));
                sysMem.setMemGroupZh(sysGroup.getGroupName());
                //SysMajor sysMajor = majorService.findMajorById(Integer.parseInt(sysMem.getMemMajor()));
                //sysMem.setMemMajorZh(sysMajor.getMajor());
            }
            PageResult result = new PageResult();
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setContent(sysMemList);
            result.setTotalSize(sysMems.getTotal());
            result.setTotalPages(sysMems.getPages());
            return RespBean.ok("成功获取所有未知组别成员",result);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取所有未知组别成员失败");
        }
    }

    /**
     * 分组获取离开成员名单
     * @param group
     * @return
     */
    @ApiOperation(value = "分页分组获取离开成员名单",notes = "显示enabled为0的成员名单")
    @ApiImplicitParam(type = "query", name = "group",value = "分组组别",required = true)
    @PostMapping("/find/leave/group")
    public RespBean findLeaveMemByGroup(String group){
        if(group == null){
            return RespBean.error("组别为空,请填写完整");
        }
        try{
            List<SysMem> sysMems = memService.findLeaveMemByGroup(group);
            for(SysMem sysMem : sysMems){
                String year = sysMem.getMemBegin().toString().substring(sysMem.getMemBegin().toString().length() - 4,sysMem.getMemBegin().toString().length())+"届";
                sysMem.setMemBeginZh(year);
                System.out.println("direct"+Integer.parseInt(sysMem.getMemDirect()));
                SysDirect sysDirect  = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
                System.out.println("group"+sysMem.getMemGroup());
                sysMem.setMemDirectZh(sysDirect.getDirectName());

                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));

                if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                    sysMem.setMemDirectZh("无组别状态");
                    sysMem.setMemGroup("4");
                    memService.updateMem(sysMem);
                }else{
                    sysMem.setMemGroupZh(sysGroup.getGroupName());
                }
//                SysMajor sysMajor = majorService.findMajorById(Integer.parseInt(sysMem.getMemMajor()));
//                sysMem.setMemMajorZh(sysMajor.getMajor());
            }
            return RespBean.ok("成功分组获取离开成员的名单",sysMems);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("分组获取离开成员名单失败");
        }
    }
    /**
     * 修改成员状态
     *
     *
     *
     *
     * @param sysMem
     * @return
     */
    @ApiOperation(value = "修改成员状态",notes = "修改成员状态,isenable=1为可用,=0为不可用,如不可用,则离开时间一并修改")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "组员ID",required = true),
            @ApiImplicitParam(type = "query",name = "memIsenable",value = "组员状态，1为可用,=0为不可用",required = true)
    })
    @PostMapping("/update/status")
    public RespBean updateMemStatus(SysMem sysMem){
        if(sysMem.getMemIsenable() == null ||sysMem.getId() == null){
            return RespBean.error("参数不全,请填写完整");
        }
        try{
            memService.updateMemIsEnabled(sysMem);

            //确定离开时间
            if(sysMem.getMemIsenable() == "0"){
                sysMem.setMemEnd(new Date());
                memService.updateMemLeaveTime(sysMem);
            }
            return RespBean.ok("修改成员状态成功",sysMem);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改成员状态失败");
        }
    }

    /**
     *修改成员组别
     * @param sysMem
     * @return
     */
    @ApiOperation(value = "修改成员组别",notes = "修改成员组别,group=4为无组别")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "组员ID",required = true),
            @ApiImplicitParam(type = "query",name = "memGroup",value = "组员组别",required = true)
    })
    @PostMapping("/update/group")
    public RespBean updateMemGroup(SysMem sysMem){
        if(sysMem.getId() == null || sysMem.getMemGroup() == null){
            return RespBean.error("缺少参数,请填写完整");
        }
        try{
            memService.updateMemGroup(sysMem);
            return RespBean.ok("修改成员组别成功",sysMem);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改成员组别失败");
        }
    }
    @ApiOperation(value = "分页获取所有组员")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "pageNum",value = "当前页数",required = true),
            @ApiImplicitParam(type = "query",name = "pageSize",value = "每页大小",required = true)
    })
    @PostMapping("/find/all")
    public RespBean findAllMem(Integer pageNum, Integer pageSize){
        try{
            Page<SysMem> sysMemPage = memService.findAllMem(pageNum,pageSize);
            List<SysMem> sysMems = sysMemPage.getResult();
            for(SysMem sysMem : sysMems){
                String year = sysMem.getMemBegin().toString().substring(sysMem.getMemBegin().toString().length() - 4,sysMem.getMemBegin().toString().length())+"届";
                sysMem.setMemBeginZh(year);
                System.out.println("direct"+Integer.parseInt(sysMem.getMemDirect()));
                SysDirect sysDirect  = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
                System.out.println("group"+sysMem.getMemGroup());
                sysMem.setMemDirectZh(sysDirect.getDirectName());

                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));
                System.out.println("groupEnabled:"+sysGroup.getGroupIsenabled());
                if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                    sysMem.setMemDirectZh("无组别状态");
                    sysMem.setMemGroup("4");
                    memService.updateMem(sysMem);
                }else{
                    sysMem.setMemGroupZh(sysGroup.getGroupName());
                }
//                SysMajor sysMajor = majorService.findMajorById(Integer.parseInt(sysMem.getMemMajor()));
//                sysMem.setMemMajorZh(sysMajor.getMajor());
            }
            PageResult pageResult = new PageResult();
            pageResult.setContent(sysMems);
            pageResult.setTotalPages(sysMemPage.getPages());
            pageResult.setTotalSize(sysMemPage.getTotal());
            pageResult.setPageSize(pageSize);
            pageResult.setPageNum(pageNum);
            return RespBean.ok("获取所有成员",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取全部成员失败");
        }
    }
    @ApiOperation(value = "分页获取所有可用组员")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "pageNum",value = "当前页数",required = true),
            @ApiImplicitParam(type = "query",name = "pageSize",value = "每页大小",required = true)
    })
    @PostMapping("/find/all/enabled")
    public RespBean findAllMemEnabled(Integer pageNum, Integer pageSize){
        try{
            Page<SysMem> sysMemPage = memService.findAllMemEnabled(pageNum,pageSize);
            List<SysMem> sysMems = sysMemPage.getResult();
            for(SysMem sysMem : sysMems){
                String year = sysMem.getMemBegin().toString().substring(sysMem.getMemBegin().toString().length() - 4,sysMem.getMemBegin().toString().length())+"届";
                sysMem.setMemBeginZh(year);
                System.out.println("direct"+Integer.parseInt(sysMem.getMemDirect()));
                SysDirect sysDirect  = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
                System.out.println("group"+sysMem.getMemGroup());
                sysMem.setMemDirectZh(sysDirect.getDirectName());

                SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));
                System.out.println("groupEnabled:"+sysGroup.getGroupIsenabled());
                if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                    sysMem.setMemDirectZh("无组别状态");
                    sysMem.setMemGroup("4");
                    memService.updateMem(sysMem);
                }else{
                    sysMem.setMemGroupZh(sysGroup.getGroupName());
                }
//                SysMajor sysMajor = majorService.findMajorById(Integer.parseInt(sysMem.getMemMajor()));
//                sysMem.setMemMajorZh(sysMajor.getMajor());
            }
            PageResult pageResult = new PageResult();
            pageResult.setContent(sysMems);
            pageResult.setTotalPages(sysMemPage.getPages());
            pageResult.setTotalSize(sysMemPage.getTotal());
            pageResult.setPageSize(pageSize);
            pageResult.setPageNum(pageNum);
            return RespBean.ok("获取所有可用成员",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取全部成员失败");
        }
    }
    @ApiOperation(value = "根据ID获取成员",notes = "根据ID获得成员")
    @ApiImplicitParam(type = "query",name = "id",required = true,value = "成员ID")
    @PostMapping("/find/id")
    public RespBean findMemById(Integer id){
        if(id == null ){
            return RespBean.error("ID为空，请填写");
        }
        try{
            SysMem sysMem = memService.findMemById(id);
            SysDirect sysDirect  = directService.findDirectById(Integer.parseInt(sysMem.getMemDirect()));
            System.out.println("group"+sysMem.getMemGroup());
            sysMem.setMemDirectZh(sysDirect.getDirectName());
            SysGroup sysGroup = groupService.findGroupById(Integer.parseInt(sysMem.getMemGroup()));
            System.out.println("groupEnabled:"+sysGroup.getGroupIsenabled());
            if(sysGroup.getGroupIsenabled() == "0" || sysGroup.getGroupIsenabled().equals("0")){
                sysMem.setMemDirectZh("无组别状态");
                sysMem.setMemGroup("4");
                memService.updateMem(sysMem);
            }else{
                sysMem.setMemGroupZh(sysGroup.getGroupName());
            }
            return RespBean.ok("获取成员成功",sysMem);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取成员失败");
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
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/mem/Img/";
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

        SysMem sysMem = memService.findMemById(id);
        String format = sdf.format(sysMem.getMemCt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/mem/Img/";
        File file = new File(realPath+format,sysMem.getMemHead());
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
