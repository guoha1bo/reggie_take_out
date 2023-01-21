package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.common.CustomException;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.entity.*;
import com.ghb.reggie.mapper.DishMapper;
import com.ghb.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableTransactionManagement
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;
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

    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        super.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (Optional.ofNullable(category).isPresent()) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish byId = super.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,byId.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        super.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public List<Dish> listById(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,Dish::getCategoryId,id);
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        return list;
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //SELECT id,name FROM `dish` WHERE id in(1612294874583834626,1612294389017649153,1612292811472863234,1612292615431094273);
        queryWrapper.in(ids != null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        for (Dish dish : list) {
            if (dish != null){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
    }

    @Transactional
    public void deleteByIds(List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        SELECT id,name from dish where id in(1413342036832100354,1413385247889891330,1612294389017649153);
        queryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        for(Dish dish: list){
            if (dish.getStatus() == 0){
                this.removeById(dish.getId());
            }else{
                throw new CustomException("删除菜品中有在售菜品，无法删除");
            }
        }
    }
    @Override
    public boolean deleteInSetmeal(List<Long> ids) {
        boolean flag = true;
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        //SELECT setmeal_id from setmeal_dish where dish_id in(1413342036832100354,1413385247889891330,1612294389017649153);
        queryWrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        ArrayList<Long> setmealIdList = new ArrayList<>();
        for(SetmealDish setmealDish : setmealDishList){
            Long setmealId = setmealDish.getSetmealId();
            setmealIdList.add(setmealId);
        }
        //如果删除菜品没有关联套餐 可以删除
        if (setmealIdList.size() == 0){
            this.deleteByIds(ids);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(queryWrapper1);
            LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.in(SetmealDish::getDishId,ids);
            setmealDishService.remove(queryWrapper2);
        }else if(setmealIdList.size() > 0){//如果删除菜品有关联套餐 并且正在售卖 不能删除
            LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.in(Setmeal::getId, setmealIdList);
            List<Setmeal> setmealList = setmealService.list(queryWrapper2);
            for (Setmeal setmeal:setmealList) {
                if (setmeal.getStatus() == 1){
                    flag = false;
                    return flag;
                }
            }
        }
        this.deleteByIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);
        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(queryWrapper2);
        return flag;
    }
}
