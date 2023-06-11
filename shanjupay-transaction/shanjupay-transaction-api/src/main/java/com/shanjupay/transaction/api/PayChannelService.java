package com.shanjupay.transaction.api;

import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/11 18:39
 **/



public interface PayChannelService {
    //查询服务类型
    List<PlatformChannelDTO> queryPlatformChannel();

}
