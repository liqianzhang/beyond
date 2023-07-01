package com.queue.server;


import com.queue.role.Role;
import com.queue.transport.TLVData;
import com.queue.transport.TLVDecoder;
import com.queue.transport.TLVEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Desctiption
 * @Author wallace
 * @Date 2021/6/4
 */
public class QueueServerInitializer extends ChannelInitializer<SocketChannel> {


	private final Role role;

	public QueueServerInitializer(Role role){
		this.role = role;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline p = socketChannel.pipeline();
		// 先解码，收到消息要解码
		p.addLast(new TLVDecoder(TLVData.MAX_FRAME_LENGTH, TLVData.LENGTH_FIELD_LENGTH,
				TLVData.LENGTH_FIELD_OFFSET, TLVData.LENGTH_ADJUSTMENT, TLVData.INITIAL_BYTES_TO_STRIP));
		// 再编码，发送消息要编码
		p.addLast(new TLVEncoder());
		// handle
		p.addLast(new QueueServerHandler(role));
	}
}
