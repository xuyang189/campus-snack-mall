package com.campus.snackmall.module.user.service;

import com.campus.snackmall.module.user.dto.AddressSaveRequest;
import com.campus.snackmall.module.user.vo.AddressVO;
import java.util.List;

public interface AddressService {

    List<AddressVO> list();

    Boolean create(AddressSaveRequest request);

    Boolean update(Long id, AddressSaveRequest request);

    Boolean delete(Long id);

    Boolean setDefault(Long id);
}
