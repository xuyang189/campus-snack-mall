package com.campus.snackmall.module.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminCategoryQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminCategorySaveRequest;
import com.campus.snackmall.module.category.entity.SnackCategory;
import com.campus.snackmall.module.category.mapper.SnackCategoryMapper;
import com.campus.snackmall.module.category.service.CategoryService;
import com.campus.snackmall.module.category.vo.CategoryVO;
import com.campus.snackmall.module.product.entity.SnackProduct;
import com.campus.snackmall.module.product.mapper.SnackProductMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final SnackCategoryMapper categoryMapper;
    private final SnackProductMapper productMapper;

    public CategoryServiceImpl(SnackCategoryMapper categoryMapper, SnackProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CategoryVO> publicList() {
        return categoryMapper.selectList(new LambdaQueryWrapper<SnackCategory>()
                        .eq(SnackCategory::getStatus, 1)
                        .orderByAsc(SnackCategory::getSort)
                        .orderByDesc(SnackCategory::getCreateTime))
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CategoryVO> adminPage(AdminCategoryQueryRequest request) {
        Page<SnackCategory> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SnackCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(SnackCategory::getName, request.getKeyword());
        }
        wrapper.orderByAsc(SnackCategory::getSort).orderByDesc(SnackCategory::getCreateTime);
        Page<SnackCategory> result = categoryMapper.selectPage(page, wrapper);
        List<CategoryVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(AdminCategorySaveRequest request) {
        SnackCategory category = new SnackCategory();
        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setStatus(request.getStatus());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(Long id, AdminCategorySaveRequest request) {
        SnackCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setStatus(request.getStatus());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        Long count = productMapper.selectCount(new LambdaQueryWrapper<SnackProduct>()
                .eq(SnackProduct::getCategoryId, id)
                .eq(SnackProduct::getIsDeleted, 0));
        if (count != null && count > 0) {
            throw new BusinessException(409, "该分类下存在商品，请先处理商品后再删除");
        }
        categoryMapper.deleteById(id);
        return true;
    }

    private CategoryVO toVO(SnackCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSort(category.getSort());
        vo.setStatus(category.getStatus());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }
}
