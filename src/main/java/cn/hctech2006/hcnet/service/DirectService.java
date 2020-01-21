package cn.hctech2006.hcnet.service;

import cn.hctech2006.hcnet.bean.SysDirect;
import cn.hctech2006.hcnet.mapper.SysDirectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectService {
    @Autowired
    private SysDirectMapper sysDirectMapper;
    public SysDirect findDirectById(Integer id){
        return sysDirectMapper.selectByPrimaryKey(id);
    }
    public List<SysDirect> findAllDirect(Byte delFlag){
        return sysDirectMapper.selectAll(delFlag);
    }
    public List<SysDirect> findAllDirectByGroup(SysDirect sysDirect){
        return sysDirectMapper.selectAllByGroup(sysDirect);
    }
    public int updateDirectGroup(SysDirect sysDirect){
        return sysDirectMapper.updateGroup(sysDirect);
    }
    public int updateDirectEnable(SysDirect sysDirect){
        return sysDirectMapper.updateEnabled(sysDirect);
    }
    public int saveDirect(SysDirect sysDirect){
        return sysDirectMapper.insert(sysDirect);
    }
    public int updateName(SysDirect sysDirect){
        return sysDirectMapper.updateByPrimaryKey(sysDirect);
    }

}
