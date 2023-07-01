package com.queue.client;

import com.queue.transport.TLVDecoder;
import com.queue.transport.TLVEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import static com.queue.transport.TLVData.*;


/**
 * @Desctiption
 * @Author wallace
 * @Date 2021/6/4
 */
public class QueueClientInitializer extends ChannelInitializer<SocketChannel> {
	public QueueClientInitializer(){
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline p = socketChannel.pipeline();

		p.addLast(new TLVEncoder());
		p.addLast(new TLVDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_LENGTH,
				LENGTH_FIELD_OFFSET, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
		p.addLast(new QueueClientHandler());

	}
}
