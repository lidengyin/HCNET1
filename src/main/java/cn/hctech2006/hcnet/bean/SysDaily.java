package cn.hctech2006.hcnet.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SysDaily implements Serializable {
    private Integer id;

    private String dailyName;

    private String dailyImage;

    private Integer dailyReadtime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dailyCt;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dailyUt;

    private String dailyContent;

    private String dailyIntro;

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

    public String getDailyName() {
        return dailyName;
    }

    public void setDailyName(String dailyName) {
        this.dailyName = dailyName;
    }

    public String getDailyImage() {
        return dailyImage;
    }

    public void setDailyImage(String dailyImage) {
        this.dailyImage = dailyImage;
    }

    public Integer getDailyReadtime() {
        return dailyReadtime;
    }

    public void setDailyReadtime(Integer dailyReadtime) {
        this.dailyReadtime = dailyReadtime;
    }

    public Date getDailyCt() {
        return dailyCt;
    }

    public void setDailyCt(Date dailyCt) {
        this.dailyCt = dailyCt;
    }

    public Date getDailyUt() {
        return dailyUt;
    }

    public void setDailyUt(Date dailyUt) {
        this.dailyUt = dailyUt;
    }

    public String getDailyContent() {
        return dailyContent;
    }

    public void setDailyContent(String dailyContent) {
        this.dailyContent = dailyContent;
    }

    public String getDailyIntro() {
        return dailyIntro;
    }

    public void setDailyIntro(String dailyIntro) {
        this.dailyIntro = dailyIntro;
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
        sb.append(", dailyName=").append(dailyName);
        sb.append(", dailyImage=").append(dailyImage);
        sb.append(", dailyReadtime=").append(dailyReadtime);
        sb.append(", dailyCt=").append(dailyCt);
        sb.append(", dailyUt=").append(dailyUt);
        sb.append(", dailyContent=").append(dailyContent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}