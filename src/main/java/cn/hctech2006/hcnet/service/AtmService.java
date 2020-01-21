package cn.hctech2006.hcnet.service;

import cn.hctech2006.hcnet.bean.RespBean;
import cn.hctech2006.hcnet.bean.SysAtm;
import cn.hctech2006.hcnet.mapper.SysAtmMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtmService {
    @Autowired
    private SysAtmMapper sysAtmMapper;
    public int uploadAtm(SysAtm sysAtm){
        return sysAtmMapper.insert(sysAtm);
    }
    public Page<SysAtm> findAllAtm(Integer pageNum, Integer pageSize, Byte delFlag){
        PageHelper.startPage(pageNum, pageSize);
        return sysAtmMapper.selectAll(delFlag);
    }
    public SysAtm findById(Integer id){
        return sysAtmMapper.selectByPrimaryKey(id);
    }
    public int updateDelFlag(SysAtm sysAtm){
        return sysAtmMapper.updateDelFlag(sysAtm);
    }
    public int updateAtm(SysAtm sysAtm){
        return sysAtmMapper.updateByPrimaryKey(sysAtm);

    }
}
