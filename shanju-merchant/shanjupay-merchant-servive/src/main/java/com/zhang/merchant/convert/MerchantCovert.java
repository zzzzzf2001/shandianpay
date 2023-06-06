package com.zhang.merchant.convert;

import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 17:49
 **/


@Mapper
public interface MerchantCovert {

    MerchantCovert INSTANCE = Mappers.getMapper(MerchantCovert.class);

    MerchantDTO entity2dto(Merchant entity);

    Merchant dto2entity(MerchantDTO dto);



}
