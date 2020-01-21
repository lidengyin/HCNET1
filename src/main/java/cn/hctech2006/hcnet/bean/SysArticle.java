package cn.hctech2006.hcnet.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SysArticle implements Serializable {
    private Integer id;

    private String articleName;

    private String articleImg;

    private String articleAuthor;

    private String articleAward;

    private Integer articleReadtime;

    private String articleGroup;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date articleCt;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date articleUt;

    private String year;

    private String articleContent;

    private String article_intro;

    private String article_group;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    private String createBy;

    private String lastUpdateBy;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastUpdateTime;

    private Byte delFlag;
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleImg() {
        return articleImg;
    }

    public void setArticleImg(String articleImg) {
        this.articleImg = articleImg;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public void setArticleAuthor(String articleAuthor) {
        this.articleAuthor = articleAuthor;
    }

    public String getArticleAward() {
        return articleAward;
    }

    public void setArticleAward(String articleAward) {
        this.articleAward = articleAward;
    }

    public Integer getArticleReadtime() {
        return articleReadtime;
    }

    public void setArticleReadtime(Integer articleReadtime) {
        this.articleReadtime = articleReadtime;
    }

    public Date getArticleCt() {
        return articleCt;
    }

    public void setArticleCt(Date articleCt) {
        this.articleCt = articleCt;
    }

    public Date getArticleUt() {
        return articleUt;
    }

    public void setArticleUt(Date articleUt) {
        this.articleUt = articleUt;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticle_intro() {
        return article_intro;
    }

    public void setArticle_intro(String article_intro) {
        this.article_intro = article_intro;
    }

    public String getArticleGroup() {
        return articleGroup;
    }

    public void setArticleGroup(String aritcleGroup) {
        this.articleGroup = aritcleGroup;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Byte getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Byte delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", articleName=").append(articleName);
        sb.append(", articleImg=").append(articleImg);
        sb.append(", articleAuthor=").append(articleAuthor);
        sb.append(", articleAward=").append(articleAward);
        sb.append(", articleReadtime=").append(articleReadtime);
        sb.append(", articleCt=").append(articleCt);
        sb.append(", articleUt=").append(articleUt);
        sb.append(", year=").append(year);
        sb.append(", articleContent=").append(articleContent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}