package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * @author WZY
 *
 */
public class RepeatKillExecution extends SeckillException {

	public RepeatKillExecution(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RepeatKillExecution(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
