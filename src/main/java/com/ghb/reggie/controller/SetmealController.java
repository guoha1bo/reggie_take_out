package com.ghb.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghb.reggie.common.R;
import com.ghb.reggie.dto.SetmealDto;
import com.ghb.reggie.service.SetmealDishService;
import com.ghb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功！");
    }
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        return R.success(setmealService.page(page,pageSize,name));
    }

//    @DeleteMapping
//    public R<String> delete(@RequestParam List<Long> ids){
//        setmealService.removeWithDish(ids);
//        return R.success("删除套餐成功！");
//    }
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.update(setmealDto);
        return R.success("套餐修改成功！");
    }
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        return R.success(setmealService.getByidWithDish(id));
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        setmealService.updateStatus(status,ids);
        return R.success("套餐状态修改成功！");
    }
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        Boolean deleteSetmeal = setmealService.deleteSetmeal(ids);
        if (deleteSetmeal){
            return R.success("删除套餐成功！");
        }else {
            return R.error("删除套餐失败，存在正在售卖套餐！");
        }
    }
}
