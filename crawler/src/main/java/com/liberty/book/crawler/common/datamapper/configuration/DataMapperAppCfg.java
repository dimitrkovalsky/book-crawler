package com.liberty.book.crawler.common.datamapper.configuration;

import com.liberty.book.crawler.common.datamapper.DataMapper;
import com.liberty.book.crawler.common.datamapper.JacksonDataMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataMapperAppCfg {
    @Bean
    public DataMapper dataMapper() {
        return new JacksonDataMapper();
    }

}
