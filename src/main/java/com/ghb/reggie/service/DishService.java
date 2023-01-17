package com.ghb.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    public void save(DishDto dishDto);

    public Page<DishDto> page(int page, int pageSize, String name);
}
