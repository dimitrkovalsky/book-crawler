package com.liberty.book.crawler.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({ "com.liberty.book" })
@Import(CommonDaoAppCfg.class)
public class AppConfig {

}