package com.zhang.merchant.convert;

import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.vo.MerchantDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/8 22:17
 **/

@Mapper
public interface MerchantDetailCovert {

    MerchantDetailCovert INSTANCE=Mappers.getMapper(MerchantDetailCovert.class);

    MerchantDetailVO DTO2MerchantDVO(MerchantDTO merchantDTO);

    @Mapping(source = "idCardFrontImg",target = "idCardFrontImg")
    @Mapping(source = "idCardAfterImg",target = "idCardAfterImg")
    @Mapping(source = "businessLicensesImg",target = "businessLicensesImg")
    @Mapping(source = "merchantType",target = "merchantType")
    @Mapping(source = "username",target = "username")
    @Mapping(source = "contactsAddress",target = "contactsAddress")
    @Mapping(source = "merchantNo",target = "merchantNo")
    @Mapping(source = "merchantName",target = "merchantName")
    @Mapping(source = "merchantAddress",target = "merchantAddress")

    MerchantDTO MerchantDVO2DTO(MerchantDetailVO merchantDetailVO);

}
