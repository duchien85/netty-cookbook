package netty.cookbook.chapter1.recipe6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProcessingPipeline extends ChannelInitializer<SocketChannel>{
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		 ChannelPipeline p = ch.pipeline();		         
         // the Decoder
         p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
         // the Encoder
         p.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
         // the log handler and data transformer
         p.addLast("logger",new MessageToMessageDecoder<String>(){
			@Override
			protected void decode(ChannelHandlerContext ctx, String msg,List<Object> out) throws Exception {
				System.out.println(String.format("logged raw data '%s'", msg));				
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				Map<String, String> request = new HashMap<>();
				request.put("data", msg);
				request.put("from-ip", address.getAddress().getHostAddress());
				out.add(request);
			}        	 
         });
         // the processing logic handler
         p.addLast("handler",new SimpleChannelInboundHandler<Map<String, String>>(){
        	 @Override
        	public void channelRead0(ChannelHandlerContext ctx, Map<String, String> request)
        			throws Exception {
        		System.out.println("from-host: "+request.get("from-ip"));
        		System.out.println("data: "+request.get("data"));
        		ctx.writeAndFlush("Done");
        	}        
         });
	}
}
