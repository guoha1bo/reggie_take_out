package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.entity.Dish;
import com.ghb.reggie.entity.DishFlavor;
import com.ghb.reggie.mapper.DishMapper;
import com.ghb.reggie.service.DishFlavorService;
import com.ghb.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableTransactionManagement
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    public void save(DishDto dishDto){
        log.info(dishDto.toString());
        super.save(dishDto);
        Long dishid = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishid);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
