package com.ghb.reggie.dto;

import com.ghb.reggie.entity.Setmeal;
import com.ghb.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
