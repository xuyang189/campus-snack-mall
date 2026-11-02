package com.campus.snackmall.module.category.service;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminCategoryQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminCategorySaveRequest;
import com.campus.snackmall.module.category.vo.CategoryVO;
import java.util.List;

public interface CategoryService {

    List<CategoryVO> publicList();

    PageResponse<CategoryVO> adminPage(AdminCategoryQueryRequest request);

    Boolean create(AdminCategorySaveRequest request);

    Boolean update(Long id, AdminCategorySaveRequest request);

    Boolean delete(Long id);
}
