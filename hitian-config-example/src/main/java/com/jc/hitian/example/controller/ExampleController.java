package com.jc.hitian.example.controller;

import com.jc.hitian.example.service.ExampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p/>
 * Created by Shylock on 15/10/18.
 */
@Controller
@Scope("request")
public class ExampleController {

    public static final Logger logger = LoggerFactory.getLogger ( ExampleController.class );

    @Resource
    Environment environment;

    @Resource
    ExampleService exampleService;

    private String placeholder;

    @Value("${hi}")
    private String annotation;

    public ExampleController () {

        logger.error ( "=========== init ExampleController ==========" );
        logger.error ( "=========== init ExampleController ==========" );
    }

    @RequestMapping("/{key}")
    @ResponseBody
    public String key ( @PathVariable("key") String key ) {

        String placeholder = environment.getProperty ( key );
        return placeholder;
    }

    @RequestMapping("/placeholder")
    @ResponseBody
    public String placeholder () {

        return getPlaceholder ();
    }


    @RequestMapping("/annotation")
    @ResponseBody
    public String annotation () {

        return annotation;
    }


    @RequestMapping("/service")
    @ResponseBody
    public String service () {

        return exampleService.getMsg ();
    }

    public String getPlaceholder () {

        return placeholder;
    }

    public void setPlaceholder ( String placeholder ) {

        this.placeholder = placeholder;
    }
}
