package cn.hctech2006.hcnet.service;

import cn.hctech2006.hcnet.bean.SysEmail;
import cn.hctech2006.hcnet.mapper.SysEmailMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private SysEmailMapper sysEmailMapper;
    public int uploadEmail(SysEmail sysEmail){
        return sysEmailMapper.insert(sysEmail);
    }
    public Page<SysEmail> findAllSmail(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        return sysEmailMapper.selectAll();
    }
    public SysEmail findById(Integer id){
        return sysEmailMapper.selectByPrimaryKey(id);
    }

    public int updateDelFlag(SysEmail sysEmail){
        return sysEmailMapper.updatedelFlag(sysEmail);
    }
    public Page<SysEmail> findByDelFlag(Byte delFlag, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        return sysEmailMapper.selectByStatus(delFlag);
    }
}
