package com.stnts.bi.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author liang.zhang
 * @date 2020年3月25日
 * @desc TODO
 */
@Data
@AllArgsConstructor
public class ResultEntity<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5835280423988735793L;
	private Integer code = 20000;
	private String status ;
	private T data ;
	
	public static <T> ResultEntity<T> success(T data){
		return new ResultEntity<>(ResultEntityEnum.SUCCESS.code, ResultEntityEnum.SUCCESS.msg, data);
	}
	
	public static <T> ResultEntity<T> error(){
		return new ResultEntity<>(ResultEntityEnum.INTERNAL_SERVER_ERROR.code, ResultEntityEnum.INTERNAL_SERVER_ERROR.msg, null);
	}
	
	public static <T> ResultEntity<T> exception(T data){
		return new ResultEntity<>(ResultEntityEnum.EXCEPTION.code, ResultEntityEnum.EXCEPTION.msg, data);
	}
	
	public static <T> ResultEntity<T> exception(String msg){
		return new ResultEntity<>(ResultEntityEnum.EXCEPTION.code, msg, null);
	}
	
	public static <T> ResultEntity<T> param(){
		return param(null);
	}
	
	public static <T> ResultEntity<T> param(T data){
		return new ResultEntity<>(ResultEntityEnum.NOT_VALID_PARAM.code, ResultEntityEnum.NOT_VALID_PARAM.msg, data);
	}
	
	public static <T> ResultEntity<T> param(String msg){
		return new ResultEntity<>(ResultEntityEnum.NOT_VALID_PARAM.code, msg, null);
	}
	
	public static <T> ResultEntity<T> sign(){
		return new ResultEntity<>(ResultEntityEnum.BAD_SIGNATURE.code, ResultEntityEnum.BAD_SIGNATURE.msg, null);
	}
	
	public static <T> ResultEntity<T> custom(int code, String msg, T data){
		return new ResultEntity<>(code, msg, data);
	}
	
	public static <T> ResultEntity<T> custom(ResultEntityEnum resultEntity){
		return new ResultEntity<>(resultEntity.code, resultEntity.msg, null);
	}
	
	public static <T> ResultEntity<T> unsupported(){
		return new ResultEntity<>(ResultEntityEnum.UNSUPPORTED_OPERATION.code, ResultEntityEnum.UNSUPPORTED_OPERATION.msg, null);
	}
	
	public static <T> ResultEntity<T> timeout(){
		return new ResultEntity<>(ResultEntityEnum.TIMEOUT.code, ResultEntityEnum.TIMEOUT.msg, null);
	}
	
	public boolean ok() {
		return this.code.intValue() == ResultEntityEnum.SUCCESS.code;
	}
	
	public static <T> ResultEntity<T> failure(T data){
		return new ResultEntity<>(ResultEntityEnum.FAILURE.code, ResultEntityEnum.FAILURE.msg, data);
	}
	
	public static <T> ResultEntity<T> failure(String msg){
		return new ResultEntity<>(ResultEntityEnum.FAILURE.code, msg, null);
	}

	public static ResultEntity forbidden(String msg){
		return new ResultEntity<>(ResultEntityEnum.FORBIDDEN.code, ResultEntityEnum.FORBIDDEN.msg, msg);
	}
	
	@Getter
	@AllArgsConstructor
	public static enum ResultEntityEnum{

		//兼容前端
	    /** 成功. */
	    SUCCESS(20000, "success"),
	    /** 失败 */
	    FAILURE(30000, "FAILED"),
	    
	    /** 未知的内部错误. */
	    INTERNAL_SERVER_ERROR(50000, "Unknown Internal Error"),
	    /** 异常. */
	    EXCEPTION(50001, "Exception"),
	    
	    /** 错误的请求. */
	    BAD_REQUEST(40000, "Bad Request"),
	    /** 没有效的参数. */
	    NOT_VALID_PARAM(40001, "Invalid Params"),
	    /** 签名认证失败. */
	    BAD_SIGNATURE(40002, "Invalid Signature"),
	    /** 权限验证不通过. */
	    FORBIDDEN(40003, "Forbidden"),
	    /** 未找到. */
	    NOT_FOUND(40004, "Not Found"),
	    /** 操作不支持. */
	    UNSUPPORTED_OPERATION(40005, "Operation not supported"),
		/** 操作不支持. */
	    TIMEOUT(40006, "Timeout");
		
	    private Integer code;
	    private String msg;
	}
}
