package com.ghb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghb.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    public void deleteById(Long id);

    public List<Category> list(Category category);
}
