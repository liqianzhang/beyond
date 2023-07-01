package com.queue.server;

import com.queue.role.Role;
import com.queue.transport.TLVData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Desctiption
 * @Author wallace
 * @Date 2021/6/4
 */
public class QueueServerHandler extends SimpleChannelInboundHandler<Object> {
	private final Role role;

	public QueueServerHandler(Role role){
		this.role = role;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
		if(o == null){
			System.err.println("Received null request.");
			return;
		}

		if(o instanceof TLVData){
			TLVData data = (TLVData) o;
			// 技巧：奇数代表请求，偶数代表响应，定位问题非常方便
			if(data.getCmdCode() == '1'){  //request cmd: odd，response：even
				/*
				* 问题：TLVData都读出来了，直接存下来不行吗？
				* 回答：TLVData可以直接传一个字符串，但是直接传给业务代码，还是要进行
				* 序列化，在new的时候直接就转成一个对象，业务代码就可以直接处理request
				* 如果要修改TLVData，只需要改PubRequest就行了，下面的代码都不用改
				* */
				PubRequest request = new PubRequest(data.getBody());

				//具体的业务处理代码，此处简化
				TLVData response = role.pub(request);
				channelHandlerContext.writeAndFlush(response);

				return;
			}else if(data.getCmdCode() == '3'){
				PullRequest request = new PullRequest(data.getBody());

				//具体的业务处理代码，此处简化
				TLVData response = role.pull(request);
				channelHandlerContext.writeAndFlush(response);

				return;
			}else {
				throw new Exception("invalid message, cmd: " + data.getCmdCode());
			}
		}

		throw new Exception("Unknown request!");
	}
}
