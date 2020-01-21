package cn.hctech2006.hcnet.mapper;

import cn.hctech2006.hcnet.bean.SysAtm;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.List;
@Mapper
public interface SysAtmMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAtm record);

    SysAtm selectByPrimaryKey(Integer id);

    Page<SysAtm> selectAll(Byte delFlag);

    int updateByPrimaryKey(SysAtm record);

    int updateDelFlag(SysAtm sysAtm);
}