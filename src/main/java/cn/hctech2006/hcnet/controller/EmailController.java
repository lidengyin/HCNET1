package cn.hctech2006.hcnet.controller;
import cn.hctech2006.hcnet.bean.PageResult;
import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysEmail;
import cn.hctech2006.hcnet.service.EmailService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
@Api(tags = "邮件信息接口")
@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @ApiOperation(value = "上传邮件", notes = "上传邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "emailName", value = "联系人", required = true),
            @ApiImplicitParam(type = "query", name = "emailAccount", value = "邮件账户", required = true),
            @ApiImplicitParam(type = "query", name = "emailContent", value = "邮件内容",required = true)
    })
    @PostMapping("/upload")
    public RespBean uplaodEmail(SysEmail sysEmail){
        try{
            sysEmail.setCreateTime(new Date());
            sysEmail.setLastUpdateTime(new Date());
            sysEmail.setCtreateBy("李登印");
            sysEmail.setLastUpdateBy("李登印");
            sysEmail.setDelFlag((byte)0);
            System.out.println("邮件内容:"+sysEmail.getEmailContent());
            emailService.uploadEmail(sysEmail);
            return RespBean.ok("上传成功", sysEmail);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("上传失败");
        }
    }
    @ApiOperation(value = "返回邮件列表", notes = "查找所有邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum", value = "页数",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize", value = "每页行数", required =
            true)
    })
    @PostMapping("/find/all")
    public RespBean findAllEmail(Integer pageNum, Integer pageSize){
        try{
            Page<SysEmail> emails = emailService.findAllSmail(pageNum,pageSize);
            List<SysEmail> emails1 = emails.getResult();
            PageResult pageResult = new PageResult();
            pageResult.setContent(emails1);
            pageResult.setTotalSize(emails.getTotal());
            pageResult.setTotalPages(emails.getPages());
            pageResult.setPageSize(pageSize);
            pageResult.setPageNum(pageNum);
            return RespBean.ok("返回邮件列表成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("返回邮件列表失败");
        }
    }
    @ApiOperation(value = "根据状态信息返回邮件列表", notes = "删除标志0为可用,1位删除")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum", value = "页数",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize", value = "每页行数", required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0为正常,1为删除",required = true)
    })
    @PostMapping("/find/status")
    public RespBean findByStatus(Byte delFlag, Integer pageNum, Integer pageSize){
        try{
            Page<SysEmail> emails = emailService.findByDelFlag(delFlag, pageNum,pageSize);
            List<SysEmail> emails1 = emails.getResult();
            PageResult pageResult = new PageResult();
            pageResult.setContent(emails1);
            pageResult.setTotalSize(emails.getTotal());
            pageResult.setTotalPages(emails.getPages());
            pageResult.setPageSize(pageSize);
            pageResult.setPageNum(pageNum);
            return RespBean.ok("返回邮件列表成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("返回邮件列表失败");
        }
    }
    @ApiOperation(value = "根据ID获取邮件", notes = "根据ID获取邮件")
    @ApiImplicitParam(type = "query", name = "id", value = "邮件ID", required = true)
    @PostMapping("/find/one")
    public RespBean findById(Integer id){



        try{
            SysEmail sysEmail = emailService.findById(id);
            return RespBean.ok("根据ID获取邮件成功", sysEmail);

        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("根据ID获取邮件失败");
        }
    }
    @ApiOperation(value = "删除邮件", notes = "删除邮件")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "ids", value = "邮件编号", required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志,0正常,1删除", required = true)

    })
    @PostMapping("/update/del")
    public RespBean updateDelFlag(String ids, Byte delFlag){
        try{
            System.out.println(ids);
            String[] strs = ids.split(",");
            for(int i = 0; i < strs.length; i ++){
                SysEmail sysEmail = new SysEmail();
                sysEmail.setId(Integer.parseInt(strs[i]));
                sysEmail.setDelFlag(delFlag);
                emailService.updateDelFlag(sysEmail);
            }

            return RespBean.ok("删除邮件成功");
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("删除邮件失败");
        }
    }
    }
