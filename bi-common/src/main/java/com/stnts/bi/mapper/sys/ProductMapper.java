package com.stnts.bi.mapper.sys;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.corba.se.impl.naming.pcosnaming.InternalBindingValue;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.ProductEntity;
import org.apache.ibatis.mapping.FetchType;

/**
 * @author liang.zhang
 * @date 2020
 * @desc TODO
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity>{

	@Select("<script>"
			+ "select * from stbi_product where status = 1 "
			+ "<choose>"
			+ "<when test='productIds != null and productIds != \"\"'>"
			+ " and product_id in ("
			+ "<foreach collection='productIds.split(\",\")' item='item' index='index' separator=','>"
			+ "#{item}"
			+ "</foreach>"
			+ ")"
			+ "</when>"
			+ "<otherwise test='productIds == null or productIds == \"\"'>"
			+ " and product_id = -1"
			+ "</otherwise>"
			+ "</choose>"
			+ "</script>")
	List<ProductEntity> getProductByIds(String productIds);
	
	@Insert("<script>"
			+ "insert into stbi_product(product_id, product_name, status, business, classification, sdkproduct, sdkproduct_display) values "
			+ "<foreach collection='products' item='product' index='i' separator=','>"
			+ "(#{product.productId}, #{product.productName}, #{product.status}, #{product.business}, #{product.classification}, #{product.sdkproduct}, #{product.sdkproductDisplay}) "
			+ "</foreach>"
			+ "ON DUPLICATE KEY UPDATE "
			+ "product_name = VALUES(product_name), status = VALUES(status), business=VALUES(business), classification=VALUES(classification), sdkproduct=VALUES(sdkproduct), sdkproduct_display=VALUES(sdkproduct_display)"
			+ "</script>")
	int updateProducts(@Param("products") List<ProductEntity> products);

	/**
	 * 查询产品线列表[带分页]
	 * @param page
	 * @param name
	 * @return
	 */
	@Select("<script>" +
			"select * from stbi_product where product_id != '-9' and status = 1 " +
			"<if test='name != null and name != \"\"'>" +
			" and (product_name like concat(\"%\",#{name},\"%\") or product_id like concat(\"%\",#{name},\"%\") or  classification like concat(\"%\",#{name},\"%\"))" +
			"</if>" +
			"</script>")
	@Results({
			@Result(id = true, column = "product_id", property = "productId"),
			@Result(column = "product_name", property = "productName"),
			@Result(column = "status", property = "status"),
			@Result(column = "business", property = "business"),
			@Result(column = "classification", property = "classification"),
			@Result(column = "sdkproduct", property = "sdkproduct"),
			@Result(column = "sdkproduct_display", property = "sdkproductDisplay"),
			@Result(column = "product_id", property = "coverUserNum", one = @One(select = "com.stnts.bi.mapper.sys.ProductMapper.coverUserNum", fetchType = FetchType.LAZY))
	})
	List<ProductEntity> listProducts(@Param("page") Page<ProductEntity> page, @Param("name") String name);

//	@Select("select count(distinct user_id) from stbi_user_role where product_ids = '-9' or find_in_set(#{productId}, product_ids) > 0")
//	Integer coverUserNum(Integer productId);

	@Select("select count(distinct user_id) from stbi_user_product where product_id = '-9' or product_id = #{productId}")
	Integer coverUserNum(String productId);

	@Update("<script>" +
			"update stbi_product set status = 0 where product_id != '-9' and product_id not in " +
			"<foreach collection='productIds' item = 'productId' open='(' close=')' separator=','>" +
			"#{productId}" +
			"</foreach>" +
			"</script>")
	Integer updateStatus(@Param("productIds") List<String> productIds);

	@Select("select * from stbi_product where product_id in (select product_id from stbi_user_product where user_id = #{userId})")
	List<ProductEntity> listProductByUser(int userId);

	@Update("update stbi_product set level_id = null where product_id = #{productId}")
	int unBind(String productId);
}
