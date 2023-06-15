package com.zhang.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import com.zhang.merchant.api.MerchantService;
import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.api.dto.StaffDTO;
import com.zhang.merchant.api.dto.StoreDTO;
import com.zhang.merchant.convert.MerchantCovert;
import com.zhang.merchant.convert.StaffConvert;
import com.zhang.merchant.convert.StoreConvert;
import com.zhang.merchant.entity.Merchant;
import com.zhang.merchant.entity.Staff;
import com.zhang.merchant.entity.Store;
import com.zhang.merchant.entity.StoreStaff;
import com.zhang.merchant.mapper.MerchantMapper;
import com.zhang.merchant.mapper.StaffMapper;
import com.zhang.merchant.mapper.StoreMapper;
import com.zhang.merchant.mapper.StoreStaffMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:39
 **/


@org.apache.dubbo.config.annotation.Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Resource
    MerchantMapper merchantMapper;

    @Resource
    StoreMapper storeMapper;

    @Resource
    StaffMapper staffMapper;

    @Resource
    StoreStaffMapper storeStaffMapper;


    @Reference
    TenantService tenantService;

    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setMerchantName(merchant.getMerchantName());
        //....
        return merchantDTO;
    }

    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //校验

        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100103);
        }
        String mobile = merchantDTO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_200230);
        }
        if (!PhoneUtil.isMatches(mobile)) {
            throw new BusinessException(CommonErrorCode.E_200224);
        }

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();

        if (merchantMapper.selectCount(queryWrapper.eq(Merchant::getMobile, mobile)) > 0) {
            throw new BusinessException(CommonErrorCode.E_200203);
        }

        CreateTenantRequestDTO createTenantRequest = new CreateTenantRequestDTO();
        createTenantRequest.setMobile(merchantDTO.getMobile());
//表示该租户类型是商户
        createTenantRequest.setTenantTypeCode("shanju‐merchant");
//设置租户套餐为初始化套餐餐
        createTenantRequest.setBundleCode("shanju‐merchant");
//租户的账号信息
        createTenantRequest.setUsername(merchantDTO.getUsername());
        createTenantRequest.setPassword(merchantDTO.getPassword());
//新增租户并设置为管理员
        createTenantRequest.setName(merchantDTO.getUsername());
        log.info("商户中心调用统一账号服务，新增租户和账号");
        TenantDTO tenantDTO = tenantService.createTenantAndAccount(createTenantRequest);
        if (tenantDTO == null || tenantDTO.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_999910);
        }
//判断租户下是否已经注册过商户
        Merchant merchant = merchantMapper
                .selectOne(new QueryWrapper<Merchant>().lambda().eq(Merchant::getTenantId,
                        tenantDTO.getId()));
        if (merchant != null && merchant.getId() != null) {
            throw new BusinessException(CommonErrorCode.E_200227);
        }
//3. 设置商户所属租户
        merchantDTO.setTenantId(tenantDTO.getId());
//设置审核状态，注册时默认为"0"
        merchantDTO.setAuditStatus("0");//审核状态 0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        Merchant entity = MerchantCovert.INSTANCE.dto2entity(merchantDTO);
//保存商户信息
        log.info("保存商户注册信息");
        merchantMapper.insert(entity);
//4.新增门店，创建根门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(entity.getId());
        storeDTO.setStoreName("根门店");
        storeDTO = createStore(storeDTO);
        log.info("门店信息:{}" + JSON.toJSONString(storeDTO));
//5.新增员工，并设置归属门店
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMerchantId(entity.getId());
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
//为员工选择归属门店,此处为根门店
        staffDTO.setStoreId(storeDTO.getId());
        staffDTO = createStaff(staffDTO);
//6.为门店设置管理员
        bindStaffToStore(storeDTO.getId(), staffDTO.getId());

        return MerchantCovert.INSTANCE.entity2dto(entity);
    }


    @Override
    public StoreDTO createStore(StoreDTO storeDTO) {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("商户下新增门店" + JSON.toJSONString(store));
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }
    @Override
    public void bindStaffToStore(Long storeId, Long staffId) {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStoreId(storeId);
        storeStaff.setStaffId(staffId);
        storeStaffMapper.insert(storeStaff);
    }


    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) {
//1.校验手机号格式及是否存在
        String mobile = staffDTO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
//根据商户id和手机号校验唯一性
        if (isExistStaffByMobile(mobile, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_200203);
        }
//2.校验用户名是否为空
        String username = staffDTO.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(CommonErrorCode.E_200225);
        }
//根据商户id和账号校验唯一性
        if (isExistStaffByUserName(username, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_200239);
        }
        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        log.info("商户下新增员工");
        staffMapper.insert(entity);
        return StaffConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * 根据手机号判断员工是否已在指定商户存在
     *
     * @param mobile 手机号
     * @return
     */
    private boolean isExistStaffByMobile(String mobile, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    /**
     * 根据账号判断员工是否已在指定商户存在
     *
     * @param userName
     * @param merchantId
     * @return
     */
    private boolean isExistStaffByUserName(String userName, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getUsername, userName).eq(Staff::getMerchantId,
                merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }


    /**
     * @param merchantId,merchantDTO
     * @return
     * @Description 注册商户服务的接口，接收账号密码手机号，为了延展性，使用MerchantDTO做接收参数
     * @author 15754
     * @Date 2023/6/8
     **/
    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        if (merchantId == null || merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }

        Merchant merchantInDB = merchantMapper.selectById(merchantId);
        if (Objects.isNull(merchantInDB)) {
            throw new BusinessException(CommonErrorCode.E_200227);
        }

        merchantInDB.setBusinessLicensesImg(merchantDTO.getBusinessLicensesImg());
        merchantInDB.setIdCardAfterImg(merchantDTO.getIdCardAfterImg());
        merchantInDB.setIdCardFrontImg(merchantDTO.getIdCardFrontImg());
        merchantInDB.setAuditStatus("1");

        //进行 修改
        merchantMapper.updateById(merchantInDB);

    }


}

