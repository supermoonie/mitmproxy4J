package com.github.supermoonie.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
@Api
@RestController
@RequestMapping(value = "/cert")
@CrossOrigin
@Slf4j
@Validated
public class CertController {


}
