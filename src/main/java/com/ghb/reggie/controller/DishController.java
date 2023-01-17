package com.ghb.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghb.reggie.common.R;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.entity.Dish;
import com.ghb.reggie.service.DishFlavorService;
import com.ghb.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.save(dishDto);
        return R.success("保存菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        return R.success(dishService.page(page,pageSize,name));
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
         return R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        return R.success(dishService.listById(dish.getCategoryId()));
    }


}
