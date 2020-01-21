package cn.hctech2006.hcnet.controller;
import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysDirect;
import cn.hctech2006.hcnet.service.DirectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@Api(tags = "方向信息接口")
@RestController
@RequestMapping("/direct")
public class DirectController {
    @Autowired
    private DirectService directService;
    @ApiOperation(value = "根据序号获取方向信息",notes = "根据序号获取方向信息")
    @ApiImplicitParam(type = "query",name = "id",value = "方向序号")
    @PostMapping("/one")
    public RespBean findDirectById(Integer id){
        if(id == null){
            return RespBean.error("序号为空，请重新填写");
        }
        try{
            SysDirect sysDirect = directService.findDirectById(id);
            return RespBean.ok("成功获取方向信息",sysDirect);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("获取方向信息失败");
        }
    }
//    @ApiOperation(value = "根据可用性获取全部方向",notes = "根据可用性获取全部方向")
//    @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志,0正常,1删除")
//    @PostMapping("/all")
//    public RespBean findAllDirect(Byte delFlag){
//        try{
//            List<SysDirect> sysDirects = directService.findAllDirect(delFlag);
//            return RespBean.ok("获取全部方向",sysDirects);
//        }catch (Exception e){
//            e.printStackTrace();
//            return RespBean.error("获取全部方向失败");
//        }
//    }
    @ApiOperation(value = "分组根据可用性获取方向",notes = "参数可选.分组根据可用性获取方向,输入数字,")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "directGroup",value = "组别"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志"),
    })
    @PostMapping("/group/one")
    public RespBean findDirectByGroup(SysDirect sysDirect){
        try{
            System.out.println("delFlag:"+sysDirect.getDelFlag());
            System.out.println("directGroup:"+sysDirect.getDirectGroup());
            List<SysDirect> directs = directService.findAllDirectByGroup(sysDirect);
            return RespBean.ok("分组获取方向成功",directs);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("分组获取方向失败");
        }
    }
    @ApiOperation(value = "改变方向可用性",notes = "改变方向可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query",name = "id",value = "方向ID",required = true),
            @ApiImplicitParam(type = "query",name = "delFlag",value = "方向可用性",required = true)
    })
    @PostMapping("/update/delFlag")
    public RespBean updateDirectEnable(SysDirect sysDirect){
        try{
            directService.updateDirectEnable(sysDirect);
            return RespBean.ok("成功改变可用性",sysDirect);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("改变可用性失败");
        }
    }
//    @ApiOperation(value = "方向改变分组",notes = "方向改变分组")
//    @ApiImplicitParams({
//            @ApiImplicitParam(type = "query",name = "id",value = "方向ID",required = true),
//            @ApiImplicitParam(type = "query",name = "directGroup",value = "方向分组",required = true)
//    })
//    @PostMapping("/group/update")
//    public RespBean updateDirectGroup(SysDirect sysDirect){
//        if(sysDirect.getDirectGroup() == null || sysDirect.getId() == null){
//            return RespBean.error("参数不全,请补全参数");
//        }
//        try{
//            directService.updateDirectGroup(sysDirect);
//            return RespBean.ok("成功改变方向分组",sysDirect);
//        }catch (Exception e){
//            e.printStackTrace();
//            return RespBean.error("改变方向分组失败");
//        }
//    }
    @ApiOperation(value = "上传方向",notes = "上传方向")
    @ApiImplicitParams({

            @ApiImplicitParam(type = "query",name = "directName",value = "方向名称",required = true),
            @ApiImplicitParam(type = "query",name = "directGroup",value = "方向组别",required = true)
    })
    @PostMapping("/upload")
    public RespBean uploadDirect(SysDirect sysDirect){
        if( sysDirect.getDirectGroup() == null || sysDirect.getDirectName() == null){
            return RespBean.error("参数不全,请填写完整");
        }
        try{
            //sysDirect.setDirectEnable("1");
            sysDirect.setDelFlag((byte)0);
            directService.saveDirect(sysDirect);
            return RespBean.ok("方向上传成功",sysDirect);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("方向上传失败");
        }
    }
    @ApiOperation(value = "更改方向包括分组以及可用性", notes = "更改方向包括分组以及可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id", value = "方向ID", required = true),
            @ApiImplicitParam(type = "query", name = "directGroup", value = "组别数字"),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志,0为正常,1位删除"),
            @ApiImplicitParam(type = "query", name = "directName",value = "方向名")
    })
    @PostMapping("/update/three")
    public RespBean updateDirectName(SysDirect newDirect){
        try{
            //获取原始数据
            SysDirect sysDirect = directService.findDirectById(newDirect.getId());
            //newDirect.setDirectEnable(sysDirect.getDirectEnable());
            if(newDirect.getDelFlag() == null){
                newDirect.setDelFlag(sysDirect.getDelFlag());
            }
            if(newDirect.getDirectGroup() == null){
                newDirect.setDirectGroup(sysDirect.getDirectGroup());
            }
            if(newDirect.getDirectName() == null){
                newDirect.setDirectName(sysDirect.getDirectName());
            }
                directService.updateName(newDirect);
                return RespBean.ok("修改成功",newDirect);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改失败");
        }
    }
}
