package com.ghb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghb.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    public void deleteById(Long id);
}
