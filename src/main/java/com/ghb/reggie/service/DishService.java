package com.ghb.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void save(DishDto dishDto);

    public Page<DishDto> page(int page, int pageSize, String name);

    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    List<Dish> listById(Long id);

    void updateStatus(Integer status,List<Long> ids);

    boolean deleteInSetmeal(List<Long> ids);
}
