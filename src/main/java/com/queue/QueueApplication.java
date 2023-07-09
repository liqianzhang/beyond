package com.queue;

import com.queue.role.Master;

import com.queue.role.Role;
import com.queue.role.Slave;
import com.queue.server.QueueServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.cli.*;

/**
 * @Desctiption SpringBoot Application
 * @Author wallace
 * @Date 2021/6/
 * 疑问：这和消息队列有什么关系？核心代码能够自己写出来
 */
public class QueueApplication {
	public static void main(String args[]) throws Exception{
		System.out.println("Welcome to queue system!");

		CommandLine cmd = parseOptions(args, buildOptions());

		//parse options
		String systemName = "";
		if(cmd.hasOption("S")) {
			systemName = cmd.getOptionValue('S');
		}else{
			System.err.println("please input system name by -S option");
			usage();
			System.exit(1);
		}

		String groupName = "";
		if(cmd.hasOption("g")) {
			groupName = cmd.getOptionValue('g');
		}else{
			System.err.println("please input group name by -g option");
			usage();
			System.exit(1);
		}

		String zkAddr = "";
		if(cmd.hasOption('z')) {
			zkAddr = cmd.getOptionValue('z');
		}else{
			System.err.println("please input the ZooKeeper address by -z option");
			usage();
			System.exit(1);
		}

		int port = 8080;
		if(cmd.hasOption('p')) {
			port = Integer.valueOf(cmd.getOptionValue('p'));
		}

		//build role
		Role role;
		// 启动参数如果指定当前的节点是master，就new一个master
		// zk的代码在master和slave中实现
		if(cmd.hasOption('m')) {
			role = new Master(systemName, groupName, zkAddr);
		}else if(cmd.hasOption('s')){
			role = new Slave(systemName, groupName, zkAddr);
		}else{
			System.err.println("please specific the role use -m (master) or -s (slave)");
			usage();
			System.exit(1);
			return;
		}

		role.start();

		//build netty server.网络如何和业务节点结合呢？
		EventLoopGroup bossGroup = new NioEventLoopGroup(1); // Reactor
		EventLoopGroup workerGroup = new NioEventLoopGroup(8);// handle
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			serverBootstrap.group(bossGroup, workerGroup) // 组装两个group
					.channel(NioServerSocketChannel.class)
					// 添加自己的处理业务，关键点是把role传进去，Netty
					// 的网络模型流水线去处理
					.childHandler(new QueueServerInitializer(role));

			Channel ch = serverBootstrap.bind(port).sync().channel();
			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private static Options buildOptions(){
		final Options options = new Options();

		options.addOption("p", true, "server port");       //port
		options.addOption("m", "node is master");                 //master
		options.addOption("s", "node is slave");                  //slave
		options.addOption("z", true, "ZooKeeper address"); //zk address
		options.addOption("S", true, "system name");       //system name
		options.addOption("g", true, "group name");        //group name

		return options;
	}

	private static CommandLine parseOptions(String args[], Options options) throws Exception {
		final CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			throw new Exception("parser command line error",e);
		}

		return cmd;
	}

	private static void usage(){
		System.out.println("Usage: \n java -jar queueDemo.jar -p {port} -S {system name} -g {group name} -z {zookeeper address} -m/-s (master/slave) ");
	}

}
