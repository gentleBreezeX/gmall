package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;

/**
 * @author breeze
 * @date 2019/11/6 14:33
 */
public interface SearchService {

    SearchResponse search(SearchParamVO searchParamVO);

}
