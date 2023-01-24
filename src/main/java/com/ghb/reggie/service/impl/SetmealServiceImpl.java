package com.ghb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghb.reggie.common.CustomException;
import com.ghb.reggie.dto.SetmealDto;
import com.ghb.reggie.entity.Category;
import com.ghb.reggie.entity.Setmeal;
import com.ghb.reggie.entity.SetmealDish;
import com.ghb.reggie.mapper.SetmealMapper;
import com.ghb.reggie.service.CategoryService;
import com.ghb.reggie.service.SetmealDishService;
import com.ghb.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableTransactionManagement
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        super.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        Page<Setmeal> setmealPage = super.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            if (Optional.ofNullable(category).isPresent()) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                BeanUtils.copyProperties(item, setmealDto);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = super.count(queryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        super.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public void update(SetmealDto setmealDto) {
        super.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);

    }

    @Override
    public SetmealDto getByidWithDish(Long id) {
        Setmeal setmeal = super.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = super.list(queryWrapper);
        for (Setmeal item : list){
            if (item != null){
                item.setStatus(status);
                super.updateById(item);
            }
        }
    }
    @Transactional
    @Override
    public Boolean deleteSetmeal(List<Long> ids) {
        //先查询删除的套餐售卖状态是否为1  为1 无法删除返回布尔值  为0进入条件
        Boolean flag = true;
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = super.list(queryWrapper1);
        for (Setmeal setmeal : list){
            if (setmeal.getStatus() == 1){
                flag = false;
                return flag;
            }
        }

        //第一步先删除掉套餐菜品关联表 将套餐关联的菜品删除
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //SELECT id,name  FROM `setmeal_dish` where setmeal_id in(1415580119015145474,1615267557973958657)
        queryWrapper.in(ids != null, SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);

        //第二部删除套餐
        super.remove(queryWrapper1);
        return flag;

    }
}
