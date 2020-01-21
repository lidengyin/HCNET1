package cn.hctech2006.hcnet.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SysTeam implements Serializable {
    private Integer id;

    private String teamName;

    private String teamDetail;

    private String teamWechatName;

    private String teamWechat;

    private String teamQqName;

    private String teamQq;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date teamUploadTime;

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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamDetail() {
        return teamDetail;
    }

    public void setTeamDetail(String teamDetail) {
        this.teamDetail = teamDetail;
    }

    public String getTeamWechatName() {
        return teamWechatName;
    }

    public void setTeamWechatName(String teamWechatName) {
        this.teamWechatName = teamWechatName;
    }

    public String getTeamWechat() {
        return teamWechat;
    }

    public void setTeamWechat(String teamWechat) {
        this.teamWechat = teamWechat;
    }

    public String getTeamQqName() {
        return teamQqName;
    }

    public void setTeamQqName(String teamQqName) {
        this.teamQqName = teamQqName;
    }

    public String getTeamQq() {
        return teamQq;
    }

    public void setTeamQq(String teamQq) {
        this.teamQq = teamQq;
    }

    public Date getTeamUploadTime() {
        return teamUploadTime;
    }

    public void setTeamUploadTime(Date teamUploadTime) {
        this.teamUploadTime = teamUploadTime;
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
        sb.append(", teamName=").append(teamName);
        sb.append(", teamDetail=").append(teamDetail);
        sb.append(", teamWechatName=").append(teamWechatName);
        sb.append(", teamWechat=").append(teamWechat);
        sb.append(", teamQqName=").append(teamQqName);
        sb.append(", teamQq=").append(teamQq);
        sb.append(", teamUploadTime=").append(teamUploadTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}