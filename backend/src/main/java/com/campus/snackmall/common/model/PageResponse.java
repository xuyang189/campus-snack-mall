package com.campus.snackmall.common.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> records;
    private long total;
    private long current;
    private long size;

    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public static <T> PageResponse<T> empty(long current, long size) {
        return new PageResponse<>(Collections.emptyList(), 0, current, size);
    }
}
