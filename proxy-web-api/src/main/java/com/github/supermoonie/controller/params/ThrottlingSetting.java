package com.github.supermoonie.controller.params;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author supermoonie
 * @since 2020/9/18
 */
@Data
@Validated
public class ThrottlingSetting {

    @NotNull
    private Boolean status;

    @NotNull
    @Min(1)
    private Long readLimit;

    @NotNull
    @Min(1)
    private Long writeLimit;
}
