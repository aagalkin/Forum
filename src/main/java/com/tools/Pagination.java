package com.tools;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {

    private Integer page;
    private Integer lastPage;

    public Pagination(Integer page, Integer lastPage) {
        this.page = page;
        this.lastPage = lastPage;
    }
}
