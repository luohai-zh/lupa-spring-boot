package com.lupa.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class AutGeneratorUtil {
    public static void main(String[] args) {
        //需要构建一个 代码自动生成器 对象
        AutoGenerator mpg = new AutoGenerator();

        //配置策略
        //1、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java"); //设置输出路径
        gc.setAuthor("lupa"); //设置作者
        gc.setOpen(false); //是否打开文件夹
        gc.setFileOverride(false); //是否覆盖
        gc.setServiceName("%sService"); //去掉Service的I前缀
        gc.setIdType(IdType.AUTO);
        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);

        //2、设置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/agricultureos?characterEncoding=utf-8&useSSL=false&serverTimeZone=GMT+8");
        dsc.setUsername("root");//数据库用户名
        dsc.setPassword("root");//数据库密码
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        //3、包的配置
        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("user");
        pc.setParent("com.lupa");//改成自己的包名
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);


        //4、配置策略
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude("lupa_user","lupa_device","lupa_historydata","lupa_newdata","lupa_speak");//这里是数据库的表名，如果有多个用逗号隔开。
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true); //自动开启lombok

        //逻辑删除
//        strategy.setLogicDeleteFieldName("deleted");

        //自动填充配置
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill modifyTime = new TableFill("modify_time", FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills  = new ArrayList<>();
        tableFills.add(createTime);
        tableFills.add(modifyTime);
        strategy.setTableFillList(tableFills);

        //乐观锁
        strategy.setVersionFieldName("version");

        //驼峰命名
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);//localhost:8080/hello_id_2
        strategy.setTablePrefix("lupa_");//表格的前缀
        mpg.setStrategy(strategy);

        mpg.execute(); //执行
    }
}
