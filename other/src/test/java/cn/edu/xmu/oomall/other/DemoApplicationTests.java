package cn.edu.xmu.oomall.other;

import cn.edu.xmu.oomall.other.model.Order;
import cn.edu.xmu.oomall.other.model.OrderDetail;
import cn.edu.xmu.oomall.other.util.ClassUtil;
import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
		Order order = new Order();
		order.setId(1L);
		order.setName("order1");
		List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderid(1L);
		orderDetail.setOrderPrice("1USD");
		orderDetail.setOrderSku("Sku1");

		orderDetailList.add(orderDetail);

		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setOrderid(1L);
		orderDetail2.setOrderPrice("2USD");
		orderDetail2.setOrderSku("Sku2");
		orderDetailList.add(orderDetail2);

		try {
			HashMap addMap = new HashMap();
			HashMap addValMap = new HashMap();
			addMap.put("orderDetail", Class.forName("java.util.List"));
			addValMap.put("orderDetail", orderDetailList);
			Object obj2= new ClassUtil().dynamicClass(order,addMap,addValMap);

			System.out.println(JSONObject.toJSONString(obj2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文档生成
	 */
	@Test
	void documentGeneration() {
		//数据源
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
		hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/oomall_other?serverTimezone=GMT%2B8");
		hikariConfig.setUsername("dbuser");
		hikariConfig.setPassword("123456");
		//设置可以获取tables remarks信息
		hikariConfig.addDataSourceProperty("useInformationSchema", "true");
		hikariConfig.setMinimumIdle(2);
		hikariConfig.setMaximumPoolSize(5);
		DataSource dataSource = new HikariDataSource(hikariConfig);
		//生成配置
		EngineConfig engineConfig = EngineConfig.builder()
				//生成文件路径
				.fileOutputDir("/home/wang/Project/doc")
				//打开目录
				.openOutputDir(true)
				//文件类型
				.fileType(EngineFileType.WORD)
				//生成模板实现
				.produceType(EngineTemplateType.freemarker)
				//自定义文件名称
				.fileName("其他模块数据字典").build();

		//忽略表
		ArrayList<String> ignoreTableName = new ArrayList<>();
		ignoreTableName.add("test_user");
		ignoreTableName.add("test_group");
		//忽略表前缀
		ArrayList<String> ignorePrefix = new ArrayList<>();
		ignorePrefix.add("test_");
		//忽略表后缀
		ArrayList<String> ignoreSuffix = new ArrayList<>();
		ignoreSuffix.add("_test");
		ProcessConfig processConfig = ProcessConfig.builder()
				//指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
				//根据名称指定表生成
				.designatedTableName(new ArrayList<>())
				//根据表前缀生成
				.designatedTablePrefix(new ArrayList<>())
				//根据表后缀生成
				.designatedTableSuffix(new ArrayList<>())
				//忽略表名
				.ignoreTableName(ignoreTableName)
				//忽略表前缀
				.ignoreTablePrefix(ignorePrefix)
				//忽略表后缀
				.ignoreTableSuffix(ignoreSuffix).build();
		//配置
		Configuration config = Configuration.builder()
				//版本
				.version("1.0.0")
				//描述
				.description("数据库设计文档生成")
				//数据源
				.dataSource(dataSource)
				//生成配置
				.engineConfig(engineConfig)
				//生成配置
				.produceConfig(processConfig)
				.build();
		//执行生成
		new DocumentationExecute(config).execute();
	}
}
