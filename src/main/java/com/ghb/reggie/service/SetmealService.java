package com.ghb.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghb.reggie.dto.SetmealDto;
import com.ghb.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    Page<SetmealDto> page(int page, int pageSize, String name);

    void removeWithDish(List<Long> ids);


    void update(SetmealDto setmealDto);

    SetmealDto getByidWithDish(Long id);
}
