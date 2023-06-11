package com.zhang.merchant.convert;



import com.zhang.merchant.api.dto.AppDTO;
import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.entity.App;
import com.zhang.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppCovert {

   AppCovert Instance  = Mappers.getMapper(AppCovert.class);


    AppDTO entity2dto(App entity);

    App dto2entity(AppDTO dto);

     List<AppDTO>  listEntity2DTO(List<App> appList);

    List<App>  listDTO2Entity(List<AppDTO> appList);


}