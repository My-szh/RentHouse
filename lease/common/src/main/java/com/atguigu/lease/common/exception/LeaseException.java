package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class LeaseException extends RuntimeException {

    private Integer code;
    private String message;

    public LeaseException(Integer code, String message) {
        super(message);  // 调用父类构造方法，将message传递给父类
        this.message = message;
        this.code = code;
    }


    public LeaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }
}
