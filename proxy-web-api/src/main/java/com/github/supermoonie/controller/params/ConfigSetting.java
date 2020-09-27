package com.github.supermoonie.controller.params;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author supermoonie
 * @since 2020/9/26
 */
@Data
@Validated
public class ConfigSetting {

    @NotBlank
    private String key;

    @NotBlank
    private String value;
}
