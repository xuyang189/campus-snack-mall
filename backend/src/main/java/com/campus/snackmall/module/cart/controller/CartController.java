package com.campus.snackmall.module.cart.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.cart.dto.CartAddRequest;
import com.campus.snackmall.module.cart.dto.CartBatchDeleteRequest;
import com.campus.snackmall.module.cart.dto.CartCheckedRequest;
import com.campus.snackmall.module.cart.dto.CartUpdateQuantityRequest;
import com.campus.snackmall.module.cart.service.CartService;
import com.campus.snackmall.module.cart.vo.CartListVO;
import com.campus.snackmall.security.RequireLogin;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequireLogin
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<CartListVO> list() {
        return Result.success("查询成功", cartService.list());
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody CartAddRequest request) {
        return Result.success("加入购物车成功", cartService.add(request));
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateQuantity(@PathVariable Long id, @Valid @RequestBody CartUpdateQuantityRequest request) {
        return Result.success("修改成功", cartService.updateQuantity(id, request));
    }

    @PutMapping("/{id}/checked")
    public Result<Boolean> updateChecked(@PathVariable Long id, @Valid @RequestBody CartCheckedRequest request) {
        return Result.success("修改成功", cartService.updateChecked(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success("删除成功", cartService.delete(id));
    }

    @PostMapping("/batch-delete")
    public Result<Boolean> batchDelete(@Valid @RequestBody CartBatchDeleteRequest request) {
        return Result.success("删除成功", cartService.batchDelete(request));
    }

    @PutMapping("/checked-all")
    public Result<Boolean> checkedAll(@Valid @RequestBody CartCheckedRequest request) {
        return Result.success("修改成功", cartService.updateCheckedAll(request));
    }
}
