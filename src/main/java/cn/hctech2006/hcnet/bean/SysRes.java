package cn.hctech2006.hcnet.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SysRes implements Serializable {
    private Integer id;

    private String resName;

    private String resImg;

    private String resDetail;
    private Byte DelFlag;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date resCt;
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResImg() {
        return resImg;
    }

    public void setResImg(String resImg) {
        this.resImg = resImg;
    }

    public String getResDetail() {
        return resDetail;
    }

    public void setResDetail(String resDetail) {
        this.resDetail = resDetail;
    }

    public Date getResCt() {
        return resCt;
    }

    public void setResCt(Date resCt) {
        this.resCt = resCt;
    }

    public Byte getDelFlag() {
        return DelFlag;
    }

    public void setDelFlag(Byte delFlag) {
        DelFlag = delFlag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", resName=").append(resName);
        sb.append(", resImg=").append(resImg);
        sb.append(", resDetail=").append(resDetail);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}