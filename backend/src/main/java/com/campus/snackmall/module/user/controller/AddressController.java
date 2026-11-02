package com.campus.snackmall.module.user.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.user.dto.AddressSaveRequest;
import com.campus.snackmall.module.user.service.AddressService;
import com.campus.snackmall.module.user.vo.AddressVO;
import com.campus.snackmall.security.RequireLogin;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/addresses")
@RequireLogin
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public Result<List<AddressVO>> list() {
        return Result.success("查询成功", addressService.list());
    }

    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody AddressSaveRequest request) {
        return Result.success("新增成功", addressService.create(request));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody AddressSaveRequest request) {
        return Result.success("修改成功", addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success("删除成功", addressService.delete(id));
    }

    @PutMapping("/{id}/default")
    public Result<Boolean> setDefault(@PathVariable Long id) {
        return Result.success("已设为默认地址", addressService.setDefault(id));
    }
}
