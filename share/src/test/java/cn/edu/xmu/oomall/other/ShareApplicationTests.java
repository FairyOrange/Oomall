package cn.edu.xmu.oomall.other;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.share.controller.ShareController;
import cn.edu.xmu.oomall.util.JwtHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
class ShareApplicationTests {

	@Autowired
	private MockMvc mvc;

	/**
	 * 创建测试用token
	 * @param userId
	 * @param departId
	 * @param expireTime
	 * @return token
	 * @author Qiuyan Qian
	 * @date Created in 2020/11/25 15:35
	 *
	 */
	private final String createTestToken(Long userId, Long departId, int expireTime){
		String token = new JwtHelper().createToken(userId, departId, expireTime);
		log.debug("test token: "+token);
		return token;
	}
	private Long shareActivityId;



	/**
	 * 买家查询所有分享记录 开始时间在结束时间之后
	 * @throws Exception
	 * @author Qiuyan Qian
	 * @date Created in 2020/11/25 18:35
	 */
	@Test
	public void getShares2() throws Exception{
		String responseString = null;
		String token = this.createTestToken(9L, 2L, 10000);

		try {
			responseString = this.mvc.perform(get("/other/shares?beginTime=2020-11-25 00:00:00&endTime=2020-11-24 00:00:00").header("authorization", token))
					.andExpect(status().isOk())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		}catch (Exception e){
			e.printStackTrace();
		}
		String expectedResponse = "{ \"errno\": 610,\n" +
				"  \"errmsg\": \"开始时间在结束时间之后\"}";
		try{
			JSONAssert.assertEquals(expectedResponse, responseString, false);
		}catch (JSONException e){
			e.printStackTrace();
		}
	}

	@Test
	@Order(1)
	public void getBesharedTest1() throws Exception{
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?page=1&pageSize=1").header("authorization",token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 13,\n" +
				"        \"pages\": 13,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434151,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	@Test
	@Order(2)
	public void getBesharedTest2() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?skuId=505&page=1&pageSize=10").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 2,\n" +
				"        \"pages\": 1,\n" +
				"        \"pageSize\": 10,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434151,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 434542,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}
	/**
	 * /beshared测试3 查询条件beginTime和endTime
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(3)
	public void getBesharedTest3() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 13,\n" +
				"        \"pages\": 13,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434151,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /beshared测试4 查询条件page和pageSize
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(4)
	public void getBesharedTest4() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?page=2&pageSize=5").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 13,\n" +
				"        \"pages\": 3,\n" +
				"        \"pageSize\": 5,\n" +
				"        \"page\": 2,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434160,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 434171,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 434526,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 434542,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 434854,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}


	/**
	 * /beshared测试6 开始时间在结束时间之后,返回空
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(5)
	public void getBesharedTest6() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?beginTime=2020-12-07 22:00:00&endTime=2019-12-07 22:00:00").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 0,\n" +
				"        \"pages\": 0,\n" +
				"        \"pageSize\": 10,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": []\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /beshared测试7 开始时间和结束时间格式错误,返回错误码503
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(6)
	public void getBesharedTest7() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/beshared?beginTime=2020-12-22:00:00&endTime=2019-12-44 :00:00").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shares测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(7)
	public void getSharesTest1() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(get("/other/shares/?page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 442316,\n" +
				"                \"sharerId\": 2,\n" +
				"                \"quantity\": 1,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(8)
	public void createShareActivity1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2021-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2021-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"shopId\": 0,\n" +
				"        \"skuId\": 501,\n" +
				"        \"beginTime\": \"2021-11-30T23:59\",\n" +
				"        \"endTime\": \"2021-12-15T23:23:23\",\n" +
				"        \"state\": 1\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);

	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试2 分享规则格式错误，返回503错误码
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(9)
	public void createShareActivity2() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试3 开始时间为空，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(10)
	public void createShareActivity3() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}



	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试4 规则为空，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(11)
	public void createShareActivity4() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试5 分享活动时段冲突，返回605错误码
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(12)
	public void createShareActivity5() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
						"\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 605\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试6 开始时间格式错误，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(13)
	public void createShareActivity6() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"1234-1321-321\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试7 结束时间格式错误，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(14)
	public void createShareActivity7() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
						"\t\"endTime\":\"23432fq\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * /shops/{shopid}/goods/{skuid}/shareactivities测试8 开始时间在结束时间之后，返回610
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(15)
	public void createShareActivity8() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(post("/other/shops/0/goods/501/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
						"\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 610\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(16)
	public void updateShareActivity1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2021-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2021-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");

		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
						"\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试2 分享规则格式错误，返回614错误码
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(17)
	public void updateShareActivity2() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-01-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-02-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试3 开始时间为空，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(18)
	public void updateShareActivity3() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-03-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-04-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}



	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试4 规则为空，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(19)
	public void updateShareActivity4() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-05-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-06-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试5 分享活动时段冲突，返回605错误码
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(20)
	public void updateShareActivity5() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-07-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-08-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
						"\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 605\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试6 开始时间格式错误，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(21)
	public void updateShareActivity6() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-09-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-10-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"1234-1321-321\",\n" +
						"\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试7 结束时间格式错误，返回503错误码。http状态400
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(22)
	public void updateShareActivity7() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2022-11-30 23:59:00\",\n" +
						"\t\"endTime\":\"2022-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
						"\t\"endTime\":\"23432fq\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试8 分享活动id不存在 错误码504 http状态404
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(23)
	public void updateShareActivity8() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/0").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
						"\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 504\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}测试9 开始时间在结束时间之后 返回610
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(24)
	public void updateShareActivity9() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2023-01-30 23:59:00\",\n" +
						"\t\"endTime\":\"2023-02-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
						"\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 610\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * PUT /shops/{shopid}/shareactivities/{id}/online测试1 成功
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(25)
	public void onlineShareActivity1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2023-03-30 23:59:00\",\n" +
						"\t\"endTime\":\"2023-04-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		String responseString = this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId+"/online").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}


	/**
	 * DELETE /shops/{shopid}/shareactivities/{id}测试1 分享活动id不存在 错误码504 http状态404
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(26)
	public void deleteShareActivity1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(delete("/other/shops/0/shareactivities/0").header("authorization", token))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 504\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}
	/**
	 * DELETE /shops/{shopid}/shareactivities/{id}测试2 分享活动不是该商铺的,返回505 http状态403
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(27)
	public void deleteShareActivity2() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(delete("/other/shops/1/shareactivities/303068").header("authorization", token))
					.andExpect(status().isForbidden())
					.andExpect(content().contentType("application/json;charset=UTF-8"))
					.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 505\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}
	/**
	 * DELETE /shops/{shopid}/shareactivities/{id}测试3 成功
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(28)
	public void deleteShareActivity3() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		//新建一个用于测试
		String responseString2 = this.mvc.perform(post("/other/shops/0/goods/502/shareactivities").header("authorization", token).content
				("{\n" +
						"\t\"beginTime\":\"2023-07-30 23:59:00\",\n" +
						"\t\"endTime\":\"2023-08-15 23:23:23\",\t\n" +
						"\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
						"}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		this.shareActivityId = jsonObject.getLong("id");
		this.mvc.perform(put("/other/shops/0/shareactivities/"+this.shareActivityId+"/online").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String responseString = this.mvc.perform(delete("/other/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shops/{did}/skus/{id}/beshared测试1 成功
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(29)
	public void adminSelectBeshared1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/501/beshared?page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 10,\n" +
				"        \"pages\": 10,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434162,\n" +
				"                \"sharerId\": 1912,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shops/{did}/skus/{id}/beshared测试2 时间条件限制 成功
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(30)
	public void adminSelectBeshared2() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/501/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 10,\n" +
				"        \"pages\": 10,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 434162,\n" +
				"                \"sharerId\": 1912,\n" +
				"                \"customerId\": null,\n" +
				"                \"orderId\": null,\n" +
				"                \"rebate\": null,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shops/{did}/skus/{id}/beshared测试3 开始时间在结束时间之后 返回空
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(31)
	public void adminSelectBeshared3() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/505/beshared?beginTime=2020-12-08 22:00:00&endTime=2020-12-07 22:00:00").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 0,\n" +
				"        \"pages\": 0,\n" +
				"        \"pageSize\": 10,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": []\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}
	/**
	 * GET /shops/{did}/skus/{id}/beshared测试4 开始时间格式错误 错误码503
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(32)
	public void adminSelectBeshared4() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/505/beshared?beginTime=2020-:00&endTime=2020-12-07 22:00:00").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}
	/**
	 * GET /shops/{did}/skus/{id}/beshared测试4 结束时间格式错误 错误码503
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(33)
	public void adminSelectBeshared5() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/505/beshared?beginTime=2020-12-07 22:00:00&endTime=202000:00").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 503\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shops/{did}/skus/{id}/shares测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(34)
	public void adminSelectShares1() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/501/shares?page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 10,\n" +
				"        \"pages\": 10,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 442327,\n" +
				"                \"sharerId\": 1912,\n" +
				"                \"quantity\": 720,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shops/{did}/skus/{id}/shares测试2 时间段查询
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(35)
	public void adminSelectShares2() throws Exception {
		String token = this.createTestToken(1L, 0L, 10000);
		String responseString = this.mvc.perform(get("/other/shops/0/skus/501/shares?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"total\": 10,\n" +
				"        \"pages\": 10,\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 442327,\n" +
				"                \"sharerId\": 1912,\n" +
				"                \"quantity\": 720,\n" +
				"                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * POST /skus/{id}/shares测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(36)
	public void addShares() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String tokenAdmin = this.createTestToken(1L, 0L, 10000);

		//上线一个501商品已有的分享活动
		this.mvc.perform(put("/other/shops/0/shareactivities/304113/online").header("authorization", tokenAdmin))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();

		String responseString = this.mvc.perform(post("/other/skus/501/shares").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"sharerId\": 2," +
				"        \"quantity\": 0\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);

		//查询是否正确
		JSONObject jsonObject = JSONObject.parseObject(new String(responseString)).getJSONObject("data");
		Long shareId = jsonObject.getLong("id");

		String responseString2 = this.mvc.perform(get("/other/shares?skuId=501&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();

		JSONObject jsonObject2 = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
		JSONArray jsonArray = jsonObject2.getJSONArray("list");
		Long shareId2 = jsonArray.getJSONObject(0).getLong("id");
		JSONAssert.assertEquals(shareId.toString(), shareId2.toString(), false);


	}

	/**
	 * POST /skus/{id}/shares测试2 skuid不存在
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(37)
	public void addShares2() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String responseString = this.mvc.perform(post("/other/skus/0/shares").header("authorization", token))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{" +
				"    \"errno\": 504" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shareactivities测试1
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(38)
	public void getShareActivity1() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String tokenAdmin = this.createTestToken(1L, 0L, 10000);

		String responseString = this.mvc.perform(get("/other/shareactivities?skuId=501&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shareactivities测试2 shopId查询
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(39)
	public void getShareActivity2() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);
		String tokenAdmin = this.createTestToken(1L, 0L, 10000);;


		String responseString = this.mvc.perform(get("/other/shareactivities?shopId=0&skuId=501&page=1&pageSize=1").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"pageSize\": 1,\n" +
				"        \"page\": 1,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

	/**
	 * GET /shareactivities测试3 分页查询
	 * @return void
	 * @author Xianwei Wang
	 * created at 12/9/20 12:45 PM
	 */
	@Test
	@Order(39)
	public void getShareActivity3() throws Exception {
		String token = this.createTestToken(2L, -2L, 10000);


		String responseString = this.mvc.perform(get("/other/shareactivities?skuId=501&page=2&pageSize=5").header("authorization", token))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		String expectedResponse = "{\n" +
				"    \"errno\": 0,\n" +
				"    \"data\": {\n" +
				"        \"pageSize\": 5,\n" +
				"        \"page\": 2,\n" +
				"        \"list\": [\n" +
				"            {\n" +
				"                \"id\": 306268,\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 306288,\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 306361,\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 306366,\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"id\": 307518,\n" +
				"                \"shopId\": 0,\n" +
				"                \"skuId\": 501,\n" +
				"                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
				"                \"endTime\": \"2021-10-10T23:23:23\"\n" +
				"            }\n" +
				"        ]\n" +
				"    },\n" +
				"    \"errmsg\": \"成功\"\n" +
				"}";
		JSONAssert.assertEquals(expectedResponse, responseString, false);
	}

}
