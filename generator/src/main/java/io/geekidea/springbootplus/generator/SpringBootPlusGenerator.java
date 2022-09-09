/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.geekidea.springbootplus.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import io.geekidea.springbootplus.generator.config.GeneratorStrategy;
import io.geekidea.springbootplus.generator.constant.GeneratorConstant;
import io.geekidea.springbootplus.generator.properties.GeneratorProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * spring-boot-plus代码生成器入口类
 *
 * @author geekidea
 * @date 2019-10-22
 **/
@Component
public class SpringBootPlusGenerator {

    /**
     * 生成代码
     * @param args
     */
    public static void main(String[] args) {
        GeneratorProperties generatorProperties = new GeneratorProperties();

        // 设置基本信息
        generatorProperties
                // 如果是子module，需要用斜线分割，例如core是modules的子，想在core下面生成代码则需要写成是："modules/core"
                .setMavenModuleName("modules/core")
                .setParentPackage("com.lakecloud")
                .setModuleName("scaffold")
                .setAuthor("Max.King")
                .setFileOverride(false);

        // 设置表信息，一次可以生成多张表的 CRUD 代码
        generatorProperties.addTable("user_msg","id");
//        generatorProperties.addTable("sys_permission","id");
        // 设置表前缀
        // generatorProperties.setTablePrefix(Arrays.asList("tb_"));

        // 数据源配置
        generatorProperties.getDataSourceConfig()
                .setDbType(DbType.POSTGRE_SQL)
                .setUsername("flutter_user")
                .setPassword("RgbL5ObC")
                .setDriverName("org.postgresql.Driver")
                .setUrl("jdbc:postgresql://10.110.10.24:5432/flutter");

        // 生成配置
        generatorProperties.getGeneratorConfig()
                .setGeneratorStrategy(GeneratorStrategy.ALL)
                .setGeneratorEntity(true)
                .setGeneratorController(true)
                .setGeneratorService(true)
                .setGeneratorServiceImpl(true)
                .setGeneratorMapper(true)
                .setGeneratorMapperXml(true)
                .setGeneratorPageParam(true)
                .setGeneratorQueryVo(true)
                .setRequiresPermissions(false)
                .setPageListOrder(true)
                .setParamValidation(true)
                .setSwaggerTags(true)
                .setOperationLog(true);

        // 全局配置
        generatorProperties.getMybatisPlusGeneratorConfig().getGlobalConfig()
                .setOpen(true)
                .setSwagger2(true)
                .setIdType(IdType.AUTO)
                .setDateType(DateType.ONLY_DATE);

        // 策略配置
        generatorProperties.getMybatisPlusGeneratorConfig().getStrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setControllerMappingHyphenStyle(true)
                .setVersionFieldName(GeneratorConstant.VERSION)
                .setLogicDeleteFieldName(GeneratorConstant.DELETED);

        // 生成代码
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generator(generatorProperties);

    }


}
