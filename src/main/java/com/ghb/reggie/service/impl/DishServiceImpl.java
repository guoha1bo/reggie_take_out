package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.entity.Dish;
import com.ghb.reggie.mapper.DishMapper;
import com.ghb.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
}
