package com.github.supermoonie.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@Data
class BaseModel {

    @TableId
    private String id;

    private Date timeCreated;
}
