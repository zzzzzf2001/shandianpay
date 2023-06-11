package com.zhang.merchant.convert;

import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 20:14
 * @Description 将 MerchantRegisterVO与MerchantDTO相互转换
 **/
@Mapper
public interface MerchantRegisterConvert {

        MerchantRegisterConvert INSTANCE=Mappers.getMapper(MerchantRegisterConvert.class);

       MerchantDTO MRVO2DTO(MerchantRegisterVO vo);
       MerchantRegisterVO DTO2MRVO(MerchantDTO dto);



}


