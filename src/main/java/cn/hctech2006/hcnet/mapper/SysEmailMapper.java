package cn.hctech2006.hcnet.mapper;

import cn.hctech2006.hcnet.bean.SysEmail;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SysEmailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysEmail record);

    SysEmail selectByPrimaryKey(Integer id);

    Page<SysEmail> selectAll();

    int updateByPrimaryKey(SysEmail record);

    int updatedelFlag(SysEmail sysEmail);

    Page<SysEmail> selectByStatus(Byte delFlag);
}