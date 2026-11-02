package com.campus.snackmall.module.category.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.category.service.CategoryService;
import com.campus.snackmall.module.category.vo.CategoryVO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<CategoryVO>> list() {
        return Result.success("查询成功", categoryService.publicList());
    }
}
