package com.campus.snackmall.module.cart.service;

import com.campus.snackmall.module.cart.dto.CartAddRequest;
import com.campus.snackmall.module.cart.dto.CartBatchDeleteRequest;
import com.campus.snackmall.module.cart.dto.CartCheckedRequest;
import com.campus.snackmall.module.cart.dto.CartUpdateQuantityRequest;
import com.campus.snackmall.module.cart.vo.CartListVO;

public interface CartService {

    CartListVO list();

    Boolean add(CartAddRequest request);

    Boolean updateQuantity(Long id, CartUpdateQuantityRequest request);

    Boolean updateChecked(Long id, CartCheckedRequest request);

    Boolean delete(Long id);

    Boolean batchDelete(CartBatchDeleteRequest request);

    Boolean updateCheckedAll(CartCheckedRequest request);
}
