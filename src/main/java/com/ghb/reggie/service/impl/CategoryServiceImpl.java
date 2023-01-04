package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.common.CustomException;
import com.ghb.reggie.entity.Category;
import com.ghb.reggie.entity.Dish;
import com.ghb.reggie.entity.Setmeal;
import com.ghb.reggie.mapper.CategoryMapper;
import com.ghb.reggie.service.CategoryService;
import com.ghb.reggie.service.DishService;
import com.ghb.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CategoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void deleteById(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //根据id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品  如果已经关联 抛出一个业务异常
        if (count1 > 0){
            throw new CustomException("当前分类项关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐 如果已经关联  抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            throw new CustomException("当前分类项关联了套餐，不能删除");
        }
        super.removeById(id);
    }
}
