package cn.hctech2006.hcnet.controller;

import cn.hctech2006.hcnet.bean.*;
import cn.hctech2006.hcnet.service.AchieveService;
import cn.hctech2006.hcnet.service.ArticleService;
import cn.hctech2006.hcnet.service.ImgService;
import cn.hctech2006.hcnet.util.HtmlSpirit;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import org.bytedeco.javacv.FrameFilter;
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

@Api(tags = "风采传输接口")
@RestController
@RequestMapping("/elegant")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private AchieveService achieveService;
    /**
     *风采详情
     * @param id
     * @return
     */
    @ApiOperation(value = "风采详情",notes = "根据主键查询风采")
    @ApiImplicitParam(type = "query",name = "id",value = "风采序号",required = true)
    @PostMapping("/details")
    public RespBean findBlogById(String id){
        if(id == null){
            return RespBean.error("序号为空");
        }
        SysArticle sysArticle = articleService.findById(Integer.parseInt(id));
        //阅读量加一
        if(sysArticle.getArticleReadtime() == null){
            sysArticle.setArticleReadtime(0);
        }
        sysArticle.setArticleReadtime(sysArticle.getArticleReadtime() + 1);
        //重新保存
        articleService.uploadArticleById(sysArticle);
        return RespBean.ok("返回博客实体", sysArticle);
    }
    /**
     *上传风采
     * @param article
     * @return
     */
    @ApiOperation(value = "上传风采",notes = "添加风采")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "articleName",value = "风采名",required = true),
            @ApiImplicitParam(type = "query", name = "articleAuthor",value = "风采作者",required = true),
            @ApiImplicitParam(type = "query", name =  "articleCt",value = "风采时间",required = true,dataType = "java.sql.Date"),
            @ApiImplicitParam(type = "query",name = "articleUt",value = "文件目录时间",required = true,dataType = "java.util.Date"),
            @ApiImplicitParam(type = "query", name = "articleContent",value = "风采内容",required = true),
            @ApiImplicitParam(type = "query", name = "articleAward",value = "风采奖项",required = true),
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public RespBean saveBlog(@ApiParam(value = "风采图片",required = true) MultipartFile image, SysArticle article,HttpServletRequest request)throws IOException {
        if(image.isEmpty() || article.getArticleName() == null|| article.getArticleAuthor() == null|| article.getArticleAward() == null || article.getArticleCt()
                == null || article.getArticleUt() == null ){
            return RespBean.error("标题图片,风采标题，风采作者,风采时间或风采奖项为空，请填写完整");
        }
        System.out.println("进入保存");
        article.setYear(article.getArticleCt().toString().substring(article.getArticleCt().toString().length()-4));
        article.setArticleImg(findImgName(image,article.getArticleUt(),request));
        try{
            article.setDelFlag((byte)0);
            articleService.saveArticle(article);
            System.out.println("成功");
            return RespBean.ok("保存成功",article);
        }catch (Exception e){
            System.out.println("失败");
            e.printStackTrace();
            return RespBean.error("保存失败");
        }
    }
    /**
     * 修改风采
     * @param image
     * @param article
     * @param request
     * @return
     */
    @ApiOperation(value = "修改风采",notes = "修改风采")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id",value = "风采序号",required = true),
            @ApiImplicitParam(type = "query", name = "articleName",value = "风采名"),
            @ApiImplicitParam(type = "query", name = "articleAuthor",value = "风采作者"),
            @ApiImplicitParam(type = "query", name =  "articleCt",value = "风采时间",dataType = "java.sql.Date"),
            @ApiImplicitParam(type = "query", name = "articleContent",value = "风采内容"),
            @ApiImplicitParam(type = "query", name = "articleAward",value = "风采奖项"),
            @ApiImplicitParam(type = "query", name = "articleGroup", value = "风采组别"),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志")
    })
    @PostMapping(value = "/update",headers = "content-type=multipart/form-data")
    public RespBean uplateArticle(@ApiParam(value = "风采图片") MultipartFile image, SysArticle article,HttpServletRequest request)throws Exception{
        if(article.getId() == null || article.getId().equals("")){
            return RespBean.error("风采序号为空,请重新修改");
        }
       // System.out.println("time"+article.getArticleCt());
        try{
            //获得以前的风采信息
            SysArticle sysArticle = articleService.findById(article.getId());
            if(sysArticle == null){
                return RespBean.error("该风采不存在，请填写正确的序号");
            }
            //风采基本信息匹配
            article.setId(sysArticle.getId());
            //article.setArticleCt(sysArticle.getArticleCt());
            article.setArticleUt(sysArticle.getArticleUt());
            if(article.getArticleCt() == null){
                article.setArticleCt(sysArticle.getArticleCt());
                article.setYear(sysArticle.getYear());
            }else{
                article.setYear(article.getArticleCt().toString().substring(article.getArticleCt().toString().length()-4));
            }
            if(article.getArticleContent() == null || article.getArticleContent().equals("")){
                article.setArticleContent(sysArticle.getArticleContent());
            }
            if(article.getArticleName() == null || article.getArticleName().equals("")){
                article.setArticleName(sysArticle.getArticleName());
            }
            if(article.getArticleAuthor() == null || article.getArticleAuthor().equals("")){
                article.setArticleAuthor(sysArticle.getArticleAuthor());
            }
            if(article.getArticleAward() == null || article.getArticleAward().equals("")){
                article.setArticleAward(sysArticle.getArticleAward());
            }
            if(image == null || image.isEmpty()){
                article.setArticleImg(sysArticle.getArticleImg());
            } else{
                //图片不为空的话执行
                article.setArticleImg(findImgName(image,article.getArticleUt(),request));
            }
            if (article.getDelFlag() == null){
                article.setDelFlag(sysArticle.getDelFlag());
            }
            if(article.getArticleGroup() == null){
                article.setArticleGroup(sysArticle.getArticleGroup());
            }
            articleService.uploadArticleById(article);
            System.out.println("time"+article.getArticleCt());
            return RespBean.ok("修改成功",article);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改失败");
        }
    }
SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    /**
     * 根据年份分页获取所有风采
     * @param pageNum
     * @param pageSize
     * @param
     * @return
     */
    @ApiOperation(value = "根据年份可用性分页获取所有风采", notes = "根据年份可用性分页获取所有风采")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value =  "分页行数",required = true),
            @ApiImplicitParam(type = "query",name = "year",value = "查询年份",required = true),
            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
    })
    @PostMapping("/age")
    public RespBean findArticlesByAge(Integer pageNum,Integer pageSize,SysArticle sysArticle){
        Page<SysArticle> articles = articleService.findArticleByAge(pageNum,pageSize,sysArticle);
        List<SysArticle> articleList = articles.getResult();
        for(SysArticle article : articleList){

            String articleIntro = HtmlSpirit.delHTMLTag(article.getArticleContent());
            article.setArticle_intro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
        }
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setContent(articleList);
        pageResult.setTotalPages(articles.getPages());
        pageResult.setTotalSize(articles.getTotal());
        return RespBean.ok("根据年份分页获取所有的风采",pageResult);
    }

    /**
     * 获取风采所有年份
     * @return
     */
    @ApiOperation(value = "获取风采所属的所有年份",notes = "获取风采所属逇所有年份")
    @PostMapping("/ages")
    public RespBean findAllArticleAges(){
        List<String> ages = articleService.findAllAges();
        return RespBean.ok("获取风采全部所属年份",ages);
    }
    @ApiOperation(value = "根据可用性分组分页获取所有风采")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "pageNum",value = "分页页码",required = true),
            @ApiImplicitParam(type = "query", name = "pageSize",value =  "分页行数",required = true),
            @ApiImplicitParam(type = "query",name = "articleGroup",value = "查询组别",required = true),

            @ApiImplicitParam(type = "query", name = "delFlag", value = "删除标志",required = true)
    })
    @PostMapping("/group")
    public RespBean findArticlesByGroup(Integer pageNum,Integer pageSize,SysArticle sysArticle){
        Page<SysArticle> articles = articleService.findArticleByGroup(pageNum,pageSize,sysArticle);
        List<SysArticle> articleList = articles.getResult();
        for(SysArticle article : articleList){
            String articleIntro = HtmlSpirit.delHTMLTag(article.getArticleContent());
            article.setArticle_intro((articleIntro.length() < 100)?articleIntro:articleIntro.substring(0,100));
        }
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setContent(articleList);
        pageResult.setTotalPages(articles.getPages());
        pageResult.setTotalSize(articles.getTotal());
        return RespBean.ok("根据可用性分组分页获取所有的风采",pageResult);
    }
    @ApiOperation(value = "修改风采可用性",notes = "修改风采可用性")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "id",value = "风采序号",required = true),

            @ApiImplicitParam(type = "query", name = "delFlag",value = "是否显示，0为正常，1为删除",required = true)
    })
    @PostMapping(value = "/update/delFlag")
    public RespBean updateStatus(SysArticle sysArticle){
        try{
            System.out.println("delFlag:"+sysArticle.getDelFlag());
            articleService.updateStatus(sysArticle);
            return RespBean.ok("修改可用性成功",sysArticle);
        }catch (Exception e){
            e.printStackTrace();
            return RespBean.error("修改可用性失败");
        }
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
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/article/Img/";
        //格式化时间,以时间分类
        String format = sdf.format(imgDate);
        //获得文件所在目
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
    public RespBean showImage(Integer id, HttpServletRequest request,HttpServletResponse response)throws IOException{
        System.out.println("id:"+id);
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //获得系统根目录

        SysArticle sysArticle = articleService.findById(id);
        String format = sdf.format(sysArticle.getArticleUt());
        String realPath = ResourceUtils.getURL("").getPath()+"/upload/article/Img/";
        File file = new File(realPath+format,sysArticle.getArticleImg());
        System.out.println(sysArticle.getArticleImg());
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




