package com.ghb.reggie.controller;

import com.ghb.reggie.common.R;
import com.ghb.reggie.dto.DishDto;
import com.ghb.reggie.service.DishFlavorService;
import com.ghb.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.soap.Addressing;

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


}
