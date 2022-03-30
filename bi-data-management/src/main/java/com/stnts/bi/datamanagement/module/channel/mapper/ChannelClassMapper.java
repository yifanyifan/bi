package com.stnts.bi.datamanagement.module.channel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelClass;
import com.stnts.bi.datamanagement.module.channel.param.ChannelClassPageParam;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassNode;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelClassVO;
import com.stnts.bi.entity.sys.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道类型 Mapper 接口
 *
 * @author 易樊
 * @since 2021-09-22
 */
@Repository
public interface ChannelClassMapper extends BaseMapper<ChannelClass> {
    //    List<ChannelClassNode> selectListAll(@Param("params") ChannelClassPageParam param);
//
//    List<ChannelClassNode> selectListAllSub(@Param("parentId") Long parentId);
//
    Long countByCCID(@Param("id") Long id);

    // 产品Start
//    List<ChannelClassNode> selectListAllByProduct(@Param("productCode") String productCode);
//
//    List<ChannelClassNode> selectListAllSubByProduct(@Param("parentId") Long parentId, @Param("productCode") String productCode);
//
//    List<ChannelClassNode> ccidListByProduct(@Param("channelClassId") Long channelClassId, @Param("productCode") String productCode);

    //使用 当前产品 的CCID 所对应部门 在 【产品分成】 中ID
    List<String> getChannelClassIdByProductCode(@Param("productCode") String productCode);

    List<ChannelClassNode> selectByChannelClassIdList(@Param("channelClassIdList") List<Long> channelClassIdList, @Param("productCode") String productCode);

    List<ChannelClassNode> ccidListByProductBatch(@Param("channelClassIdList") List<Long> channelClassIdList, @Param("productCode") String productCode);
    // 产品End

    /**
     * 向上找所有父级节点【包括本节点】
     *
     * @param id
     * @return
     */
    List<ChannelClass> getUpLoad(@Param("id") Long id);

    /**
     * 向下找所有子级节点【包括本节点】
     *
     * @param id
     * @return
     */
    List<ChannelClass> getDownLoad(@Param("id") Long id);

    List<ChannelClass> getDownLoadByIdList(@Param("params") List<String> idList);

    IPage<ChannelClassVO> selectAll(@Param("page") Page<ChannelClass> page, @Param("params") ChannelClassPageParam channelClassPageParam, @Param("user") UserEntity user);

    List<ChannelClass> getPullList(@Param("params") ChannelClassPageParam channelClassPageParam);

    List<ChannelClassNode> selectAllList(@Param("params") ChannelClassPageParam channelClassPageParam);

    List<ChannelClassNode> selectCountCCIDByLeaf(@Param("params") ChannelClassPageParam channelClassPageParam);


    void updateSubPath(@Param("nowIdPath") String nowIdPath, @Param("newIdPath") String s, @Param("nowNamePath") String nowNamePath, @Param("newNamePath") String s1);
}
